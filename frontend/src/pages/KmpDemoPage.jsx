import React, { useState } from 'react';
import { Cpu, Play, CheckCircle, Clock, Zap, BookOpen, Code2 } from 'lucide-react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';

const KmpDemoPage = () => {
  const [text, setText] = useState('Knuth Morris Pratt is a linear time string matching algorithm used in digital library search systems.');
  const [pattern, setPattern] = useState('algorithm');
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
      const res = await searchAPI.testKMP({ text, pattern });
      setResult(res.data);
    } catch (err) {
      console.error(err);
      setError('Failed to execute KMP on backend: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {/* Title */}
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'inline-block', marginBottom: '0.5rem' }}>
          <span className="badge badge-kmp">CO2 • String Matching</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', marginBottom: '0.5rem' }}>
          Knuth-Morris-Pratt <span className="text-gradient">(KMP) Algorithm</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '800px', fontSize: '0.98rem', lineHeight: 1.6 }}>
          KMP searches for occurrences of a pattern within a text by utilizing preprocessed information about the pattern itself.
          It constructs the <strong>Longest Proper Prefix which is also a Suffix (LPS)</strong> array to bypass redundant comparisons.
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
              <span>Execute KMP</span>
            </button>
          </div>
        </form>
      </div>

      {loading && <LoadingSpinner message="Building LPS table and executing KMP pattern matching in Java..." />}
      {error && <ErrorMessage message={error} onRetry={handleRun} />}

      {/* Result Visualizer */}
      {result && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem', marginBottom: '3rem' }}>
          {/* Key Metrics Strip */}
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

          {/* LPS Array Table */}
          {result.lps && result.lps.length > 0 && (
            <div className="glass-card">
              <h3 style={{ fontSize: '1.15rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Zap size={18} color="var(--accent-cyan)" /> Preprocessed LPS Array (Longest Proper Prefix which is also Suffix)
              </h3>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem', marginBottom: '1rem' }}>
                The LPS table dictates how many characters the pattern can skip after a mismatch, avoiding backtracking the text pointer.
              </p>

              <div style={{ overflowX: 'auto' }}>
                <table className="dp-table" style={{ width: '100%', minWidth: '400px' }}>
                  <thead>
                    <tr>
                      <th style={{ width: '120px' }}>Index (i)</th>
                      {result.pattern.split('').map((_, i) => (
                        <th key={i}>{i}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td style={{ fontWeight: 600, color: 'var(--text-secondary)' }}>Pattern[i]</td>
                      {result.pattern.split('').map((char, i) => (
                        <td key={i} className="text-mono" style={{ color: 'var(--accent-cyan)', fontWeight: 700 }}>{char}</td>
                      ))}
                    </tr>
                    <tr>
                      <td style={{ fontWeight: 600, color: 'var(--text-secondary)' }}>LPS[i]</td>
                      {result.lps.map((val, i) => (
                        <td key={i} className={val > 0 ? 'dp-active' : ''}>{val}</td>
                      ))}
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Highlighted Match Positions in Text */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.15rem', marginBottom: '0.75rem' }}>
              Corpus Match Positions: {JSON.stringify(result.positions)}
            </h3>
            <div className="visualizer-box" style={{ whiteSpace: 'pre-wrap' }}>
              {renderHighlightedText(result.text, result.pattern, result.positions)}
            </div>
          </div>

          {/* Step-by-Step Execution Trace */}
          {result.steps && result.steps.length > 0 && (
            <div className="glass-card">
              <h3 style={{ fontSize: '1.15rem', marginBottom: '0.75rem' }}>
                Algorithm Step-by-Step Execution Trace
              </h3>
              <div className="visualizer-box" style={{ maxHeight: '250px', overflowY: 'auto' }}>
                {result.steps.map((st, idx) => (
                  <div key={idx} style={{ color: st.includes('>>') ? 'var(--accent-emerald)' : (st.includes('Mismatch') ? '#fca5a5' : '#94a3b8'), padding: '2px 0' }}>
                    {st}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Complexity & Viva Theory Breakdown Card */}
      <div className="glass-card" style={{ borderLeft: '4px solid var(--accent-cyan)', marginBottom: '2.5rem' }}>
        <h3 style={{ fontSize: '1.25rem', marginBottom: '0.75rem' }}>
          Theoretical Complexity & Algorithmic Analysis (Viva Review)
        </h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem', marginBottom: '1rem' }}>
          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>LPS Construction</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-cyan)' }}>O(m)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Single pass over pattern of length m</div>
          </div>

          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Searching Phase</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-emerald)' }}>O(n)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Text pointer i monotonically increases</div>
          </div>

          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Total Time Complexity</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-amber)' }}>O(n + m)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Guaranteed linear worst-case bound</div>
          </div>

          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Auxiliary Space</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-purple)' }}>O(m)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Required exclusively for integer LPS array</div>
          </div>
        </div>

        <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: 1.6 }}>
          <strong>Why KMP avoids backtracking:</strong> In the naive approach, whenever a mismatch occurs at text index <code>i</code> and pattern index <code>j</code>, the text pointer resets back to <code>i - j + 1</code>, degrading to $O(n \times m)$ time. In KMP, the text pointer <code>i</code> never decreases; instead, the pattern pointer jumps to <code>lps[j - 1]</code>, guaranteeing that characters already matched are never re-examined.
        </p>

        {/* Source Code Toggle */}
        <div style={{ marginTop: '1.5rem', paddingTop: '1rem', borderTop: '1px solid var(--border-subtle)' }}>
          <button
            onClick={() => setShowCode(!showCode)}
            className="btn btn-secondary"
            style={{ fontSize: '0.85rem' }}
          >
            <Code2 size={16} />
            <span>{showCode ? 'Hide Java Implementation' : 'View Backend Java KMP Implementation'}</span>
          </button>

          {showCode && (
            <div className="visualizer-box" style={{ marginTop: '1rem', fontSize: '0.82rem', maxHeight: '350px', overflowY: 'auto' }}>
{`// Knuth-Morris-Pratt (KMP.java) Backend Implementation
public int[] buildLPS(String pattern) {
    int[] lps = new int[pattern.length()];
    int length = 0;
    int i = 1;
    while (i < pattern.length()) {
        if (pattern.charAt(i) == pattern.charAt(length)) {
            length++;
            lps[i] = length;
            i++;
        } else if (length != 0) {
            length = lps[length - 1];
        } else {
            lps[i] = 0;
            i++;
        }
    }
    return lps;
}

public List<Integer> search(String text, String pattern) {
    List<Integer> positions = new ArrayList<>();
    int[] lps = buildLPS(pattern);
    int i = 0, j = 0;
    while (i < text.length()) {
        if (text.charAt(i) == pattern.charAt(j)) {
            i++; j++;
            if (j == pattern.length()) {
                positions.add(i - j);
                j = lps[j - 1];
            }
        } else if (j != 0) {
            j = lps[j - 1];
        } else {
            i++;
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

// Helper function to render text with highlighted matching spans
function renderHighlightedText(text, pattern, positions) {
  if (!positions || positions.length === 0) return text;
  const elements = [];
  let lastIdx = 0;
  const m = pattern.length;

  positions.forEach((pos, i) => {
    if (pos > lastIdx) {
      elements.push(text.substring(lastIdx, pos));
    }
    elements.push(
      <mark
        key={i}
        style={{
          background: 'rgba(56, 189, 248, 0.35)',
          color: '#38bdf8',
          padding: '2px 4px',
          borderRadius: '4px',
          fontWeight: 700,
        }}
      >
        {text.substring(pos, pos + m)}
      </mark>
    );
    lastIdx = pos + m;
  });

  if (lastIdx < text.length) {
    elements.push(text.substring(lastIdx));
  }
  return elements;
}

export default KmpDemoPage;
