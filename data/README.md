# Data module

`data` owns Ktor client configuration and CMP repository implementations. It is not a React data
layer and it is not an authorization authority.

Before replacing fixture repositories, decide the OpenAPI version, authentication integration,
error envelope, retry/idempotency policy, and whether the data is tenant-scoped. A tenant ID supplied
by a client is only a selector; the backend must verify membership and scope every protected query.
Avoid logging credentials, response bodies containing sensitive data, or tenant-owned data.
