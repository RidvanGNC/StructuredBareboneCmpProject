# Shared UI components

Reusable Compose components that use the `theme` tokens and contain no business logic.

- Grouped lists: `SectionHeader`, `SectionCard`, `ListRow`, `RowDivider`, `IconTile`, `AvatarCircle`.
- Screen parts: `ScreenHeader`, `ScreenBody` (centered, width limited), `LoadingIndicator`, `StateMessage`.
- Input: `AppTextField`, `PasswordTextField`; dialogs: `ConfirmDialog`, `TypeToConfirmDialog`.
- Messages: `MessageBanner` renders an `AppMessage` (info, warning, error) with its localized text.

The module also owns the texts shared by every screen (common actions, language and theme names and the
error messages) in `composeResources`; `ErrorKind.messageResource()` maps an error category to its text.
Add a new `ErrorKind` there. Components take every other text as a parameter, so they stay language neutral.

Not included on purpose: file pickers, camera capture and other platform-specific pieces are product
driven. Add them behind `expect`/`actual` in this module when a product needs them, and implement iOS only
when it can be built and tested.
