# Theme and design tokens

`theme` owns the look of the app: the design tokens (`Spacing`, `Radius`, `IconSize`, `LineWidth`,
`Elevation`, `AppLayout`, `Breakpoints`), the Material 3 theme (`AppTheme`, light/dark/system) and the
window width class (`LayoutSize`, read through `LocalLayoutSize`).

This is the only module where raw `dp` and `sp` numbers, `Color(0x…)`, `fontSize`, `FontWeight` and
`RoundedCornerShape` may appear. Every other module uses the tokens, `MaterialTheme.colorScheme`,
`MaterialTheme.typography` and `MaterialTheme.shapes`; `StyleRulesTest` in `composeApp` fails otherwise and
has no exception list.

A product changes the palette or typography by passing `lightColors`, `darkColors` or `typography` to
`AppTheme`. The defaults are neutral Material 3 values.
