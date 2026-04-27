export default function SuggestionsPanel({ suggestions }) {
  return (
    <article className="panel-card">
      <div className="panel-header">
        <div>
          <p className="eyebrow">Suggestions</p>
          <h3>Improvement Tips</h3>
        </div>
      </div>
      {suggestions && suggestions.length ? (
        <ul className="suggestion-list">
          {suggestions.map((item, index) => (
            <li key={`${item}-${index}`}>{item}</li>
          ))}
        </ul>
      ) : (
        <p className="empty-state">Run an analysis to see suggestions.</p>
      )}
    </article>
  );
}
