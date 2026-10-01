import React, { useState } from 'react';
import { Play, Shuffle, CheckCircle, Clock, ShieldCheck, FileText } from 'lucide-react';
import { searchAPI } from '../api/apiService';
import AlgorithmBadge from '../components/AlgorithmBadge';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';

const RandomizedSamplingPage = () => {
  const [sampleSize, setSampleSize] = useState(5);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSample = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.sampleCorpus(sampleSize);
      setResult(res.data);
    } catch (err) {
      console.error(err);
      setError('Sampling failed: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'inline-block', marginBottom: '0.5rem' }}>
          <span className="badge badge-amber">Module 6 • Randomized Algorithms</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', marginBottom: '0.5rem' }}>
          Reservoir Sampling <span className="text-gradient">& Randomized Corpus Auditing</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '820px', fontSize: '0.98rem', lineHeight: 1.6 }}>
          Implements <strong>Algorithm R (Alan G. Waterman / Jeffrey Vitter)</strong> to select an unbiased,
          uniform random sample of $k$ documents from a large repository in a single $O(N)$ streaming pass without requiring all items to fit in memory.
        </p>
      </div>

      {/* Control Form */}
      <div className="glass-card" style={{ marginBottom: '2.5rem' }}>
        <form onSubmit={handleSample}>
          <div style={{ display: 'flex', alignItems: 'flex-end', gap: '1.5rem', flexWrap: 'wrap' }}>
            <div style={{ minWidth: '220px' }}>
              <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                Sample Size (k): {sampleSize} documents
              </label>
              <input
                type="range"
                min="2"
                max="10"
                value={sampleSize}
                onChange={(e) => setSampleSize(parseInt(e.target.value))}
                style={{ width: '100%', accentColor: 'var(--accent-amber)' }}
              />
            </div>

            <button type="submit" disabled={loading} className="btn btn-primary" style={{ padding: '0.75rem 1.75rem' }}>
              <Shuffle size={16} />
              <span>Extract Random Reservoir Sample</span>
            </button>
          </div>
        </form>
      </div>

      {loading && <LoadingSpinner message="Executing Reservoir Sampling stream algorithm in Java..." />}
      {error && <ErrorMessage message={error} onRetry={handleSample} />}

      {/* Sampled Results */}
      {result && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem', marginBottom: '3rem' }}>
          {/* Key Metrics */}
          <div className="glass-card" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1.5rem', textAlign: 'center' }}>
            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-amber)' }}>
                {result.sampleSize} / {result.totalPopulation}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Sample Reservoir Size</div>
            </div>

            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-emerald)' }}>
                {((result.sampleSize / result.totalPopulation) * 100).toFixed(1)}%
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Uniform Inclusion Probability (k/N)</div>
            </div>

            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-cyan)' }}>
                {(result.executionTimeNanos / 1_000_000.0).toFixed(3)} ms
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Execution Time ({result.executionTimeNanos} ns)</div>
            </div>
          </div>

          {/* Sampled Documents Cards */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.2rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Shuffle size={18} color="var(--accent-amber)" /> Uniformly Sampled Documents (k = {result.sampleSize})
            </h3>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {result.sampledDocuments.map((doc, idx) => (
                <div
                  key={idx}
                  style={{
                    background: 'rgba(255, 255, 255, 0.03)',
                    border: '1px solid var(--border-subtle)',
                    borderRadius: '8px',
                    padding: '0.85rem 1.25rem',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    flexWrap: 'wrap',
                    gap: '0.5rem',
                  }}
                >
                  <div>
                    <span style={{ fontWeight: 700, color: 'var(--accent-cyan)', marginRight: '0.5rem' }}>#{doc.id}</span>
                    <strong style={{ fontSize: '0.95rem' }}>{doc.title}</strong>
                    <div style={{ fontSize: '0.82rem', color: 'var(--text-muted)' }}>By {doc.author} • {doc.category}</div>
                  </div>
                  <AlgorithmBadge type={doc.documentType} />
                </div>
              ))}
            </div>
          </div>

          {/* Mathematical Proof Card */}
          <div className="glass-card" style={{ borderLeft: '4px solid var(--accent-amber)' }}>
            <h3 style={{ fontSize: '1.15rem', marginBottom: '0.5rem' }}>Mathematical Invariance Proof</h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.6 }}>
              {result.mathematicalProof}
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default RandomizedSamplingPage;
