import { useI18n, type LanguageChoice } from "../i18n/I18nProvider";
import { languageCodes } from "../i18n/dictionaries";
import { apiBaseUrl } from "../shared/config/runtimeConfig";
import { workspaceDemo } from "./demoData";

const languageChoices: readonly LanguageChoice[] = ["system", ...languageCodes];

function LanguageSelector() {
  const { choice, setChoice, t } = useI18n();

  return (
    <div className="language-selector" role="group" aria-label={t("language.label")}>
      {languageChoices.map((option) => (
        <button
          key={option}
          type="button"
          className="language-button"
          aria-pressed={option === choice}
          onClick={() => setChoice(option)}
        >
          {t(option === "system" ? "language.system" : `language.${option}`)}
        </button>
      ))}
    </div>
  );
}

export function App() {
  const { t } = useI18n();

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">{t("topbar.eyebrow")}</p>
          <h1>{t("topbar.title")}</h1>
        </div>
        <div className="topbar-actions">
          <LanguageSelector />
          <span className="environment-badge">{t("topbar.badge")}</span>
        </div>
      </header>

      <section aria-labelledby="workspace-title" className="workspace-heading">
        <div>
          <p className="eyebrow">{t("workspace.eyebrow")}</p>
          <h2 id="workspace-title">{workspaceDemo.workspaceName}</h2>
          <p>{t("workspace.description")}</p>
        </div>
        <div className="tag-list" aria-label={t("workspace.capabilities")}>
          <span className="tag">{t("workspace.mode")}</span>
          <span className="tag">{t("workspace.packagesEnabled", { count: workspaceDemo.packageCount })}</span>
        </div>
      </section>

      <section aria-label={t("notice.label")} className="notice">
        <strong>{t("notice.title")}</strong> {t("notice.body")}
      </section>

      <section aria-label={t("metrics.label")} className="metric-grid">
        {workspaceDemo.metrics.map((metric) => (
          <article className="metric-card" key={metric.labelKey}>
            <strong>{metric.value}</strong>
            <span>{t(metric.labelKey)}</span>
          </article>
        ))}
      </section>

      <section className="content-grid">
        <article className="panel">
          <h2>{t("packages.title")}</h2>
          <ul className="detail-list">
            {workspaceDemo.packages.map((item) => (
              <li key={item.nameKey}>
                <strong>{t(item.nameKey)}</strong>
                <span>{t(item.detailKey)}</span>
              </li>
            ))}
          </ul>
        </article>

        <article className="panel">
          <h2>{t("activity.title")}</h2>
          <ul className="detail-list">
            {workspaceDemo.activity.map((item) => (
              <li key={item.primaryKey}>
                <strong>{t(item.primaryKey)}</strong>
                <span>{t(item.secondaryKey)}</span>
              </li>
            ))}
          </ul>
        </article>
      </section>

      <footer className="app-footer">
        {apiBaseUrl === undefined ? t("footer.apiNotConfigured") : t("footer.apiConfigured")} {t("footer.independence")}
      </footer>
    </main>
  );
}
