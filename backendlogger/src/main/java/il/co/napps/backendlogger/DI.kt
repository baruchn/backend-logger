package il.co.napps.backendlogger

import il.co.napps.backendlogger.di.DI
import il.co.napps.backendlogger.utils.DIProvidable

private lateinit var di: DI

internal fun initializeDi() {
    if (!::di.isInitialized) {
        di = Class.forName("il.co.napps.backendlogger.di.DIImpl").newInstance() as DI
    }
}

internal inline fun <reified T: DIProvidable> get(): T =
    di.get(T::class)

internal inline fun <reified T: DIProvidable> inject(): Lazy<T> =
    di.inject(T::class)