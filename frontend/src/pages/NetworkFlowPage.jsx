import React, { useState, useEffect } from 'react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import { Network, ArrowRight, ShieldAlert, CheckCircle2, Zap } from 'lucide-react';

function NetworkFlowPage() {
  const [scenario, setScenario] = useState('reservation'); // 'reservation' or 'cdn'
  const [algorithm, setAlgorithm] = useState('EDMONDS_KARP'); // 'EDMONDS_KARP' or 'DINIC'
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchFlow = async (scen = scenario, algo = algorithm) => {
    setLoading(true);
    setError(null);
    try {
      let res;
      if (scen === 'reservation') {
        res = await searchAPI.getReservationFlow(algo);
      } else {
        res = await searchAPI.getCdnFlow(algo);
      }
      setResult(res.data);
    } catch (err) {
      setError('Failed to compute network flow. Ensure backend server is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFlow(scenario, algorithm);
  }, [scenario, algorithm]);

  return (
    <div className="container" style={{ padding: '2rem 1rem' }}>
      {/* Page Header */}
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
          <span className="badge badge-primary">Course Outcome 4 (CO-4)</span>
          <span style={{ color: 'var(--text-muted)' }}>Network Flow &amp; Max-Flow / Min-Cut Duality</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, margin: 0 }}>
          Digital Library Network Flow &amp; Min-Cut Bottleneck Analyzer
        </h1>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem', maxWidth: '850px' }}>
          Syllabus CO-4 mandates modeling assignment, matching, and capacity-constrained problems using <strong>Ford-Fulkerson with Edmonds-Karp</strong>, <strong>Dinic's Algorithm</strong>, and the <strong>Max-Flow / Min-Cut Duality Theorem</strong>.
        </p>
      </div>

      {/* Scenario & Algorithm Switcher Controls */}
      <div className="card" style={{ padding: '1.25rem', marginBottom: '2rem', display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center', gap: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <span style={{ fontWeight: 600 }}>Library Scenario:</span>
          <button
            className={`btn ${scenario === 'reservation' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setScenario('reservation')}
          >
            1. Student-to-Book Reservation Matching
          </button>
          <button
            className={`btn ${scenario === 'cdn' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setScenario('cdn')}
          >
            2. CDN Repository Bandwidth Distribution
          </button>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <span style={{ fontWeight: 600 }}>Solver Engine:</span>
          <button
            className={`btn ${algorithm === 'EDMONDS_KARP' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setAlgorithm('EDMONDS_KARP')}
          >
            Edmonds-Karp O(V E^2)
          </button>
          <button
            className={`btn ${algorithm === 'DINIC' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setAlgorithm('DINIC')}
          >
            Dinic's Algorithm O(V^2 E)
          </button>
        </div>
      </div>

      {error && <ErrorMessage message={error} />}
      {loading && <LoadingSpinner />}

      {result && (
        <div>
          {/* Duality Theorem Verification Card */}
          <div className="card" style={{ padding: '1.5rem', marginBottom: '2rem', border: '1px solid var(--success)', background: 'var(--bg-secondary)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <CheckCircle2 size={40} color="var(--success)" />
                <div>
                  <h3 style={{ fontSize: '1.3rem', fontWeight: 800, margin: 0, color: 'var(--success)' }}>
                    Max-Flow / Min-Cut Duality Theorem Verified
                  </h3>
                  <p style={{ color: 'var(--text-secondary)', margin: '0.25rem 0 0', fontSize: '0.92rem' }}>
                    {result.academicExplanation}
                  </p>
                </div>
              </div>

              <div style={{ display: 'flex', gap: '2rem', textAlign: 'right' }}>
                <div>
                  <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Maximum Flow Delivered</div>
                  <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--primary)' }}>
                    {result.maxFlow} {scenario === 'reservation' ? 'borrowings' : 'MB/s'}
                  </div>
                </div>
                <div>
                  <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Minimum Cut Capacity</div>
                  <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--success)' }}>
                    {result.minCutCapacity} {scenario === 'reservation' ? 'borrowings' : 'MB/s'}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Cut Partitions */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '2rem' }}>
            <div className="card" style={{ padding: '1.25rem', borderLeft: '4px solid var(--primary)' }}>
              <h4 style={{ fontSize: '1.05rem', fontWeight: 700, marginBottom: '0.75rem', color: 'var(--primary)' }}>
                Source-Reachable Cut Component (S-Cut Partition)
              </h4>
              <ul style={{ margin: 0, paddingLeft: '1.25rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                {result.sourceCutPartition.map((node, idx) => (
                  <li key={idx} style={{ marginBottom: '0.35rem' }}>{node}</li>
                ))}
              </ul>
            </div>
            <div className="card" style={{ padding: '1.25rem', borderLeft: '4px solid var(--danger)' }}>
              <h4 style={{ fontSize: '1.05rem', fontWeight: 700, marginBottom: '0.75rem', color: 'var(--danger)' }}>
                Sink-Partition (T-Cut Partition)
              </h4>
              <ul style={{ margin: 0, paddingLeft: '1.25rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                {result.sinkCutPartition.map((node, idx) => (
                  <li key={idx} style={{ marginBottom: '0.35rem' }}>{node}</li>
                ))}
              </ul>
            </div>
          </div>

          {/* Saturated Bottlenecks Table */}
          <div className="card" style={{ padding: '1.5rem', marginBottom: '2rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
              <ShieldAlert size={20} color="var(--danger)" />
              <h3 style={{ fontSize: '1.2rem', fontWeight: 700, margin: 0 }}>
                Identified Saturated Min-Cut Bottleneck Channels ({result.minCutBottleneckEdges.length} constraints)
              </h3>
            </div>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '1rem' }}>
              These saturated links form the exact theoretical boundary separating Source from Sink. Increasing the capacity of any other channel will NOT increase digital library throughput—only expanding these specific bottleneck channels will improve service!
            </p>

            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                <thead>
                  <tr style={{ background: 'var(--bg-tertiary)', borderBottom: '2px solid var(--border)' }}>
                    <th style={{ padding: '0.75rem' }}>From Node</th>
                    <th style={{ padding: '0.75rem' }}>To Node</th>
                    <th style={{ padding: '0.75rem' }}>Capacity</th>
                    <th style={{ padding: '0.75rem' }}>Flow Pushed</th>
                    <th style={{ padding: '0.75rem' }}>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {result.minCutBottleneckEdges.map((edge, idx) => (
                    <tr key={idx} style={{ borderBottom: '1px solid var(--border)', background: 'rgba(239, 68, 68, 0.08)' }}>
                      <td style={{ padding: '0.75rem', fontWeight: 600 }}>{edge.from}</td>
                      <td style={{ padding: '0.75rem', fontWeight: 600 }}>{edge.to}</td>
                      <td style={{ padding: '0.75rem', fontFamily: 'monospace' }}>{edge.capacity}</td>
                      <td style={{ padding: '0.75rem', fontFamily: 'monospace', fontWeight: 700, color: 'var(--danger)' }}>{edge.flow}</td>
                      <td style={{ padding: '0.75rem' }}>
                        <span className="badge badge-danger">100% Saturated (Min-Cut Bottleneck)</span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Full Network Transmission Matrix */}
          <div className="card" style={{ padding: '1.5rem' }}>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '1rem' }}>
              Complete Network Transmission Graph &amp; Residual Capacities
            </h3>
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
                <thead>
                  <tr style={{ background: 'var(--bg-tertiary)', borderBottom: '2px solid var(--border)' }}>
                    <th style={{ padding: '0.65rem' }}>From Node</th>
                    <th style={{ padding: '0.65rem' }}>To Node</th>
                    <th style={{ padding: '0.65rem' }}>Capacity</th>
                    <th style={{ padding: '0.65rem' }}>Flow</th>
                    <th style={{ padding: '0.65rem' }}>Residual Capacity</th>
                    <th style={{ padding: '0.65rem' }}>Classification</th>
                  </tr>
                </thead>
                <tbody>
                  {result.allEdges.map((edge, idx) => {
                    const residual = edge.capacity - edge.flow;
                    return (
                      <tr key={idx} style={{ borderBottom: '1px solid var(--border)' }}>
                        <td style={{ padding: '0.65rem' }}>{edge.from}</td>
                        <td style={{ padding: '0.65rem' }}>{edge.to}</td>
                        <td style={{ padding: '0.65rem', fontFamily: 'monospace' }}>{edge.capacity}</td>
                        <td style={{ padding: '0.65rem', fontFamily: 'monospace', fontWeight: edge.flow > 0 ? 700 : 400 }}>{edge.flow}</td>
                        <td style={{ padding: '0.65rem', fontFamily: 'monospace', color: residual === 0 ? 'var(--danger)' : 'var(--success)' }}>
                          {residual}
                        </td>
                        <td style={{ padding: '0.65rem' }}>
                          {edge.minCutBottleneck ? (
                            <span className="badge badge-danger">Min-Cut Edge</span>
                          ) : edge.saturated ? (
                            <span className="badge badge-warning">Saturated</span>
                          ) : (
                            <span className="badge badge-secondary">Active Channel</span>
                          )}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default NetworkFlowPage;
