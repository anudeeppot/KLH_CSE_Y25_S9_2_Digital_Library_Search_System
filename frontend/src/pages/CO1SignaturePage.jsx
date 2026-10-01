import React, { useState, useEffect } from 'react';
import { searchAPI } from '../api/apiService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import { Sparkles, Cpu, Sliders, CheckCircle, ArrowRight, ShieldCheck, Zap } from 'lucide-react';

function CO1SignaturePage() {
  const [profile, setProfile] = useState({
    queryPattern: 'algorithms',
    textLength: 50000,
    alphabetEntropy: 2.8,
    multiPatternOrRecurring: false,
    toleranceForTypos: false,
    hasFlowConstraints: false,
    subsetCardinality: 0,
    isTreeStructured: false,
  });

  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const evaluate = async (currentProfile = profile) => {
    setLoading(true);
    setError(null);
    try {
      const res = await searchAPI.evaluateSignature(currentProfile);
      setResult(res.data);
    } catch (err) {
      setError('Failed to evaluate problem-class signature. Ensure backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    evaluate(profile);
  }, []);

  const handleInputChange = (field, value) => {
    const updated = { ...profile, [field]: value };
    setProfile(updated);
  };

  const applyPreset = (presetName) => {
    let p = { ...profile };
    if (presetName === 'exact_kmp') {
      p = {
        queryPattern: 'abababa',
        textLength: 20000,
        alphabetEntropy: 1.2,
        multiPatternOrRecurring: false,
        toleranceForTypos: false,
        hasFlowConstraints: false,
        subsetCardinality: 0,
        isTreeStructured: false,
      };
    } else if (presetName === 'sublinear_bm') {
      p = {
        queryPattern: 'information retrieval architecture',
        textLength: 100000,
        alphabetEntropy: 3.8,
        multiPatternOrRecurring: false,
        toleranceForTypos: false,
        hasFlowConstraints: false,
        subsetCardinality: 0,
        isTreeStructured: false,
      };
    } else if (presetName === 'suffix_indexing') {
      p = {
        queryPattern: 'neural network',
        textLength: 250000,
        alphabetEntropy: 3.1,
        multiPatternOrRecurring: true,
        toleranceForTypos: false,
        hasFlowConstraints: false,
        subsetCardinality: 0,
        isTreeStructured: false,
      };
    } else if (presetName === 'sequence_alignment') {
      p = {
        queryPattern: 'dna_fasta_v1',
        textLength: 5000,
        alphabetEntropy: 2.0,
        multiPatternOrRecurring: false,
        toleranceForTypos: true,
        hasFlowConstraints: false,
        subsetCardinality: 0,
        isTreeStructured: false,
      };
    } else if (presetName === 'network_flow') {
      p = {
        queryPattern: 'cdn_bandwidth',
        textLength: 1000,
        alphabetEntropy: 2.0,
        multiPatternOrRecurring: false,
        toleranceForTypos: false,
        hasFlowConstraints: true,
        subsetCardinality: 0,
        isTreeStructured: false,
      };
    } else if (presetName === 'tree_taxonomy') {
      p = {
        queryPattern: 'acm_dewey_tree',
        textLength: 500,
        alphabetEntropy: 2.0,
        multiPatternOrRecurring: false,
        toleranceForTypos: false,
        hasFlowConstraints: false,
        subsetCardinality: 0,
        isTreeStructured: true,
      };
    } else if (presetName === 'bitmask_curator') {
      p = {
        queryPattern: 'topic_coverage',
        textLength: 800,
        alphabetEntropy: 2.0,
        multiPatternOrRecurring: false,
        toleranceForTypos: false,
        hasFlowConstraints: false,
        subsetCardinality: 8,
        isTreeStructured: false,
      };
    }
    setProfile(p);
    evaluate(p);
  };

  return (
    <div className="container" style={{ padding: '2rem 1rem' }}>
      {/* Header */}
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
          <span className="badge badge-primary" style={{ fontSize: '0.85rem' }}>Course Outcome 1 (CO-1)</span>
          <span style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Problem-Class Signatures & Advanced Strategy Selection</span>
        </div>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, letterSpacing: '-0.02em', margin: 0 }}>
          Algorithm Strategy Evaluator & Signature Classifier
        </h1>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem', maxWidth: '850px' }}>
          Syllabus CO-1 mandates evaluating problem-class signatures (substring search, sequence alignment, flow on a network, NP-hard combinatorial optimization) to select the mathematically optimal algorithm strategy.
        </p>
      </div>

      {/* Preset Buttons */}
      <div className="card" style={{ marginBottom: '2rem', padding: '1.25rem' }}>
        <div style={{ fontWeight: 600, marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Zap size={18} color="var(--primary)" />
          Quick Test Signatures (Presets):
        </div>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
          <button className="btn btn-secondary" onClick={() => applyPreset('exact_kmp')}>
            Low Entropy Pattern (KMP)
          </button>
          <button className="btn btn-secondary" onClick={() => applyPreset('sublinear_bm')}>
            High Entropy Natural Text (Boyer-Moore)
          </button>
          <button className="btn btn-secondary" onClick={() => applyPreset('suffix_indexing')}>
            Recurring Static Corpus (Suffix Array / SAM)
          </button>
          <button className="btn btn-secondary" onClick={() => applyPreset('sequence_alignment')}>
            Fuzzy / Typo Alignment (Needleman-Wunsch)
          </button>
          <button className="btn btn-secondary" onClick={() => applyPreset('network_flow')}>
            Capacity & Matching (Network Flow)
          </button>
          <button className="btn btn-secondary" onClick={() => applyPreset('tree_taxonomy')}>
            Hierarchical Taxonomy (Tree DP)
          </button>
          <button className="btn btn-secondary" onClick={() => applyPreset('bitmask_curator')}>
            Small Subset Coverage (Bitmask DP)
          </button>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
        {/* Input Parameters Controls */}
        <div className="card" style={{ padding: '1.5rem' }}>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Sliders size={20} color="var(--primary)" /> Signature Characteristics
          </h2>

          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>
              Query / Pattern String
            </label>
            <input
              type="text"
              className="input-field"
              value={profile.queryPattern}
              onChange={(e) => handleInputChange('queryPattern', e.target.value)}
              placeholder="Enter query pattern..."
              style={{ width: '100%' }}
            />
            <small style={{ color: 'var(--text-muted)' }}>
              Shannon entropy is dynamically computed over unique character distributions.
            </small>
          </div>

          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>
              Corpus Text Length (|T|): {profile.textLength.toLocaleString()} characters
            </label>
            <input
              type="range"
              min="1000"
              max="1000000"
              step="5000"
              value={profile.textLength}
              onChange={(e) => handleInputChange('textLength', parseInt(e.target.value))}
              style={{ width: '100%' }}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1.25rem' }}>
            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>
                Subset Size (N): {profile.subsetCardinality}
              </label>
              <input
                type="number"
                min="0"
                max="20"
                className="input-field"
                value={profile.subsetCardinality}
                onChange={(e) => handleInputChange('subsetCardinality', parseInt(e.target.value) || 0)}
                style={{ width: '100%' }}
              />
              <small style={{ color: 'var(--text-muted)' }}>Set &gt; 0 for Bitmask DP</small>
            </div>
            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>
                Entropy Hint
              </label>
              <input
                type="number"
                step="0.1"
                min="0"
                max="8"
                className="input-field"
                value={profile.alphabetEntropy}
                onChange={(e) => handleInputChange('alphabetEntropy', parseFloat(e.target.value) || 0)}
                style={{ width: '100%' }}
              />
            </div>
          </div>

          {/* Feature Checkboxes */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginBottom: '1.5rem' }}>
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={profile.multiPatternOrRecurring}
                onChange={(e) => handleInputChange('multiPatternOrRecurring', e.target.checked)}
              />
              <span><strong>Static Corpus / Recurring Multi-Queries:</strong> triggers index structures</span>
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={profile.toleranceForTypos}
                onChange={(e) => handleInputChange('toleranceForTypos', e.target.checked)}
              />
              <span><strong>Error &amp; Typo Tolerance:</strong> triggers sequence alignment DP</span>
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={profile.hasFlowConstraints}
                onChange={(e) => handleInputChange('hasFlowConstraints', e.target.checked)}
              />
              <span><strong>Source-Sink Capacity Constraints:</strong> triggers Network Flow</span>
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={profile.isTreeStructured}
                onChange={(e) => handleInputChange('isTreeStructured', e.target.checked)}
              />
              <span><strong>Tree Taxonomy Hierarchy:</strong> triggers Tree Dynamic Programming</span>
            </label>
          </div>

          <button className="btn btn-primary" onClick={() => evaluate(profile)} style={{ width: '100%' }} disabled={loading}>
            {loading ? <LoadingSpinner size="small" /> : <><Sparkles size={16} /> Re-evaluate Signature</>}
          </button>
        </div>

        {/* Evaluation Output Decision Matrix */}
        <div>
          {error && <ErrorMessage message={error} />}

          {result && (
            <div className="card" style={{ padding: '1.5rem', height: '100%', border: '1px solid var(--primary)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                <div>
                  <span className="badge badge-success" style={{ marginBottom: '0.5rem', display: 'inline-block' }}>
                    {result.identifiedClass?.mappedCO || 'CO-1'}
                  </span>
                  <h3 style={{ fontSize: '1.4rem', fontWeight: 800, margin: 0 }}>
                    {result.identifiedClass?.title || 'Evaluated Class'}
                  </h3>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Selected Strategy:</span>
                  <div style={{ fontWeight: 700, color: 'var(--primary)', fontSize: '1.1rem' }}>
                    {result.selectedStrategy}
                  </div>
                </div>
              </div>

              {/* Metrics Pill Grid */}
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '0.75rem', marginBottom: '1.25rem' }}>
                <div style={{ background: 'var(--bg-tertiary)', padding: '0.75rem', borderRadius: '8px', textAlign: 'center' }}>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Query Length |P|</div>
                  <div style={{ fontWeight: 700, fontSize: '1.1rem' }}>{result.signatureMetrics?.patternLength || 0}</div>
                </div>
                <div style={{ background: 'var(--bg-tertiary)', padding: '0.75rem', borderRadius: '8px', textAlign: 'center' }}>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Text Length |T|</div>
                  <div style={{ fontWeight: 700, fontSize: '1.1rem' }}>{result.signatureMetrics?.textLength?.toLocaleString() || 0}</div>
                </div>
                <div style={{ background: 'var(--bg-tertiary)', padding: '0.75rem', borderRadius: '8px', textAlign: 'center' }}>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Shannon Entropy</div>
                  <div style={{ fontWeight: 700, fontSize: '1.1rem', color: 'var(--accent)' }}>
                    {result.signatureMetrics?.shannonEntropy || 0} bits
                  </div>
                </div>
              </div>

              {/* Justification Box */}
              <div style={{ background: 'var(--bg-secondary)', padding: '1rem', borderRadius: '8px', marginBottom: '1.25rem', borderLeft: '4px solid var(--primary)' }}>
                <div style={{ fontWeight: 600, fontSize: '0.9rem', marginBottom: '0.35rem', color: 'var(--primary)' }}>
                  Mathematical &amp; Algorithmic Rationale:
                </div>
                <div style={{ fontSize: '0.92rem', lineHeight: '1.5' }}>
                  {result.justification}
                </div>
              </div>

              {/* Complexity Analysis */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1.25rem' }}>
                <div style={{ background: 'var(--bg-tertiary)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
                  <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Theoretical Time Complexity</div>
                  <div style={{ fontWeight: 700, fontFamily: 'monospace', color: 'var(--success)' }}>
                    {result.theoreticalTimeComplexity}
                  </div>
                </div>
                <div style={{ background: 'var(--bg-tertiary)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
                  <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Theoretical Space Complexity</div>
                  <div style={{ fontWeight: 700, fontFamily: 'monospace', color: 'var(--warning)' }}>
                    {result.theoreticalSpaceComplexity}
                  </div>
                </div>
              </div>

              {/* Alternative Candidate Strategies */}
              {result.alternativeCandidates && result.alternativeCandidates.length > 0 && (
                <div>
                  <div style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.5rem' }}>
                    Alternative Problem Formulations:
                  </div>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                    {result.alternativeCandidates.map((alt, idx) => (
                      <span key={idx} className="badge badge-secondary" style={{ fontSize: '0.8rem' }}>
                        {alt}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default CO1SignaturePage;
