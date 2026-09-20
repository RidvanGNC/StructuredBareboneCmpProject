export class ApiError extends Error {
  public constructor(
    public readonly status: number,
    message: string,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

export class ApiClient {
  public constructor(private readonly baseUrl: string) {}

  public async get<T>(path: string, signal?: AbortSignal): Promise<T> {
    return this.request<T>(path, { method: "GET", signal });
  }

  public async put<TResponse, TBody>(
    path: string,
    body: TBody,
    signal?: AbortSignal,
  ): Promise<TResponse> {
    return this.request<TResponse>(path, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
      signal,
    });
  }

  private async request<T>(path: string, init: RequestInit): Promise<T> {
    const response = await fetch(new URL(path, this.baseUrl), {
      ...init,
      headers: {
        Accept: "application/json",
        ...init.headers,
      },
    });

    if (!response.ok) {
      throw new ApiError(response.status, `Request failed with HTTP ${response.status}.`);
    }

    return (await response.json()) as T;
  }
}
