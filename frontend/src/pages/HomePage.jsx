import { useMemo, useRef, useState } from 'react';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { analyzeCode } from '../api/client';
import ChartsPanel from '../components/ChartsPanel';
import IssueTable from '../components/IssueTable';
import SuggestionsPanel from '../components/SuggestionsPanel';

const defaultCode = `public class Sample {
    public int sum(int a, int b) {
        if (a > b) {
            return a + b;
        }
        return a + b;
    }
}`;

export default function HomePage() {
  const [code, setCode] = useState(defaultCode);
  const [language, setLanguage] = useState('java');
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);
  const reportRef = useRef(null);

  const fileLabel = useMemo(() => file?.name || 'No file selected', [file]);

  async function handleAnalyze(event) {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await analyzeCode({ code, language, file });
      setResult(response);
      requestAnimationFrame(() => {
        document.getElementById('analysis-report')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
      });
    } catch (analysisError) {
      setError(analysisError?.response?.data?.message || 'Unable to analyze the provided code.');
    } finally {
      setLoading(false);
    }
  }

  async function exportPdf() {
    if (!reportRef.current) {
      return;
    }

    const canvas = await html2canvas(reportRef.current, {
      backgroundColor: null,
      scale: 2
    });

    const imageData = canvas.toDataURL('image/png');
    const pdf = new jsPDF('p', 'mm', 'a4');
    const pdfWidth = pdf.internal.pageSize.getWidth();
    const pdfHeight = (canvas.height * pdfWidth) / canvas.width;
    pdf.addImage(imageData, 'PNG', 0, 0, pdfWidth, pdfHeight);
    pdf.save('code-quality-report.pdf');
  }

  return (
    <main className="page-shell">
      <section className="hero panel-card">
        <div>
          <p className="eyebrow">Static Code Analysis</p>
          <h1>Code Quality Dashboard</h1>
          <p className="hero-copy">
            Analyze source code, inspect complexity and maintainability metrics, detect smells, and review history from one place.
          </p>
        </div>
        <div className="hero-metrics">
          <div>
            <span>Backend</span>
            <strong>Spring Boot + JPA</strong>
          </div>
          <div>
            <span>Frontend</span>
            <strong>React + Chart.js</strong>
          </div>
          <div>
            <span>API</span>
            <strong>localhost:8080</strong>
          </div>
        </div>
      </section>

      <section className="panel-card analyzer-card">
        <form onSubmit={handleAnalyze} className="analyzer-form">
          <div className="row g-3 align-items-end">
            <div className="col-lg-7">
              <label className="form-label">Source Code</label>
              <textarea
                className="form-control code-editor"
                rows="14"
                value={code}
                onChange={(event) => setCode(event.target.value)}
                spellCheck="false"
              />
            </div>
            <div className="col-lg-5">
              <div className="stacked-controls">
                <div>
                  <label className="form-label">Upload File</label>
                  <input
                    type="file"
                    className="form-control"
                    accept=".java,.py,.cpp,.cc,.cxx,.c"
                    onChange={(event) => setFile(event.target.files?.[0] || null)}
                  />
                  <small className="helper-text">{fileLabel}</small>
                </div>
                <div>
                  <label className="form-label">Language</label>
                  <select className="form-select" value={language} onChange={(event) => setLanguage(event.target.value)}>
                    <option value="java">Java</option>
                    <option value="python">Python</option>
                    <option value="cpp">C++</option>
                  </select>
                </div>
                <button className="btn btn-primary btn-lg action-button" type="submit" disabled={loading}>
                  {loading ? 'Analyzing...' : 'Analyze Code'}
                </button>
                <button
                  type="button"
                  className="btn btn-outline-light action-button"
                  onClick={() => {
                    setCode(defaultCode);
                    setFile(null);
                    setResult(null);
                    setError('');
                  }}
                >
                  Reset
                </button>
                {error ? <div className="alert alert-danger mb-0">{error}</div> : null}
              </div>
            </div>
          </div>
        </form>
      </section>

      {result ? (
        <section ref={reportRef} className="report-stack">
          <div className="report-actions">
            <div>
              <p className="eyebrow">Analysis Output</p>
              <h2>Dashboard Results</h2>
            </div>
            <button type="button" className="btn btn-outline-info" onClick={exportPdf}>
              Download PDF
            </button>
          </div>

          <ChartsPanel result={result} />

          <section className="summary-grid">
            <article className="panel-card metric-card">
              <p className="eyebrow">Complexity</p>
              <h3>{result.complexity}</h3>
              <span>Cyclomatic complexity score</span>
            </article>
            <article className="panel-card metric-card">
              <p className="eyebrow">LOC Total</p>
              <h3>{result.loc?.total || 0}</h3>
              <span>{result.loc?.comments || 0} comments, {result.loc?.blanks || 0} blanks</span>
            </article>
            <article className="panel-card metric-card">
              <p className="eyebrow">Maintainability</p>
              <h3>{Number(result.maintainabilityScore || 0).toFixed(1)}</h3>
              <span>Quality score out of 100</span>
            </article>
          </section>

          <IssueTable smells={result.smells} />
          <SuggestionsPanel suggestions={result.suggestions} />
        </section>
      ) : null}
    </main>
  );
}
