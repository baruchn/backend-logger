# backend-logger
This project is provided as a sample for the Android library modularized architecture.

This library provides a way to log information to a remote server. 
The messages are persisted until sent and a retry mechnism is implemented if the operation fails.

Example of use:
```kotlin
val backendLogger = BackendLogger("https://your.domain/api/log") {
    sizeLimit = 2
}
val message = mutableMapOf<String, Any>()
message["message"] = "Test message"
backendLogger.sendMessage(message)
```
The messages are sent as Json payload on a POST request.
