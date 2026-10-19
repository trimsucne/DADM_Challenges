package com.example.reto6.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "companies.db"
        const val DATABASE_VERSION = 1

        const val TABLE_COMPANIES = "companies"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "nombre"
        const val COLUMN_URL = "url"
        const val COLUMN_PHONE = "telefono"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_SERVICES = "servicios"
        const val COLUMN_CLASSIFICATION = "clasificacion"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSql = """
            CREATE TABLE $TABLE_COMPANIES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_URL TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_SERVICES TEXT NOT NULL,
                $COLUMN_CLASSIFICATION TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMPANIES")
        onCreate(db)
    }

    fun getAllCompanies(): List<Company> {
        return searchCompanies(query = "", classificationFilter = "All")
    }

    fun searchCompanies(query: String, classificationFilter: String): List<Company> {
        val list = mutableListOf<Company>()
        val db = readableDatabase

        val selectionList = mutableListOf<String>()
        val selectionArgsList = mutableListOf<String>()

        if (query.isNotBlank()) {
            selectionList.add("$COLUMN_NAME LIKE ?")
            selectionArgsList.add("%$query%")
        }

        if (classificationFilter.isNotBlank() && classificationFilter != "All") {
            selectionList.add("$COLUMN_CLASSIFICATION = ?")
            selectionArgsList.add(classificationFilter)
        }

        val selection = if (selectionList.isNotEmpty()) selectionList.joinToString(" AND ") else null
        val selectionArgs = if (selectionArgsList.isNotEmpty()) selectionArgsList.toTypedArray() else null

        val cursor = db.query(
            TABLE_COMPANIES,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$COLUMN_NAME ASC"
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(COLUMN_ID)
            val nameIndex = c.getColumnIndexOrThrow(COLUMN_NAME)
            val urlIndex = c.getColumnIndexOrThrow(COLUMN_URL)
            val phoneIndex = c.getColumnIndexOrThrow(COLUMN_PHONE)
            val emailIndex = c.getColumnIndexOrThrow(COLUMN_EMAIL)
            val servicesIndex = c.getColumnIndexOrThrow(COLUMN_SERVICES)
            val classIndex = c.getColumnIndexOrThrow(COLUMN_CLASSIFICATION)

            while (c.moveToNext()) {
                list.add(
                    Company(
                        id = c.getLong(idIndex),
                        name = c.getString(nameIndex),
                        url = c.getString(urlIndex),
                        phone = c.getString(phoneIndex),
                        email = c.getString(emailIndex),
                        services = c.getString(servicesIndex),
                        classification = c.getString(classIndex)
                    )
                )
            }
        }
        return list
    }

    fun insertCompany(company: Company): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, company.name)
            put(COLUMN_URL, company.url)
            put(COLUMN_PHONE, company.phone)
            put(COLUMN_EMAIL, company.email)
            put(COLUMN_SERVICES, company.services)
            put(COLUMN_CLASSIFICATION, company.classification)
        }
        return db.insert(TABLE_COMPANIES, null, values)
    }

    fun updateCompany(company: Company): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, company.name)
            put(COLUMN_URL, company.url)
            put(COLUMN_PHONE, company.phone)
            put(COLUMN_EMAIL, company.email)
            put(COLUMN_SERVICES, company.services)
            put(COLUMN_CLASSIFICATION, company.classification)
        }
        return db.update(
            TABLE_COMPANIES,
            values,
            "$COLUMN_ID = ?",
            arrayOf(company.id.toString())
        )
    }

    fun deleteCompany(id: Long): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_COMPANIES,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }
}
