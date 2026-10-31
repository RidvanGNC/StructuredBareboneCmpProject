# Data module

`data` owns Ktor client configuration and CMP repository implementations. It is not a React data
layer and it is not an authorization authority.

## HTTP client

`createHttpClient(HttpClientOptions)` builds the shared client: lenient JSON, timeouts, the
`X-Client-Version` header (`AppInfo`), retries for idempotent requests only, optional bearer
authentication with single-flight refresh, and a validator that turns failed responses into
`ServerException` carrying the parsed `ProblemDetails`. See `contracts/README.md` for the rules.

- Token handling goes through `TokenProvider`; the secure implementation belongs to the host app.
  `InMemoryTokenProvider` is for tests only.
- The refresh request uses a separate client without the Auth plugin, otherwise a rejected refresh
  would trigger another refresh and deadlock.
- A 426 response opens the `UpdateGate`; the host decides how to present it.
- Logging is off unless `AppLog.debug` and `enableNetworkLogging` are on; `Authorization` is masked.

## Errors

Repositories extend `BaseRepository` and wrap calls in `safeCall`. Failures become
`Result.Error(AppError)`: a kind, the server `code`, the `traceId` and field errors, never text.
The presentation layer maps them to localized text. `AppError.detail` is technical text for logs and a
"copy details" action only. Cancellation is never an error.

Before replacing fixture repositories, decide the OpenAPI version, authentication integration,
and whether the data is tenant-scoped. A tenant ID supplied by a client is only a selector; the backend
must verify membership and scope every protected query. Avoid logging credentials, response bodies
containing sensitive data, or tenant-owned data.
