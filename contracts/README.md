# API contract boundary

OpenAPI definitions in this folder are the source of truth for communication between the backend,
KMP clients, and React clients. They do not make client authorization authoritative.

Before changing a contract, decide the API versioning, authentication mechanism, error envelope,
idempotency needs, pagination, and backwards-compatibility policy. Keep external `$ref` resources
reviewed and trusted. Generate Kotlin and TypeScript models independently when a real backend is
available; do not make React import the KMP data layer.

For tenant-aware endpoints, read `tenant-api.openapi.yaml` with `docs/tenant-architecture.md`.
