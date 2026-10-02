import { useEffect, useState } from "react";
import DiagnosticForm from "./components/DiagnosticForm.jsx";
import HistoryChart from "./components/HistoryChart.jsx";
import {
  TargetCard,
  DnsCard,
  ConnectivityCard,
  PortsCard,
  HttpCard,
  LocalNetworkCard,
} from "./components/ResultSections.jsx";
import { runDiagnostic } from "./api.js";
import "./App.css";

export default function App() {
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [history, setHistory] = useState([]);
  const [monitoring, setMonitoring] = useState(false);
  const [monitorInterval, setMonitorInterval] = useState(10);
  const [monitorTarget, setMonitorTarget] = useState("google.com");

  useEffect(() => {
    const savedHistory = localStorage.getItem("networkMonitorHistory");

    if (savedHistory) {
      try {
        setHistory(JSON.parse(savedHistory));
      } catch (err) {
        console.error("Failed to load local history:", err);
        localStorage.removeItem("networkMonitorHistory");
      }
    }
  }, []);

  function saveHistory(data) {
    setHistory((currentHistory) => {
      const updatedHistory = [...currentHistory, data].slice(-50);
      localStorage.setItem(
        "networkMonitorHistory",
        JSON.stringify(updatedHistory)
      );
      return updatedHistory;
    });
  }

  async function handleRun(target) {
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const data = await runDiagnostic(target);
      setResult(data);

      saveHistory({
        id: Date.now(),
        target: data.target,
        durationMs: data.totalDurationMs,
        dnsOnline: data.dns?.resolved ?? false,
        reachable: data.ping?.reachable ?? false,
        packetLoss: data.ping?.packetLossPercent ?? 0,
        timestamp: new Date().toISOString(),
      });
    } catch (err) {
      setError(err.message || "Something went wrong.");
    } finally {
      setLoading(false);
    }
  }

  function startMonitoring() {
    if (!monitorTarget.trim() || monitoring) return;

    setMonitoring(true);
    window.monitoringActive = true;

    const runMonitoringCheck = async () => {
      const startTime = Date.now();

      try {
        const data = await runDiagnostic(monitorTarget.trim());
        setResult(data);

        saveHistory({
          id: Date.now(),
          target: data.target,
          durationMs: data.totalDurationMs,
          dnsOnline: data.dns?.resolved ?? false,
          reachable: data.ping?.reachable ?? false,
          packetLoss: data.ping?.packetLossPercent ?? 0,
          timestamp: new Date().toISOString(),
        });
      } catch (err) {
        setError(err.message || "Monitoring check failed.");
      }

      if (window.monitoringActive) {
        const elapsed = Date.now() - startTime;
        const intervalMs = monitorInterval * 1000;

        const remainingTime = Math.max(0, intervalMs - elapsed);

        window.monitorTimeoutId = setTimeout(
          runMonitoringCheck,
          remainingTime
        );
      }
    };

    runMonitoringCheck();
  }

  function stopMonitoring() {
    window.monitoringActive = false;

    if (window.monitorTimeoutId) {
      clearTimeout(window.monitorTimeoutId);
      window.monitorTimeoutId = null;
    }

    setMonitoring(false);
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>NetworkMonitor</h1>
        <p className="subtitle">Network Monitoring &amp; Diagnostic Dashboard</p>
      </header>

      <DiagnosticForm
        onRun={handleRun}
        loading={loading}
        onTargetChange={setMonitorTarget}
      />

      <div className="monitor-controls">
        <label>
          Interval (seconds):
          <input
            type="number"
            min="5"
            value={monitorInterval}
            onChange={(e) => setMonitorInterval(Number(e.target.value))}
            disabled={monitoring}
          />
        </label>

        {!monitoring ? (
          <button
            onClick={startMonitoring}
            disabled={loading}
          >
            Start Monitoring
          </button>
        ) : (
          <button onClick={stopMonitoring}>
            Stop Monitoring
          </button>
        )}
      </div>

      {loading && <p className="status-message">Running diagnostics against the target...</p>}
      {error && <p className="status-message error-text">Error: {error}</p>}

      {result && (
        <div className="results-grid">
          <TargetCard target={result.target} totalDurationMs={result.totalDurationMs} />
          <DnsCard dns={result.dns} />
          <ConnectivityCard ping={result.ping} />
          <PortsCard ports={result.ports} />
          <HttpCard http={result.http} />
          <LocalNetworkCard localNetwork={result.localNetwork} />
        </div>
      )}

      {history.length > 0 && (
        <section className="history-section">
          <h2>Diagnostic History</h2>

          <HistoryChart history={history} />

          <table className="result-table">
            <thead>
              <tr>
                <th>Target</th>
                <th>Status</th>
                <th>Packet Loss</th>
                <th>Duration</th>
                <th>Time</th>
              </tr>
            </thead>

            <tbody>
              {history.map((item) => (
                <tr key={item.id}>
                  <td>{item.target}</td>
                  <td>{item.reachable ? "ONLINE" : "OFFLINE"}</td>
                  <td>{item.packetLoss}%</td>
                  <td>{item.durationMs} ms</td>
                  <td>{new Date(item.timestamp).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      )}

      {!loading && !error && !result && (
        <p className="status-message muted">
          Enter a hostname or IP address above and click "Run Diagnostic" to begin.
        </p>
      )}
    </div>
  );
}
