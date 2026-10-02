// Maps a status word to a CSS class so every card shows badges consistently.
const STATUS_CLASSES = {
  ONLINE: "badge badge-good",
  OPEN: "badge badge-good",
  OFFLINE: "badge badge-bad",
  CLOSED: "badge badge-bad",
  FAILED: "badge badge-bad",
  TIMEOUT: "badge badge-warn",
  ERROR: "badge badge-bad",
};

export default function StatusBadge({ status }) {
  const key = (status || "").toUpperCase();
  const className = STATUS_CLASSES[key] || "badge";
  return <span className={className}>{status}</span>;
}
