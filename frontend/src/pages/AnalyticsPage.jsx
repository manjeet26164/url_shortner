import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import { api } from '../api/api';

export default function AnalyticsPage() {
  const { shortCode } = useParams();
  const navigate = useNavigate();
  const [analytics, setAnalytics] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        const res = await api.get(`/api/urls/${shortCode}/analytics`);
        setAnalytics(res.data);
      } catch (err) {
        setError('Could not load analytics for this URL.');
      } finally {
        setLoading(false);
      }
    };
    fetchAnalytics();
  }, [shortCode]);

  if (loading) return <p>Loading analytics...</p>;
  if (error) return <p className="form-error">{error}</p>;
  if (!analytics) return null;

  return (
    <main className="dashboard-shell">
      <section className="dashboard-card">
        <button type="button" onClick={() => navigate('/dashboard')}>
          &larr; Back to dashboard
        </button>
        <h1>Analytics for /{analytics.shortCode}</h1>
        <p>
          <strong>Original URL:</strong> {analytics.longUrl}
        </p>
        <p>
          <strong>Total Clicks:</strong> {analytics.totalClicks}
        </p>

        <h2>Clicks by day</h2>
        {analytics.clicksByDay.length === 0 ? (
          <p>No clicks recorded yet.</p>
        ) : (
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={analytics.clicksByDay}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Bar dataKey="count" fill="#4f46e5" />
            </BarChart>
          </ResponsiveContainer>
        )}
      </section>
    </main>
  );
}