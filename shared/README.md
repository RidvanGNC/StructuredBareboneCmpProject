# Shared CMP logic

This module contains platform-neutral Kotlin contracts and models used by CMP modules. It must not
contain Compose UI, navigation route definitions, Android/iOS/Desktop code, or React concerns.

Before placing a model here, decide whether it is truly shared CMP business logic or merely an API
DTO. Keep cross-client DTOs in `contracts/` and generate/map them independently. Tenant identity
must not be scattered through unrelated UI models; use the optional tenant boundary when the host
product chooses multi-tenancy.
