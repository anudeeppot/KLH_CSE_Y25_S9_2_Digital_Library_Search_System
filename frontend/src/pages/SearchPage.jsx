import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Search, Clock, Cpu, HelpCircle, Sparkles, Filter, AlertTriangle } from 'lucide-react';
import SearchBar from '../components/SearchBar';
import DocumentCard from '../components/DocumentCard';
import AlgorithmBadge from '../components/AlgorithmBadge';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import { searchAPI } from '../api/apiService';

const SearchPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const queryParam = searchParams.get('q') || '';
  const algoParam = searchParams.get('algorithm') || 'AUTO';
  const docTypeParam = searchParams.get('documentType') || '';
  const categoryParam = searchParams.get('category') || '';

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchResponse, setSearchResponse] = useState(null);

  const executeSearch = async (params) => {
    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.search({
        q: params.query,
        algorithm: params.algorithm,
        documentType: params.documentType,
        category: params.category,
      });
      setSearchResponse(res.data);
    } catch (err) {
      console.error('Search error:', err);
      setError('Failed to reach backend search service. Make sure the Spring Boot server is running on port 8080.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (queryParam) {
      executeSearch({
        query: queryParam,
        algorithm: algoParam,
        documentType: docTypeParam,
        category: categoryParam,
      });
    }
  }, [queryParam, algoParam, docTypeParam, categoryParam]);

  const handleSearchSubmit = (newParams) => {
    const updated = {};
    if (newParams.query) updated.q = newParams.query;
    if (newParams.algorithm) updated.algorithm = newParams.algorithm;
    if (newParams.documentType) updated.documentType = newParams.documentType;
    if (newParams.category) updated.category = newParams.category;
    setSearchParams(updated);
  };

  return (
    <div>
      {/* Top Search Controls Bar */}
      <div style={{ marginBottom: '2.5rem' }}>
        <h1 style={{ fontSize: '2rem', marginBottom: '1rem' }}>
          Interactive <span className="text-gradient">Library Search</span>
        </h1>
        <SearchBar
          initialQuery={queryParam}
          initialAlgorithm={algoParam}
          onSearch={handleSearchSubmit}
        />
      </div>

      {loading && <LoadingSpinner message="Scanning corpus using selected DSA search algorithm..." />}

      {error && <ErrorMessage message={error} onRetry={() => executeSearch({ query: queryParam, algorithm: algoParam })} />}

      {/* Search Metadata & Analytics Banner */}
      {!loading && searchResponse && (
        <div>
          <div style={{
            background: 'var(--bg-card)',
            backdropFilter: 'blur(12px)',
            border: '1px solid var(--border-subtle)',
            borderRadius: 'var(--radius-lg)',
            padding: '1.25rem 1.5rem',
            marginBottom: '2rem',
            display: 'flex',
            flexWrap: 'wrap',
            justifyContent: 'space-between',
            alignItems: 'center',
            gap: '1rem',
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', flexWrap: 'wrap' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                <Cpu size={16} color="var(--accent-cyan)" />
                <span style={{ fontSize: '0.88rem', color: 'var(--text-secondary)' }}>Algorithm Executed:</span>
                <AlgorithmBadge type={searchResponse.algorithmUsed} />
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.88rem', color: 'var(--text-secondary)' }}>
                <Clock size={16} color="var(--accent-emerald)" />
                <span>Execution Time: <strong style={{ color: 'var(--text-primary)' }}>{searchResponse.executionTimeMs} ms</strong> ({searchResponse.executionTimeNanos.toLocaleString()} ns)</span>
              </div>
            </div>

            <div style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Found <strong style={{ color: 'var(--accent-cyan)' }}>{searchResponse.totalResults}</strong> matching documents
            </div>
          </div>

          {/* Auto Algorithm Rationale Card */}
          {searchResponse.selectionRationale && (
            <div style={{
              background: 'rgba(99, 102, 241, 0.08)',
              border: '1px solid rgba(99, 102, 241, 0.25)',
              borderRadius: 'var(--radius-md)',
              padding: '0.85rem 1.25rem',
              marginBottom: '1.5rem',
              display: 'flex',
              alignItems: 'flex-start',
              gap: '0.75rem',
              fontSize: '0.88rem',
              color: '#c7d2fe',
            }}>
              <Sparkles size={18} color="var(--accent-indigo)" style={{ flexShrink: 0, marginTop: '2px' }} />
              <div>
                <strong>Algorithmic Decision Engine:</strong> {searchResponse.selectionRationale}
              </div>
            </div>
          )}

          {/* Did You Mean Suggestion (Fuzzy Search / Typo Detection) */}
          {searchResponse.didYouMean && searchResponse.didYouMean.toLowerCase() !== queryParam.toLowerCase() && (
            <div style={{
              background: 'rgba(245, 158, 11, 0.1)',
              border: '1px solid rgba(245, 158, 11, 0.3)',
              borderRadius: 'var(--radius-md)',
              padding: '0.85rem 1.25rem',
              marginBottom: '1.5rem',
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem',
              fontSize: '0.92rem',
              color: '#fde68a',
            }}>
              <HelpCircle size={18} color="var(--accent-amber)" />
              <span>
                Did you mean: {' '}
                <button
                  onClick={() => handleSearchSubmit({ query: searchResponse.didYouMean, algorithm: 'AUTO' })}
                  style={{
                    background: 'none',
                    border: 'none',
                    color: 'var(--accent-cyan)',
                    fontWeight: 700,
                    textDecoration: 'underline',
                    cursor: 'pointer',
                    fontSize: '0.95rem',
                  }}
                >
                  "{searchResponse.didYouMean}"
                </button>?
              </span>
            </div>
          )}

          {/* Results Grid */}
          {searchResponse.results && searchResponse.results.length > 0 ? (
            <div className="grid-cols-2">
              {searchResponse.results.map((item, index) => (
                <DocumentCard key={index} item={item} isSearchResult={true} />
              ))}
            </div>
          ) : (
            <div style={{
              textAlign: 'center',
              padding: '4rem 2rem',
              background: 'var(--bg-card)',
              borderRadius: 'var(--radius-lg)',
              border: '1px solid var(--border-subtle)',
            }}>
              <AlertTriangle size={40} color="var(--accent-amber)" style={{ margin: '0 auto 1rem' }} />
              <h3 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>No Matching Documents Found</h3>
              <p style={{ color: 'var(--text-secondary)', maxWidth: '480px', margin: '0 auto 1.5rem' }}>
                No occurrences of "<strong>{queryParam}</strong>" were found using {searchResponse.algorithmUsed}.
                Try selecting <strong>Fuzzy Search</strong> or <strong>Auto</strong> to tolerate potential typos.
              </p>
              <button
                onClick={() => handleSearchSubmit({ query: queryParam, algorithm: 'FUZZY' })}
                className="btn btn-primary"
              >
                Retry with Fuzzy Search (Edit Distance DP)
              </button>
            </div>
          )}
        </div>
      )}

      {/* Initial Empty State Guide */}
      {!loading && !searchResponse && (
        <div style={{
          textAlign: 'center',
          padding: '4rem 2rem',
          background: 'var(--bg-card)',
          borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--border-subtle)',
        }}>
          <Search size={44} color="var(--accent-cyan)" style={{ margin: '0 auto 1rem', opacity: 0.8 }} />
          <h2 style={{ fontSize: '1.4rem', marginBottom: '0.5rem' }}>Ready to Search the Digital Library</h2>
          <p style={{ color: 'var(--text-secondary)', maxWidth: '540px', margin: '0 auto 2rem' }}>
            Enter terms in the search bar above and select an algorithm to inspect real-time execution statistics, matching field locations, and relevance rankings.
          </p>
          <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', flexWrap: 'wrap' }}>
            <button onClick={() => handleSearchSubmit({ query: 'algorithms', algorithm: 'KMP' })} className="btn btn-secondary">
              Search "algorithms" (KMP)
            </button>
            <button onClick={() => handleSearchSubmit({ query: 'hash', algorithm: 'RABIN_KARP' })} className="btn btn-secondary">
              Search "hash" (Rabin-Karp)
            </button>
            <button onClick={() => handleSearchSubmit({ query: 'database', algorithm: 'BOYER_MOORE' })} className="btn btn-secondary">
              Search "database" (Boyer-Moore)
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default SearchPage;
