import { createContext, useCallback, useContext, useMemo, useState } from "react";
import type { ReactNode } from "react";
import type { TenantBootstrap, TenantSessionState } from "./model";
import type { TenantRepository } from "./tenantApi";

interface TenantContextValue {
  readonly state: TenantSessionState;
  refresh(): Promise<void>;
  selectActiveTenant(tenantId: string): Promise<void>;
}

const TenantContext = createContext<TenantContextValue | undefined>(undefined);

export interface TenantProviderProps {
  readonly repository: TenantRepository;
  readonly children: ReactNode;
}

/** Opt-in React runtime for tenant-aware applications; the base SPA does not mount it. */
export function TenantProvider({ repository, children }: TenantProviderProps) {
  const [state, setState] = useState<TenantSessionState>({ kind: "idle" });

  const update = useCallback(async (request: () => Promise<TenantBootstrap>) => {
    setState({ kind: "loading" });
    try {
      setState({ kind: "ready", bootstrap: await request() });
    } catch {
      // Do not surface raw backend messages, which may disclose implementation details.
      setState({ kind: "error", message: "Tenant context could not be refreshed." });
    }
  }, []);

  const refresh = useCallback(() => update(() => repository.loadBootstrap()), [repository, update]);
  const selectActiveTenant = useCallback(
    (tenantId: string) => update(() => repository.selectActiveTenant(tenantId)),
    [repository, update],
  );

  const value = useMemo<TenantContextValue>(
    () => ({ state, refresh, selectActiveTenant }),
    [refresh, selectActiveTenant, state],
  );

  return <TenantContext value={value}>{children}</TenantContext>;
}

export function useTenantContext(): TenantContextValue {
  const context = useContext(TenantContext);
  if (context === undefined) {
    throw new Error("useTenantContext must be used inside TenantProvider.");
  }
  return context;
}
