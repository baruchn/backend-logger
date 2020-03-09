# backend-logger
This project is provided as a sample for the Android library modularized architecture.

This library provides a way to log information to a remote server. 
The messages are persisted until sent and a retry mechnism is implemented if the operation fails.

Example of use:
```kotlin
val backendLogger = BackendLogger("https://your.domain/api/log") {
    sizeLimit = 100 // this is the default value
}
val message = mutableMapOf<String, Any>()
message["message"] = "Test message"
backendLogger.sendMessage(message)
```
The messages are sent as JSON payload on a POST request.

Structure diagram:
![Backend Logger Modules](https://user-images.githubusercontent.com/20625205/76210786-7da0ab80-620d-11ea-9ac7-db4cb28c7033.png)
