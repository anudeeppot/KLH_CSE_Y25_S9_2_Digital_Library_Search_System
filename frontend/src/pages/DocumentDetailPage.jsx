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
  Maximize2,
  Minimize2,
  Search,
  Clock,
  Layers
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
  const [isExpanded, setIsExpanded] = useState(false);
  const [inDocQuery, setInDocQuery] = useState('');

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

  if (loading) return <LoadingSpinner message="Retrieving full document text and computing vector similarities..." />;
  if (error || !document) return <ErrorMessage message={error || 'Document not found'} onRetry={() => window.location.reload()} />;

  const wordCount = document.content ? document.content.split(/\s+/).filter(Boolean).length : 0;
  const charCount = document.content ? document.content.length : 0;
  const readTimeMin = Math.max(1, Math.ceil(wordCount / 200));

  // In-document highlighting helper
  const renderHighlightedContent = () => {
    if (!document.content) return 'No content available.';
    if (!inDocQuery.trim()) {
      return document.content;
    }

    const escapedQuery = inDocQuery.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const regex = new RegExp(`(${escapedQuery})`, 'gi');
    const parts = document.content.split(regex);

    return parts.map((part, index) =>
      regex.test(part) ? (
        <mark key={index} style={{ backgroundColor: 'rgba(234, 179, 8, 0.45)', color: '#fff', padding: '0 2px', borderRadius: '3px' }}>
          {part}
        </mark>
      ) : (
        part
      )
    );
  };

  return (
    <div className="container" style={{ padding: '2rem 1rem' }}>
      {/* Back button */}
      <button
        onClick={() => navigate(-1)}
        className="btn btn-outline"
        style={{ marginBottom: '1.5rem', display: 'inline-flex', alignItems: 'center', gap: '0.4rem' }}
      >
        <ArrowLeft size={16} />
        <span>Back to Catalog</span>
      </button>

      {/* Main Document Details Card */}
      <div className="card" style={{ padding: '2rem', marginBottom: '2.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.25rem' }}>
          <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
            <span className="badge badge-primary">{document.documentType || 'Research Paper'}</span>
            <AlgorithmBadge algorithm={document.category || 'Computer Science'} />
          </div>

          {/* Quick Metrics */}
          <div style={{ display: 'flex', gap: '1.25rem', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
              <Layers size={14} /> {charCount.toLocaleString()} chars ({wordCount.toLocaleString()} words)
            </span>
            <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
              <Clock size={14} /> ~{readTimeMin} min read
            </span>
          </div>
        </div>

        <h1 style={{ fontSize: '2rem', fontWeight: 800, marginBottom: '0.75rem', lineHeight: 1.3 }}>
          {document.title}
        </h1>

        <div style={{
          display: 'flex',
          flexWrap: 'wrap',
          gap: '1.5rem',
          fontSize: '0.9rem',
          color: 'var(--text-secondary)',
          marginBottom: '1.5rem',
          paddingBottom: '1.25rem',
          borderBottom: '1px solid var(--border)'
        }}>
          <div><strong>Author(s):</strong> {document.author}</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
            <Calendar size={15} /> <strong>Year:</strong> {document.year || '2026'}
          </div>
          {document.isbn && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
              <Hash size={15} /> <strong>ISBN/ID:</strong> {document.isbn}
            </div>
          )}
          <div><strong>Source File:</strong> <code>{document.fileName}</code></div>
        </div>

        {/* Abstract */}
        {document.abstractText && (
          <div style={{ marginBottom: '2rem', background: 'var(--bg-secondary)', padding: '1.25rem', borderRadius: '8px', borderLeft: '4px solid var(--primary)' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '0.5rem', color: 'var(--primary)' }}>
              Document Abstract &amp; Executive Summary
            </h3>
            <p style={{ color: 'var(--text-primary)', lineHeight: 1.7, fontSize: '0.95rem', margin: 0 }}>
              {document.abstractText}
            </p>
          </div>
        )}

        {/* Keywords */}
        {document.keywords && (
          <div style={{ marginBottom: '2rem' }}>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 600, marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Index Keywords</h4>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
              {document.keywords.split(',').map((kw, i) => (
                <span key={i} className="badge badge-secondary" style={{ fontSize: '0.8rem' }}>
                  <Tag size={12} style={{ display: 'inline', marginRight: '4px', verticalAlign: 'middle' }} />
                  {kw.trim()}
                </span>
              ))}
            </div>
          </div>
        )}

        {/* In-Document Real-Time Keyword Search Bar */}
        <div style={{ background: 'var(--bg-tertiary)', padding: '0.75rem 1rem', borderRadius: '8px', marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flex: 1 }}>
            <Search size={16} color="var(--primary)" />
            <input
              type="text"
              placeholder="Highlight terms inside this document (e.g. KMP, rolling hash, Dinic, Suffix)..."
              value={inDocQuery}
              onChange={(e) => setInDocQuery(e.target.value)}
              style={{
                background: 'transparent',
                border: 'none',
                color: 'inherit',
                width: '100%',
                outline: 'none',
                fontSize: '0.9rem'
              }}
            />
          </div>
          {inDocQuery && (
            <button
              onClick={() => setInDocQuery('')}
              className="btn btn-secondary"
              style={{ padding: '0.2rem 0.6rem', fontSize: '0.75rem' }}
            >
              Clear
            </button>
          )}
          <button
            onClick={() => setIsExpanded(!isExpanded)}
            className="btn btn-secondary"
            style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.8rem', padding: '0.35rem 0.75rem' }}
          >
            {isExpanded ? <Minimize2 size={14} /> : <Maximize2 size={14} />}
            <span>{isExpanded ? 'Collapse' : 'Full Reader'}</span>
          </button>
        </div>

        {/* Full Content Preview */}
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, margin: 0 }}>
              Full Academic Content ({wordCount.toLocaleString()} words)
            </h3>
          </div>
          <div
            className="visualizer-box"
            style={{
              maxHeight: isExpanded ? 'none' : '550px',
              overflowY: 'auto',
              whiteSpace: 'pre-wrap',
              lineHeight: '1.75',
              fontSize: '0.95rem',
              color: 'var(--text-primary)',
              background: 'var(--bg-secondary)',
              padding: '1.5rem',
              borderRadius: '8px',
              border: '1px solid var(--border)'
            }}
          >
            {renderHighlightedContent()}
          </div>
        </div>

        {/* Citations List if any */}
        {document.citations && document.citations.length > 0 && (
          <div style={{ marginTop: '2rem', paddingTop: '1.5rem', borderTop: '1px solid var(--border)' }}>
            <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--primary)' }}>
              <Compass size={18} /> Outgoing Academic Citations ({document.citations.length})
            </h4>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
              {document.citations.map((c, i) => (
                <span key={i} className="badge badge-secondary" style={{ fontFamily: 'monospace' }}>
                  &bull; {c}
                </span>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Related Resources (TF-IDF Similarity Module) */}
      <section style={{ marginBottom: '3rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
          <div>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 800, margin: 0 }}>
              Thematic Related Resources <span className="text-gradient">(TF-IDF Vector Space)</span>
            </h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem', margin: '0.25rem 0 0 0' }}>
              Ranked via Cosine Similarity between high-dimensional document term frequency vectors.
            </p>
          </div>
        </div>

        {similarDocs.length === 0 ? (
          <div className="card" style={{ padding: '1.5rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            No related documents calculated for this entry.
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1.25rem' }}>
            {similarDocs.map((sim, index) => (
              <div key={index} className="card" style={{ padding: '1.25rem', display: 'flex', flexDirection: 'column' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                  <span className="badge badge-success">{Math.round(sim.similarityScore * 100)}% Similarity</span>
                  <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{sim.category}</span>
                </div>
                <h4 style={{ fontSize: '1.05rem', fontWeight: 700, margin: '0.25rem 0 0.5rem 0' }}>
                  {sim.title}
                </h4>
                <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.75rem' }}>
                  Author: {sim.author}
                </div>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '1rem', marginTop: 'auto' }}>
                  Shared Terms: {sim.sharedKeywords ? sim.sharedKeywords.slice(0, 4).join(', ') : 'Lexical context'}
                </div>
                <Link to={`/documents/${sim.id}`} className="btn btn-outline" style={{ fontSize: '0.82rem', textAlign: 'center' }}>
                  Open Document
                </Link>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default DocumentDetailPage;
