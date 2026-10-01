import React, { useState } from 'react';
import { Play, FastForward, Clock, Cpu, Code2, ArrowRight } from 'lucide-react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';

const BoyerMooreDemoPage = () => {
  const [text, setText] = useState('Boyer Moore algorithm scans from right to left using the bad character heuristic to jump across large sections of text.');
  const [pattern, setPattern] = useState('character');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showCode, setShowCode] = useState(false);

  const handleRun = async (e) => {
    if (e) e.preventDefault();
    if (!text || !pattern) return;

    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.testBoyerMoore({ text, pattern });
      setResult(res.data);
    } catch (err) {
      console.error(err);
      setError('Failed to execute Boyer-Moore on backend: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'inline-block', marginBottom: '0.5rem' }}>
          <span className="badge badge-bm">Module 2 • Bad Character Heuristic</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', marginBottom: '0.5rem' }}>
          Boyer-Moore <span className="text-gradient">Pattern Search Algorithm</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '800px', fontSize: '0.98rem', lineHeight: 1.6 }}>
          Boyer-Moore matches characters from <strong>right to left</strong> within the pattern window.
          When a mismatch occurs, it applies the <strong>Bad Character Rule</strong> to jump multiple positions forward,
          often yielding sub-linear search speeds in natural language text.
        </p>
      </div>

      {/* Input Form */}
      <div className="glass-card" style={{ marginBottom: '2.5rem' }}>
        <form onSubmit={handleRun}>
          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
              Target Corpus Text (n):
            </label>
            <textarea
              className="input-control text-mono"
              rows={3}
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Enter text to search within..."
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'minmax(240px, 1fr) auto', gap: '1rem', alignItems: 'flex-end' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                Search Pattern (m):
              </label>
              <input
                type="text"
                className="input-control text-mono"
                value={pattern}
                onChange={(e) => setPattern(e.target.value)}
                placeholder="Enter pattern to locate..."
              />
            </div>

            <button type="submit" disabled={loading} className="btn btn-primary" style={{ padding: '0.75rem 1.75rem' }}>
              <Play size={16} />
              <span>Execute Boyer-Moore</span>
            </button>
          </div>
        </form>
      </div>

      {loading && <LoadingSpinner message="Calculating Bad-Character heuristics and shifting pattern in Java..." />}
      {error && <ErrorMessage message={error} onRetry={handleRun} />}

      {/* Results visualizer */}
      {result && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem', marginBottom: '3rem' }}>
          {/* Key Metrics */}
          <div className="glass-card" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1.5rem', textAlign: 'center' }}>
            <div>
              <div style={{ fontSize: '1.8rem', fontWeight: 800, color: result.found ? 'var(--accent-emerald)' : 'var(--accent-rose)' }}>
                {result.found ? 'FOUND' : 'NOT FOUND'}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Pattern Status</div>
            </div>

            <div>
              <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--accent-cyan)' }}>
                {result.positions.length}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Match Count</div>
            </div>

            <div>
              <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--accent-amber)' }}>
                {result.comparisons}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Total Comparisons</div>
            </div>

            <div>
              <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--accent-purple)' }}>
                {result.executionTimeMs} ms
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>{result.executionTime.toLocaleString()} ns</div>
            </div>
          </div>

          {/* Bad Character Table */}
          {result.badCharacterTable && (
            <div className="glass-card">
              <h3 style={{ fontSize: '1.15rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <FastForward size={18} color="var(--accent-rose)" /> Bad Character Rule Precomputation Table
              </h3>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem', marginBottom: '1rem' }}>
                Records the last rightmost occurrence index of each character in pattern <code>'{result.pattern}'</code>.
                When a mismatch occurs with character <code>c</code>, jump = <code>max(1, j - lastOccurrence[c])</code>.
              </p>

              <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
                {Object.entries(result.badCharacterTable).map(([char, pos]) => (
                  <div key={char} style={{
                    background: 'rgba(255, 255, 255, 0.05)',
                    border: '1px solid var(--border-subtle)',
                    borderRadius: '8px',
                    padding: '0.5rem 0.85rem',
                    textAlign: 'center',
                  }}>
                    <div style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--accent-rose)', fontFamily: 'var(--font-mono)' }}>
                      '{char}'
                    </div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Index: {pos}</div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Right-to-Left Shift Trace */}
          {result.steps && (
            <div className="glass-card">
              <h3 style={{ fontSize: '1.15rem', marginBottom: '0.75rem' }}>
                Pattern Shift & Jump Distance Trace (Right-to-Left Scan)
              </h3>
              <div className="visualizer-box" style={{ maxHeight: '250px', overflowY: 'auto' }}>
                {result.steps.map((st, idx) => (
                  <div key={idx} style={{ color: st.includes('>>') ? 'var(--accent-emerald)' : (st.includes('Mismatch') ? '#fb7185' : '#94a3b8'), padding: '2px 0' }}>
                    {st}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Theoretical Analysis Card */}
      <div className="glass-card" style={{ borderLeft: '4px solid var(--accent-rose)', marginBottom: '2.5rem' }}>
        <h3 style={{ fontSize: '1.25rem', marginBottom: '0.75rem' }}>
          Theoretical Complexity & Sublinear Speedup (Viva Review)
        </h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem', marginBottom: '1rem' }}>
          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Best-Case Time</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-emerald)' }}>O(n / m)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Sublinear speed achieved when bad char skips m chars every step</div>
          </div>

          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Average-Case Time</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-cyan)' }}>O(n)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Empirically the fastest text search algorithm on natural language</div>
          </div>

          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Worst-Case Time</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-rose)' }}>O(n * m)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Occurs on pathological inputs (e.g. searching 'a...ab' in 'a...aa')</div>
          </div>

          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Auxiliary Space</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-purple)' }}>O(sigma)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Size of alphabet table (256 ASCII characters)</div>
          </div>
        </div>

        <div style={{ marginTop: '1.5rem', paddingTop: '1rem', borderTop: '1px solid var(--border-subtle)' }}>
          <button onClick={() => setShowCode(!showCode)} className="btn btn-secondary" style={{ fontSize: '0.85rem' }}>
            <Code2 size={16} />
            <span>{showCode ? 'Hide Java Implementation' : 'View Backend Java Boyer-Moore Code'}</span>
          </button>

          {showCode && (
            <div className="visualizer-box" style={{ marginTop: '1rem', fontSize: '0.82rem', maxHeight: '350px', overflowY: 'auto' }}>
{`// Boyer-Moore Bad Character Heuristic Backend Java Implementation
public int[] buildBadCharTable(String pattern) {
    int[] badChar = new int[256];
    Arrays.fill(badChar, -1);
    for (int i = 0; i < pattern.length(); i++) {
        badChar[pattern.charAt(i)] = i;
    }
    return badChar;
}

public List<Integer> search(String text, String pattern) {
    List<Integer> positions = new ArrayList<>();
    int n = text.length(), m = pattern.length();
    int[] badChar = buildBadCharTable(pattern);

    int shift = 0;
    while (shift <= (n - m)) {
        int j = m - 1;
        // Right-to-left scan
        while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
            j--;
        }
        if (j < 0) {
            positions.add(shift);
            shift += (shift + m < n) ? m - badChar[text.charAt(shift + m)] : 1;
        } else {
            shift += Math.max(1, j - badChar[text.charAt(shift + j)]);
        }
    }
    return positions;
}`}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default BoyerMooreDemoPage;
