const API_BASE =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/network";

/**
 * Calls the backend diagnose endpoint. Throws with a readable message
 * on network failure or a non-OK response, so the caller can show it.
 */
export async function runDiagnostic(target) {
  const url = `${API_BASE}/diagnose?target=${encodeURIComponent(target)}`;
  const response = await fetch(url);

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;
    try {
      const body = await response.json();
      if (body?.error) message = body.error;
    } catch {
      // response wasn't JSON; keep the default message
    }
    throw new Error(message);
  }

  return response.json();
}
export async function getHistory() {
  const response = await fetch("http://localhost:8080/api/history");

  if (!response.ok) {
    throw new Error(`Failed to load history: ${response.status}`);
  }

  return response.json();
}