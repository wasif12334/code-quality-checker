import { useEffect, useState } from 'react';
import { fetchHistory } from '../api/client';

export default function HistoryPage() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;

    async function loadHistory() {
      try {
        const data = await fetchHistory();
        if (mounted) {
          setHistory(data);
        }
      } catch (historyError) {
        if (mounted) {
          setError(historyError?.response?.data?.message || 'Unable to load history.');
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    }

    loadHistory();

    return () => {
      mounted = false;
    };
  }, []);

  return (
    <main className="page-shell">
      <section className="panel-card">
        <p className="eyebrow">History</p>
        <h1>Previous Reports</h1>
        <p className="hero-copy">Review recently analyzed snippets and their maintainability scores.</p>
      </section>

      <section className="panel-card">
        {loading ? <p className="empty-state">Loading history...</p> : null}
        {error ? <div className="alert alert-danger">{error}</div> : null}

        {!loading && !error ? (
          <div className="table-responsive">
            <table className="table table-dark table-borderless align-middle issue-table mb-0">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Code Snippet</th>
                  <th>Score</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {history.length ? history.map((item) => (
                  <tr key={item.id}>
                    <td>{item.id}</td>
                    <td>{item.codeSnippet}</td>
                    <td>{Number(item.score || 0).toFixed(1)}</td>
                    <td>{item.date ? new Date(item.date).toLocaleString() : '-'}</td>
                  </tr>
                )) : (
                  <tr>
                    <td colSpan="4" className="empty-state">No analysis history yet.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        ) : null}
      </section>
    </main>
  );
}
