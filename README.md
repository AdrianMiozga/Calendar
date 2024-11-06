https://github.com/user-attachments/assets/5a4bcb8c-e70e-4457-964e-5ce5917383ba

## Setup

1. Create a new app in [Google Cloud](https://console.cloud.google.com/projectcreate).
2. Configure OAuth consent screen with `https://www.googleapis.com/auth/calendar.events.readonly` scope.
3. Add OAuth 2.0 web application client.
4. Create `config.json` in root project directory or directory with `exe`/`jar` and fill the following template.

```json
{
  "clientId": "<google_client_id>",
  "clientSecret": "<google_client_secret>",
  "redirectURI": "<google_redirect_uri>"
}
```

## Technology Stack

- Java 16
  - [`com.sun.net.httpserver`](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/package-summary.html) - HTTP server
- [Retrofit](https://square.github.io/retrofit/) - HTTP client
- [Gson](https://github.com/google/gson) - serialization/deserialization
- [jSystemThemeDetector](https://github.com/Dansoftowner/jSystemThemeDetector) - dark mode detector
- Logging
  - [SLF4J](https://github.com/qos-ch/slf4j) - abstraction layer for various logging frameworks
  - [Logback](https://github.com/qos-ch/logback) - logging library
- [JUnit 5](https://junit.org/junit5/) - unit testing
- [Gradle](https://gradle.org/) - build tool
  - [Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
  - [Shadow](https://github.com/GradleUp/shadow) - creating a fat `jar`
  - [gradle-launch4j](https://github.com/TheBoegl/gradle-launch4j) - creating `exe`
  - [GraalVM](https://www.graalvm.org/) - creating a native executable
- [google-java-format](https://github.com/google/google-java-format) with AOSP style - code formatter

### API

- [Google Calendar API](https://developers.google.com/calendar/api/guides/overview)
