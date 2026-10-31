# Data module

`data` owns Ktor client configuration and CMP repository implementations. It is not a React data
layer and it is not an authorization authority.

## HTTP client

`createHttpClient(HttpClientOptions)` builds the shared client: lenient JSON, timeouts, the
`X-Client-Version` header (`AppInfo`), retries for idempotent requests only, optional bearer
authentication with single-flight refresh, and a validator that turns failed responses into
`ServerException` carrying the parsed `ProblemDetails`. See `contracts/README.md` for the rules.

- Token handling goes through `TokenProvider`. `TokenStorage` is the real implementation: tokens are
  kept in the `SecureKeyStore`, `Settings` only holds the non-secret "a session exists" flag.
  `InMemoryTokenProvider` is for tests only.
- The refresh request uses a separate client without the Auth plugin, otherwise a rejected refresh
  would trigger another refresh and deadlock.
- A 426 response opens the `UpdateGate`; the host decides how to present it.
- Logging is off unless `AppLog.debug` and `enableNetworkLogging` are on; `Authorization` is masked.

## Secure storage and sign-out

`SecureKeyStore` keeps small secrets under operating-system protection; an unreadable entry is `null`,
never an exception, and the user simply signs in again.

| Platform | Protection | Status |
| --- | --- | --- |
| Android | AES-256-GCM, key generated in the Android Keystore; the encrypted file is excluded from backups | Verified on an emulator |
| Windows desktop | DPAPI, user scope, one file per key in `%APPDATA%` | Verified by tests |
| Other desktop | File restricted to its owner (0600); weaker, for development | Not exercised |
| iOS | Keychain, this device only, while unlocked | Compiles only on a Mac; unverified |

Rename the storage names (`JvmSecureKeyStore` directory, `IosSecureKeyStore` service) together with
the application id when the template becomes a product.

`SessionCleaner.signOut()` runs the registered `SignOutStep`s in order while the tokens still exist
(each step is time-limited and failures are skipped, so an offline device is never stuck), then always
clears the tokens, even when cancelled. Register steps in Koin as `SignOutStep`; products add their own
(for example unregistering a device on the server). A refresh token rejected by the server clears the
tokens and `TokenProvider.isLoggedIn` turns false; the app shell observes it to return to sign-in.

## Errors

Repositories extend `BaseRepository` and wrap calls in `safeCall`. Failures become
`Result.Error(AppError)`: a kind, the server `code`, the `traceId` and field errors, never text.
The presentation layer maps them to localized text. `AppError.detail` is technical text for logs and a
"copy details" action only. Cancellation is never an error.

Before replacing fixture repositories, decide the OpenAPI version, authentication integration,
and whether the data is tenant-scoped. A tenant ID supplied by a client is only a selector; the backend
must verify membership and scope every protected query. Avoid logging credentials, response bodies
containing sensitive data, or tenant-owned data.
