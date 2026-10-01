import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Calendar,
  Hash,
  BookOpen,
  FileCode,
  Tag,
  Compass,
  Cpu,
  Sparkles,
  ExternalLink
} from 'lucide-react';
import AlgorithmBadge from '../components/AlgorithmBadge';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import { searchAPI } from '../api/apiService';

const DocumentDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [document, setDocument] = useState(null);
  const [similarDocs, setSimilarDocs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDocAndSimilar = async () => {
      setLoading(true);
      setError(null);
      try {
        const docRes = await searchAPI.getDocumentById(id);
        setDocument(docRes.data);

        // Fetch related similar documents calculated via TF-IDF + Cosine Similarity
        const simRes = await searchAPI.getSimilarDocuments(id);
        setSimilarDocs(simRes.data || []);
      } catch (err) {
        console.error(err);
        setError('Document not found or backend service is unreachable.');
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchDocAndSimilar();
    }
  }, [id]);

  if (loading) return <LoadingSpinner message="Retrieving document metadata and calculating vector similarities..." />;
  if (error || !document) return <ErrorMessage message={error || 'Document not found'} onRetry={() => window.location.reload()} />;

  return (
    <div>
      {/* Back button */}
      <button
        onClick={() => navigate(-1)}
        className="btn btn-secondary"
        style={{ marginBottom: '1.5rem', display: 'inline-flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.85rem' }}
      >
        <ArrowLeft size={16} /> Back to Search / Documents
      </button>

      {/* Main Details Card */}
      <div className="glass-card" style={{ padding: '2.5rem', marginBottom: '2.5rem' }}>
        {/* Badges */}
        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', marginBottom: '1rem', flexWrap: 'wrap' }}>
          <AlgorithmBadge type={document.documentType} />
          <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Category: <strong style={{ color: 'var(--text-primary)' }}>{document.category}</strong></span>
          {document.year && (
            <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
              <Calendar size={14} /> Year: {document.year}
            </span>
          )}
          {document.isbn && (
            <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
              <Hash size={14} /> ISBN: {document.isbn}
            </span>
          )}
        </div>

        {/* Title */}
        <h1 style={{ fontSize: 'clamp(1.8rem, 3.5vw, 2.5rem)', lineHeight: 1.25, marginBottom: '0.75rem' }}>
          {document.title}
        </h1>

        {/* Authors */}
        <div style={{ fontSize: '1.1rem', color: 'var(--accent-cyan)', marginBottom: '1.5rem', fontWeight: 500 }}>
          {document.author}
        </div>

        {/* Action Buttons */}
        <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', marginBottom: '2rem', paddingBottom: '1.5rem', borderBottom: '1px solid var(--border-subtle)' }}>
          <Link
            to={`/algorithms/similarity?docA=${encodeURIComponent(document.title)}`}
            className="btn btn-primary"
          >
            <Sparkles size={16} />
            <span>Search Similar Documents</span>
          </Link>

          <Link
            to={`/search?q=${encodeURIComponent(document.title.split(' ')[0])}&algorithm=KMP`}
            className="btn btn-outline"
          >
            <Cpu size={16} />
            <span>KMP Text Match</span>
          </Link>
        </div>

        {/* Abstract */}
        {document.abstractText && (
          <div style={{ marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1.15rem', marginBottom: '0.6rem', color: 'var(--text-primary)' }}>Abstract</h3>
            <p style={{ color: 'var(--text-secondary)', lineHeight: 1.7, fontSize: '0.98rem' }}>
              {document.abstractText}
            </p>
          </div>
        )}

        {/* Keywords */}
        {document.keywords && (
          <div style={{ marginBottom: '2rem' }}>
            <h4 style={{ fontSize: '0.95rem', marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Index Keywords</h4>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
              {document.keywords.split(',').map((kw, i) => (
                <span
                  key={i}
                  style={{
                    background: 'rgba(255, 255, 255, 0.05)',
                    border: '1px solid var(--border-subtle)',
                    borderRadius: '6px',
                    padding: '0.25rem 0.65rem',
                    fontSize: '0.82rem',
                    color: 'var(--text-secondary)',
                  }}
                >
                  <Tag size={12} style={{ display: 'inline', marginRight: '4px', verticalAlign: 'middle' }} />
                  {kw.trim()}
                </span>
              ))}
            </div>
          </div>
        )}

        {/* Full Content Preview */}
        <div>
          <h3 style={{ fontSize: '1.15rem', marginBottom: '0.6rem', color: 'var(--text-primary)' }}>Full Content Preview</h3>
          <div className="visualizer-box" style={{ maxHeight: '300px', overflowY: 'auto', whiteSpace: 'pre-wrap', color: '#cbd5e1' }}>
            {document.content}
          </div>
        </div>

        {/* Citations List if any */}
        {document.citations && document.citations.length > 0 && (
          <div style={{ marginTop: '2rem', paddingTop: '1.5rem', borderTop: '1px solid var(--border-subtle)' }}>
            <h4 style={{ fontSize: '1rem', marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--accent-indigo)' }}>
              <Compass size={18} /> Outgoing Citations ({document.citations.length})
            </h4>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
              {document.citations.map((c, i) => (
                <li key={i} style={{ fontSize: '0.88rem', color: 'var(--text-secondary)' }}>
                  • <span className="text-mono" style={{ color: 'var(--accent-cyan)' }}>{c}</span>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>

      {/* Related Resources (TF-IDF Similarity Module) */}
      <section style={{ marginBottom: '3rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '0.5rem' }}>
          <div>
            <h2 style={{ fontSize: '1.5rem', marginBottom: '0.2rem' }}>
              Related <span className="text-gradient">Resources</span>
            </h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem' }}>
              Calculated dynamically via <strong>TF-IDF & Cosine Similarity</strong> in vector space.
            </p>
          </div>
          <Link to="/algorithms/similarity" className="btn btn-outline" style={{ fontSize: '0.82rem' }}>
            Open Similarity Lab
          </Link>
        </div>

        {similarDocs.length > 0 ? (
          <div className="grid-cols-3">
            {similarDocs.map((sim) => (
              <div key={sim.id} className="glass-card" style={{ display: 'flex', flexDirection: 'column' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                  <AlgorithmBadge type={sim.documentType} />
                  <span style={{
                    fontSize: '0.82rem',
                    fontWeight: 700,
                    color: 'var(--accent-emerald)',
                    background: 'rgba(16, 185, 129, 0.1)',
                    padding: '0.2rem 0.5rem',
                    borderRadius: '4px',
                    border: '1px solid rgba(16, 185, 129, 0.25)',
                  }}>
                    {sim.similarityPercentage}% Match
                  </span>
                </div>

                <h4 style={{ fontSize: '1.05rem', lineHeight: 1.4, marginBottom: '0.4rem' }}>
                  <Link to={`/documents/${sim.id}`} style={{ color: 'var(--text-primary)' }}>
                    {sim.title}
                  </Link>
                </h4>

                <div style={{ fontSize: '0.82rem', color: 'var(--text-muted)', marginBottom: '0.8rem' }}>
                  {sim.author}
                </div>

                {sim.sharedKeywords && sim.sharedKeywords.length > 0 && (
                  <div style={{ marginTop: 'auto', paddingTop: '0.6rem', borderTop: '1px solid var(--border-subtle)', fontSize: '0.78rem', color: 'var(--text-secondary)' }}>
                    <strong>Shared Terms:</strong> {sim.sharedKeywords.join(', ')}
                  </div>
                )}
              </div>
            ))}
          </div>
        ) : (
          <div style={{ padding: '2rem', background: 'var(--bg-card)', borderRadius: 'var(--radius-md)', textAlign: 'center', color: 'var(--text-muted)' }}>
            No strong thematic similarity detected above 5% threshold.
          </div>
        )}
      </section>
    </div>
  );
};

export default DocumentDetailPage;
