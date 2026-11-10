package com.example.reto7.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.reto7.data.PoiItem
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

val ESRI_WORLD_STREET_TILE_SOURCE = object : OnlineTileSourceBase(
    "EsriWorldStreetMap",
    0,
    19,
    256,
    ".png",
    arrayOf(
        "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/",
        "https://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/"
    )
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" +
                MapTileIndex.getY(pMapTileIndex) + "/" +
                MapTileIndex.getX(pMapTileIndex)
    }
}

@Composable
fun OsmMapView(
    userLat: Double,
    userLon: Double,
    radiusKm: Double,
    nearbyPois: List<Pair<PoiItem, Double>>,
    onPoiSelected: (Pair<PoiItem, Double>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val userMarkerDrawable = remember(context) { createUserLocationDrawable(context) }

    var isInitialCenterDone by remember { mutableStateOf(false) }
    var lastRadiusKm by remember { mutableDoubleStateOf(-1.0) }

    val mapView = remember {
        val appUserAgent = "Reto7GpsPoiApp/1.0 (com.example.reto7; Android)"
        Configuration.getInstance().userAgentValue = appUserAgent
        Configuration.getInstance().additionalHttpRequestProperties["User-Agent"] = appUserAgent
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

        MapView(context).apply {
            setTileSource(ESRI_WORLD_STREET_TILE_SOURCE)
            setMultiTouchControls(true)
        }
    }

    AndroidView(
        factory = { mapView },
        update = { map ->
            map.overlays.clear()

            val userPoint = GeoPoint(userLat, userLon)

            // Auto-center and zoom ONLY on initial load or when search radius changes
            if (!isInitialCenterDone || lastRadiusKm != radiusKm) {
                val targetZoom = when {
                    radiusKm <= 2.0 -> 15.0
                    radiusKm <= 5.0 -> 13.5
                    radiusKm <= 15.0 -> 12.0
                    radiusKm <= 50.0 -> 10.0
                    radiusKm <= 150.0 -> 8.5
                    radiusKm <= 350.0 -> 7.0
                    radiusKm <= 600.0 -> 6.0
                    else -> 5.0
                }
                map.controller.setCenter(userPoint)
                map.controller.setZoom(targetZoom)
                isInitialCenterDone = true
                lastRadiusKm = radiusKm
            }

            // User GPS Live Location Marker (Updates position in real-time without resetting user's manual zoom/pan)
            val userMarker = Marker(map).apply {
                position = userPoint
                title = "Tu Ubicación GPS (En Vivo)"
                snippet = "Coordenadas: Lat %.5f, Lon %.5f".format(userLat, userLon)
                icon = userMarkerDrawable
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            }
            map.overlays.add(userMarker)

            // POI Markers
            nearbyPois.forEach { pair ->
                val poi = pair.first
                val distance = pair.second
                val poiPoint = GeoPoint(poi.latitude, poi.longitude)

                val poiMarker = Marker(map).apply {
                    position = poiPoint
                    title = poi.name
                    snippet = "${poi.category} • %.1f km\nLat: %.4f, Lon: %.4f".format(distance, poi.latitude, poi.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        onPoiSelected(pair)
                        showInfoWindow()
                        true
                    }
                }
                map.overlays.add(poiMarker)
            }

            map.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )
}

/**
 * Generates a distinct bright blue circle marker for the user's live GPS position
 */
private fun createUserLocationDrawable(context: Context): Drawable {
    val sizePx = (32 * context.resources.displayMetrics.density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val center = sizePx / 2f
    val outerRadius = sizePx / 2f - 2f
    val innerRadius = sizePx / 4f

    // Translucent Blue Pulse Ring
    val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#442196F3")
        style = Paint.Style.FILL
    }
    canvas.drawCircle(center, center, outerRadius, outerPaint)

    // White Border Ring
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(center, center, innerRadius + 5f, borderPaint)

    // Bright Blue Core Circle
    val corePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1976D2")
        style = Paint.Style.FILL
    }
    canvas.drawCircle(center, center, innerRadius, corePaint)

    return BitmapDrawable(context.resources, bitmap)
}
