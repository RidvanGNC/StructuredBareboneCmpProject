const localDevelopmentHosts = new Set(["localhost", "127.0.0.1", "[::1]"]);

function readApiBaseUrl(value: string | undefined): string | undefined {
  if (value === undefined || value.trim() === "") {
    return undefined;
  }

  const parsed = new URL(value);
  const isLocalHttp = import.meta.env.DEV && parsed.protocol === "http:" && localDevelopmentHosts.has(parsed.hostname);

  if (parsed.protocol !== "https:" && !isLocalHttp) {
    throw new Error("VITE_API_BASE_URL must use HTTPS outside local development.");
  }

  return parsed.toString().replace(/\/$/, "");
}

// VITE_* values are intentionally treated as public build-time configuration.
// Authentication tokens and service credentials must never be placed here.
export const apiBaseUrl = readApiBaseUrl(import.meta.env.VITE_API_BASE_URL);
