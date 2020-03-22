package il.co.napps.backendlogger.services.os.builddata

import il.co.napps.backendlogger.utils.DIProvidable


private const val TAG = "OsBuildData"

interface OSBuildData: DIProvidable {
    val isDebug: Boolean
}