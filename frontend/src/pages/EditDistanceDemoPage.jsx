import React, { useState } from 'react';
import { Play, Layers, CheckCircle2, Clock, Code2, ArrowRight } from 'lucide-react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';

const EditDistanceDemoPage = () => {
  const [source, setSource] = useState('algoritm');
  const [target, setTarget] = useState('algorithm');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showCode, setShowCode] = useState(false);

  const handleRun = async (e) => {
    if (e) e.preventDefault();
    if (!source || !target) return;

    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.testEditDistance({ text: source, target });
      setResult(res.data);
    } catch (err) {
      console.error(err);
      setError('Failed to compute Edit Distance: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'inline-block', marginBottom: '0.5rem' }}>
          <span className="badge badge-edit">Module 3 • Dynamic Programming</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', marginBottom: '0.5rem' }}>
          Levenshtein <span className="text-gradient">Edit Distance & DP Matrix</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '800px', fontSize: '0.98rem', lineHeight: 1.6 }}>
          Edit Distance quantifies the dissimilarity between two string sequences by determining the minimum number of
          single-character <strong>Insertions, Deletions, and Substitutions</strong> required to transform the source into the target.
          Powers <strong>Fuzzy Search</strong> and typo tolerance across the Digital Library.
        </p>
      </div>

      {/* Input Controls */}
      <div className="glass-card" style={{ marginBottom: '2.5rem' }}>
        <form onSubmit={handleRun}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr auto', gap: '1.25rem', alignItems: 'flex-end' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                Query / Source String (n):
              </label>
              <input
                type="text"
                className="input-control text-mono"
                value={source}
                onChange={(e) => setSource(e.target.value)}
                placeholder="e.g. algoritm"
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                Library Target / Keyword (m):
              </label>
              <input
                type="text"
                className="input-control text-mono"
                value={target}
                onChange={(e) => setTarget(e.target.value)}
                placeholder="e.g. algorithm"
              />
            </div>

            <button type="submit" disabled={loading} className="btn btn-primary" style={{ padding: '0.75rem 1.75rem' }}>
              <Play size={16} />
              <span>Compute DP Matrix</span>
            </button>
          </div>
        </form>
      </div>

      {loading && <LoadingSpinner message="Constructing (n+1) x (m+1) Dynamic Programming grid in Java..." />}
      {error && <ErrorMessage message={error} onRetry={handleRun} />}

      {/* Results visualizer */}
      {result && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem', marginBottom: '3rem' }}>
          {/* Metrics */}
          <div className="glass-card" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1.5rem', textAlign: 'center' }}>
            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: result.distance === 0 ? 'var(--accent-emerald)' : 'var(--accent-cyan)' }}>
                {result.distance}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Levenshtein Distance</div>
            </div>

            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: result.similarity >= 70 ? 'var(--accent-emerald)' : 'var(--accent-amber)' }}>
                {result.similarity}%
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Normalized Similarity</div>
            </div>

            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-purple)' }}>
                {result.operations.length}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Alignment Operations</div>
            </div>

            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-rose)' }}>
                {(result.executionTime / 1_000_000.0).toFixed(3)} ms
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>{result.executionTime.toLocaleString()} ns</div>
            </div>
          </div>

          {/* Dynamic Programming Matrix Visualizer */}
          {result.dpMatrix && (
            <div className="glass-card">
              <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Layers size={20} color="var(--accent-purple)" /> Full 2D Dynamic Programming Cost Matrix
              </h3>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem', marginBottom: '1.25rem' }}>
                Rows represent prefixes of source string <code>'{result.source}'</code>; columns represent prefixes of target string <code>'{result.target}'</code>.
                Cell <code>dp[i][j]</code> holds the optimal edit distance between <code>source[0..i-1]</code> and <code>target[0..j-1]</code>.
              </p>

              <div style={{ overflowX: 'auto', paddingBottom: '0.5rem' }}>
                <table className="dp-table" style={{ margin: '0 auto' }}>
                  <thead>
                    <tr>
                      <th style={{ background: '#020617' }}>DP[i][j]</th>
                      <th style={{ background: '#020617' }}>ε</th>
                      {result.target.split('').map((char, j) => (
                        <th key={j} style={{ color: 'var(--accent-cyan)' }}>
                          {char} <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>({j+1})</span>
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {result.dpMatrix.map((row, i) => (
                      <tr key={i}>
                        <th style={{ background: '#0f172a', color: i === 0 ? 'var(--text-muted)' : 'var(--accent-emerald)', textAlign: 'left' }}>
                          {i === 0 ? 'ε (0)' : `${result.source[i - 1]} (${i})`}
                        </th>
                        {row.map((val, j) => {
                          const isFinal = i === result.dpMatrix.length - 1 && j === row.length - 1;
                          return (
                            <td
                              key={j}
                              className={isFinal ? 'dp-active' : ''}
                              style={{
                                background: isFinal ? 'rgba(56, 189, 248, 0.3)' : undefined,
                                fontWeight: isFinal ? 800 : undefined,
                              }}
                            >
                              {val}
                            </td>
                          );
                        })}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Optimal Alignment Operations Sequence */}
          {result.operations && (
            <div className="glass-card">
              <h3 style={{ fontSize: '1.15rem', marginBottom: '0.75rem' }}>
                Optimal Sequence of Edit Operations (Backtracked Path)
              </h3>
              <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                {result.operations.map((op, idx) => {
                  let color = 'var(--text-secondary)';
                  let bg = 'rgba(255,255,255,0.05)';
                  if (op.type === 'MATCH') { color = 'var(--accent-emerald)'; bg = 'rgba(16, 185, 129, 0.15)'; }
                  else if (op.type === 'INSERT') { color = 'var(--accent-cyan)'; bg = 'rgba(56, 189, 248, 0.15)'; }
                  else if (op.type === 'DELETE') { color = 'var(--accent-rose)'; bg = 'rgba(244, 63, 94, 0.15)'; }
                  else if (op.type === 'SUBSTITUTE') { color = 'var(--accent-amber)'; bg = 'rgba(245, 158, 11, 0.15)'; }

                  return (
                    <div
                      key={idx}
                      style={{
                        background: bg,
                        border: `1px solid ${color}40`,
                        borderRadius: '8px',
                        padding: '0.5rem 0.85rem',
                        fontSize: '0.85rem',
                        display: 'flex',
                        flexDirection: 'column',
                        gap: '0.2rem',
                      }}
                    >
                      <span style={{ fontSize: '0.7rem', fontWeight: 700, color, textTransform: 'uppercase' }}>
                        Step {idx + 1}: {op.type}
                      </span>
                      <span className="text-mono" style={{ color: '#fff' }}>
                        '{op.sourceChar}' → '{op.targetChar}' (Cost: {op.cost})
                      </span>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </div>
      )}

      {/* DP Recurrence & Theoretical Viva Review Card */}
      <div className="glass-card" style={{ borderLeft: '4px solid var(--accent-purple)', marginBottom: '2.5rem' }}>
        <h3 style={{ fontSize: '1.25rem', marginBottom: '0.75rem' }}>
          Dynamic Programming Recurrence & Viva Analysis
        </h3>

        <div className="visualizer-box" style={{ marginBottom: '1.25rem', color: '#e9d5ff' }}>
          {`Base Cases:
dp[i][0] = i  (Deleting i characters)
dp[0][j] = j  (Inserting j characters)

Recurrence Relation for i > 0, j > 0:
if (source[i-1] == target[j-1]):
    dp[i][j] = dp[i-1][j-1]                // No cost (Match)
else:
    dp[i][j] = 1 + min(
        dp[i-1][j],                         // Deletion
        dp[i][j-1],                         // Insertion
        dp[i-1][j-1]                        // Substitution
    )`}
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginBottom: '1rem' }}>
          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Time Complexity</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-purple)' }}>O(n * m)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Each of (n+1)*(m+1) cells computed in O(1)</div>
          </div>

          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Space Complexity</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-indigo)' }}>O(n * m)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Full matrix storage enables optimal alignment backtracking</div>
          </div>
        </div>

        <div style={{ marginTop: '1.5rem', paddingTop: '1rem', borderTop: '1px solid var(--border-subtle)' }}>
          <button onClick={() => setShowCode(!showCode)} className="btn btn-secondary" style={{ fontSize: '0.85rem' }}>
            <Code2 size={16} />
            <span>{showCode ? 'Hide Java Implementation' : 'View Backend Java EditDistance Code'}</span>
          </button>

          {showCode && (
            <div className="visualizer-box" style={{ marginTop: '1rem', fontSize: '0.82rem', maxHeight: '350px', overflowY: 'auto' }}>
{`// Dynamic Programming Edit Distance Java Implementation
public EditDistanceResult compute(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];

    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (Character.toLowerCase(s1.charAt(i - 1)) == Character.toLowerCase(s2.charAt(j - 1))) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = 1 + Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
            }
        }
    }
    int distance = dp[m][n];
    double similarity = (1.0 - (double) distance / Math.max(m, n)) * 100.0;
    return new EditDistanceResult(s1, s2, distance, similarity, dp, backtrackOperations(s1, s2, dp));
}`}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default EditDistanceDemoPage;
