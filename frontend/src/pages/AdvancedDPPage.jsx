import React, { useState, useEffect } from 'react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import { Layers, GitBranch, Binary, AlignLeft, CheckCircle2 } from 'lucide-react';

function AdvancedDPPage() {
  const [activeTab, setActiveTab] = useState('interval'); // 'interval', 'bitmask', 'tree', 'sequence'

  // Interval DP State
  const [intervalResult, setIntervalResult] = useState(null);
  const [intervalLoading, setIntervalLoading] = useState(false);

  // Bitmask DP State
  const [bitmaskResult, setBitmaskResult] = useState(null);
  const [bitmaskLoading, setBitmaskLoading] = useState(false);

  // Tree DP State
  const [treeResult, setTreeResult] = useState(null);
  const [treeLoading, setTreeLoading] = useState(false);

  // Sequence Alignment State
  const [seqA, setSeqA] = useState('ALGORITHM');
  const [seqB, setSeqB] = useState('LOGARITHM');
  const [seqResult, setSeqResult] = useState(null);
  const [seqLoading, setSeqLoading] = useState(false);

  const [error, setError] = useState(null);

  // Fetch Interval DP
  const loadIntervalDP = async () => {
    setIntervalLoading(true);
    try {
      const res = await searchAPI.testIntervalDP({});
      setIntervalResult(res.data);
    } catch (err) {
      setError('Failed to load Interval DP.');
    } finally {
      setIntervalLoading(false);
    }
  };

  // Fetch Bitmask DP
  const loadBitmaskDP = async () => {
    setBitmaskLoading(true);
    try {
      const res = await searchAPI.testBitmaskDP({});
      setBitmaskResult(res.data);
    } catch (err) {
      setError('Failed to load Bitmask DP.');
    } finally {
      setBitmaskLoading(false);
    }
  };

  // Fetch Tree DP
  const loadTreeDP = async () => {
    setTreeLoading(true);
    try {
      const res = await searchAPI.getTreeDP();
      setTreeResult(res.data);
    } catch (err) {
      setError('Failed to load Tree DP.');
    } finally {
      setTreeLoading(false);
    }
  };

  // Run Sequence Alignment
  const runSequenceAlignment = async () => {
    setSeqLoading(true);
    try {
      const res = await searchAPI.testSequenceAlignment({ text: seqA, target: seqB });
      setSeqResult(res.data);
    } catch (err) {
      setError('Failed to run Sequence Alignment.');
    } finally {
      setSeqLoading(false);
    }
  };

  useEffect(() => {
    loadIntervalDP();
    loadBitmaskDP();
    loadTreeDP();
    runSequenceAlignment();
  }, []);

  return (
    <div className="container" style={{ padding: '2rem 1rem' }}>
      {/* Page Title */}
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
          <span className="badge badge-primary">Course Outcome 3 (CO-3)</span>
          <span style={{ color: 'var(--text-muted)' }}>Advanced Dynamic Programming Patterns</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, margin: 0 }}>
          Advanced Dynamic Programming Suite
        </h1>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem', maxWidth: '850px' }}>
          Syllabus CO-3 mandates four advanced dynamic programming patterns: <strong>Interval DP</strong>, <strong>Bitmask DP</strong>, <strong>DP on Trees</strong>, and <strong>DP on Subsets / Sequence Alignment</strong> to design polynomial-time algorithms for digital library combinatorial optimization.
        </p>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '2rem', borderBottom: '1px solid var(--border)', paddingBottom: '0.5rem' }}>
        <button
          className={`btn ${activeTab === 'interval' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveTab('interval')}
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
        >
          <Layers size={18} /> 1. Interval DP (Query Optimization)
        </button>
        <button
          className={`btn ${activeTab === 'bitmask' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveTab('bitmask')}
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
        >
          <Binary size={18} /> 2. Bitmask DP (Topic Coverage)
        </button>
        <button
          className={`btn ${activeTab === 'tree' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveTab('tree')}
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
        >
          <GitBranch size={18} /> 3. DP on Trees (Taxonomy Hierarchy)
        </button>
        <button
          className={`btn ${activeTab === 'sequence' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveTab('sequence')}
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
        >
          <AlignLeft size={18} /> 4. Sequence Alignment (Manuscript Diff)
        </button>
      </div>

      {error && <ErrorMessage message={error} />}

      {/* ----------------- TAB 1: INTERVAL DP ----------------- */}
      {activeTab === 'interval' && (
        <div>
          {intervalLoading && <LoadingSpinner />}
          {intervalResult && (
            <div>
              <div className="card" style={{ padding: '1.5rem', marginBottom: '1.5rem' }}>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.5rem' }}>
                  Interval DP: Optimal Boolean Search Filter Evaluation Order
                </h3>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.92rem', marginBottom: '1rem' }}>
                  In high-throughput digital library querying, merging adjacent filter results in the wrong order wastes millions of operations. Interval DP computes the optimal parenthesization tree in O(N^3) time.
                </p>
                <div style={{ background: 'var(--bg-secondary)', padding: '1rem', borderRadius: '8px', marginBottom: '1.25rem', fontFamily: 'monospace' }}>
                  <strong>Recurrence Relation:</strong><br />
                  <span style={{ color: 'var(--primary)', fontWeight: 600 }}>{intervalResult.recurrenceFormula}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: 'var(--bg-tertiary)', padding: '1rem', borderRadius: '8px' }}>
                  <div>
                    <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Optimal Evaluation Execution Tree:</span>
                    <div style={{ fontFamily: 'monospace', fontSize: '1.15rem', fontWeight: 800, color: 'var(--success)' }}>
                      {intervalResult.optimalParenthesization}
                    </div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Minimum Multiplication Cost:</span>
                    <div style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--primary)' }}>
                      {intervalResult.optimalCost.toLocaleString()} units
                    </div>
                  </div>
                </div>
              </div>

              {/* DP Cost Table */}
              <div className="card" style={{ padding: '1.5rem', marginBottom: '1.5rem' }}>
                <h4 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '1rem' }}>
                  Interval Cost Matrix M[i][j] (Diagonal DP Filling)
                </h4>
                <div style={{ overflowX: 'auto' }}>
                  <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'center', fontFamily: 'monospace' }}>
                    <thead>
                      <tr style={{ background: 'var(--bg-tertiary)', borderBottom: '2px solid var(--border)' }}>
                        <th style={{ padding: '0.6rem' }}>i \ j</th>
                        {intervalResult.queryTokens.map((t, j) => (
                          <th key={j} style={{ padding: '0.6rem' }}>{t} ({j})</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {intervalResult.dpTable.map((row, i) => (
                        <tr key={i} style={{ borderBottom: '1px solid var(--border)' }}>
                          <td style={{ padding: '0.6rem', fontWeight: 700, background: 'var(--bg-tertiary)' }}>
                            {intervalResult.queryTokens[i]} ({i})
                          </td>
                          {row.map((val, j) => (
                            <td
                              key={j}
                              style={{
                                padding: '0.6rem',
                                color: i <= j ? (val === 0 ? 'var(--text-muted)' : 'var(--primary)') : 'transparent',
                                fontWeight: i === 0 && j === row.length - 1 ? 800 : 500,
                                background: i === 0 && j === row.length - 1 ? 'rgba(16, 185, 129, 0.2)' : 'transparent',
                              }}
                            >
                              {i <= j ? val.toLocaleString() : '-'}
                            </td>
                          ))}
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* ----------------- TAB 2: BITMASK DP ----------------- */}
      {activeTab === 'bitmask' && (
        <div>
          {bitmaskLoading && <LoadingSpinner />}
          {bitmaskResult && (
            <div>
              <div className="card" style={{ padding: '1.5rem', marginBottom: '1.5rem' }}>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.5rem' }}>
                  Bitmask DP: Curator's Minimum Cost Syllabus Topic Coverage
                </h3>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.92rem', marginBottom: '1rem' }}>
                  Given N syllabus topics represented by binary bitmasks (1 &lt;&lt; N), select the minimum weight/page-count subset of library books that covers all required competencies. Avoids O(N!) brute force by memoizing 2^N states.
                </p>
                <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '1rem' }}>
                  {bitmaskResult.requiredTopics.map((topic, idx) => (
                    <span key={idx} className="badge badge-primary">
                      Topic #{idx}: {topic} (Bit {1 << idx})
                    </span>
                  ))}
                </div>
                <div style={{ background: 'var(--bg-tertiary)', padding: '1rem', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Target Bitmask:</span>
                    <div style={{ fontFamily: 'monospace', fontWeight: 700, color: 'var(--primary)' }}>
                      {(1 << bitmaskResult.requiredTopics.length) - 1} (Binary: {((1 << bitmaskResult.requiredTopics.length) - 1).toString(2)})
                    </div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Optimal Total Cost:</span>
                    <div style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--success)' }}>
                      {bitmaskResult.optimalCost} units
                    </div>
                  </div>
                </div>
              </div>

              {/* Selected Volumes */}
              <div className="card" style={{ padding: '1.5rem', marginBottom: '1.5rem' }}>
                <h4 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '1rem' }}>
                  Backtracked Optimal Book Bundle Selection ({bitmaskResult.selectedBooks.length} volumes):
                </h4>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1rem' }}>
                  {bitmaskResult.selectedBooks.map((b) => (
                    <div key={b.id} style={{ border: '1px solid var(--primary)', borderRadius: '8px', padding: '1rem', background: 'var(--bg-secondary)' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <strong style={{ fontSize: '1rem' }}>{b.title}</strong>
                        <span className="badge badge-success">{b.cost} pts</span>
                      </div>
                      <div style={{ marginTop: '0.75rem', fontSize: '0.85rem' }}>
                        <span style={{ color: 'var(--text-muted)' }}>Covered Topics: </span>
                        {b.coveredTopics.join(', ')}
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Transition History */}
              <div className="card" style={{ padding: '1.5rem' }}>
                <h4 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '0.75rem' }}>
                  Bitmask State Transition Trace:
                </h4>
                <div style={{ maxHeight: '280px', overflowY: 'auto' }}>
                  {bitmaskResult.transitionHistory.map((step, idx) => (
                    <div key={idx} style={{ padding: '0.5rem 0.75rem', borderBottom: '1px solid var(--border)', fontSize: '0.88rem', fontFamily: 'monospace', display: 'flex', justifyContent: 'space-between' }}>
                      <span>
                        Mask <strong>{step.fromMaskBinary}</strong> &rarr; <strong>{step.toMaskBinary}</strong>
                        <span style={{ marginLeft: '1rem', color: 'var(--text-secondary)' }}>via [{step.bookAdded}]</span>
                      </span>
                      <span style={{ fontWeight: 700, color: 'var(--primary)' }}>Cost: {step.newCost}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* ----------------- TAB 3: TREE DP ----------------- */}
      {activeTab === 'tree' && (
        <div>
          {treeLoading && <LoadingSpinner />}
          {treeResult && (
            <div>
              <div className="card" style={{ padding: '1.5rem', marginBottom: '1.5rem' }}>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.5rem' }}>
                  Tree DP: ACM / Dewey Knowledge Classification Taxonomy Subtree Optimization
                </h3>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.92rem', marginBottom: '1rem' }}>
                  Computes the <strong>Maximum Weight Independent Set on Tree</strong> in strictly linear O(N) time. Recurrence evaluates DP[u][0] (exclude node u) and DP[u][1] (include node u) using post-order tree traversal.
                </p>
                <div style={{ background: 'var(--bg-tertiary)', padding: '1.25rem', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Optimal Curriculum Impact Score:</span>
                    <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--primary)' }}>
                      {treeResult.maxImpactScore} pts
                    </div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Selected Non-Conflicting Categories:</span>
                    <div style={{ fontWeight: 700, color: 'var(--success)' }}>
                      {treeResult.selectedCategories.length} Categories Chosen
                    </div>
                  </div>
                </div>
              </div>

              {/* Taxonomy Node Decisions Table */}
              <div className="card" style={{ padding: '1.5rem' }}>
                <h4 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '1rem' }}>
                  Post-Order Subtree Decisions &amp; Inclusion States:
                </h4>
                <div style={{ overflowX: 'auto' }}>
                  <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontFamily: 'monospace' }}>
                    <thead>
                      <tr style={{ background: 'var(--bg-tertiary)', borderBottom: '2px solid var(--border)' }}>
                        <th style={{ padding: '0.75rem' }}>Taxonomy ID</th>
                        <th style={{ padding: '0.75rem' }}>Subject Discipline / Category</th>
                        <th style={{ padding: '0.75rem' }}>Weight</th>
                        <th style={{ padding: '0.75rem' }}>DP[u][0] (Exclude)</th>
                        <th style={{ padding: '0.75rem' }}>DP[u][1] (Include)</th>
                        <th style={{ padding: '0.75rem' }}>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {treeResult.nodeDecisions.map((node) => (
                        <tr key={node.id} style={{ borderBottom: '1px solid var(--border)', background: node.selectedInOptimal ? 'rgba(16, 185, 129, 0.1)' : 'transparent' }}>
                          <td style={{ padding: '0.65rem 0.75rem', fontWeight: 700 }}>#{node.id}</td>
                          <td style={{ padding: '0.65rem 0.75rem' }}>{node.name}</td>
                          <td style={{ padding: '0.65rem 0.75rem' }}>{node.weight}</td>
                          <td style={{ padding: '0.65rem 0.75rem', color: 'var(--text-muted)' }}>{node.dpExclude}</td>
                          <td style={{ padding: '0.65rem 0.75rem', color: 'var(--primary)' }}>{node.dpInclude}</td>
                          <td style={{ padding: '0.65rem 0.75rem' }}>
                            {node.selectedInOptimal ? (
                              <span className="badge badge-success" style={{ display: 'inline-flex', alignItems: 'center', gap: '0.25rem' }}>
                                <CheckCircle2 size={12} /> Included in Optimal
                              </span>
                            ) : (
                              <span className="badge badge-secondary">Excluded</span>
                            )}
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
      )}

      {/* ----------------- TAB 4: SEQUENCE ALIGNMENT ----------------- */}
      {activeTab === 'sequence' && (
        <div>
          <div className="card" style={{ padding: '1.5rem', marginBottom: '1.5rem' }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Sequence Alignment: Needleman-Wunsch Global Alignment &amp; Manuscript Diff
            </h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.92rem', marginBottom: '1.25rem' }}>
              Compares successive revisions of manuscripts, historical texts, or citation strings using optimal global sequence alignment in O(N * M) time.
            </p>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr auto', gap: '1rem', alignItems: 'end' }}>
              <div>
                <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>Sequence A (Manuscript Edition 1)</label>
                <input
                  type="text"
                  className="input-field"
                  value={seqA}
                  onChange={(e) => setSeqA(e.target.value)}
                  style={{ width: '100%' }}
                />
              </div>
              <div>
                <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>Sequence B (Manuscript Edition 2)</label>
                <input
                  type="text"
                  className="input-field"
                  value={seqB}
                  onChange={(e) => setSeqB(e.target.value)}
                  style={{ width: '100%' }}
                />
              </div>
              <button className="btn btn-primary" onClick={runSequenceAlignment} disabled={seqLoading} style={{ height: '42px' }}>
                {seqLoading ? <LoadingSpinner size="small" /> : 'Align Sequences'}
              </button>
            </div>
          </div>

          {seqResult && (
            <div>
              {/* Alignment Banner */}
              <div className="card" style={{ padding: '1.5rem', marginBottom: '1.5rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                  <div>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Alignment Similarity</span>
                    <div style={{ fontSize: '1.6rem', fontWeight: 800, color: 'var(--primary)' }}>
                      {seqResult.similarityPercent}% Match
                    </div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Optimal Alignment Score</span>
                    <div style={{ fontSize: '1.6rem', fontWeight: 800, color: 'var(--success)' }}>
                      {seqResult.alignmentScore} pts
                    </div>
                  </div>
                </div>

                {/* Visual Alignment Comparison */}
                <div style={{ background: 'var(--bg-tertiary)', padding: '1.25rem', borderRadius: '8px', fontFamily: 'monospace', fontSize: '1.2rem', letterSpacing: '0.2em', overflowX: 'auto' }}>
                  <div style={{ color: 'var(--primary)', fontWeight: 700 }}>A: {seqResult.alignedA}</div>
                  <div style={{ color: 'var(--text-muted)' }}>   {seqResult.alignmentSymbols}</div>
                  <div style={{ color: 'var(--accent)', fontWeight: 700 }}>B: {seqResult.alignedB}</div>
                </div>
                <div style={{ display: 'flex', gap: '1.5rem', marginTop: '1rem', fontSize: '0.88rem' }}>
                  <span>Matches: <strong>{seqResult.matches}</strong></span>
                  <span>Substitutions: <strong>{seqResult.mismatches}</strong></span>
                  <span>Gaps/Indels: <strong>{seqResult.gaps}</strong></span>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default AdvancedDPPage;
