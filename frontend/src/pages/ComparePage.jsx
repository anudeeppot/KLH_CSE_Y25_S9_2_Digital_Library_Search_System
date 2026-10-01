import React, { useState } from 'react';
import { Play, BarChart2, Zap, Clock, ShieldAlert, Layers } from 'lucide-react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';

const ComparePage = () => {
  const [text, setText] = useState(
    'Knuth Morris Pratt, Rabin Karp rolling hashing, and Boyer Moore pattern searching represent foundational text retrieval algorithms implemented across digital libraries.'
  );
  const [pattern, setPattern] = useState('algorithms');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleRun = async (e) => {
    if (e) e.preventDefault();
    if (!text || !pattern) return;

    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.compareAlgorithms({ text, pattern });
      setResult(res.data);
    } catch (err) {
      console.error(err);
      setError('Comparison benchmark failed: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // Find max time for normalized bar width
  const maxTime = result?.metrics ? Math.max(...result.metrics.map((m) => m.executionTimeNanos), 1) : 1;

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'inline-block', marginBottom: '0.5rem' }}>
          <span className="badge badge-kmp">Module 2 & 5 • Empirical Benchmarking</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', marginBottom: '0.5rem' }}>
          Algorithm Comparison <span className="text-gradient">& Performance Benchmark</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '820px', fontSize: '0.98rem', lineHeight: 1.6 }}>
          Run multiple string searching algorithms simultaneously on the same text and pattern in the Java backend.
          Compare real execution timings, comparison counts, and theoretical complexity boundaries.
        </p>
      </div>

      {/* Input Form */}
      <div className="glass-card" style={{ marginBottom: '2.5rem' }}>
        <form onSubmit={handleRun}>
          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
              Corpus Text (n = {text.length} characters):
            </label>
            <textarea
              className="input-control text-mono"
              rows={3}
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Enter benchmark text..."
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'minmax(240px, 1fr) auto', gap: '1rem', alignItems: 'flex-end' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                Search Pattern (m = {pattern.length} characters):
              </label>
              <input
                type="text"
                className="input-control text-mono"
                value={pattern}
                onChange={(e) => setPattern(e.target.value)}
                placeholder="Enter search pattern..."
              />
            </div>

            <button type="submit" disabled={loading} className="btn btn-primary" style={{ padding: '0.75rem 1.75rem' }}>
              <Play size={16} />
              <span>Run Benchmark</span>
            </button>
          </div>
        </form>
      </div>

      {loading && <LoadingSpinner message="Executing KMP, Rabin-Karp, Boyer-Moore, and Suffix Array concurrently in Java..." />}
      {error && <ErrorMessage message={error} onRetry={handleRun} />}

      {/* Benchmark Results */}
      {result && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem', marginBottom: '3rem' }}>
          {/* Visual Execution Time Bar Chart */}
          <div className="glass-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem', flexWrap: 'wrap', gap: '0.5rem' }}>
              <h3 style={{ fontSize: '1.2rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <BarChart2 size={20} color="var(--accent-cyan)" /> Empirical Execution Time Comparison (Nanoseconds)
              </h3>
              <span style={{ fontSize: '0.82rem', background: 'rgba(56, 189, 248, 0.1)', color: 'var(--accent-cyan)', padding: '0.2rem 0.6rem', borderRadius: '4px' }}>
                Fastest in this run: <strong>{result.fastestAlgorithm}</strong>
              </span>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
              {result.metrics.map((m, idx) => {
                const percent = Math.max(8, Math.round((m.executionTimeNanos / maxTime) * 100));
                let barColor = 'var(--accent-cyan)';
                if (m.name.includes('Rabin')) barColor = 'var(--accent-amber)';
                else if (m.name.includes('Boyer')) barColor = 'var(--accent-rose)';
                else if (m.name.includes('Suffix')) barColor = 'var(--accent-emerald)';

                return (
                  <div key={idx}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.9rem', marginBottom: '0.35rem' }}>
                      <span style={{ fontWeight: 600 }}>{m.name}</span>
                      <span className="text-mono" style={{ color: 'var(--text-secondary)' }}>
                        <strong>{m.executionTimeNanos.toLocaleString()} ns</strong> ({m.executionTimeMs.toFixed(3)} ms) | {m.comparisons} comparisons
                      </span>
                    </div>

                    <div style={{ width: '100%', height: '18px', background: 'rgba(255, 255, 255, 0.05)', borderRadius: '9999px', overflow: 'hidden' }}>
                      <div
                        style={{
                          width: `${percent}%`,
                          height: '100%',
                          background: barColor,
                          borderRadius: '9999px',
                          transition: 'width 0.6s cubic-bezier(0.16, 1, 0.3, 1)',
                        }}
                      />
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Critical Note on Universality */}
            <div style={{
              marginTop: '1.75rem',
              padding: '1rem',
              background: 'rgba(245, 158, 11, 0.08)',
              border: '1px solid rgba(245, 158, 11, 0.25)',
              borderRadius: 'var(--radius-md)',
              fontSize: '0.88rem',
              color: '#fef3c7',
              lineHeight: 1.6,
            }}>
              <strong>Crucial Project Defense Note:</strong> No single string matching algorithm is universally fastest across all inputs!
              Performance is governed by alphabet size, pattern length, pattern repetition, and whether the text collection is static or dynamic.
              {result.explanation}
            </div>
          </div>

          {/* Theoretical Complexity Comparison Table */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.2rem', marginBottom: '1rem' }}>
              Theoretical Complexity Comparison Table
            </h3>

            <div style={{ overflowX: 'auto' }}>
              <table className="dp-table" style={{ width: '100%' }}>
                <thead>
                  <tr>
                    <th>Algorithm</th>
                    <th>Best Case</th>
                    <th>Average Case</th>
                    <th>Worst Case</th>
                    <th>Auxiliary Space</th>
                    <th>Key Advantage in Digital Library</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td style={{ fontWeight: 700, color: 'var(--accent-cyan)' }}>Knuth-Morris-Pratt (KMP)</td>
                    <td>O(n)</td>
                    <td>O(n + m)</td>
                    <td>O(n + m)</td>
                    <td>O(m)</td>
                    <td>Guaranteed linear time; never backtracks; ideal for repetitive text.</td>
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 700, color: 'var(--accent-amber)' }}>Rabin-Karp</td>
                    <td>O(n + m)</td>
                    <td>O(n + m)</td>
                    <td>O(n * m)</td>
                    <td>O(1)</td>
                    <td>O(1) rolling hash sliding window; enables multi-pattern search.</td>
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 700, color: 'var(--accent-rose)' }}>Boyer-Moore</td>
                    <td>O(n / m)</td>
                    <td>O(n)</td>
                    <td>O(n * m)</td>
                    <td>O(sigma)</td>
                    <td>Sub-linear jumps in natural language text with large alphabets.</td>
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 700, color: 'var(--accent-purple)' }}>Edit Distance (DP)</td>
                    <td>O(n * m)</td>
                    <td>O(n * m)</td>
                    <td>O(n * m)</td>
                    <td>O(n * m)</td>
                    <td>Tolerates spelling mistakes; powers "Did you mean?" suggestions.</td>
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 700, color: 'var(--accent-emerald)' }}>Suffix Array</td>
                    <td>O(m + log n)</td>
                    <td>O(m * log n)</td>
                    <td>O(m * log n)</td>
                    <td>O(n)</td>
                    <td>Preprocessing amortizes full-text searches over repetitive user queries.</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ComparePage;
