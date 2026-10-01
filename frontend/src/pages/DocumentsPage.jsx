import React, { useState, useEffect } from 'react';
import { Plus, Search, Filter, BookOpen, FileText, Check, X, UploadCloud, FileUp, CheckCircle2 } from 'lucide-react';
import DocumentCard from '../components/DocumentCard';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import { searchAPI } from '../api/apiService';

const DocumentsPage = () => {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters
  const [filterType, setFilterType] = useState('All');
  const [filterCategory, setFilterCategory] = useState('All');
  const [searchTerm, setSearchTerm] = useState('');

  // Add Document Modal State
  const [modalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    category: 'Computer Science',
    year: '2026',
    isbn: '978-013110' + Math.floor(1000 + Math.random() * 9000),
    documentType: 'Research Paper',
    keywords: '',
    abstractText: '',
    content: '',
  });
  const [saving, setSaving] = useState(false);

  // File Upload State
  const [uploadedFile, setUploadedFile] = useState(null);
  const [uploadSuccessMessage, setUploadSuccessMessage] = useState(null);
  const [isDragOver, setIsDragOver] = useState(false);
  const [uploadingDirect, setUploadingDirect] = useState(false);

  const resetModalState = () => {
    setFormData({
      title: '',
      author: '',
      category: 'Computer Science',
      year: '2026',
      isbn: '978-013110' + Math.floor(1000 + Math.random() * 9000),
      documentType: 'Research Paper',
      keywords: '',
      abstractText: '',
      content: '',
    });
    setUploadedFile(null);
    setUploadSuccessMessage(null);
    setIsDragOver(false);
  };

  const processFile = (file) => {
    if (!file) return;
    setUploadedFile(file);
    const reader = new FileReader();
    reader.onload = (e) => {
      const text = e.target.result;
      const lines = text.split('\n');
      let parsed = {
        title: '',
        author: '',
        category: 'Computer Science',
        year: '2026',
        isbn: '978-013110' + Math.floor(1000 + Math.random() * 9000),
        documentType: 'Research Paper',
        keywords: '',
        abstractText: '',
        content: '',
      };
      let contentLines = [];
      let hasLabels = false;

      for (let line of lines) {
        const trimmed = line.trim();
        if (trimmed.startsWith('Title:')) {
          parsed.title = trimmed.replace(/^Title:\s*/, '');
          hasLabels = true;
        } else if (trimmed.startsWith('Author:')) {
          parsed.author = trimmed.replace(/^Author:\s*/, '');
          hasLabels = true;
        } else if (trimmed.startsWith('Category:')) {
          parsed.category = trimmed.replace(/^Category:\s*/, '');
          hasLabels = true;
        } else if (trimmed.startsWith('Year:')) {
          parsed.year = trimmed.replace(/^Year:\s*/, '');
          hasLabels = true;
        } else if (trimmed.startsWith('ISBN:')) {
          parsed.isbn = trimmed.replace(/^ISBN:\s*/, '');
          hasLabels = true;
        } else if (trimmed.startsWith('Type:')) {
          parsed.documentType = trimmed.replace(/^Type:\s*/, '');
          hasLabels = true;
        } else if (trimmed.startsWith('Keywords:')) {
          parsed.keywords = trimmed.replace(/^Keywords:\s*/, '');
          hasLabels = true;
        } else if (trimmed.startsWith('Abstract:')) {
          parsed.abstractText = trimmed.replace(/^Abstract:\s*/, '');
          hasLabels = true;
        } else if (trimmed.startsWith('Content:')) {
          contentLines.push(trimmed.replace(/^Content:\s*/, ''));
          hasLabels = true;
        } else {
          contentLines.push(line);
        }
      }

      if (hasLabels) {
        parsed.content = contentLines.join('\n').trim();
      } else {
        const cleanTitle = file.name.replace(/\.[^/.]+$/, '').replace(/_/g, ' ');
        parsed.title = cleanTitle.charAt(0).toUpperCase() + cleanTitle.slice(1);
        parsed.content = text.trim();
        parsed.abstractText = text.substring(0, 180).trim() + (text.length > 180 ? '...' : '');
      }

      setFormData(parsed);
      setUploadSuccessMessage(`Successfully parsed "${file.name}" (${(file.size / 1024).toFixed(1)} KB). Form pre-filled below.`);
    };
    reader.readAsText(file);
  };

  const handleDirectUploadAndSave = async () => {
    if (!uploadedFile) return;
    setUploadingDirect(true);
    try {
      await searchAPI.uploadDocumentFile(uploadedFile);
      setModalOpen(false);
      resetModalState();
      fetchDocuments();
    } catch (err) {
      alert('Failed to upload document file: ' + err.message);
    } finally {
      setUploadingDirect(false);
    }
  };

  const fetchDocuments = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.getAllDocuments();
      setDocuments(res.data || []);
    } catch (err) {
      console.error(err);
      setError('Could not fetch documents from server. Make sure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDocuments();
  }, []);

  const handleCreateDocument = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await searchAPI.createDocument(formData);
      setModalOpen(false);
      resetModalState();
      fetchDocuments();
    } catch (err) {
      alert('Failed to save document: ' + err.message);
    } finally {
      setSaving(false);
    }
  };

  // Derive categories
  const categories = ['All', ...new Set(documents.map((d) => d.category).filter(Boolean))];

  // Filtered documents
  const filtered = documents.filter((doc) => {
    const matchType = filterType === 'All' || doc.documentType?.toLowerCase() === filterType.toLowerCase();
    const matchCat = filterCategory === 'All' || doc.category?.toLowerCase() === filterCategory.toLowerCase();
    const matchSearch = !searchTerm ||
      doc.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      doc.author?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      doc.keywords?.toLowerCase().includes(searchTerm.toLowerCase());
    return matchType && matchCat && matchSearch;
  });

  return (
    <div>
      {/* Page Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '2rem', marginBottom: '0.4rem' }}>
            Library <span className="text-gradient">Document Repository</span>
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
            Browse and manage {documents.length} cataloged academic books, journals, research papers, and magazines.
          </p>
        </div>

        <button onClick={() => setModalOpen(true)} className="btn btn-primary">
          <Plus size={18} />
          <span>Add New Document</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div style={{
        background: 'var(--bg-card)',
        border: '1px solid var(--border-subtle)',
        borderRadius: 'var(--radius-lg)',
        padding: '1.25rem',
        marginBottom: '2rem',
        display: 'flex',
        flexWrap: 'wrap',
        gap: '1rem',
        alignItems: 'center',
        justifyContent: 'space-between',
      }}>
        {/* Quick Filter Search */}
        <div style={{ position: 'relative', flex: '1', minWidth: '220px' }}>
          <Search size={18} style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
          <input
            type="text"
            className="input-control"
            placeholder="Filter by title, author, or keyword..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ paddingLeft: '2.5rem' }}
          />
        </div>

        {/* Type Select */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Type:</span>
          <select
            className="select-control"
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
          >
            <option value="All">All Types</option>
            <option value="Book">Books</option>
            <option value="Research Paper">Research Papers</option>
            <option value="Journal">Journals</option>
            <option value="Magazine">Magazines</option>
          </select>
        </div>

        {/* Category Select */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Category:</span>
          <select
            className="select-control"
            value={filterCategory}
            onChange={(e) => setFilterCategory(e.target.value)}
          >
            {categories.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
        </div>
      </div>

      {loading && <LoadingSpinner message="Fetching documents from library repository..." />}

      {error && <ErrorMessage message={error} onRetry={fetchDocuments} />}

      {/* Document Grid */}
      {!loading && !error && (
        <>
          <div style={{ marginBottom: '1rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
            Showing <strong>{filtered.length}</strong> of {documents.length} documents
          </div>

          <div className="grid-cols-2">
            {filtered.map((doc) => (
              <DocumentCard key={doc.id} item={doc} isSearchResult={false} />
            ))}
          </div>
        </>
      )}

      {/* Add Document Modal */}
      {modalOpen && (
        <div style={{
          position: 'fixed',
          inset: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.75)',
          backdropFilter: 'blur(8px)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 200,
          padding: '1rem',
        }}>
          <div style={{
            background: 'var(--bg-secondary)',
            border: '1px solid var(--border-accent)',
            borderRadius: 'var(--radius-xl)',
            width: '100%',
            maxWidth: '680px',
            maxHeight: '90vh',
            overflowY: 'auto',
            padding: '2rem',
            position: 'relative',
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
              <div>
                <h2 style={{ fontSize: '1.4rem', color: 'var(--text-primary)' }}>Add New Library Document</h2>
                <p style={{ fontSize: '0.82rem', color: 'var(--text-muted)' }}>
                  Upload a document file or fill out metadata manually.
                </p>
              </div>
              <button
                onClick={() => { setModalOpen(false); resetModalState(); }}
                style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', padding: '0.25rem' }}
                aria-label="Close modal"
              >
                <X size={20} />
              </button>
            </div>

            {/* File Upload Dropzone */}
            <div style={{ marginBottom: '1.5rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
                <span style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Upload File (.txt, .md, .json)
                </span>
                {uploadedFile && (
                  <button
                    type="button"
                    onClick={() => {
                      setUploadedFile(null);
                      setUploadSuccessMessage(null);
                    }}
                    style={{ background: 'transparent', border: 'none', color: 'var(--accent-rose)', cursor: 'pointer', fontSize: '0.8rem', fontWeight: 600 }}
                  >
                    Clear File
                  </button>
                )}
              </div>

              <div
                className={`file-dropzone ${isDragOver ? 'dragover' : ''}`}
                onDragOver={(e) => { e.preventDefault(); setIsDragOver(true); }}
                onDragLeave={() => setIsDragOver(false)}
                onDrop={(e) => {
                  e.preventDefault();
                  setIsDragOver(false);
                  if (e.dataTransfer.files && e.dataTransfer.files[0]) {
                    processFile(e.dataTransfer.files[0]);
                  }
                }}
              >
                <input
                  type="file"
                  accept=".txt,.md,.json,.csv,text/*"
                  onChange={(e) => {
                    if (e.target.files && e.target.files[0]) {
                      processFile(e.target.files[0]);
                    }
                  }}
                />
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '0.4rem' }}>
                  <UploadCloud size={32} color="var(--accent-cyan)" />
                  <div style={{ fontSize: '0.92rem', fontWeight: 600, color: 'var(--text-primary)' }}>
                    {uploadedFile ? uploadedFile.name : 'Drag & drop document here or click to browse'}
                  </div>
                  <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                    Automatically parses Title, Author, Category, Abstract, and Full Content
                  </div>
                </div>
              </div>

              {uploadSuccessMessage && (
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  marginTop: '0.5rem',
                  padding: '0.5rem 0.75rem',
                  background: 'rgba(16, 185, 129, 0.1)',
                  border: '1px solid rgba(16, 185, 129, 0.25)',
                  borderRadius: 'var(--radius-sm)',
                  fontSize: '0.82rem',
                  color: 'var(--accent-emerald)',
                }}>
                  <CheckCircle2 size={16} style={{ flexShrink: 0 }} />
                  <span>{uploadSuccessMessage}</span>
                </div>
              )}
            </div>

            <form onSubmit={handleCreateDocument} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-secondary)' }}>Document Title *</label>
                <input
                  required
                  className="input-control"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="e.g., Advanced String Matching and Suffix Structures"
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-secondary)' }}>Author(s) *</label>
                  <input
                    required
                    className="input-control"
                    value={formData.author}
                    onChange={(e) => setFormData({ ...formData, author: e.target.value })}
                    placeholder="e.g., Robert Sedgewick, Kevin Wayne"
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-secondary)' }}>Document Type</label>
                  <select
                    className="select-control"
                    style={{ width: '100%' }}
                    value={formData.documentType}
                    onChange={(e) => setFormData({ ...formData, documentType: e.target.value })}
                  >
                    <option value="Research Paper">Research Paper</option>
                    <option value="Book">Book</option>
                    <option value="Journal">Journal</option>
                    <option value="Magazine">Magazine</option>
                  </select>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-secondary)' }}>Category</label>
                  <input
                    className="input-control"
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    placeholder="e.g., Algorithms"
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-secondary)' }}>Year</label>
                  <input
                    className="input-control"
                    value={formData.year}
                    onChange={(e) => setFormData({ ...formData, year: e.target.value })}
                    placeholder="e.g., 2026"
                  />
                </div>
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-secondary)' }}>Keywords (Comma-separated)</label>
                <input
                  className="input-control"
                  value={formData.keywords}
                  onChange={(e) => setFormData({ ...formData, keywords: e.target.value })}
                  placeholder="e.g., pattern matching, linear search, prefix table"
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-secondary)' }}>Abstract</label>
                <textarea
                  className="input-control"
                  rows={3}
                  value={formData.abstractText}
                  onChange={(e) => setFormData({ ...formData, abstractText: e.target.value })}
                  placeholder="Summary of document purpose and contributions..."
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-secondary)' }}>Full Content *</label>
                <textarea
                  required
                  className="input-control"
                  rows={5}
                  value={formData.content}
                  onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                  placeholder="Detailed text content for full-text pattern matching and indexing..."
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem', marginTop: '1rem', paddingTop: '1rem', borderTop: '1px solid var(--border-subtle)' }}>
                {uploadedFile ? (
                  <button
                    type="button"
                    onClick={handleDirectUploadAndSave}
                    disabled={uploadingDirect || saving}
                    className="btn btn-secondary"
                    style={{ borderColor: 'var(--accent-cyan)', color: 'var(--accent-cyan)' }}
                  >
                    <FileUp size={16} />
                    <span>{uploadingDirect ? 'Uploading...' : 'Direct Upload & Save'}</span>
                  </button>
                ) : <div />}

                <div style={{ display: 'flex', gap: '0.75rem' }}>
                  <button
                    type="button"
                    onClick={() => { setModalOpen(false); resetModalState(); }}
                    className="btn btn-secondary"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={saving || uploadingDirect}
                    className="btn btn-primary"
                  >
                    {saving ? 'Saving...' : 'Add to Repository'}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default DocumentsPage;
