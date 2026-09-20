# Web security baseline

## Dependency policy

- All direct dependencies use exact versions; `package-lock.json` is committed and installations in
  CI use `npm ci`.
- The supported registry is the public npm registry. Do not install look-alike packages. In
  particular, use the official `@vitejs/plugin-react`, never the unrelated `plugin-react-vite`.
- Before adding or upgrading a package, review its maintainer, license, release cadence,
  dependencies and GitHub/npm security advisories. Run `npm audit` and `npm audit --omit=dev` on
  the resulting lockfile.
- Automated dependency updates must remain pull-request based and pass type-check, build, and both
  audits before merge. A security update may still require a compatibility review.

## Runtime policy

- This is a browser-only SPA. React Server Components and `@vitejs/plugin-rsc` are not enabled.
- The Vite development and preview servers bind only to `127.0.0.1`. Do not pass `--host` or expose
  them through a tunnel without an explicit, reviewed need.
- `VITE_*` values are public build-time values. Secrets, tokens, private keys and backend service
  credentials never belong in them.
- Production hosting must send CSP, HSTS, `X-Content-Type-Options: nosniff`, and an appropriate
  `Referrer-Policy` as HTTP response headers. The meta CSP in `index.html` is defense in depth, not
  a replacement for host-level headers.
- Do not use `dangerouslySetInnerHTML` for untrusted content. If rich user-provided HTML becomes a
  requirement, define a sanitizer and its test cases first.
