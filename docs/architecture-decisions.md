# Architecture decision guide

Answer these before adding a product feature. Record the result in the product repository or issue;
the template must not silently decide them for a consuming team.

1. Is this product single-tenant, multi-tenant, or a single-tenant product that may migrate later?
2. Which clients need the feature: CMP, React, or both? What is shared: contract, business rule, or
   only product language?
3. Is the feature always shipped, entitlement-controlled, or an experiment? Is it safe to remove?
4. What backend API version owns authorization and data isolation? Does an OpenAPI contract exist?
5. Which user roles, audit events, retention rules, and failure states are required?

Do not call a remote package manifest executable code. Client packages are compiled into releases;
the server only grants access to known features.

## Error model

Failed API responses are problem documents (RFC 9457) with a stable `code` and a `traceId`; successful
responses are unwrapped. Below the presentation layer an error is data only (`AppError`: kind, code,
status, trace id, field errors) and never carries user-visible text, so changing the language does not
leave stale text in state. The UI maps kinds and codes to localized text. See `contracts/README.md`.
