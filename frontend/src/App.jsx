import { useEffect, useMemo, useState } from 'react';
import { Link, Navigate, Route, Routes, useLocation } from 'react-router-dom';
import HomePage from './pages/HomePage';
import HistoryPage from './pages/HistoryPage';

function AppShell() {
  const location = useLocation();
  const [darkMode, setDarkMode] = useState(true);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', darkMode ? 'dark' : 'light');
  }, [darkMode]);

  const isHistory = useMemo(() => location.pathname.startsWith('/history'), [location.pathname]);

  return (
    <div className="app-shell">
      <header className="topbar">
        <div>
          <Link to="/" className="brand-link">
            Code Quality Dashboard
          </Link>
          <p className="topbar-subtitle">Static analysis, visualization, and report history.</p>
        </div>
        <div className="topbar-actions">
          <nav className="nav-tabs">
            <Link className={`nav-tab ${!isHistory ? 'active' : ''}`} to="/">
              Analyze
            </Link>
            <Link className={`nav-tab ${isHistory ? 'active' : ''}`} to="/history">
              History
            </Link>
          </nav>
          <button type="button" className="btn btn-outline-light" onClick={() => setDarkMode((value) => !value)}>
            {darkMode ? 'Light Mode' : 'Dark Mode'}
          </button>
        </div>
      </header>

      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/history" element={<HistoryPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}

export default function App() {
  return <AppShell />;
}
