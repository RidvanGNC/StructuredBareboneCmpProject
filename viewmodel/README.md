# View models

- `SessionViewModel`: the sign-in gate (`CHECKING`, `SIGNED_OUT`, `SIGNED_IN`). Without a backend it is
  always signed in. A stored session is checked once at start (`GET /v1/auth/me`): a rejected session signs
  out, a network failure keeps the user signed in. A session that ends by itself posts an `AppMessage`.
- `LoginViewModel`, `ProfileViewModel`, `LanguageViewModel`, `AppearanceViewModel`, `TemplateViewModel`.
- `PagedList`: debounced search and cursor paging for list screens. A view model creates one with its
  `viewModelScope` and exposes `state`; a new query or refresh cancels the request in flight, and a failed
  page keeps the items already shown. It is not a view model itself.

State holds data (errors are `AppError`), never formatted text; the UI maps it to localized resources.
Testing note: `backgroundScope` coroutines did not run on `advanceUntilIdle` with the coroutines-test
version used here, so tests build a scope on the test scheduler (see `PagedListTest`).
