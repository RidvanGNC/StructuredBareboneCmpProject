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
    const response = await fetch(new URL(path, this.baseUrl), {
      headers: { Accept: "application/json" },
      signal,
    });

    if (!response.ok) {
      throw new ApiError(response.status, `Request failed with HTTP ${response.status}.`);
    }

    return (await response.json()) as T;
  }
}
