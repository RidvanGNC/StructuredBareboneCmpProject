import type { PackageEntitlement } from "./model";

export interface InstalledFeaturePackage {
  readonly packageId: string;
  readonly version: string;
  readonly featureIds: readonly string[];
}

export interface EnabledFeaturePackage {
  readonly definition: InstalledFeaturePackage;
  readonly enabledFeatureIds: readonly string[];
}

/** Does not load remote modules: it resolves server grants against the locally shipped registry. */
export function resolveEnabledFeaturePackages(
  installedPackages: readonly InstalledFeaturePackage[],
  entitlements: readonly PackageEntitlement[],
): readonly EnabledFeaturePackage[] {
  return installedPackages.flatMap((definition) => {
    const entitlement = entitlements.find(({ packageId }) => packageId === definition.packageId);
    if (entitlement === undefined) {
      return [];
    }

    const enabledFeatureIds = definition.featureIds.filter((id) => entitlement.enabledFeatures.includes(id));
    return enabledFeatureIds.length === 0 ? [] : [{ definition, enabledFeatureIds }];
  });
}
