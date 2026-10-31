/** Error document of every failed API response (RFC 9457); see contracts/common.openapi.yaml. */
export interface Problem {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  /** Stable machine code; map it to localized text, never show `detail` to users. */
  code?: string;
  traceId?: string;
  errors?: Record<string, string[]>;
}

export class ApiError extends Error {
  public constructor(
    public readonly status: number,
    public readonly problem?: Problem,
  ) {
    super(`Request failed with HTTP ${status}${problem?.code ? ` (${problem.code})` : ""}.`);
    this.name = "ApiError";
  }

  /** True when the server no longer supports this client version (HTTP 426). */
  public get updateRequired(): boolean {
    return this.status === 426;
  }
}

async function readProblem(response: Response): Promise<Problem | undefined> {
  try {
    const parsed: unknown = await response.json();
    return typeof parsed === "object" && parsed !== null ? (parsed as Problem) : undefined;
  } catch {
    return undefined;
  }
}

/** `exactOptionalPropertyTypes` forbids an explicit `signal: undefined`, so the property is only set when present. */
function withSignal(init: RequestInit, signal: AbortSignal | undefined): RequestInit {
  return signal === undefined ? init : { ...init, signal };
}

export class ApiClient {
  public constructor(private readonly baseUrl: string) {}

  public async get<T>(path: string, signal?: AbortSignal): Promise<T> {
    return this.request<T>(path, withSignal({ method: "GET" }, signal));
  }

  public async put<TResponse, TBody>(
    path: string,
    body: TBody,
    signal?: AbortSignal,
  ): Promise<TResponse> {
    return this.request<TResponse>(
      path,
      withSignal({ method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) }, signal),
    );
  }

  private async request<T>(path: string, init: RequestInit): Promise<T> {
    const response = await fetch(new URL(path, this.baseUrl), {
      ...init,
      headers: {
        Accept: "application/json, application/problem+json",
        ...init.headers,
      },
    });

    if (!response.ok) {
      throw new ApiError(response.status, await readProblem(response));
    }

    return (await response.json()) as T;
  }
}
