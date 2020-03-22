package il.co.napps.backendlogger.utils.builddata

interface BuildData {
    val isDebug: Boolean
}

internal class BuildDataImpl(override val isDebug: Boolean):
    BuildData