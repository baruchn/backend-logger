package il.co.napps.backendlogger.services.os.database

import com.squareup.sqldelight.db.SqlDriver

interface DatabaseDriverProvider {
    val driver: SqlDriver
}