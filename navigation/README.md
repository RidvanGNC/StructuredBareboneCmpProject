# CMP navigation

Navigation route types and Compose navigation remain CMP presentation concerns. React owns its own
routing model and must not import this module.

When adding a package route, decide whether that package is compiled into every app release and
whether its entry is entitlement-controlled. The route can be hidden for experience, but the backend
must still authorize every protected operation.
