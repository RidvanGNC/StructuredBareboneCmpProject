import type { MessageKey } from "../i18n/dictionaries";

interface MetricFixture {
  readonly value: string;
  readonly labelKey: MessageKey;
}

interface PackageFixture {
  readonly nameKey: MessageKey;
  readonly detailKey: MessageKey;
}

interface ActivityFixture {
  readonly primaryKey: MessageKey;
  readonly secondaryKey: MessageKey;
}

/** Fixture values of the demo. Numbers and names are data; all text comes from `src/i18n`. */
export const workspaceDemo = {
  workspaceName: "Northstar Studio",
  packageCount: 2,
  metrics: [
    { value: "18", labelKey: "metrics.openWork" },
    { value: "7", labelKey: "metrics.teamMembers" },
    { value: "64%", labelKey: "metrics.usage" },
  ] satisfies readonly MetricFixture[],
  packages: [
    { nameKey: "packages.reporting", detailKey: "packages.reportingDetail" },
    { nameKey: "packages.team", detailKey: "packages.teamDetail" },
  ] satisfies readonly PackageFixture[],
  activity: [
    { primaryKey: "activity.reportAdded", secondaryKey: "activity.reportAddedTime" },
    { primaryKey: "activity.reviewReady", secondaryKey: "activity.reviewReadyTime" },
    { primaryKey: "activity.contractUpdated", secondaryKey: "activity.contractUpdatedTime" },
  ] satisfies readonly ActivityFixture[],
} as const;
