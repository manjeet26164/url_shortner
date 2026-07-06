import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function DashboardPage() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <main className="dashboard-shell">
      <section className="dashboard-card">
        <h1>Dashboard</h1>
        <p>
          Signed in as <strong>{user?.email || 'Authenticated user'}</strong>.
        </p>
        <p>This frontend is ready to connect to the URL creation and redirect APIs.</p>

        <button type="button" onClick={handleLogout}>
          Log out
        </button>
      </section>
    </main>
  );
}