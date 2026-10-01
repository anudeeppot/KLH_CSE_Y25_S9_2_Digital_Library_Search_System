import React, { useState } from 'react';
import { Play, Cpu, Zap, Activity, Clock, Layers } from 'lucide-react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';

const ParallelBenchmarkPage = () => {
  const [query, setQuery] = useState('algorithm');
  const [multiplier, setMultiplier] = useState(25);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleRun = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.runParallelBenchmark(query, multiplier);
      setResult(res.data);
    } catch (err) {
      console.error(err);
      setError('Parallel benchmark failed: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'inline-block', marginBottom: '0.5rem' }}>
          <span className="badge badge-blue">Module 6 • Concurrent Systems</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', marginBottom: '0.5rem' }}>
          Multi-Core Parallel Search <span className="text-gradient">& Amdahl's Law Benchmark</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '820px', fontSize: '0.98rem', lineHeight: 1.6 }}>
          Demonstrates concurrent full-text pattern searching across partitioned document collections using
          Java's <strong>ExecutorService</strong> thread pool compared to sequential execution.
        </p>
      </div>

      {/* Controls */}
      <div className="glass-card" style={{ marginBottom: '2.5rem' }}>
        <form onSubmit={handleRun}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr auto', gap: '1.25rem', alignItems: 'flex-end' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                Search Query:
              </label>
              <input
                type="text"
                className="input-control text-mono"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="e.g. algorithm"
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                Corpus Workload Multiplier: {multiplier}x ({multiplier * 20} documents)
              </label>
              <input
                type="range"
                min="5"
                max="50"
                value={multiplier}
                onChange={(e) => setMultiplier(parseInt(e.target.value))}
                style={{ width: '100%', accentColor: 'var(--accent-cyan)' }}
              />
            </div>

            <button type="submit" disabled={loading} className="btn btn-primary" style={{ padding: '0.75rem 1.75rem' }}>
              <Play size={16} />
              <span>Run Parallel Benchmark</span>
            </button>
          </div>
        </form>
      </div>

      {loading && <LoadingSpinner message="Dispatching tasks to ExecutorService thread pool in Java..." />}
      {error && <ErrorMessage message={error} onRetry={handleRun} />}

      {/* Results */}
      {result && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem', marginBottom: '3rem' }}>
          {/* Key Metrics */}
          <div className="glass-card" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1.5rem', textAlign: 'center' }}>
            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-cyan)' }}>
                {result.speedup}x
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Empirical Speedup Factor</div>
            </div>

            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-purple)' }}>
                {result.threadPoolSize} Cores
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Hardware Worker Threads</div>
            </div>

            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-rose)' }}>
                {(result.sequentialTimeNanos / 1_000_000.0).toFixed(2)} ms
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Sequential Scan Time</div>
            </div>

            <div>
              <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-emerald)' }}>
                {(result.parallelTimeNanos / 1_000_000.0).toFixed(2)} ms
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Parallel Scan Time</div>
            </div>
          </div>

          {/* Analysis Note */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.15rem', marginBottom: '0.5rem' }}>Benchmark Summary & Analysis</h3>
            <p style={{ color: 'var(--text-secondary)', lineHeight: 1.6, fontSize: '0.92rem' }}>
              {result.analysis}
            </p>
          </div>
        </div>
      )}

      {/* Theory Card */}
      <div className="glass-card" style={{ borderLeft: '4px solid var(--accent-blue)', marginBottom: '2.5rem' }}>
        <h3 style={{ fontSize: '1.25rem', marginBottom: '0.75rem' }}>
          Parallel Scalability & Amdahl's Law (Viva Review)
        </h3>

        <div className="visualizer-box" style={{ marginBottom: '1rem', color: '#bfdbfe' }}>
          {`Amdahl's Law Formula:
Speedup = 1 / [ (1 - P) + (P / N) ]

Where:
P = Proportion of program that is parallelizable (corpus document scanning)
1 - P = Serial proportion (thread scheduling, result aggregation)
N = Number of processor cores`}
        </div>

        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.6 }}>
          <strong>Academic Caveat:</strong> Speedup is never perfectly linear (equal to $N$) due to thread context switching overhead, cache coherency invalidation, and garbage collection pauses. As the dataset grows, the parallelizable portion $P$ approaches 1, allowing higher scaling efficiency.
        </p>
      </div>
    </div>
  );
};

export default ParallelBenchmarkPage;
