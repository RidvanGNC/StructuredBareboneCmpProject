# CMP navigation

Navigation route types and Compose navigation remain CMP presentation concerns. React owns its own
routing model and must not import this module.

`SetupNavGraph` shows a bottom bar on phones and a side rail on wider windows (`LayoutSize` from `:theme`).
`NavCatalog` lists the top-level destinations of the template; a compiled-in feature adds its own with a
`FeatureDestination` (string route, label, icon, content), which is listed only while the feature
registry's visibility policy shows it. The sign-in screen is not a route: the app shell shows it while
signed out (see `composeApp`).

When adding a package route, decide whether that package is compiled into every app release and
whether its entry is entitlement-controlled. The route can be hidden for experience, but the backend
must still authorize every protected operation.
