export default function IssueTable({ smells }) {
  if (!smells || smells.length === 0) {
    return (
      <article className="panel-card">
        <div className="panel-header">
          <div>
            <p className="eyebrow">Issues</p>
            <h3>Detected Smells</h3>
          </div>
        </div>
        <p className="empty-state">No issues detected.</p>
      </article>
    );
  }

  return (
    <article className="panel-card">
      <div className="panel-header">
        <div>
          <p className="eyebrow">Issues</p>
          <h3>Detected Smells</h3>
        </div>
      </div>
      <div className="table-responsive">
        <table className="table table-dark table-borderless align-middle issue-table mb-0">
          <thead>
            <tr>
              <th>Type</th>
              <th>Description</th>
              <th>Line</th>
              <th>Severity</th>
            </tr>
          </thead>
          <tbody>
            {smells.map((smell, index) => (
              <tr key={`${smell.type}-${index}`}>
                <td>{smell.type}</td>
                <td>{smell.description}</td>
                <td>
                  {smell.lineNumber}
                  {smell.endLineNumber ? ` - ${smell.endLineNumber}` : ''}
                </td>
                <td>
                  <span className={`severity-badge severity-${String(smell.severity || '').toLowerCase()}`}>
                    {smell.severity}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </article>
  );
}
