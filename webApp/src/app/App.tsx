import { apiBaseUrl } from "../shared/config/runtimeConfig";

export function App() {
  return (
    <main className="app-shell">
      <section aria-labelledby="application-title" className="welcome-card">
        <p className="eyebrow">Independent React application</p>
        <h1 id="application-title">Structured Barebone Web</h1>
        <p>
          This application owns its UI, routing, client state, and HTTP client. It shares API
          contracts with CMP; it does not import CMP UI or data modules.
        </p>
        <p className="status">
          API configuration: {apiBaseUrl === undefined ? "not configured" : "configured"}
        </p>
      </section>
    </main>
  );
}
