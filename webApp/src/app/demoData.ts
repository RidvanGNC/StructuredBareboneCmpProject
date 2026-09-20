export const workspaceDemo = {
  workspaceName: "Northstar Studio",
  mode: "Multi-tenant demo",
  packages: [
    { name: "Reporting", detail: "Overview and export" },
    { name: "Team workspace", detail: "Members and roles" },
  ],
  metrics: [
    { value: "18", label: "Open work" },
    { value: "7", label: "Team members" },
    { value: "64%", label: "Usage" },
  ],
  activity: [
    { primary: "Mina added a report", secondary: "10 minutes ago" },
    { primary: "Quarterly review is ready", secondary: "Today" },
    { primary: "API contract updated", secondary: "Yesterday" },
  ],
} as const;
