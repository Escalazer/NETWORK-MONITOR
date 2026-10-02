import Card from "./Card.jsx";
import StatusBadge from "./StatusBadge.jsx";

export function TargetCard({ target, totalDurationMs }) {
  return (
    <Card title="Target Information">
      <p>
        <strong>Target:</strong> {target}
      </p>
      <p>
        <strong>Total diagnostic time:</strong> {totalDurationMs} ms
      </p>
    </Card>
  );
}

export function DnsCard({ dns }) {
  if (!dns) return null;
  return (
    <Card title="DNS">
      <p>
        <StatusBadge status={dns.resolved ? "ONLINE" : "FAILED"} />{" "}
        {dns.resolved ? "Resolved" : "Resolution failed"}
      </p>
      {dns.resolved ? (
        <>
          {dns.ipv4Addresses?.length > 0 && (
            <p>
              <strong>IPv4:</strong> {dns.ipv4Addresses.join(", ")}
            </p>
          )}
          {dns.ipv6Addresses?.length > 0 && (
            <p>
              <strong>IPv6:</strong> {dns.ipv6Addresses.join(", ")}
            </p>
          )}
          <p className="muted">Lookup time: {dns.resolutionTimeMs} ms</p>
        </>
      ) : (
        <p className="error-text">{dns.errorMessage}</p>
      )}
    </Card>
  );
}

export function ConnectivityCard({ ping }) {
  if (!ping) return null;
  return (
    <Card title="Connectivity">
      <p>
        <StatusBadge status={ping.reachable ? "ONLINE" : "OFFLINE"} />{" "}
        {ping.reachable ? "Reachable" : "Unreachable"}
      </p>
      {ping.reachable && (
        <p>
          <strong>Avg latency:</strong> {ping.averageLatencyMs} ms
        </p>
      )}
      <p>
        <strong>Packets:</strong> {ping.packetsReceived}/{ping.packetsSent} received (
        {ping.packetLossPercent?.toFixed(1)}% loss)
      </p>
      <p className="muted">Method: {ping.method}</p>
      {ping.errorMessage && <p className="error-text">{ping.errorMessage}</p>}
    </Card>
  );
}

export function PortsCard({ ports }) {
  if (!ports) return null;
  return (
    <Card title="TCP Ports">
      <table className="result-table">
        <thead>
          <tr>
            <th>Port</th>
            <th>Service</th>
            <th>Status</th>
            <th>Time</th>
          </tr>
        </thead>
        <tbody>
          {ports.map((p) => (
            <tr key={p.port}>
              <td>{p.port}</td>
              <td>{p.service}</td>
              <td>
                <StatusBadge status={p.status} />
              </td>
              <td>{p.connectTimeMs} ms</td>
            </tr>
          ))}
        </tbody>
      </table>
    </Card>
  );
}

export function HttpCard({ http }) {
  if (!http) return null;
  return (
    <Card title="HTTP / HTTPS">
      {http.map((h) => (
        <div key={h.scheme} className="http-row">
          <p>
            <strong>{h.scheme.toUpperCase()}:</strong>{" "}
            <StatusBadge status={h.reachable ? "ONLINE" : "FAILED"} />
          </p>
          {h.reachable ? (
            <p className="muted">
              Status {h.statusCode} &middot; {h.responseTimeMs} ms
            </p>
          ) : (
            <p className="error-text">{h.errorMessage}</p>
          )}
        </div>
      ))}
    </Card>
  );
}

export function LocalNetworkCard({ localNetwork }) {
  if (!localNetwork) return null;
  return (
    <Card title="Local Network">
      {localNetwork.length === 0 ? (
        <p className="muted">No active interfaces detected.</p>
      ) : (
        localNetwork.map((iface, idx) => (
          <div key={idx} className="iface-row">
            <p>
              <strong>{iface.displayName || iface.interfaceName}</strong>
            </p>
            <p className="muted">
              IP: {iface.localIpAddress} &middot; MAC: {iface.macAddress}
            </p>
          </div>
        ))
      )}
    </Card>
  );
}
