# Operations Workspace demo screen

The screen is deliberately fixture-only content that demonstrates a SaaS workspace layout on CMP.
Its labels such as “Multi-tenant demo” and “packages enabled” do not activate tenant behavior.

Before replacing it, decide the product's tenant mode, feature-package registry, backend contract,
target platforms, and required loading/error/empty states. For live tenant integration, use the
optional `:packages:tenant` capability and its documented backend boundary.
