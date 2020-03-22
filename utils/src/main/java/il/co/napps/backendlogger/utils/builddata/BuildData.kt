package il.co.napps.backendlogger.utils.builddata

import il.co.napps.backendlogger.utils.DIProvidable

interface BuildData: DIProvidable {
    val isDebug: Boolean
}

internal class BuildDataImpl(override val isDebug: Boolean):
    BuildData