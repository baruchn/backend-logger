package il.co.napps.backendlogger.services.os.instancecreator

interface InstanceCreator {
    fun createInstance(name: String, vararg constructorArgs: Any): Any
}