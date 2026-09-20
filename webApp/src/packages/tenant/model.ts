export type TenantMode = "SINGLE_TENANT" | "MULTI_TENANT";

export interface TenantRef {
  readonly id: string;
  readonly displayName: string;
}

export interface TenantMembership {
  readonly tenant: TenantRef;
  readonly roles: readonly string[];
}

/** An entitlement enables code shipped with the client; it is never a remote-code manifest. */
export interface PackageEntitlement {
  readonly packageId: string;
  readonly packageVersion: string;
  readonly enabledFeatures: readonly string[];
}

export interface TenantBootstrap {
  readonly tenantMode: TenantMode;
  readonly subjectId: string;
  readonly activeTenant?: TenantRef;
  readonly memberships: readonly TenantMembership[];
  readonly packageEntitlements: readonly PackageEntitlement[];
  readonly authorizationVersion: string;
}

export type TenantSessionState =
  | { readonly kind: "idle" }
  | { readonly kind: "loading" }
  | { readonly kind: "ready"; readonly bootstrap: TenantBootstrap }
  | { readonly kind: "error"; readonly message: string };
