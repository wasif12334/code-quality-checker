import { Bar, Pie } from 'react-chartjs-2';
import {
  ArcElement,
  BarElement,
  CategoryScale,
  Chart as ChartJS,
  Legend,
  LinearScale,
  Tooltip
} from 'chart.js';

ChartJS.register(ArcElement, BarElement, CategoryScale, LinearScale, Legend, Tooltip);

function Gauge({ score }) {
  const percentage = Math.max(0, Math.min(100, score || 0));
  const circumference = 2 * Math.PI * 54;
  const dashOffset = circumference - (percentage / 100) * circumference;

  return (
    <div className="gauge-card">
      <svg viewBox="0 0 140 140" className="gauge-svg">
        <circle cx="70" cy="70" r="54" className="gauge-track" />
        <circle
          cx="70"
          cy="70"
          r="54"
          className="gauge-progress"
          strokeDasharray={circumference}
          strokeDashoffset={dashOffset}
        />
      </svg>
      <div className="gauge-label">
        <span>{percentage.toFixed(1)}</span>
        <small>Maintainability</small>
      </div>
    </div>
  );
}

export default function ChartsPanel({ result }) {
  const functionLabels = result?.functions?.map((item) => item.name) || [];
  const functionComplexities = result?.functions?.map((item) => item.cyclomaticComplexity) || [];
  const functionChartData = {
    labels: functionLabels.length ? functionLabels : ['No functions found'],
    datasets: [
      {
        label: 'Cyclomatic Complexity',
        data: functionComplexities.length ? functionComplexities : [0],
        backgroundColor: '#7c8cff'
      }
    ]
  };

  const loc = result?.loc || { total: 0, comments: 0, blanks: 0 };
  const locChartData = {
    labels: ['Code', 'Comments', 'Blanks'],
    datasets: [
      {
        data: [
          Math.max(0, loc.total - loc.comments - loc.blanks),
          loc.comments,
          loc.blanks
        ],
        backgroundColor: ['#7c8cff', '#22c55e', '#f59e0b']
      }
    ]
  };

  return (
    <section className="dashboard-grid" id="analysis-report">
      <article className="panel-card chart-card">
        <div className="panel-header">
          <div>
            <p className="eyebrow">Complexity</p>
            <h3>Per Function</h3>
          </div>
        </div>
        <Bar
          data={functionChartData}
          options={{
            responsive: true,
            plugins: { legend: { display: false } },
            scales: {
              x: { ticks: { color: 'var(--text-muted)' }, grid: { display: false } },
              y: { beginAtZero: true, ticks: { color: 'var(--text-muted)' } }
            }
          }}
        />
      </article>

      <article className="panel-card chart-card">
        <div className="panel-header">
          <div>
            <p className="eyebrow">Lines of Code</p>
            <h3>Breakdown</h3>
          </div>
        </div>
        <Pie
          data={locChartData}
          options={{
            responsive: true,
            plugins: {
              legend: {
                position: 'bottom',
                labels: { color: 'var(--text-muted)' }
              }
            }
          }}
        />
      </article>

      <article className="panel-card chart-card">
        <div className="panel-header">
          <div>
            <p className="eyebrow">Quality</p>
            <h3>Score</h3>
          </div>
        </div>
        <Gauge score={result?.maintainabilityScore || 0} />
      </article>
    </section>
  );
}
