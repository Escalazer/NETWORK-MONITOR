import { useState } from "react";

export default function DiagnosticForm({ onRun, loading, onTargetChange }) {
  const [target, setTarget] = useState("google.com");

  function handleSubmit(e) {
    e.preventDefault();
    const trimmed = target.trim();
    if (trimmed) {
      onRun(trimmed);
    }
  }

  return (
    <form className="diagnostic-form" onSubmit={handleSubmit}>
      <input
        type="text"
        value={target}
        onChange={(e) => {
          setTarget(e.target.value);
          onTargetChange(e.target.value);
        }}
        placeholder="Enter hostname or IP (e.g. google.com, 8.8.8.8)"
        disabled={loading}
      />
      <button type="submit" disabled={loading || !target.trim()}>
        {loading ? "Running..." : "Run Diagnostic"}
      </button>
    </form>
  );
}
