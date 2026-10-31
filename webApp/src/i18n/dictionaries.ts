/**
 * All user-visible text of the app. The English dictionary defines the keys; every other dictionary
 * must provide exactly the same keys (checked by the compiler through `Dictionary`).
 * Use `{name}` placeholders for values that are filled in at run time.
 */
const en = {
  "topbar.eyebrow": "Structured Barebone",
  "topbar.title": "Operations Workspace",
  "topbar.badge": "Demo fixture",
  "workspace.eyebrow": "Workspace",
  "workspace.description": "A React SaaS console fixture with the same product story as the CMP demo.",
  "workspace.capabilities": "Demo capabilities",
  "workspace.mode": "Multi-tenant demo",
  "workspace.packagesEnabled": "{count} packages enabled",
  "notice.label": "Demo safety note",
  "notice.title": "Local demonstration only.",
  "notice.body":
    "No tenant is selected, authorized, or fetched here. Connect the optional tenant package when the host product has a verified backend contract.",
  "metrics.label": "Workspace metrics",
  "metrics.openWork": "Open work",
  "metrics.teamMembers": "Team members",
  "metrics.usage": "Usage",
  "packages.title": "Enabled packages",
  "packages.reporting": "Reporting",
  "packages.reportingDetail": "Overview and export",
  "packages.team": "Team workspace",
  "packages.teamDetail": "Members and roles",
  "activity.title": "Workspace activity",
  "activity.reportAdded": "Mina added a report",
  "activity.reportAddedTime": "10 minutes ago",
  "activity.reviewReady": "Quarterly review is ready",
  "activity.reviewReadyTime": "Today",
  "activity.contractUpdated": "API contract updated",
  "activity.contractUpdatedTime": "Yesterday",
  "language.label": "Language",
  "language.system": "System default",
  "language.en": "English",
  "language.tr": "Türkçe",
  "footer.apiConfigured": "API configuration: configured.",
  "footer.apiNotConfigured": "API configuration: not configured.",
  "footer.independence": "React remains independent from CMP; both consume the same API contract when integrated.",
} as const;

export type MessageKey = keyof typeof en;
export type Dictionary = Record<MessageKey, string>;

const tr = {
  "topbar.eyebrow": "Structured Barebone",
  "topbar.title": "Operasyon Çalışma Alanı",
  "topbar.badge": "Demo örneği",
  "workspace.eyebrow": "Çalışma alanı",
  "workspace.description": "CMP demosuyla aynı ürün hikâyesine sahip bir React SaaS konsolu örneği.",
  "workspace.capabilities": "Demo özellikleri",
  "workspace.mode": "Çok kiracılı demo",
  "workspace.packagesEnabled": "{count} paket etkin",
  "notice.label": "Demo güvenlik notu",
  "notice.title": "Yalnızca yerel gösterim.",
  "notice.body":
    "Burada hiçbir kiracı seçilmez, yetkilendirilmez veya getirilmez. Ana ürünün doğrulanmış bir sunucu sözleşmesi olduğunda isteğe bağlı kiracı paketini bağlayın.",
  "metrics.label": "Çalışma alanı metrikleri",
  "metrics.openWork": "Açık işler",
  "metrics.teamMembers": "Ekip üyeleri",
  "metrics.usage": "Kullanım",
  "packages.title": "Etkin paketler",
  "packages.reporting": "Raporlama",
  "packages.reportingDetail": "Genel bakış ve dışa aktarma",
  "packages.team": "Ekip çalışma alanı",
  "packages.teamDetail": "Üyeler ve roller",
  "activity.title": "Çalışma alanı etkinliği",
  "activity.reportAdded": "Mina bir rapor ekledi",
  "activity.reportAddedTime": "10 dakika önce",
  "activity.reviewReady": "Çeyreklik inceleme hazır",
  "activity.reviewReadyTime": "Bugün",
  "activity.contractUpdated": "API sözleşmesi güncellendi",
  "activity.contractUpdatedTime": "Dün",
  "language.label": "Dil",
  "language.system": "Sistem varsayılanı",
  "language.en": "English",
  "language.tr": "Türkçe",
  "footer.apiConfigured": "API yapılandırması: yapılandırıldı.",
  "footer.apiNotConfigured": "API yapılandırması: yapılandırılmadı.",
  "footer.independence": "React, CMP'den bağımsız kalır; entegre edildiğinde ikisi de aynı API sözleşmesini kullanır.",
} as const satisfies Dictionary;

/** Languages with a dictionary. To add one, add its dictionary here and its name to every dictionary. */
export const dictionaries = { en, tr } as const satisfies Record<string, Dictionary>;

export type LanguageCode = keyof typeof dictionaries;
export const languageCodes = Object.keys(dictionaries) as LanguageCode[];
export const defaultLanguage: LanguageCode = "en";
