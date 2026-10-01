import React, { useState } from 'react';
import { Play, Binary, Clock, Code2, HelpCircle } from 'lucide-react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';

const SuffixArrayDemoPage = () => {
  const [text, setText] = useState('banana');
  const [pattern, setPattern] = useState('ana');
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
      const res = await searchAPI.testSuffixArray({ text, pattern });
      setResult(res.data);
    } catch (err) {
      console.error(err);
      setError('Failed to execute Suffix Array: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'inline-block', marginBottom: '0.5rem' }}>
          <span className="badge badge-suffix">Module 2 • Full-Text Indexing</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', marginBottom: '0.5rem' }}>
          Suffix Array <span className="text-gradient">& Binary Search Retrieval</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '800px', fontSize: '0.98rem', lineHeight: 1.6 }}>
          A Suffix Array is a compact data structure storing the starting indices of all suffixes of a text, sorted in lexicographical order.
          Once constructed, any substring query can be answered via <strong>Binary Search</strong> over the sorted suffix indices.
        </p>
      </div>

      {/* Input Form */}
      <div className="glass-card" style={{ marginBottom: '2.5rem' }}>
        <form onSubmit={handleRun}>
          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
              Corpus Text to Index (n):
            </label>
            <input
              type="text"
              className="input-control text-mono"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="e.g. banana or digital library text..."
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'minmax(240px, 1fr) auto', gap: '1rem', alignItems: 'flex-end' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                Search Query / Pattern (m):
              </label>
              <input
                type="text"
                className="input-control text-mono"
                value={pattern}
                onChange={(e) => setPattern(e.target.value)}
                placeholder="e.g. ana"
              />
            </div>

            <button type="submit" disabled={loading} className="btn btn-primary" style={{ padding: '0.75rem 1.75rem' }}>
              <Play size={16} />
              <span>Build & Search Suffix Array</span>
            </button>
          </div>
        </form>
      </div>

      {loading && <LoadingSpinner message="Generating lexicographically sorted Suffix Array in Java..." />}
      {error && <ErrorMessage message={error} onRetry={handleRun} />}

      {/* Result Visualizer */}
      {result && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem', marginBottom: '3rem' }}>
          {/* Key Metrics */}
          <div className="glass-card" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1.5rem', textAlign: 'center' }}>
            <div>
              <div style={{ fontSize: '1.8rem', fontWeight: 800, color: result.matchPositions.length > 0 ? 'var(--accent-emerald)' : 'var(--accent-rose)' }}>
                {result.matchPositions.length > 0 ? 'FOUND' : 'NOT FOUND'}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Search Status</div>
            </div>

            <div>
              <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--accent-cyan)' }}>
                {result.matchPositions.length}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Matches Found</div>
            </div>

            <div>
              <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--accent-amber)' }}>
                {result.suffixArray.length}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Total Suffixes</div>
            </div>

            <div>
              <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--accent-purple)' }}>
                {(result.executionTime / 1_000_000.0).toFixed(3)} ms
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>{result.executionTime.toLocaleString()} ns</div>
            </div>
          </div>

          {/* Suffix Array Table */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.15rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Binary size={18} color="var(--accent-cyan)" /> Sorted Suffix Array (Lexicographical Order)
            </h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem', marginBottom: '1rem' }}>
              Notice that every substring of text appears as a prefix of some suffix in this sorted table.
            </p>

            <div style={{ overflowX: 'auto', maxHeight: '350px' }}>
              <table className="dp-table" style={{ width: '100%' }}>
                <thead>
                  <tr>
                    <th style={{ width: '60px' }}>Rank</th>
                    <th style={{ width: '100px' }}>Suffix Index SA[i]</th>
                    <th style={{ width: '80px' }}>LCP</th>
                    <th>Suffix String</th>
                    <th style={{ width: '120px' }}>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {result.sampleSuffixes.map((item, rank) => {
                    const isMatch = result.matchPositions.includes(item.index);
                    const lcpVal = result.lcpArray && rank < result.lcpArray.length ? result.lcpArray[rank] : 0;
                    return (
                      <tr key={rank} style={{ background: isMatch ? 'rgba(56, 189, 248, 0.15)' : undefined }}>
                        <td style={{ fontWeight: 600 }}>{rank}</td>
                        <td className="text-mono" style={{ color: 'var(--accent-cyan)', fontWeight: 700 }}>
                          {item.index}
                        </td>
                        <td className="text-mono">{lcpVal}</td>
                        <td className="text-mono" style={{ textAlign: 'left', paddingLeft: '1rem' }}>
                          {isMatch ? (
                            <span>
                              <strong style={{ color: 'var(--accent-emerald)', background: 'rgba(16, 185, 129, 0.2)', padding: '2px 4px', borderRadius: '4px' }}>
                                {item.suffix.substring(0, result.pattern.length)}
                              </strong>
                              {item.suffix.substring(result.pattern.length)}
                            </span>
                          ) : (
                            item.suffix
                          )}
                        </td>
                        <td>
                          {isMatch ? (
                            <span style={{ color: 'var(--accent-emerald)', fontWeight: 700 }}>Match Prefix</span>
                          ) : (
                            <span style={{ color: 'var(--text-muted)' }}>-</span>
                          )}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>

          {/* Binary Search Steps Trace */}
          {result.binarySearchSteps && (
            <div className="glass-card">
              <h3 style={{ fontSize: '1.15rem', marginBottom: '0.75rem' }}>
                Binary Search Narrowing Steps
              </h3>
              <div className="visualizer-box" style={{ maxHeight: '250px', overflowY: 'auto' }}>
                {result.binarySearchSteps.map((step, idx) => (
                  <div key={idx} style={{ color: step.includes('>>') ? 'var(--accent-emerald)' : '#94a3b8', padding: '2px 0' }}>
                    {step}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Theoretical Analysis Card */}
      <div className="glass-card" style={{ borderLeft: '4px solid var(--accent-cyan)', marginBottom: '2.5rem' }}>
        <h3 style={{ fontSize: '1.25rem', marginBottom: '0.75rem' }}>
          Honest Suffix Array Complexity Analysis (Viva Review)
        </h3>
        <p style={{ color: 'var(--text-secondary)', lineHeight: 1.6, marginBottom: '1rem' }}>
          <strong>Critical Review Distinction:</strong> Do not claim that Suffix Array substring search is purely $O(\log n)$!
          A standard binary search takes $O(\log n)$ comparison steps. However, each comparison compares two strings of up to length $m$.
          Therefore, standard Suffix Array search takes <strong style={{ color: 'var(--accent-cyan)' }}>$O(m \times \log n)$</strong> time.
        </p>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem', marginBottom: '1rem' }}>
          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Binary Search Query</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-cyan)' }}>O(m * log n)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>m character comparisons per binary step</div>
          </div>

          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>With LCP Acceleration</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-emerald)' }}>O(m + log n)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Using Longest Common Prefix skip tables</div>
          </div>

          <div style={{ background: 'rgba(0,0,0,0.3)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Auxiliary Space</span>
            <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--accent-amber)' }}>O(n)</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Compact 4-byte integer array of n indices</div>
          </div>
        </div>

        <div style={{ marginTop: '1.5rem', paddingTop: '1rem', borderTop: '1px solid var(--border-subtle)' }}>
          <button onClick={() => setShowCode(!showCode)} className="btn btn-secondary" style={{ fontSize: '0.85rem' }}>
            <Code2 size={16} />
            <span>{showCode ? 'Hide Java Implementation' : 'View Backend Java Suffix Array Code'}</span>
          </button>

          {showCode && (
            <div className="visualizer-box" style={{ marginTop: '1rem', fontSize: '0.82rem', maxHeight: '350px', overflowY: 'auto' }}>
{`// Suffix Array Construction and Binary Search Java Implementation
public SuffixArrayResult buildAndSearch(String text, String pattern) {
    int n = text.length(), m = pattern.length();
    Integer[] sa = new Integer[n];
    for (int i = 0; i < n; i++) sa[i] = i;

    // Sort suffixes lexicographically
    Arrays.sort(sa, (a, b) -> text.substring(a).compareTo(text.substring(b)));

    // Binary Search: O(m * log n)
    int low = 0, high = n - 1, first = -1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        String midSuffix = text.substring(sa[mid], Math.min(sa[mid] + m, n));
        int cmp = midSuffix.compareTo(pattern);
        if (cmp >= 0) {
            if (cmp == 0) first = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }
    // Collect all matches in prefix range
    List<Integer> matches = new ArrayList<>();
    if (first != -1) {
        for (int i = first; i < n && text.startsWith(pattern, sa[i]); i++) {
            matches.add(sa[i]);
        }
    }
    return new SuffixArrayResult(text, pattern, sa, matches);
}`}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SuffixArrayDemoPage;
