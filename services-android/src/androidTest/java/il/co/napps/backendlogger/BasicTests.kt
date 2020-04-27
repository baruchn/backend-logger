package il.co.napps.backendlogger

import androidx.test.platform.app.InstrumentationRegistry
import il.co.napps.backendlogger.di.DI
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BasicTests {

    init {
        setContext()
    }

    @Test
    fun initDI() {
        Class.forName("il.co.napps.backendlogger.di.DIImpl").newInstance() as DI
    }

    private fun setContext() {
        val clazz = Class.forName("il.co.napps.backendlogger.services.android.ContextProviderKt")
        val field = clazz.declaredFields[0]
        field.isAccessible = true
        field.set(clazz, InstrumentationRegistry.getInstrumentation().context)
    }

}
