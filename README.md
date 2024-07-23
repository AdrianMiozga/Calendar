> [!IMPORTANT]
> Work in progress.

## Technology Stack

- Java 16
  - [`com.sun.net.httpserver`](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/package-summary.html) - HTTP server
- [Retrofit](https://square.github.io/retrofit/) - HTTP client
- [Gson](https://github.com/google/gson) - serialization/deserialization
- [JUnit 5](https://junit.org/junit5/) - unit testing
- [Gradle](https://gradle.org/) - build tool
  - [Shadow](https://github.com/johnrengelman/shadow) - creating a fat `jar`
  - [Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [google-java-format](https://github.com/google/google-java-format) with AOSP style - code formatter

### API

- [Google Calendar API](https://developers.google.com/calendar/api/guides/overview)
