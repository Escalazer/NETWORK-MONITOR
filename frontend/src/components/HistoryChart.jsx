import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Tooltip,
  Legend,
} from "chart.js";

import { Line } from "react-chartjs-2";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Tooltip,
  Legend
);

export default function HistoryChart({ history }) {
  if (!history || history.length === 0) {
    return null;
  }

  const data = {
    labels: history.map((item) =>
      new Date(item.timestamp).toLocaleTimeString()
    ),

    datasets: [
      {
        label: "Diagnostic Duration (ms)",
        data: history.map((item) => item.durationMs),
        borderColor: "#45d6cf",
        backgroundColor: "rgba(69, 214, 207, 0.15)",
        pointBackgroundColor: "#45d6cf",
        pointRadius: 4,
        tension: 0.3,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,

    plugins: {
      legend: {
        labels: {
          color: "#e5e7eb",
        },
      },

      tooltip: {
        callbacks: {
          title: (tooltipItems) => {
            const index = tooltipItems[0].dataIndex;
            return history[index].target;
          },

          label: (context) => {
            return `Duration: ${context.parsed.y} ms`;
          },

          afterLabel: (context) => {
            const index = context.dataIndex;
            return `Time: ${new Date(
              history[index].timestamp
            ).toLocaleTimeString()}`;
          },
        },
      },
    },

    scales: {
      x: {
        ticks: {
          color: "#9ca3af",
        },
        grid: {
          color: "rgba(255, 255, 255, 0.08)",
        },
      },

      y: {
        ticks: {
          color: "#9ca3af",
        },
        grid: {
          color: "rgba(255, 255, 255, 0.08)",
        },
      },
    },
  };

  return (
    <div className="history-chart">
      <h2>Diagnostic Performance</h2>

      <div style={{ height: "300px" }}>
        <Line data={data} options={options} />
      </div>
    </div>
  );
}