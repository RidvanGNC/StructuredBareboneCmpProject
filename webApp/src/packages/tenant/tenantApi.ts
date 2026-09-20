import { ApiClient } from "../../shared/api/apiClient";
import type { TenantBootstrap } from "./model";

export interface TenantRepository {
  loadBootstrap(signal?: AbortSignal): Promise<TenantBootstrap>;
  selectActiveTenant(tenantId: string, signal?: AbortSignal): Promise<TenantBootstrap>;
}

export class HttpTenantRepository implements TenantRepository {
  public constructor(private readonly apiClient: ApiClient) {}

  public loadBootstrap(signal?: AbortSignal): Promise<TenantBootstrap> {
    return this.apiClient.get<TenantBootstrap>("/v1/client/tenant-bootstrap", signal);
  }

  public selectActiveTenant(tenantId: string, signal?: AbortSignal): Promise<TenantBootstrap> {
    return this.apiClient.put<TenantBootstrap, { tenantId: string }>(
      "/v1/client/tenant-context",
      { tenantId },
      signal,
    );
  }
}
