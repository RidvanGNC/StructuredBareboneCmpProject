import { apiBaseUrl } from "../shared/config/runtimeConfig";
import { workspaceDemo } from "./demoData";

export function App() {
  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Structured Barebone</p>
          <h1>Operations Workspace</h1>
        </div>
        <span className="environment-badge">Demo fixture</span>
      </header>

      <section aria-labelledby="workspace-title" className="workspace-heading">
        <div>
          <p className="eyebrow">Workspace</p>
          <h2 id="workspace-title">{workspaceDemo.workspaceName}</h2>
          <p>A React SaaS console fixture with the same product story as the CMP demo.</p>
        </div>
        <div className="tag-list" aria-label="Demo capabilities">
          <span className="tag">{workspaceDemo.mode}</span>
          <span className="tag">2 packages enabled</span>
        </div>
      </section>

      <section aria-label="Demo safety note" className="notice">
        <strong>Local demonstration only.</strong> No tenant is selected, authorized, or fetched here.
        Connect the optional tenant package when the host product has a verified backend contract.
      </section>

      <section aria-label="Workspace metrics" className="metric-grid">
        {workspaceDemo.metrics.map((metric) => (
          <article className="metric-card" key={metric.label}>
            <strong>{metric.value}</strong>
            <span>{metric.label}</span>
          </article>
        ))}
      </section>

      <section className="content-grid">
        <article className="panel">
          <h2>Enabled packages</h2>
          <ul className="detail-list">
            {workspaceDemo.packages.map((item) => (
              <li key={item.name}>
                <strong>{item.name}</strong>
                <span>{item.detail}</span>
              </li>
            ))}
          </ul>
        </article>

        <article className="panel">
          <h2>Workspace activity</h2>
          <ul className="detail-list">
            {workspaceDemo.activity.map((item) => (
              <li key={item.primary}>
                <strong>{item.primary}</strong>
                <span>{item.secondary}</span>
              </li>
            ))}
          </ul>
        </article>
      </section>

      <footer className="app-footer">
        API configuration: {apiBaseUrl === undefined ? "not configured" : "configured"}. React
        remains independent from CMP; both consume the same API contract when integrated.
      </footer>
    </main>
  );
}
