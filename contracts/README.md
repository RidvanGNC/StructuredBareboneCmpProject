# API contract boundary

OpenAPI definitions in this folder are the source of truth for communication between the backend,
KMP clients, and React clients. They do not make client authorization authoritative.

- `common.openapi.yaml`: the error document, the client version gate, the health probe and the
  session endpoints. Every product contract references it instead of redefining these.
- `tenant-api.openapi.yaml`: optional tenant context contract; read it with
  `docs/tenant-architecture.md`.

## Error envelope

Failed responses carry a problem document (`application/problem+json`, RFC 9457) with the extension
members `code` (stable machine code) and `traceId` (server correlation id). Successful responses
return the resource itself, with no wrapper. Clients branch on `status` and `code` and show their own
localized text; `detail` is for logs and support and is never shown to users as is.

## Client version gate

Every request carries `X-Client-Version`. The server answers `426 Upgrade Required` when the version
is below its configured minimum; clients then stop and ask the user to update. Development builds
send `9999.0.0`.

## Retries and idempotency

Clients repeat only idempotent requests (GET, HEAD, OPTIONS, PUT, DELETE) after a gateway error
(502 to 504) or a network failure, at most twice with exponential back-off. POST is never repeated
automatically; an operation that must be repeatable needs an idempotency key defined in its contract.

## Sessions

Access tokens are short lived. A `401` with `WWW-Authenticate: Bearer` makes the client call
`POST /v1/auth/refresh` once (refresh tokens are single use) and repeat the request. A rejected refresh
token ends the session. The access token is sent without a prior challenge only to the API host.

Sign-in is email and password (`POST /v1/auth/login`); `GET /v1/auth/me` returns the user and doubles as
the start-up session check, and `POST /v1/auth/logout` revokes the refresh token on sign-out.

Before changing a contract, decide the API versioning, authentication mechanism, pagination and
backwards-compatibility policy. Keep external `$ref` resources reviewed and trusted. Generate Kotlin
and TypeScript models independently when a real backend is available; do not make React import the
KMP data layer.
