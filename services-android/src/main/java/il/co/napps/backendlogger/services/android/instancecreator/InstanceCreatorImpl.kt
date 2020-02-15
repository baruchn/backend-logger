package il.co.napps.backendlogger.services.android.instancecreator

import il.co.napps.backendlogger.services.android.context
import il.co.napps.backendlogger.services.os.InstanceCreator

@Suppress("unused")
internal class InstanceCreatorImpl: InstanceCreator {
    override fun createInstance(name: String, vararg constructorArgs: Any): Any {
        return Class.forName(name, false, context.classLoader).constructors.first().newInstance(*constructorArgs)
//        if (constructorArgs.size == 1) {
//            return (Class.forName(providerName, false, context.classLoader).kotlin.objectInstance as ClassProvider<T>).get(constructorArgs[0])
//        }
//        return (Class.forName(providerName, false, context.classLoader).kotlin.objectInstance as ClassProvider<T>).get(*constructorArgs)
    }
}