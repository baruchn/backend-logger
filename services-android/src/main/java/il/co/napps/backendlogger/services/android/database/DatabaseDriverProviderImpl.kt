package il.co.napps.backendlogger.services.android.database

import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import il.co.napps.backendlogger.services.android.context
import il.co.napps.backendlogger.services.database.BackendLoggerDatabase
import il.co.napps.backendlogger.services.os.database.DatabaseDriverProvider

@Suppress("unused")
internal class DatabaseDriverProviderImpl:
    DatabaseDriverProvider {
    override val driver: SqlDriver = AndroidSqliteDriver(BackendLoggerDatabase.Schema, context, "il.co.napps.backendlogger.db")
}