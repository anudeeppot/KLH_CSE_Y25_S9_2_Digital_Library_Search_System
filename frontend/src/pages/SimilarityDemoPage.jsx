import React, { useState } from 'react';
import { Play, Sparkles, FileText, CheckCircle, Clock, Code2 } from 'lucide-react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';

const SimilarityDemoPage = () => {
  const [docA, setDocA] = useState('Machine learning algorithms and deep neural networks in artificial intelligence and data science.');
  const [docB, setDocB] = useState('Artificial intelligence systems utilizing deep learning, machine learning, and neural network models.');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showCode, setShowCode] = useState(false);

  const handleRun = async (e) => {
    if (e) e.preventDefault();
    if (!docA || !docB) return;

    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.testSimilarity({ text: docA, target: docB });
      setResult(res.data);
    } catch (err) {
      console.error(err);
      setError('Failed to compute similarity: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'inline-block', marginBottom: '0.5rem' }}>
          <span className="badge badge-emerald">Module 1 • Vector Space Model</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', marginBottom: '0.5rem' }}>
          Document Similarity <span className="text-gradient">& Cosine Vector Space</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '800px', fontSize: '0.98rem', lineHeight: 1.6 }}>
          Calculates the thematic relatedness between two documents by tokenizing text, computing term frequencies (TF),
          filtering stop words, and evaluating the <strong>Cosine Angle</strong> between high-dimensional vector representations.
        </p>
      </div>

      {/* Input Form */}
      <div className="glass-card" style={{ marginBottom: '2.5rem' }}>
        <form onSubmit={handleRun}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '1.5rem' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                Document A (Text / Abstract):
              </label>
              <textarea
                className="input-control"
                rows={4}
                value={docA}
                onChange={(e) => setDocA(e.target.value)}
                placeholder="Enter text of first document..."
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                Document B (Text / Abstract):
              </label>
              <textarea
                className="input-control"
                rows={4}
                value={docB}
                onChange={(e) => setDocB(e.target.value)}
                placeholder="Enter text of second document..."
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <button type="submit" disabled={loading} className="btn btn-primary" style={{ padding: '0.75rem 2rem' }}>
              <Play size={16} />
              <span>Compute Cosine Similarity</span>
            </button>
          </div>
        </form>
      </div>

      {loading && <LoadingSpinner message="Tokenizing text and calculating vector dot products in Java..." />}
      {error && <ErrorMessage message={error} onRetry={handleRun} />}

      {/* Results */}
      {result && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem', marginBottom: '3rem' }}>
          {/* Metrics */}
          <div className="glass-card" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.5rem', textAlign: 'center' }}>
            <div>
              <div style={{ fontSize: '2.4rem', fontWeight: 800, color: 'var(--accent-emerald)' }}>
                {result.percentage}%
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Cosine Similarity Percentage</div>
            </div>

            <div>
              <div style={{ fontSize: '2.4rem', fontWeight: 800, color: 'var(--accent-cyan)' }}>
                {result.score.toFixed(4)}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Normalized Vector Cosine</div>
            </div>

            <div>
              <div style={{ fontSize: '2.4rem', fontWeight: 800, color: 'var(--accent-purple)' }}>
                {result.sharedKeywords.length}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Dominant Common Terms</div>
            </div>

            <div>
              <div style={{ fontSize: '2.4rem', fontWeight: 800, color: 'var(--accent-amber)' }}>
                {(result.executionTime / 1_000_000.0).toFixed(3)} ms
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Execution Time</div>
            </div>
          </div>

          {/* Shared Key Terms */}
          {result.sharedKeywords && result.sharedKeywords.length > 0 && (
            <div className="glass-card">
              <h3 style={{ fontSize: '1.15rem', marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Sparkles size={18} color="var(--accent-emerald)" /> Dominant Thematic Overlap Keywords
              </h3>
              <div style={{ display: 'flex', gap: '0.6rem', flexWrap: 'wrap' }}>
                {result.sharedKeywords.map((kw, i) => (
                  <span
                    key={i}
                    style={{
                      background: 'rgba(16, 185, 129, 0.15)',
                      border: '1px solid rgba(16, 185, 129, 0.3)',
                      color: 'var(--accent-emerald)',
                      padding: '0.4rem 0.85rem',
                      borderRadius: '9999px',
                      fontWeight: 600,
                      fontSize: '0.9rem',
                    }}
                  >
                    #{kw}
                  </span>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Theory Card */}
      <div className="glass-card" style={{ borderLeft: '4px solid var(--accent-emerald)', marginBottom: '2.5rem' }}>
        <h3 style={{ fontSize: '1.25rem', marginBottom: '0.75rem' }}>
          Vector Space Model & Mathematical Formulations (Viva Review)
        </h3>

        <div className="visualizer-box" style={{ marginBottom: '1.25rem', color: '#a7f3d0' }}>
          {`Cosine Similarity Formulation:
cos(θ) = (A · B) / (||A|| * ||B||)
       = Σ (TF_A(t) * TF_B(t)) / [ sqrt(Σ TF_A(t)^2) * sqrt(Σ TF_B(t)^2) ]

Where:
TF(t, d) = frequency of term t in document d
t ∈ Vocabulary (V)`}
        </div>

        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.6 }}>
          In the Digital Library Search System, this algorithm evaluates every candidate publication against the currently viewed document.
          If the cosine angle yields cosine similarity &gt; 0.05 (or &gt; 5%), the document is designated as a <strong>Related Resource</strong>.
        </p>
      </div>
    </div>
  );
};

export default SimilarityDemoPage;
