package il.co.napps.backendlogger.services.os

interface InstanceCreator {
    fun createInstance(name: String, vararg constructorArgs: Any): Any
}