package com.example.reto5.online

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class FirebaseGameService {
    private var firestore: FirebaseFirestore? = null
    private var activeGameListener: ListenerRegistration? = null

    // Fallback cloud endpoint if Firestore is not reachable
    private val cloudBaseUrl = "https://triqui-online-default-rtdb.firebaseio.com/games"

    // In-memory fallback
    private val mockLobbyGames = MutableStateFlow<Map<String, OnlineGameModel>>(emptyMap())

    init {
        try {
            firestore = FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            firestore = null
        }
    }

    fun getAvailableGames(): Flow<List<OnlineGameModel>> = callbackFlow {
        val db = firestore
        if (db != null) {
            val listener = db.collection("games")
                .whereEqualTo("status", OnlineGameModel.STATUS_WAITING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(mockLobbyGames.value.values.filter { it.status == OnlineGameModel.STATUS_WAITING })
                        return@addSnapshotListener
                    }
                    val games = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(OnlineGameModel::class.java)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    } ?: emptyList()
                    trySend(games)
                }
            awaitClose { listener.remove() }
        } else {
            val subscription = mockLobbyGames.collect { gamesMap ->
                trySend(gamesMap.values.filter { it.status == OnlineGameModel.STATUS_WAITING })
            }
            awaitClose { }
        }
    }

    fun createGame(hostName: String, onComplete: (String?) -> Unit) {
        val gameId = UUID.randomUUID().toString().take(6).uppercase()
        val newGame = OnlineGameModel(
            gameId = gameId,
            hostName = hostName,
            guestName = "",
            board = "         ",
            currentTurn = "X",
            status = OnlineGameModel.STATUS_WAITING,
            winner = " "
        )

        val db = firestore
        if (db != null) {
            try {
                db.collection("games").document(gameId).set(gameToMap(newGame))
                    .addOnSuccessListener { onComplete(gameId) }
                    .addOnFailureListener {
                        saveGameToCloudRest(newGame, onComplete)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                saveGameToCloudRest(newGame, onComplete)
            }
        } else {
            saveGameToCloudRest(newGame, onComplete)
        }
    }

    private fun saveGameToCloudRest(game: OnlineGameModel, onComplete: (String?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("$cloudBaseUrl/${game.gameId}.json")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")

                val jsonStr = gameToJson(game)
                conn.outputStream.use { it.write(jsonStr.toByteArray(Charsets.UTF_8)) }

                val responseCode = conn.responseCode
                if (responseCode in 200..299) {
                    withContext(Dispatchers.Main) { onComplete(game.gameId) }
                } else {
                    mockLobbyGames.value = mockLobbyGames.value + (game.gameId to game)
                    withContext(Dispatchers.Main) { onComplete(game.gameId) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mockLobbyGames.value = mockLobbyGames.value + (game.gameId to game)
                withContext(Dispatchers.Main) { onComplete(game.gameId) }
            }
        }
    }

    fun joinGame(gameId: String, guestName: String, onComplete: (Boolean) -> Unit) {
        val db = firestore
        if (db != null) {
            val gameRef = db.collection("games").document(gameId)
            gameRef.get().addOnSuccessListener { doc ->
                val game = try { doc.toObject(OnlineGameModel::class.java) } catch (e: Exception) { null }
                if (game != null && game.status == OnlineGameModel.STATUS_WAITING) {
                    val updated = game.copy(
                        guestName = guestName,
                        status = OnlineGameModel.STATUS_PLAYING
                    )
                    gameRef.set(gameToMap(updated))
                        .addOnSuccessListener { onComplete(true) }
                        .addOnFailureListener {
                            joinGameCloudRest(gameId, guestName, onComplete)
                        }
                } else {
                    joinGameCloudRest(gameId, guestName, onComplete)
                }
            }.addOnFailureListener {
                joinGameCloudRest(gameId, guestName, onComplete)
            }
        } else {
            joinGameCloudRest(gameId, guestName, onComplete)
        }
    }

    private fun joinGameCloudRest(gameId: String, guestName: String, onComplete: (Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val existing = fetchSingleCloudGame(gameId) ?: mockLobbyGames.value[gameId]
                if (existing != null && existing.status == OnlineGameModel.STATUS_WAITING) {
                    val updated = existing.copy(
                        guestName = guestName,
                        status = OnlineGameModel.STATUS_PLAYING
                    )
                    val url = URL("$cloudBaseUrl/$gameId.json")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "PUT"
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json")

                    val jsonStr = gameToJson(updated)
                    conn.outputStream.use { it.write(jsonStr.toByteArray(Charsets.UTF_8)) }

                    val code = conn.responseCode
                    if (code in 200..299) {
                        withContext(Dispatchers.Main) { onComplete(true) }
                    } else {
                        mockLobbyGames.value = mockLobbyGames.value + (gameId to updated)
                        withContext(Dispatchers.Main) { onComplete(true) }
                    }
                } else {
                    withContext(Dispatchers.Main) { onComplete(false) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val existing = mockLobbyGames.value[gameId]
                if (existing != null && existing.status == OnlineGameModel.STATUS_WAITING) {
                    val updated = existing.copy(
                        guestName = guestName,
                        status = OnlineGameModel.STATUS_PLAYING
                    )
                    mockLobbyGames.value = mockLobbyGames.value + (gameId to updated)
                    withContext(Dispatchers.Main) { onComplete(true) }
                } else {
                    withContext(Dispatchers.Main) { onComplete(false) }
                }
            }
        }
    }

    fun observeGame(gameId: String): Flow<OnlineGameModel?> = callbackFlow {
        val db = firestore
        if (db != null) {
            val listener = db.collection("games").document(gameId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(mockLobbyGames.value[gameId])
                        return@addSnapshotListener
                    }
                    val game = try { snapshot?.toObject(OnlineGameModel::class.java) } catch (e: Exception) { null }
                    trySend(game)
                }
            awaitClose { listener.remove() }
        } else {
            val subscription = mockLobbyGames.collect { gamesMap ->
                trySend(gamesMap[gameId])
            }
            awaitClose { }
        }
    }

    fun makeMove(gameId: String, newBoard: List<Char>, nextTurn: String, winner: String) {
        val boardStr = newBoard.joinToString("")
        val status = if (winner != " ") OnlineGameModel.STATUS_FINISHED else OnlineGameModel.STATUS_PLAYING

        val db = firestore
        if (db != null) {
            val updates = mapOf(
                "board" to boardStr,
                "currentTurn" to nextTurn,
                "winner" to winner,
                "status" to status
            )
            db.collection("games").document(gameId).update(updates)
                .addOnFailureListener {
                    makeMoveCloudRest(gameId, boardStr, nextTurn, winner, status)
                }
        } else {
            makeMoveCloudRest(gameId, boardStr, nextTurn, winner, status)
        }
    }

    private fun makeMoveCloudRest(gameId: String, boardStr: String, nextTurn: String, winner: String, status: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val existing = fetchSingleCloudGame(gameId) ?: mockLobbyGames.value[gameId]
                if (existing != null) {
                    val updated = existing.copy(
                        board = boardStr,
                        currentTurn = nextTurn,
                        winner = winner,
                        status = status
                    )
                    val url = URL("$cloudBaseUrl/$gameId.json")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "PUT"
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json")

                    val jsonStr = gameToJson(updated)
                    conn.outputStream.use { it.write(jsonStr.toByteArray(Charsets.UTF_8)) }
                    conn.responseCode
                    mockLobbyGames.value = mockLobbyGames.value + (gameId to updated)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetOnlineGame(gameId: String) {
        val db = firestore
        if (db != null) {
            val updates = mapOf(
                "board" to "         ",
                "currentTurn" to "X",
                "winner" to " ",
                "status" to OnlineGameModel.STATUS_PLAYING
            )
            db.collection("games").document(gameId).update(updates)
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val existing = fetchSingleCloudGame(gameId) ?: mockLobbyGames.value[gameId]
                    if (existing != null) {
                        val updated = existing.copy(
                            board = "         ",
                            currentTurn = "X",
                            winner = " ",
                            status = OnlineGameModel.STATUS_PLAYING
                        )
                        val url = URL("$cloudBaseUrl/$gameId.json")
                        val conn = url.openConnection() as HttpURLConnection
                        conn.requestMethod = "PUT"
                        conn.doOutput = true
                        conn.setRequestProperty("Content-Type", "application/json")

                        val jsonStr = gameToJson(updated)
                        conn.outputStream.use { it.write(jsonStr.toByteArray(Charsets.UTF_8)) }
                        conn.responseCode
                        mockLobbyGames.value = mockLobbyGames.value + (gameId to updated)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun fetchCloudGames(): List<OnlineGameModel>? {
        val url = URL("$cloudBaseUrl.json")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 3000
        conn.readTimeout = 3000

        if (conn.responseCode == 200) {
            val response = conn.inputStream.bufferedReader().use { it.readText() }
            if (response == "null" || response.isBlank()) return emptyList()

            val jsonMap = JSONObject(response)
            val list = mutableListOf<OnlineGameModel>()
            val keys = jsonMap.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val itemObj = jsonMap.optJSONObject(key)
                if (itemObj != null) {
                    list.add(jsonToGame(itemObj))
                }
            }
            return list
        }
        return null
    }

    private fun fetchSingleCloudGame(gameId: String): OnlineGameModel? {
        val url = URL("$cloudBaseUrl/$gameId.json")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 3000
        conn.readTimeout = 3000

        if (conn.responseCode == 200) {
            val response = conn.inputStream.bufferedReader().use { it.readText() }
            if (response == "null" || response.isBlank()) return null
            val obj = JSONObject(response)
            return jsonToGame(obj)
        }
        return null
    }

    private fun gameToMap(game: OnlineGameModel): Map<String, Any> {
        return mapOf(
            "gameId" to game.gameId,
            "hostName" to game.hostName,
            "guestName" to game.guestName,
            "board" to game.board,
            "currentTurn" to game.currentTurn,
            "status" to game.status,
            "winner" to game.winner,
            "createdAt" to game.createdAt
        )
    }

    private fun gameToJson(game: OnlineGameModel): String {
        val json = JSONObject()
        json.put("gameId", game.gameId)
        json.put("hostName", game.hostName)
        json.put("guestName", game.guestName)
        json.put("board", game.board)
        json.put("currentTurn", game.currentTurn)
        json.put("status", game.status)
        json.put("winner", game.winner)
        json.put("createdAt", game.createdAt)
        return json.toString()
    }

    private fun jsonToGame(obj: JSONObject): OnlineGameModel {
        return OnlineGameModel(
            gameId = obj.optString("gameId", ""),
            hostName = obj.optString("hostName", ""),
            guestName = obj.optString("guestName", ""),
            board = obj.optString("board", "         "),
            currentTurn = obj.optString("currentTurn", "X"),
            status = obj.optString("status", OnlineGameModel.STATUS_WAITING),
            winner = obj.optString("winner", " "),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
        )
    }

    fun detachListener() {
        activeGameListener?.remove()
        activeGameListener = null
    }
}
