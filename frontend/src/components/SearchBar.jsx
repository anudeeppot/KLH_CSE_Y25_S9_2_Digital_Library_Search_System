import React, { useState } from 'react';
import { Search, SlidersHorizontal, Sparkles } from 'lucide-react';

const SearchBar = ({ initialQuery = '', initialAlgorithm = 'AUTO', onSearch, showFilters = true }) => {
  const [query, setQuery] = useState(initialQuery);
  const [algorithm, setAlgorithm] = useState(initialAlgorithm);
  const [docType, setDocType] = useState('All');
  const [category, setCategory] = useState('All');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (onSearch) {
      onSearch({
        query: query.trim(),
        algorithm,
        documentType: docType === 'All' ? '' : docType,
        category: category === 'All' ? '' : category,
      });
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ width: '100%' }}>
      <div className="search-bar-wrapper">
        <div className="search-input-box">
          <Search className="search-icon" size={20} />
          <input
            id="main-search-input"
            type="text"
            placeholder="Search books, research papers, algorithms, authors, content..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>

        {/* Algorithm Selection */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
          <SlidersHorizontal size={16} color="var(--text-muted)" />
          <select
            id="algo-select-dropdown"
            className="select-control"
            value={algorithm}
            onChange={(e) => setAlgorithm(e.target.value)}
            style={{ fontWeight: 600, color: 'var(--accent-cyan)' }}
          >
            <option value="AUTO">✨ Auto (DSA Optimal Strategy)</option>
            <option value="KMP">KMP (Knuth-Morris-Pratt)</option>
            <option value="RABIN_KARP">Rabin-Karp (Rolling Hash)</option>
            <option value="BOYER_MOORE">Boyer-Moore (Bad Character)</option>
            <option value="FUZZY">Fuzzy Search (Edit Distance DP)</option>
            <option value="SUFFIX_ARRAY">Suffix Array (Binary Index)</option>
          </select>
        </div>

        {showFilters && (
          <>
            {/* Document Type Filter */}
            <select
              id="doctype-select-dropdown"
              className="select-control"
              value={docType}
              onChange={(e) => setDocType(e.target.value)}
            >
              <option value="All">All Types</option>
              <option value="Book">Books</option>
              <option value="Research Paper">Research Papers</option>
              <option value="Journal">Journals</option>
              <option value="Magazine">Magazines</option>
            </select>
          </>
        )}

        {/* Search Action Button */}
        <button id="search-action-btn" type="submit" className="btn btn-primary" style={{ padding: '0.75rem 1.5rem' }}>
          <Search size={18} />
          <span>Search</span>
        </button>
      </div>
    </form>
  );
};

export default SearchBar;
