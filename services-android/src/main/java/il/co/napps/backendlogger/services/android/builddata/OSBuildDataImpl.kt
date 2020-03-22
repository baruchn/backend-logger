package il.co.napps.backendlogger.services.android.builddata

import il.co.napps.backendlogger.BuildConfig
import il.co.napps.backendlogger.services.os.builddata.OSBuildData


private const val TAG = "OsBuildData"

class OSBuildDataImpl: OSBuildData {
    override val isDebug = BuildConfig.DEBUG
}