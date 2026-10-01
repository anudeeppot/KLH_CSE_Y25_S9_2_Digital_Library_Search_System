import React, { useState, useEffect } from 'react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import { Play, Network, CheckCircle, XCircle } from 'lucide-react';

function SuffixAutomatonDemoPage() {
  const [text, setText] = useState('algorithmics');
  const [pattern, setPattern] = useState('algo');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const runTest = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.testSuffixAutomaton({ text, pattern });
      setResult(res.data);
    } catch (err) {
      setError('Failed to build Suffix Automaton. Check backend server.');
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
          <span style={{ color: 'var(--text-muted)' }}>Suffix-Based Structures (Intuition Level)</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, margin: 0 }}>
          Suffix Automaton (DAWG) Explorer
        </h1>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem', maxWidth: '850px' }}>
          A Suffix Automaton is the minimal deterministic finite state machine (DFA) accepting all substrings of a document. It bounds states to &le; 2N-1 and transitions to &le; 3N-4, answering any substring query in strictly O(|pattern|) time!
        </p>
      </div>

      <div className="card" style={{ padding: '1.5rem', marginBottom: '2rem' }}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr auto', gap: '1rem', alignItems: 'end' }}>
          <div>
            <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>Source Document Text</label>
            <input
              type="text"
              className="input-field"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="e.g. algorithmics"
              style={{ width: '100%' }}
            />
          </div>
          <div>
            <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>Query Substring</label>
            <input
              type="text"
              className="input-field"
              value={pattern}
              onChange={(e) => setPattern(e.target.value)}
              placeholder="e.g. algo"
              style={{ width: '100%' }}
            />
          </div>
          <button className="btn btn-primary" onClick={runTest} disabled={loading} style={{ height: '42px' }}>
            {loading ? <LoadingSpinner size="small" /> : <><Play size={16} /> Query Automaton</>}
          </button>
        </div>
      </div>

      {error && <ErrorMessage message={error} />}

      {result && (
        <div>
          {/* Result Banner */}
          <div className="card" style={{ padding: '1.5rem', marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              {result.substring ? (
                <CheckCircle size={36} color="var(--success)" />
              ) : (
                <XCircle size={36} color="var(--danger)" />
              )}
              <div>
                <div style={{ fontSize: '1.25rem', fontWeight: 800 }}>
                  {result.substring ? `Substring '${result.queryPattern}' is ACCEPTED!` : `Substring '${result.queryPattern}' is REJECTED!`}
                </div>
                <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                  {result.theoreticalInsight}
                </div>
              </div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Automaton Complexity</div>
              <div style={{ fontWeight: 700, fontFamily: 'monospace' }}>
                {result.totalStates} States | {result.totalTransitions} Transitions
              </div>
            </div>
          </div>

          {/* Automaton Traversal Path */}
          <div className="card" style={{ padding: '1.5rem', marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '0.75rem' }}>
              State Traversal Path for Query: "{result.queryPattern}"
            </h3>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
              {result.traversalPath.map((stateId, idx) => (
                <React.Fragment key={idx}>
                  <div style={{
                    padding: '0.5rem 0.85rem',
                    borderRadius: '8px',
                    background: idx === result.traversalPath.length - 1 ? 'var(--primary)' : 'var(--bg-tertiary)',
                    color: idx === result.traversalPath.length - 1 ? '#fff' : 'inherit',
                    fontWeight: 700,
                    fontFamily: 'monospace'
                  }}>
                    State {stateId} {idx === 0 && '(Root)'}
                  </div>
                  {idx < result.traversalPath.length - 1 && (
                    <span style={{ color: 'var(--text-muted)', fontWeight: 700 }}>&rarr; [{result.queryPattern[idx]}] &rarr;</span>
                  )}
                </React.Fragment>
              ))}
            </div>
          </div>

          {/* State Transition Table */}
          <div className="card" style={{ padding: '1.5rem' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '1rem' }}>
              Suffix Automaton State Transition Table (First 25 States)
            </h3>
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontFamily: 'monospace' }}>
                <thead>
                  <tr style={{ background: 'var(--bg-tertiary)', borderBottom: '2px solid var(--border)' }}>
                    <th style={{ padding: '0.75rem' }}>State ID</th>
                    <th style={{ padding: '0.75rem' }}>Max Length (len)</th>
                    <th style={{ padding: '0.75rem' }}>Suffix Link</th>
                    <th style={{ padding: '0.75rem' }}>Is Clone?</th>
                    <th style={{ padding: '0.75rem' }}>Outgoing Transitions [char &rarr; next_state]</th>
                  </tr>
                </thead>
                <tbody>
                  {result.sampleStates.map((st) => (
                    <tr key={st.id} style={{ borderBottom: '1px solid var(--border)' }}>
                      <td style={{ padding: '0.6rem 0.75rem', fontWeight: 700 }}>State {st.id}</td>
                      <td style={{ padding: '0.6rem 0.75rem' }}>{st.len}</td>
                      <td style={{ padding: '0.6rem 0.75rem', color: 'var(--primary)' }}>
                        {st.suffixLink === -1 ? 'None (Root)' : `State ${st.suffixLink}`}
                      </td>
                      <td style={{ padding: '0.6rem 0.75rem' }}>
                        {st.clone ? <span className="badge badge-warning">Clone</span> : 'Original'}
                      </td>
                      <td style={{ padding: '0.6rem 0.75rem' }}>
                        <div style={{ display: 'flex', gap: '0.35rem', flexWrap: 'wrap' }}>
                          {Object.entries(st.transitions).map(([ch, target]) => (
                            <span key={ch} className="badge badge-secondary" style={{ fontSize: '0.75rem' }}>
                              '{ch}' &rarr; State {target}
                            </span>
                          ))}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default SuffixAutomatonDemoPage;
