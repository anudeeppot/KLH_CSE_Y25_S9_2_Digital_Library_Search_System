import React from 'react';
import { Link } from 'react-router-dom';
import { Book, FileText, Bookmark, Calendar, ArrowRight, Hash, Compass } from 'lucide-react';
import AlgorithmBadge from './AlgorithmBadge';

const DocumentCard = ({ item, isSearchResult = false }) => {
  // Support both raw Document or SearchResultItem wrapping Document
  const doc = isSearchResult ? item.document : item;
  const matchedField = isSearchResult ? item.matchedField : null;
  const matchCount = isSearchResult ? item.matchCount : null;
  const snippet = isSearchResult ? item.highlightedSnippet : (doc.abstractText || doc.content);

  const truncatedSnippet = snippet && snippet.length > 180 ? snippet.substring(0, 180) + '...' : snippet;

  return (
    <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      {/* Header Badges */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem', gap: '0.5rem', flexWrap: 'wrap' }}>
        <div style={{ display: 'flex', gap: '0.4rem', alignItems: 'center' }}>
          <AlgorithmBadge type={doc.documentType || 'Document'} />
          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{doc.category}</span>
        </div>
        {matchedField && (
          <span style={{
            fontSize: '0.75rem',
            background: 'rgba(56, 189, 248, 0.12)',
            color: 'var(--accent-cyan)',
            padding: '0.2rem 0.5rem',
            borderRadius: '4px',
            fontWeight: 600,
            border: '1px solid rgba(56, 189, 248, 0.25)',
          }}>
            Matched: {matchedField} ({matchCount} hits)
          </span>
        )}
      </div>

      {/* Title */}
      <h3 style={{ fontSize: '1.15rem', lineHeight: 1.4, marginBottom: '0.5rem' }}>
        <Link to={`/documents/${doc.id}`} style={{ color: 'var(--text-primary)', transition: 'color 0.2s' }}>
          {doc.title}
        </Link>
      </h3>

      {/* Metadata */}
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem', fontSize: '0.82rem', color: 'var(--text-secondary)', marginBottom: '0.85rem' }}>
        <span><strong>Author:</strong> {doc.author}</span>
        {doc.year && (
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
            <Calendar size={13} /> {doc.year}
          </span>
        )}
        {doc.isbn && (
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
            <Hash size={13} /> {doc.isbn}
          </span>
        )}
      </div>

      {/* Snippet */}
      <p style={{
        fontSize: '0.88rem',
        color: 'var(--text-muted)',
        lineHeight: 1.6,
        marginBottom: '1.25rem',
        flex: 1,
      }}>
        {truncatedSnippet}
      </p>

      {/* Card Footer Actions */}
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingTop: '0.75rem',
        borderTop: '1px solid var(--border-subtle)',
        marginTop: 'auto',
      }}>
        <Link
          to={`/documents/${doc.id}`}
          className="btn btn-outline"
          style={{ fontSize: '0.82rem', padding: '0.45rem 0.9rem' }}
        >
          <span>View Details</span>
          <ArrowRight size={14} />
        </Link>

        {doc.citations && doc.citations.length > 0 && (
          <span style={{ fontSize: '0.78rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
            <Compass size={13} color="var(--accent-indigo)" />
            {doc.citations.length} Citations
          </span>
        )}
      </div>
    </div>
  );
};

export default DocumentCard;
