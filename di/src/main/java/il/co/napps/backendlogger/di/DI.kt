package il.co.napps.backendlogger.di

import il.co.napps.backendlogger.services.database.DatabaseService
import il.co.napps.backendlogger.services.messages.MessagesRepository
import il.co.napps.backendlogger.services.os.InstanceCreator
import il.co.napps.backendlogger.services.os.RestProvider
import il.co.napps.backendlogger.services.rest.RestService
import il.co.napps.backendlogger.utils.DIProvidable
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.module
import kotlin.reflect.KClass

interface DI {
    fun <T: DIProvidable> get(clazz: KClass<T>): T
    fun <T: DIProvidable> inject(clazz: KClass<T>): Lazy<T>
}

@Suppress("unused")
internal class DIImpl: DI {

    private var koin: Koin

    init {

        val instanceCreator = Class.forName("il.co.napps.backendlogger.services.android.instancecreator.InstanceCreatorImpl").newInstance() as InstanceCreator

        val restProvider = instanceCreator.createInstance("il.co.napps.backendlogger.services.android.rest.RestProviderImpl") as RestProvider
        val restService = instanceCreator.createInstance("il.co.napps.backendlogger.services.rest.RestServiceImpl", restProvider) as RestService
        val databaseService = instanceCreator.createInstance("il.co.napps.backendlogger.services.database.DatabaseServiceImpl") as DatabaseService
        val messagesRepository = instanceCreator.createInstance("il.co.napps.backendlogger.services.messages.MessageRepositoryImpl", restService, databaseService) as MessagesRepository

        val modules = module {
            single { messagesRepository }
        }

        koin = KoinApplication.create()
            .modules(modules)
            .koin
    }

    override fun <T : DIProvidable> get(clazz: KClass<T>): T {
        return koin.get(clazz, null, null)
    }

    override fun <T : DIProvidable> inject(clazz: KClass<T>): Lazy<T> {
        @Suppress("RemoveExplicitTypeArguments")
        return lazy<T> { koin.get(clazz, null, null) }
    }
}

