const API_ROOT =
  import.meta.env.VITE_API_BASE || "http://localhost:8080/api";

const API_BASE = `${API_ROOT}/network`;

export async function runDiagnostic(target) {
  const url = `${API_BASE}/diagnose?target=${encodeURIComponent(target)}`;
  const response = await fetch(url);

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;

    try {
      const body = await response.json();
      if (body?.error) message = body.error;
    } catch {}

    throw new Error(message);
  }

  return response.json();
}

export async function getHistory() {
  const response = await fetch(`${API_ROOT}/history`);

  if (!response.ok) {
    throw new Error(`Failed to load history: ${response.status}`);
  }

  return response.json();
}