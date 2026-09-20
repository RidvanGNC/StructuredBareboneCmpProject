# Web architecture boundary

`webApp` is a standalone React application. It is intentionally not a Kotlin/JS output and it is
not included in the Gradle build graph. React and CMP can evolve their presentation, navigation,
state-management and data-access implementations independently.

The only shared boundary is the API contract. When an API is available, make its OpenAPI definition
the source of truth and generate or validate Kotlin and TypeScript DTOs independently. Do not copy
the `data/`, `screen/`, `navigation/`, or DI directory layout into this application.

The initial web client deliberately uses the browser `fetch` API and local React state. A router,
server-state cache, form library, component suite, global store or CSS framework should be added
only for a concrete requirement, after checking its maintainer, release status, transitive
dependencies, license, and published advisories.
