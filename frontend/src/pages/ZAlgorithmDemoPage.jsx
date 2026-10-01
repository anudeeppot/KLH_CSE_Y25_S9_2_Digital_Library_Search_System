import React, { useState, useEffect } from 'react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import { Play, Sparkles, HelpCircle } from 'lucide-react';

function ZAlgorithmDemoPage() {
  const [text, setText] = useState('banana');
  const [pattern, setPattern] = useState('an');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const runTest = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.testZAlgorithm({ text, pattern });
      setResult(res.data);
    } catch (err) {
      setError('Failed to execute Z-Algorithm. Check backend server.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    runTest();
  }, []);

  return (
    <div className="container" style={{ padding: '2rem 1rem' }}>
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
          <span className="badge badge-primary">Course Outcome 2 (CO-2)</span>
          <span style={{ color: 'var(--text-muted)' }}>Linear-Time String Algorithms</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, margin: 0 }}>
          Z-Algorithm (Z-Function) Interactive Lab
        </h1>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem', maxWidth: '850px' }}>
          Computes the Z-array for string S = P + '$' + T in strictly linear O(|P| + |T|) time. Z[i] is the length of the longest substring starting at index i that matches a prefix of S.
        </p>
      </div>

      <div className="card" style={{ padding: '1.5rem', marginBottom: '2rem' }}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr auto', gap: '1rem', alignItems: 'end' }}>
          <div>
            <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>Text (T)</label>
            <input
              type="text"
              className="input-field"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="e.g. banana"
              style={{ width: '100%' }}
            />
          </div>
          <div>
            <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>Pattern (P)</label>
            <input
              type="text"
              className="input-field"
              value={pattern}
              onChange={(e) => setPattern(e.target.value)}
              placeholder="e.g. an"
              style={{ width: '100%' }}
            />
          </div>
          <button className="btn btn-primary" onClick={runTest} disabled={loading} style={{ height: '42px' }}>
            {loading ? <LoadingSpinner size="small" /> : <><Play size={16} /> Run Z-Algorithm</>}
          </button>
        </div>
      </div>

      {error && <ErrorMessage message={error} />}

      {result && (
        <div>
          {/* Summary Banner */}
          <div className="card" style={{ padding: '1.25rem', marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Combined Concatenation (P + '$' + T)</div>
              <div style={{ fontFamily: 'monospace', fontSize: '1.2rem', fontWeight: 700, color: 'var(--primary)' }}>
                {result.combinedString}
              </div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Occurrences Found</div>
              <div style={{ fontSize: '1.3rem', fontWeight: 800, color: result.matchPositions.length > 0 ? 'var(--success)' : 'var(--danger)' }}>
                {result.matchPositions.length} matches (indices: {result.matchPositions.join(', ') || 'None'})
              </div>
            </div>
          </div>

          {/* Z-Array Visual Table */}
          <div className="card" style={{ padding: '1.5rem', marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '1rem' }}>
              Z-Array &amp; Character Alignment Table
            </h3>
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'center', fontFamily: 'monospace' }}>
                <thead>
                  <tr style={{ background: 'var(--bg-tertiary)', borderBottom: '2px solid var(--border)' }}>
                    <th style={{ padding: '0.6rem' }}>Index (i)</th>
                    {result.combinedString.split('').map((_, i) => (
                      <th key={i} style={{ padding: '0.6rem', minWidth: '35px' }}>{i}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  <tr style={{ borderBottom: '1px solid var(--border)' }}>
                    <td style={{ padding: '0.6rem', fontWeight: 700 }}>Char S[i]</td>
                    {result.combinedString.split('').map((char, i) => (
                      <td key={i} style={{ padding: '0.6rem', fontWeight: 600, color: char === '$' ? 'var(--danger)' : 'inherit' }}>
                        {char}
                      </td>
                    ))}
                  </tr>
                  <tr style={{ background: 'var(--bg-secondary)' }}>
                    <td style={{ padding: '0.6rem', fontWeight: 700, color: 'var(--primary)' }}>Z[i] Value</td>
                    {result.zArray.map((zVal, i) => {
                      const isMatch = zVal === pattern.length && i > pattern.length;
                      return (
                        <td
                          key={i}
                          style={{
                            padding: '0.6rem',
                            fontWeight: isMatch ? 800 : 500,
                            background: isMatch ? 'rgba(16, 185, 129, 0.25)' : 'transparent',
                            color: isMatch ? 'var(--success)' : (zVal > 0 ? 'var(--primary)' : 'var(--text-muted)'),
                          }}
                        >
                          {i === 0 ? '-' : zVal}
                        </td>
                      );
                    })}
                  </tr>
                </tbody>
              </table>
            </div>
            <div style={{ marginTop: '1rem', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Green cells indicate <strong>Z[i] == |Pattern|</strong>, signaling an exact pattern occurrence starting at text position <code>i - |P| - 1</code>.
            </div>
          </div>

          {/* Step-by-Step Z-box Log */}
          <div className="card" style={{ padding: '1.5rem' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '1rem' }}>
              Execution Trace: Z-Box [L, R] Invariant Transitions
            </h3>
            <div style={{ maxHeight: '350px', overflowY: 'auto' }}>
              {result.steps.map((st, idx) => (
                <div key={idx} style={{ padding: '0.6rem 0.8rem', borderBottom: '1px solid var(--border)', fontSize: '0.9rem', display: 'flex', justifyContent: 'space-between' }}>
                  <div>
                    <span className="badge badge-secondary" style={{ marginRight: '0.75rem' }}>Step {idx + 1}</span>
                    <span style={{ fontFamily: 'monospace', fontWeight: 600 }}>i={st.index} (Z={st.zValue})</span>
                    <span style={{ marginLeft: '1rem', color: 'var(--text-secondary)' }}>{st.action}</span>
                  </div>
                  <div style={{ fontFamily: 'monospace', color: 'var(--primary)', fontWeight: 600 }}>
                    [L={st.l}, R={st.r}]
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ZAlgorithmDemoPage;
