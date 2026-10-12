/**
 * app.js — Digital Library Search System Frontend
 *
 * Architecture:
 *   Browser (this file) → HTTP fetch → Java API → Java algorithms → corpus → JSON → display
 *
 * Rules enforced here:
 *  - JavaScript NEVER computes search results, edit distances, alignments, or flow values.
 *  - All algorithm output comes from Java via the API.
 *  - No inline event handlers; all bindings are in the DOMContentLoaded block.
 */

'use strict';

// ============================================================
// Constants
// ============================================================
// Automatically use http://localhost:8080 if running via Live Server (port 5500), Vite, or local file
const API_BASE = (window.location.port === '8080')
  ? ''
  : (window.location.hostname && window.location.protocol.startsWith('http')
      ? `${window.location.protocol}//${window.location.hostname}:8080`
      : 'http://localhost:8080');

// ============================================================
// Utility: fetch wrappers
// ============================================================

async function apiGet(path) {
  const res = await fetch(API_BASE + path);
  if (!res.ok) {
    const body = await res.json().catch(() => ({ error: 'HTTP ' + res.status }));
    throw new Error(body.error || 'HTTP ' + res.status);
  }
  return res.json();
}

async function apiPost(path, data) {
  const res = await fetch(API_BASE + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({ error: 'HTTP ' + res.status }));
    throw new Error(body.error || 'HTTP ' + res.status);
  }
  return res.json();
}

// ============================================================
// Utility: DOM helpers
// ============================================================

function el(id)            { return document.getElementById(id); }
function setText(id, v)    { const e = el(id); if (e) e.textContent = v; }
function setHTML(id, v)    { const e = el(id); if (e) e.innerHTML = v; }
function show(id)          { const e = el(id); if (e) e.style.display = ''; }
function hide(id)          { const e = el(id); if (e) e.style.display = 'none'; }

/** Safely escape text for display inside innerHTML contexts. */
function esc(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

// ============================================================
// Utility: Toast notifications
// ============================================================

let _toastTimer = null;
function toast(message, type = 'info') {
  const t = el('toast');
  t.textContent = message;
  t.className = 'toast ' + type;
  t.classList.add('visible');
  clearTimeout(_toastTimer);
  _toastTimer = setTimeout(() => t.classList.remove('visible'), 3500);
}

// ============================================================
// Utility: Loading helpers
// ============================================================

function loadingHTML() {
  return `<div class="loading-wrap"><span class="spinner"></span> Loading…</div>`;
}

function errorHTML(msg) {
  return `<div class="error-state">
    <strong>Error:</strong> ${esc(msg)}
  </div>`;
}

function emptyHTML(msg) {
  return `<div class="empty-state">
    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
      <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
    </svg>
    <p>${esc(msg)}</p>
  </div>`;
}

// ============================================================
// Section navigation
// ============================================================

const SECTIONS = ['home','search','fuzzy','compare','allocation','matching','readingplan','documents','settings','about'];

function showSection(id) {
  SECTIONS.forEach(s => {
    el('section-' + s).classList.toggle('active', s === id);
    el('nav-' + s).classList.toggle('active', s === id);
  });
  // On mobile: close sidebar after navigation
  if (window.innerWidth <= 900) closeSidebar();
  // Lazy-load documents list
  if (id === 'documents' && !_docsLoaded) loadDocumentsList();
  if (id === 'readingplan' && (!_planCandidatesLoaded || document.querySelectorAll('#candidate-table-body tr').length <= 1)) {
    initReadingPlanCandidates();
  }
  if (id === 'settings') {
    syncSettingsView();
  }
}

// Sidebar mobile toggle
function openSidebar() {
  el('sidebar').classList.add('open');
  el('sidebar-overlay').classList.add('open');
  el('hamburger-btn').setAttribute('aria-expanded', 'true');
}
function closeSidebar() {
  el('sidebar').classList.remove('open');
  el('sidebar-overlay').classList.remove('open');
  el('hamburger-btn').setAttribute('aria-expanded', 'false');
}

// ============================================================
// Status check
// ============================================================

async function checkStatus() {
  try {
    const data = await apiGet('/api/status');
    el('status-dot').className = 'status-dot online';
    setText('status-label', 'Online · ' + data.documents + ' docs');
    setText('stat-total-docs', data.documents);
  } catch (e) {
    el('status-dot').className = 'status-dot offline';
    setText('status-label', 'Offline');
    setText('stat-total-docs', '—');
    toast('Cannot connect to the Java server. Start the backend and try again.', 'error');
  }
}

// ============================================================
// KMP SEARCH
// ============================================================

let _searchBusy = false;

async function runSearch(query, field) {
  if (_searchBusy) return;
  if (!query || !query.trim()) { toast('Please enter a search query.', 'error'); return; }

  _searchBusy = true;
  el('search-btn').disabled = true;
  setHTML('search-results-area', loadingHTML());
  hide('search-metrics-banner');

  try {
    const mode = el('search-mode')?.value || 'auto';
    const params = new URLSearchParams({ q: query.trim(), field: field || 'all', mode });
    const data = await apiGet('/api/search?' + params.toString());
    renderSearchResults(data);
  } catch (e) {
    setHTML('search-results-area', errorHTML(e.message));
  } finally {
    _searchBusy = false;
    el('search-btn').disabled = false;
  }
}

function renderSearchResults(data) {
  const area = el('search-results-area');
  const metricsBanner = el('search-metrics-banner');

  // Render performance metrics
  if (data.metrics && metricsBanner) {
    metricsBanner.innerHTML = `
      <span>Scanned <strong>${data.metrics.scannedDocuments}</strong> documents in <strong>${data.metrics.elapsedMs} ms</strong></span>
      <span style="display:flex;gap:6px">
        <span class="metrics-tag">Mode: ${esc(data.metrics.executedMode)}</span>
        ${data.metrics.workerThreads > 1 ? `<span class="metrics-tag">${data.metrics.workerThreads} Worker Threads</span>` : ''}
      </span>`;
    metricsBanner.style.display = 'flex';
  } else if (metricsBanner) {
    metricsBanner.style.display = 'none';
  }

  if (data.total === 0) {
    area.innerHTML =
      `<div class="results-header">
        <span class="results-count">No results for "${esc(data.query)}"</span>
        <span class="algo-badge">${esc(data.algorithm)}</span>
      </div>` + emptyHTML('No documents matched your query. Try a different term or use Fuzzy Search.');
    return;
  }

  let html = `<div class="results-header">
    <span class="results-count">${data.total} match${data.total !== 1 ? 'es' : ''} for &ldquo;${esc(data.query)}&rdquo;
      ${data.field !== 'all' ? '· field: <strong>' + esc(data.field) + '</strong>' : ''}
    </span>
    <span class="algo-badge">${esc(data.algorithm)} · ${esc(data.complexity)}</span>
  </div>`;

  data.results.forEach(r => {
    const posChips = r.positions.slice(0, 12).map(p =>
      `<span class="pos-chip">${p}</span>`).join('') +
      (r.positions.length > 12 ? `<span class="pos-chip">+${r.positions.length - 12} more</span>` : '');

    html += `<div class="result-card">
      <div class="result-card-header">
        <div>
          <div class="result-title">${esc(r.title || r.filename)}</div>
          <div class="result-meta">
            <span class="result-badge field">${esc(r.matchedField)}</span>
            <span class="result-badge count">${r.occurrences} occurrence${r.occurrences !== 1 ? 's' : ''}</span>
          </div>
        </div>
        <button class="btn btn-outline" onclick="viewDocument('${esc(r.filename)}')">View</button>
      </div>
      <div class="result-row">
        <span><strong>Author:</strong> ${esc(r.author || '—')}</span>
        <span><strong>Category:</strong> ${esc(r.category || '—')}</span>
        <span><strong>File:</strong> ${esc(r.filename)}</span>
      </div>
      <div class="position-chips" title="Match positions (0-indexed)">${posChips}</div>
    </div>`;
  });

  area.innerHTML = html;
}

// ============================================================
// KMP DEMO
// ============================================================

let _kmpdBusy = false;

async function runKmpDemo(docId, pattern) {
  if (_kmpdBusy) return;
  if (!docId)    { toast('Please select a document.', 'error'); return; }
  if (!pattern || !pattern.trim()) { toast('Please enter a pattern.', 'error'); return; }

  _kmpdBusy = true;
  el('kmp-demo-run-btn').disabled = true;
  setHTML('kmp-demo-result', loadingHTML());

  try {
    const data = await apiPost('/api/kmp-demo', { docId, pattern: pattern.trim() });
    renderKmpDemo(data);
  } catch (e) {
    setHTML('kmp-demo-result', errorHTML(e.message));
  } finally {
    _kmpdBusy = false;
    el('kmp-demo-run-btn').disabled = false;
  }
}

function renderKmpDemo(data) {
  const lpsChars = data.pattern.toLowerCase().split('');
  const lpsVals  = data.lpsArray;

  const lpsRow = lpsChars.map((ch, i) =>
    `<div class="lps-cell">
      <div class="lps-char">${esc(ch)}</div>
      <div class="lps-val">${lpsVals[i]}</div>
    </div>`).join('');

  const posChips = data.positions.slice(0, 20).map(p =>
    `<span class="pos-chip">${p}</span>`).join('');

  const posNote = data.positions.length > 20
    ? `<span class="pos-chip">+${data.positions.length - 20} more</span>` : '';

  const html = `
    <div style="margin-top:1.2rem">
      <div class="results-header" style="margin-bottom:.8rem">
        <span class="results-count">Document: <strong>${esc(data.title)}</strong></span>
        <span class="algo-badge">${esc(data.algorithm)}</span>
      </div>
      <div class="card" style="margin-bottom:.75rem">
        <p class="card-desc" style="margin-bottom:.6rem">
          <strong>Pattern:</strong> <code style="font-family:var(--font-mono)">${esc(data.pattern)}</code> &nbsp;
          <strong>Occurrences:</strong> ${data.occurrences} &nbsp;
          <strong>Time:</strong> ${esc(data.timeComplexity)} &nbsp;
          <strong>Space:</strong> ${esc(data.spaceComplexity)}
        </p>
        <p class="card-desc" style="margin-bottom:.6rem"><strong>LPS Array (Failure Function):</strong></p>
        <div class="lps-row">${lpsRow}</div>
      </div>
      <div class="card">
        <p class="card-desc" style="margin-bottom:.5rem"><strong>Match positions in document (0-indexed):</strong></p>
        <div class="position-chips">${posChips}${posNote}</div>
        ${data.occurrences === 0 ? '<p class="card-desc" style="margin-top:.5rem;color:var(--col-text-faint)">Pattern not found in this document.</p>' : ''}
      </div>
    </div>`;
  setHTML('kmp-demo-result', html);
}

// ============================================================
// FUZZY SEARCH
// ============================================================

let _fuzzyBusy = false;

async function runFuzzySearch(query, maxDistance) {
  if (_fuzzyBusy) return;
  if (!query || !query.trim()) { toast('Please enter a query.', 'error'); return; }
  if (isNaN(maxDistance) || maxDistance < 0 || maxDistance > 10) {
    toast('Max edit distance must be between 0 and 10.', 'error'); return;
  }

  _fuzzyBusy = true;
  el('fuzzy-btn').disabled = true;
  setHTML('fuzzy-results-area', loadingHTML());

  try {
    const data = await apiPost('/api/fuzzy-search', {
      query: query.trim(),
      maxDistance: parseInt(maxDistance, 10)
    });
    renderFuzzyResults(data);
  } catch (e) {
    setHTML('fuzzy-results-area', errorHTML(e.message));
  } finally {
    _fuzzyBusy = false;
    el('fuzzy-btn').disabled = false;
  }
}

function renderFuzzyResults(data) {
  const area = el('fuzzy-results-area');

  if (data.exactMatch) {
    // Java found an exact KMP match — display those
    let html = `<div class="results-header">
      <span class="results-count">Exact KMP match — ${data.total} result${data.total !== 1 ? 's' : ''}</span>
      <span class="algo-badge">${esc(data.algorithm)}</span>
    </div>
    <div class="info-box" style="margin-bottom:1rem">
      KMP found an exact match for &ldquo;${esc(data.query)}&rdquo;. Showing exact search results.
    </div>`;

    data.results.forEach(r => {
      html += `<div class="result-card">
        <div class="result-card-header">
          <div>
            <div class="result-title">${esc(r.title || r.filename)}</div>
            <div class="result-meta">
              <span class="result-badge field">${esc(r.matchedField)}</span>
              <span class="result-badge count">${r.occurrences} occurrence${r.occurrences !== 1 ? 's' : ''}</span>
            </div>
          </div>
          <button class="btn btn-outline" onclick="viewDocument('${esc(r.filename)}')">View</button>
        </div>
        <div class="result-row">
          <span><strong>Author:</strong> ${esc(r.author || '—')}</span>
          <span><strong>Category:</strong> ${esc(r.category || '—')}</span>
        </div>
      </div>`;
    });

    area.innerHTML = html;
    return;
  }

  // Levenshtein suggestions
  if (data.suggestions.length === 0) {
    area.innerHTML = `<div class="results-header">
      <span class="results-count">No suggestions within edit distance ${data.maxDistance}</span>
      <span class="algo-badge">${esc(data.algorithm)}</span>
    </div>` + emptyHTML('No close matches found. Try increasing the max edit distance.');
    return;
  }

  let html = `<div class="results-header">
    <span class="results-count">${data.total} suggestion${data.total !== 1 ? 's' : ''} for &ldquo;${esc(data.query)}&rdquo;
      (max distance ${data.maxDistance})</span>
    <span class="algo-badge">${esc(data.algorithm)} · ${esc(data.complexity)}</span>
  </div>
  <div class="card" style="padding:0;overflow:hidden">
    <table class="data-table">
      <thead><tr><th>#</th><th>Suggested Term</th><th>Edit Distance</th></tr></thead>
      <tbody>`;

  data.suggestions.forEach((s, i) => {
    html += `<tr>
      <td>${i + 1}</td>
      <td><strong>${esc(s.term)}</strong></td>
      <td><span class="result-badge dist">distance ${s.distance}</span></td>
    </tr>`;
  });

  html += `</tbody></table></div>`;
  area.innerHTML = html;
}

// ============================================================
// COMPARE DOCUMENTS
// ============================================================

let _compareBusy = false;

async function runCompare(docA, docB, match, mismatch, gap) {
  if (_compareBusy) return;
  if (!docA) { toast('Please select Document A.', 'error'); return; }
  if (!docB) { toast('Please select Document B.', 'error'); return; }
  if (docA === docB) { toast('Please select two different documents.', 'error'); return; }

  _compareBusy = true;
  el('compare-btn').disabled = true;
  setHTML('compare-results-area', loadingHTML());

  try {
    const data = await apiPost('/api/compare', {
      docA, docB,
      match: parseInt(match, 10) || 1,
      mismatch: parseInt(mismatch, 10) || -1,
      gap: parseInt(gap, 10) || -2
    });
    renderCompareResults(data);
  } catch (e) {
    setHTML('compare-results-area', errorHTML(e.message));
  } finally {
    _compareBusy = false;
    el('compare-btn').disabled = false;
  }
}

function renderCompareResults(data) {
  // Build coloured alignment
  const seqA = data.alignedA;
  const seqB = data.alignedB;

  let styledA = '', styledB = '';
  for (let i = 0; i < seqA.length; i++) {
    const ca = seqA[i], cb = i < seqB.length ? seqB[i] : '-';
    if (ca === '-') {
      styledA += `<span class="gap">${esc(ca)}</span>`;
    } else if (ca.toLowerCase() === cb.toLowerCase()) {
      styledA += `<span class="match">${esc(ca)}</span>`;
    } else {
      styledA += `<span class="mismatch">${esc(ca)}</span>`;
    }
    if (cb === '-') {
      styledB += `<span class="gap">${esc(cb)}</span>`;
    } else if (ca.toLowerCase() === cb.toLowerCase()) {
      styledB += `<span class="match">${esc(cb)}</span>`;
    } else {
      styledB += `<span class="mismatch">${esc(cb)}</span>`;
    }
  }

  const html = `
    <div class="card" style="margin-bottom:1rem">
      <div class="results-header">
        <span class="results-count">
          <strong>${esc(data.titleA)}</strong> vs <strong>${esc(data.titleB)}</strong>
        </span>
        <span class="algo-badge">${esc(data.algorithm)}</span>
      </div>
      <div class="score-row">
        <div class="score-card"><div class="label">Score</div><div class="value">${data.score}</div></div>
        <div class="score-card"><div class="label">Match</div><div class="value">${data.match}</div></div>
        <div class="score-card"><div class="label">Mismatch</div><div class="value">${data.mismatch}</div></div>
        <div class="score-card"><div class="label">Gap</div><div class="value">${data.gap}</div></div>
        <div class="score-card"><div class="label">Char Limit</div><div class="value">${data.charLimit}</div></div>
      </div>
      <p class="card-desc">${esc(data.note)}</p>
    </div>
    <div class="card" style="padding:0;overflow:hidden">
      <div class="align-panel">
        <div class="align-label">Document A — ${esc(data.docA)}</div>
        <div class="align-seq">${styledA}</div>
        <div class="align-label" style="margin-top:.75rem">Document B — ${esc(data.docB)}</div>
        <div class="align-seq">${styledB}</div>
      </div>
      <div style="padding:.6rem 1rem;background:var(--col-bg);font-size:.75rem;color:var(--col-text-muted);display:flex;gap:1rem">
        <span><span style="color:#4ade80">■</span> Match</span>
        <span><span style="color:#facc15">■</span> Mismatch</span>
        <span><span style="color:#f87171">■</span> Gap (–)</span>
      </div>
    </div>`;

  setHTML('compare-results-area', html);
}

// ============================================================
// RESOURCE ALLOCATION
// ============================================================

let _allocBusy = false;

async function runAllocation() {
  if (_allocBusy) return;
  _allocBusy = true;
  el('alloc-run-btn').disabled = true;
  setHTML('alloc-results-area', loadingHTML());

  try {
    const data = await apiPost('/api/resource-allocation', {});
    renderAllocationResults(data);
  } catch (e) {
    setHTML('alloc-results-area', errorHTML(e.message));
  } finally {
    _allocBusy = false;
    el('alloc-run-btn').disabled = false;
  }
}

function renderAllocationResults(data) {
  const groupRows = data.userGroups.map(g =>
    `<tr><td>${esc(g.group)}</td><td>${g.capacity} requests</td></tr>`).join('');

  const resRows = data.resources.map(r =>
    `<tr><td>${esc(r.resource)}</td><td>${r.licences} licences</td></tr>`).join('');

  const bottleneckRows = data.bottleneckEdges.map(e =>
    `<tr><td>${esc(e.from)}</td><td>${esc(e.to)}</td><td>${e.capacity}</td></tr>`).join('');

  const srcNodes = data.sourceSideNodes.map(n => `<span class="result-badge">${esc(n)}</span>`).join(' ');
  const snkNodes = data.sinkSideNodes.map(n => `<span class="result-badge dist">${esc(n)}</span>`).join(' ');

  const html = `
    <div class="results-header">
      <span class="results-count">Allocation Result</span>
      <span class="algo-badge">${esc(data.algorithm)} · ${esc(data.complexity)}</span>
    </div>
    <div class="alloc-grid">
      <div class="alloc-card">
        <h4>Total Requests</h4>
        <div class="big-number">${data.totalRequests}</div>
        <div class="sub-number">from all user groups</div>
      </div>
      <div class="alloc-card">
        <h4>Max Satisfied</h4>
        <div class="big-number" style="color:var(--col-success)">${data.maxFlow}</div>
        <div class="sub-number">concurrent users served</div>
      </div>
      <div class="alloc-card">
        <h4>Unfulfilled</h4>
        <div class="big-number" style="color:var(--col-error)">${data.unfulfilled}</div>
        <div class="sub-number">requests exceeded licence limits</div>
      </div>
      <div class="alloc-card">
        <h4>Min-Cut Capacity</h4>
        <div class="big-number" style="color:var(--col-accent)">${data.minCutCapacity}</div>
        <div class="sub-number">equals max-flow (theorem verified)</div>
      </div>
    </div>

    <div style="display:grid;grid-template-columns:repeat(auto-fill,minmax(280px,1fr));gap:1rem;margin-bottom:1rem">
      <div class="card" style="padding:0;overflow:hidden">
        <div style="padding:.75rem 1rem;font-weight:600;font-size:.85rem;background:var(--col-bg);border-bottom:1px solid var(--col-border)">User Groups</div>
        <table class="data-table"><thead><tr><th>Group</th><th>Capacity</th></tr></thead>
        <tbody>${groupRows}</tbody></table>
      </div>
      <div class="card" style="padding:0;overflow:hidden">
        <div style="padding:.75rem 1rem;font-weight:600;font-size:.85rem;background:var(--col-bg);border-bottom:1px solid var(--col-border)">Resources</div>
        <table class="data-table"><thead><tr><th>Resource</th><th>Licences</th></tr></thead>
        <tbody>${resRows}</tbody></table>
      </div>
    </div>

    <div class="card">
      <h3 class="card-title">Bottleneck Analysis (Min-Cut)</h3>
      <p class="card-desc" style="margin-bottom:.75rem">${esc(data.interpretation)}</p>
      <div style="margin-bottom:.75rem">
        <strong style="font-size:.8rem">Source-side nodes:</strong><br/>${srcNodes}
      </div>
      <div style="margin-bottom:1rem">
        <strong style="font-size:.8rem">Sink-side nodes:</strong><br/>${snkNodes}
      </div>
      ${bottleneckRows ? `
        <div class="card" style="padding:0;overflow:hidden">
          <div style="padding:.75rem 1rem;font-weight:600;font-size:.85rem;background:var(--col-bg);border-bottom:1px solid var(--col-border)">Bottleneck Edges (cut edges)</div>
          <table class="data-table">
            <thead><tr><th>From</th><th>To</th><th>Capacity</th></tr></thead>
            <tbody>${bottleneckRows}</tbody>
          </table>
        </div>` : '<p class="card-desc">No bottleneck edges identified.</p>'}
    </div>`;

  setHTML('alloc-results-area', html);
}

// ============================================================
// COMPATIBILITY MATRIX builder
// ============================================================

function buildCompatMatrix() {
  const usersRaw     = el('matching-users').value;
  const resourcesRaw = el('matching-resources').value;

  const users     = usersRaw.split(',').map(s => s.trim()).filter(Boolean);
  const resources = resourcesRaw.split(',').map(s => s.trim()).filter(Boolean);

  if (users.length === 0)     { toast('Enter at least one user.', 'error'); return; }
  if (resources.length === 0) { toast('Enter at least one resource.', 'error'); return; }
  if (users.length > 10 || resources.length > 10) {
    toast('Maximum 10 users and 10 resources for the web interface.', 'error'); return;
  }

  let html = `<table class="compat-table">
    <thead><tr><th>User \\ Resource</th>`;
  resources.forEach(r => { html += `<th>${esc(r)}</th>`; });
  html += `</tr></thead><tbody>`;

  // Pre-fill defaults matching the original Java demo
  const defaults = [
    [true,  true,  false, false, true ],
    [false, true,  true,  false, false],
    [true,  false, true,  true,  false],
    [false, false, false, true,  true ],
    [true,  true,  false, false, false]
  ];

  users.forEach((u, i) => {
    html += `<tr><td>${esc(u)}</td>`;
    resources.forEach((r, j) => {
      const checked = (defaults[i] && defaults[i][j]) ? 'checked' : '';
      html += `<td><input type="checkbox" id="compat-${i}-${j}"
        data-user="${i}" data-resource="${j}" ${checked} /></td>`;
    });
    html += `</tr>`;
  });

  html += `</tbody></table>`;
  setHTML('compat-matrix-container', html);
}

// ============================================================
// MATCHING
// ============================================================

let _matchBusy = false;

async function runMatching() {
  if (_matchBusy) return;

  const usersRaw     = el('matching-users').value;
  const resourcesRaw = el('matching-resources').value;
  const users     = usersRaw.split(',').map(s => s.trim()).filter(Boolean);
  const resources = resourcesRaw.split(',').map(s => s.trim()).filter(Boolean);

  if (users.length === 0)     { toast('Enter at least one user.', 'error'); return; }
  if (resources.length === 0) { toast('Enter at least one resource.', 'error'); return; }

  // Read compat matrix
  const compatible = [];
  for (let i = 0; i < users.length; i++) {
    const row = [];
    for (let j = 0; j < resources.length; j++) {
      const cb = el(`compat-${i}-${j}`);
      if (!cb) {
        toast('Please click "Build Matrix" before running matching.', 'error'); return;
      }
      row.push(cb.checked);
    }
    compatible.push(row);
  }

  _matchBusy = true;
  el('matching-run-btn').disabled = true;
  setHTML('matching-results-area', loadingHTML());

  try {
    const data = await apiPost('/api/matching', { users, resources, compatible });
    renderMatchingResults(data, users, resources);
  } catch (e) {
    setHTML('matching-results-area', errorHTML(e.message));
  } finally {
    _matchBusy = false;
    el('matching-run-btn').disabled = false;
  }
}

function renderMatchingResults(data, users, resources) {
  const assignRows = data.assignments.map(a =>
    `<tr>
      <td><strong>${esc(a.user)}</strong></td>
      <td>${esc(a.resource)}</td>
      <td><span class="result-badge" style="background:var(--col-success-lt);color:var(--col-success)">Matched</span></td>
    </tr>`).join('');

  const unmatchedRows = data.unmatchedUsers.map(u =>
    `<tr>
      <td><strong>${esc(u)}</strong></td>
      <td>—</td>
      <td><span class="result-badge dist">Unmatched</span></td>
    </tr>`).join('');

  const html = `
    <div class="results-header">
      <span class="results-count">Matching Result — ${data.maxMatching} / ${data.totalUsers} users matched</span>
      <span class="algo-badge">${esc(data.algorithm)} · ${esc(data.complexity)}</span>
    </div>
    <div class="alloc-grid" style="margin-bottom:1rem">
      <div class="alloc-card">
        <h4>Max Matching</h4>
        <div class="big-number" style="color:var(--col-success)">${data.maxMatching}</div>
        <div class="sub-number">out of ${data.totalUsers} users</div>
      </div>
      <div class="alloc-card">
        <h4>Unmatched Users</h4>
        <div class="big-number" style="color:var(--col-error)">${data.unmatchedUsers.length}</div>
        <div class="sub-number">no compatible resource available</div>
      </div>
    </div>
    <div class="card" style="padding:0;overflow:hidden">
      <table class="data-table">
        <thead><tr><th>User</th><th>Assigned Resource</th><th>Status</th></tr></thead>
        <tbody>${assignRows}${unmatchedRows}</tbody>
      </table>
    </div>`;

  setHTML('matching-results-area', html);
}

// ============================================================
// READING PLAN (MODULE 5: 1/2-APPROXIMATION FOR 0/1 KNAPSACK)
// ============================================================

let _planBusy = false;
let _planCandidatesLoaded = false;

async function initReadingPlanCandidates() {
  if (!_allDocs || _allDocs.length === 0) {
    try {
      const data = await apiGet('/api/documents');
      _allDocs = data.documents || [];
    } catch (e) {
      setHTML('candidate-table-body', '<tr><td colspan="5" style="text-align:center;padding:1.5rem">Failed to load candidate documents.</td></tr>');
      return;
    }
  }
  _planCandidatesLoaded = true;
  renderReadingPlanCandidates();
}

function renderReadingPlanCandidates(filterQuery = '') {
  const tbody = el('candidate-table-body');
  if (!tbody) return;

  if (!_allDocs || _allDocs.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:1.5rem">No documents loaded in corpus.</td></tr>';
    return;
  }

  const speed = parseInt(el('plan-speed')?.value, 10) || 200;
  const q = (filterQuery || '').toLowerCase().trim();

  let html = '';
  _allDocs.forEach(doc => {
    const title = doc.title || doc.filename;
    const cat = doc.category || 'General';
    const author = doc.author || '';
    if (q && !title.toLowerCase().includes(q) && !cat.toLowerCase().includes(q) && !author.toLowerCase().includes(q)) {
      return;
    }

    const words = doc.wordCount || 200;
    const estTime = Math.max(1, Math.ceil(words / speed));

    let defaultPrio = 5;
    const lt = title.toLowerCase();
    if (lt.includes('algorithm') || lt.includes('kmp') || lt.includes('knapsack') || lt.includes('search')) {
      defaultPrio = 9;
    } else if (cat.toLowerCase().includes('computer science') || cat.toLowerCase().includes('data')) {
      defaultPrio = 7;
    }

    html += `
      <tr data-doc-id="${esc(doc.filename)}" data-words="${words}" data-cat="${esc(cat)}" data-title="${esc(title)}">
        <td><input type="checkbox" class="plan-doc-check" checked data-doc-id="${esc(doc.filename)}" /></td>
        <td>
          <div style="font-weight:600;color:var(--col-text)">${esc(title)}</div>
          <div style="font-size:.72rem;color:var(--col-text-muted)">${esc(cat)} · ${esc(doc.filename)}</div>
        </td>
        <td>${words.toLocaleString()}</td>
        <td class="plan-est-time">${estTime} min</td>
        <td>
          <input type="number" min="1" max="10" value="${defaultPrio}" class="priority-input plan-prio-input" data-doc-id="${esc(doc.filename)}" />
        </td>
      </tr>`;
  });

  if (!html) {
    tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:1.5rem">No documents match filter criteria.</td></tr>';
  } else {
    tbody.innerHTML = html;
  }
}

function updatePlanEstimatedTimes() {
  const speed = parseInt(el('plan-speed')?.value, 10) || 200;
  const rows = document.querySelectorAll('#candidate-table-body tr');
  rows.forEach(tr => {
    const words = parseInt(tr.dataset.words, 10) || 200;
    const timeTd = tr.querySelector('.plan-est-time');
    if (timeTd) {
      timeTd.textContent = Math.max(1, Math.ceil(words / speed)) + ' min';
    }
  });
}

async function runGenerateReadingPlan() {
  if (_planBusy) return;
  const budget = parseInt(el('plan-budget')?.value, 10);
  const speed = parseInt(el('plan-speed')?.value, 10) || 200;

  if (isNaN(budget) || budget <= 0) {
    toast('Please enter a valid reading time budget (> 0 minutes).', 'error');
    return;
  }

  const checkedBoxes = document.querySelectorAll('.plan-doc-check:checked');
  if (checkedBoxes.length === 0) {
    toast('Please select at least one candidate document from the table.', 'error');
    return;
  }

  const candidates = [];
  checkedBoxes.forEach(cb => {
    const id = cb.dataset.docId;
    const prioInput = document.querySelector(`.plan-prio-input[data-doc-id="${id}"]`);
    const prio = prioInput ? parseInt(prioInput.value, 10) : 5;
    candidates.push({ id, priority: Math.max(1, Math.min(10, prio || 5)) });
  });

  _planBusy = true;
  el('generate-plan-btn').disabled = true;
  setHTML('plan-results-area', loadingHTML());

  try {
    const data = await apiPost('/api/reading-plan', {
      budget,
      readingSpeed: speed,
      candidates
    });
    renderPlanResults(data);
  } catch (e) {
    setHTML('plan-results-area', errorHTML(e.message));
  } finally {
    _planBusy = false;
    el('generate-plan-btn').disabled = false;
  }
}

function renderPlanResults(data) {
  const area = el('plan-results-area');
  if (!data || data.selectedCount === 0) {
    area.innerHTML = `
      <div class="card" style="margin-top:var(--space-md)">
        <div class="results-header">
          <span class="results-count">No Documents Fit Budget (${data.budgetMinutes} min)</span>
          <span class="algo-badge">${esc(data.strategy)}</span>
        </div>
        <p class="card-desc">All candidate documents require more reading time than the given budget. Increase your time budget or increase your reading speed.</p>
      </div>`;
    return;
  }

  let docCards = '';
  data.selectedDocuments.forEach((doc, idx) => {
    docCards += `
      <div class="result-card">
        <div class="result-card-header">
          <div>
            <div class="result-title">${idx + 1}. ${esc(doc.title)}</div>
            <div class="result-meta">
              <span class="result-badge field">${esc(doc.category)}</span>
              <span class="result-badge count">Est. ${doc.estimatedMinutes} min (${doc.wordCount} words)</span>
              <span class="result-badge" style="background:rgba(16,185,129,0.12);color:#10b981">Priority: ${doc.priority}/10</span>
              <span class="result-badge dist" title="Priority density">Density: ${doc.density} pts/min</span>
            </div>
          </div>
          <button class="btn btn-outline" onclick="viewDocument('${esc(doc.id)}')">View</button>
        </div>
        <div class="result-row">
          <span><strong>Author:</strong> ${esc(doc.author || '—')}</span>
          <span><strong>File:</strong> ${esc(doc.filename)}</span>
        </div>
      </div>`;
  });

  const html = `
    <div class="card" style="margin-top:var(--space-md);margin-bottom:var(--space-md)">
      <div class="results-header">
        <span class="results-count">
          Recommended Study Pack — <strong>${data.selectedCount}</strong> Documents (${data.totalMinutes} / ${data.budgetMinutes} min)
        </span>
        <span class="algo-badge" style="background:rgba(16,185,129,0.15);color:#10b981">
          ${esc(data.strategy)}
        </span>
      </div>

      <div class="score-row" style="margin:var(--space-md) 0">
        <div class="score-card">
          <div class="label">Time Used</div>
          <div class="value" style="color:var(--col-accent)">${data.totalMinutes} <span style="font-size:.85rem;font-weight:400">min</span></div>
        </div>
        <div class="score-card">
          <div class="label">Total Priority Score</div>
          <div class="value" style="color:#10b981">${data.totalPriority} <span style="font-size:.85rem;font-weight:400">pts</span></div>
        </div>
        <div class="score-card">
          <div class="label">Remaining Budget</div>
          <div class="value">${data.remainingMinutes} <span style="font-size:.85rem;font-weight:400">min</span></div>
        </div>
        <div class="score-card">
          <div class="label">Reading Speed</div>
          <div class="value">${data.readingSpeedWpm} <span style="font-size:.85rem;font-weight:400">WPM</span></div>
        </div>
      </div>

      <div style="background:var(--col-bg);border:1px solid var(--col-border);border-left:3px solid #10b981;border-radius:var(--radius-sm);padding:.75rem 1rem;font-size:.82rem;line-height:1.5;color:var(--col-text)">
        <strong>Approximation Guarantee:</strong> ${esc(data.approximationGuarantee)}<br/>
        <span style="color:var(--col-text-muted)">${esc(data.explanation)}</span>
      </div>
    </div>

    <div class="cards-list">
      ${docCards}
    </div>`;

  setHTML('plan-results-area', html);
}

// ============================================================
// SURPRISE ME (MODULE 6: RESERVOIR SAMPLING)
// ============================================================

let _surpriseBusy = false;

async function triggerSurpriseMe() {
  if (_surpriseBusy) return;
  _surpriseBusy = true;

  try {
    const data = await apiGet('/api/random-document');
    renderSurpriseModal(data);
  } catch (e) {
    toast('Error selecting random document: ' + e.message, 'error');
  } finally {
    _surpriseBusy = false;
  }
}

function renderSurpriseModal(data) {
  const content = el('surprise-modal-content');
  if (!content) return;

  content.innerHTML = `
    <div class="surprise-spotlight">
      <span class="surprise-badge">${esc(data.algorithm)}</span>
      <h3 style="font-size:1.25rem;font-weight:700;margin:.3rem 0;color:var(--col-text)">${esc(data.title)}</h3>
      <div style="font-size:.82rem;color:var(--col-text-muted);display:flex;gap:1rem;flex-wrap:wrap">
        <span><strong>Author:</strong> ${esc(data.author || '—')}</span>
        <span><strong>Category:</strong> ${esc(data.category || 'General')}</span>
        <span><strong>Year:</strong> ${esc(data.year || '—')}</span>
        <span><strong>Words:</strong> ${data.wordCount} (~${data.estimatedReadingMinutes} min)</span>
      </div>
      <div class="surprise-preview">
        &ldquo;${esc(data.preview)}&rdquo;
      </div>
      <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:.75rem">
        <button class="btn btn-primary" onclick="closeSurpriseModal();viewDocument('${esc(data.id)}')">
          Read Full Document
        </button>
        <button class="btn btn-outline" onclick="triggerSurpriseMe()">
          🎲 Roll Another Document
        </button>
      </div>
    </div>

    <details class="theory-accordion" style="margin-top:0">
      <summary style="font-size:.82rem;padding:.6rem .8rem">
        <span>Probability &amp; Proof: Why is this selection uniform?</span>
        <span>▾</span>
      </summary>
      <div class="theory-content" style="font-size:.8rem;padding:.8rem">
        <p><strong>Selection Probability:</strong> <code>${esc(data.probability)}</code></p>
        <p><strong>Proof Formula:</strong> <code>${esc(data.proofFormula)}</code></p>
        <p>${esc(data.explanation)}</p>
      </div>
    </details>`;

  el('surprise-modal-overlay').style.display = 'flex';
  document.body.style.overflow = 'hidden';
}

function closeSurpriseModal() {
  const modal = el('surprise-modal-overlay');
  if (modal) modal.style.display = 'none';
  document.body.style.overflow = '';
}

// ============================================================
// ALL DOCUMENTS
// ============================================================

let _allDocs      = [];
let _filteredDocs = [];
let _docsLoaded   = false;
let _docsPage     = 0;
const DOCS_PER_PAGE = 30;

async function loadDocumentsList() {
  _docsLoaded = true;
  setHTML('docs-grid', loadingHTML());

  try {
    const data = await apiGet('/api/documents');
    _allDocs = data.documents || [];
    setText('docs-count-label', data.total + ' documents total');

    // Populate category filter
    const cats = [...new Set(_allDocs.map(d => d.category).filter(Boolean))].sort();
    const catSel = el('docs-filter-cat');
    cats.forEach(c => {
      const opt = document.createElement('option');
      opt.value = c; opt.textContent = c;
      catSel.appendChild(opt);
    });

    applyDocsFilter();
  } catch (e) {
    setHTML('docs-grid', errorHTML(e.message));
  }
}

function applyDocsFilter() {
  const cat  = el('docs-filter-cat').value;
  const sort = el('docs-sort').value;

  _filteredDocs = _allDocs.filter(d => !cat || d.category === cat);

  _filteredDocs.sort((a, b) => {
    const va = (a[sort] || '').toLowerCase();
    const vb = (b[sort] || '').toLowerCase();
    return va < vb ? -1 : va > vb ? 1 : 0;
  });

  _docsPage = 0;
  renderDocPage(true);
}

function renderDocPage(reset) {
  const grid = el('docs-grid');
  if (reset) grid.innerHTML = '';

  if (_filteredDocs.length === 0) {
    grid.innerHTML = emptyHTML('No documents found.');
    hide('docs-load-more-btn');
    return;
  }

  const start = _docsPage * DOCS_PER_PAGE;
  const slice = _filteredDocs.slice(start, start + DOCS_PER_PAGE);

  slice.forEach(d => {
    const card = document.createElement('div');
    card.className = 'doc-card';
    card.innerHTML = `
      <div class="doc-card-title">${esc(d.title || d.filename)}</div>
      <div class="doc-card-meta"><strong>Author:</strong> ${esc(d.author || '—')}</div>
      <div class="doc-card-meta"><strong>Year:</strong> ${esc(d.year || '—')}</div>
      <div class="doc-card-meta" style="font-size:.72rem;color:var(--col-text-faint)">${esc(d.filename)}</div>
      <span class="doc-card-cat">${esc(d.category || 'General')}</span>
      <div class="doc-card-actions">
        <button class="btn btn-outline" style="font-size:.78rem;padding:.35rem .7rem"
          onclick="viewDocument('${esc(d.filename)}')">View</button>
        <button class="btn btn-ghost" style="font-size:.78rem;padding:.35rem .7rem"
          onclick="searchFromDoc('${esc(d.title || d.filename)}')">Search</button>
      </div>`;
    grid.appendChild(card);
  });

  _docsPage++;
  const shown = _docsPage * DOCS_PER_PAGE;
  const loadMore = el('docs-load-more-btn');
  if (shown < _filteredDocs.length) {
    loadMore.style.display = '';
    loadMore.textContent = `Load More (${_filteredDocs.length - shown} remaining)`;
  } else {
    loadMore.style.display = 'none';
  }
}

function searchFromDoc(title) {
  el('search-query').value = title;
  el('search-field').value = 'all';
  showSection('search');
  runSearch(title, 'all');
}

// ============================================================
// DOCUMENT VIEWER (modal)
// ============================================================

async function viewDocument(filename) {
  el('modal-title').textContent = 'Loading…';
  setHTML('modal-meta', '');
  setHTML('modal-body', loadingHTML());
  el('doc-modal-overlay').style.display = 'flex';
  document.body.style.overflow = 'hidden';

  try {
    const data = await apiGet('/api/document?id=' + encodeURIComponent(filename));
    el('modal-title').textContent = data.title || data.filename;

    const metaBadge = (label, val) => val
      ? `<span class="result-badge">${esc(label)}: ${esc(val)}</span>` : '';

    setHTML('modal-meta',
      metaBadge('Author',   data.author) +
      metaBadge('Category', data.category) +
      metaBadge('Year',     data.year) +
      metaBadge('ISBN',     data.isbn) +
      `<span class="result-badge" style="background:var(--col-bg);color:var(--col-text-muted)">${esc(data.filename)}</span>`
    );

    // Safely render as plain text (no HTML injection)
    const bodyEl = el('modal-body');
    bodyEl.textContent = data.content || '(No content available)';
  } catch (e) {
    setHTML('modal-body', errorHTML(e.message));
  }
}

function closeModal() {
  el('doc-modal-overlay').style.display = 'none';
  document.body.style.overflow = '';
}

// ============================================================
// Populate document <select> dropdowns for Compare & KMP Demo
// ============================================================

async function populateDocSelects() {
  try {
    const data = await apiGet('/api/documents');
    const docs = data.documents || [];
    if (!_allDocs || _allDocs.length === 0) {
      _allDocs = docs;
    }

    const selects = ['compare-doc-a', 'compare-doc-b', 'kmp-demo-doc'];
    selects.forEach(selId => {
      const sel = el(selId);
      if (!sel) return;
      docs.forEach(d => {
        const opt = document.createElement('option');
        opt.value = d.filename;
        opt.textContent = (d.title || d.filename).substring(0, 60);
        sel.appendChild(opt);
      });
    });

    // Populate reading plan candidate table on startup
    initReadingPlanCandidates();
  } catch (e) {
    // Silently ignore — selects will stay empty; user can navigate to All Docs
  }
}

function applyPlanPreset(type) {
  const checkboxes = document.querySelectorAll('.plan-doc-check');
  const rows = document.querySelectorAll('#candidate-table-body tr');
  if (type === 'all') {
    checkboxes.forEach(cb => { cb.checked = true; });
  } else if (type === 'clear') {
    checkboxes.forEach(cb => { cb.checked = false; });
  } else if (type === 'algo') {
    rows.forEach(r => {
      const cat = (r.dataset.cat || '').toLowerCase();
      const title = (r.dataset.title || '').toLowerCase();
      const match = cat.includes('algorithm') || cat.includes('data structure') ||
                    title.includes('algorithm') || title.includes('kmp') ||
                    title.includes('search') || title.includes('graph') ||
                    title.includes('sort') || title.includes('tree') ||
                    title.includes('dynamic');
      const cb = r.querySelector('.plan-doc-check');
      if (cb) cb.checked = match;
    });
  } else if (type === 'ai') {
    rows.forEach(r => {
      const cat = (r.dataset.cat || '').toLowerCase();
      const title = (r.dataset.title || '').toLowerCase();
      const match = cat.includes('intelligence') || cat.includes('machine learning') ||
                    cat.includes('data') || title.includes('ai') ||
                    title.includes('neural') || title.includes('learning') ||
                    title.includes('model');
      const cb = r.querySelector('.plan-doc-check');
      if (cb) cb.checked = match;
    });
  }
}

// ============================================================
// THEME & SETTINGS MANAGER
// ============================================================

const THEME_KEY = 'diglib-theme';
const SEARCH_MODE_KEY = 'diglib-search-mode';
const READING_SPEED_KEY = 'diglib-reading-speed';
const READING_BUDGET_KEY = 'diglib-reading-budget';

function getCurrentTheme() {
  const saved = localStorage.getItem(THEME_KEY);
  if (saved === 'dark' || saved === 'light') return saved;
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    return 'dark';
  }
  return 'light';
}

function setTheme(theme) {
  if (theme !== 'dark' && theme !== 'light') theme = 'light';
  document.documentElement.setAttribute('data-theme', theme);
  try {
    localStorage.setItem(THEME_KEY, theme);
  } catch (e) {}

  // Update Settings UI components if rendered
  const checkbox = el('theme-toggle-checkbox');
  if (checkbox) checkbox.checked = (theme === 'dark');

  const statusText = el('theme-status-text');
  if (statusText) statusText.textContent = (theme === 'dark' ? 'Dark Mode' : 'Light Mode');

  const cardLight = el('theme-card-light');
  const cardDark = el('theme-card-dark');
  if (cardLight) cardLight.classList.toggle('active', theme === 'light');
  if (cardDark) cardDark.classList.toggle('active', theme === 'dark');

  const quickBtn = el('quick-theme-btn');
  if (quickBtn) quickBtn.setAttribute('title', `Current: ${theme === 'dark' ? 'Dark' : 'Light'} Mode (Click to switch)`);

  const mobileBtn = el('mobile-theme-btn');
  if (mobileBtn) mobileBtn.setAttribute('title', `Current: ${theme === 'dark' ? 'Dark' : 'Light'} Mode (Click to switch)`);
}

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme') || getCurrentTheme();
  const next = (current === 'dark') ? 'light' : 'dark';
  setTheme(next);
  toast(`Switched to ${next === 'dark' ? 'Dark' : 'Light'} Mode`, 'info');
}

function syncSettingsView() {
  const theme = document.documentElement.getAttribute('data-theme') || getCurrentTheme();
  setTheme(theme);

  // Sync search mode
  const searchMode = el('search-mode')?.value || localStorage.getItem(SEARCH_MODE_KEY) || 'auto';
  const settingsSearchMode = el('settings-search-mode');
  if (settingsSearchMode) settingsSearchMode.value = searchMode;

  // Sync reading plan defaults
  const planSpeed = el('plan-speed')?.value || localStorage.getItem(READING_SPEED_KEY) || '200';
  const planBudget = el('plan-budget')?.value || localStorage.getItem(READING_BUDGET_KEY) || '30';
  const setSpeed = el('settings-reading-speed');
  const setBudget = el('settings-time-budget');
  if (setSpeed) setSpeed.value = planSpeed;
  if (setBudget) setBudget.value = planBudget;
}

function applySettingsDefaults() {
  const speed = parseInt(el('settings-reading-speed')?.value, 10) || 200;
  const budget = parseInt(el('settings-time-budget')?.value, 10) || 30;

  try {
    localStorage.setItem(READING_SPEED_KEY, speed);
    localStorage.setItem(READING_BUDGET_KEY, budget);
  } catch (e) {}

  const planSpeed = el('plan-speed');
  const planBudget = el('plan-budget');
  if (planSpeed) planSpeed.value = speed;
  if (planBudget) planBudget.value = budget;

  updatePlanEstimatedTimes();
  toast('Settings applied to Reading Plan builder!', 'success');
}

function resetAllSettings() {
  try {
    localStorage.removeItem(THEME_KEY);
    localStorage.removeItem(SEARCH_MODE_KEY);
    localStorage.removeItem(READING_SPEED_KEY);
    localStorage.removeItem(READING_BUDGET_KEY);
  } catch (e) {}

  setTheme('light');
  if (el('search-mode')) el('search-mode').value = 'auto';
  if (el('settings-search-mode')) el('settings-search-mode').value = 'auto';
  if (el('plan-speed')) el('plan-speed').value = 200;
  if (el('settings-reading-speed')) el('settings-reading-speed').value = 200;
  if (el('plan-budget')) el('plan-budget').value = 30;
  if (el('settings-time-budget')) el('settings-time-budget').value = 30;
  updatePlanEstimatedTimes();

  toast('All settings reset to defaults!', 'info');
}

// Immediately apply saved theme on script parse to avoid white flicker
(function() {
  try {
    const saved = localStorage.getItem(THEME_KEY);
    const theme = (saved === 'dark' || saved === 'light') ? saved :
      (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
    document.documentElement.setAttribute('data-theme', theme);
  } catch(e) {}
})();

// ============================================================
// Expose global functions (called from onclick in dynamic HTML)
// ============================================================
window.viewDocument        = viewDocument;
window.searchFromDoc       = searchFromDoc;
window.triggerSurpriseMe   = triggerSurpriseMe;
window.closeSurpriseModal  = closeSurpriseModal;
window.setTheme            = setTheme;
window.toggleTheme         = toggleTheme;

// ============================================================
// DOMContentLoaded — wire up all event listeners
// ============================================================
document.addEventListener('DOMContentLoaded', () => {

  // --- Navigation ---
  SECTIONS.forEach(s => {
    const navBtn = el('nav-' + s);
    if (navBtn) navBtn.addEventListener('click', () => showSection(s));
  });

  // Feature cards & hero CTAs
  document.querySelectorAll('[data-nav]').forEach(btn => {
    btn.addEventListener('click', () => showSection(btn.dataset.nav));
  });

  // Hamburger
  el('hamburger-btn').addEventListener('click', openSidebar);
  el('sidebar-overlay').addEventListener('click', closeSidebar);

  // --- Theme toggles ---
  const quickThemeBtn = el('quick-theme-btn');
  if (quickThemeBtn) quickThemeBtn.addEventListener('click', toggleTheme);
  const mobileThemeBtn = el('mobile-theme-btn');
  if (mobileThemeBtn) mobileThemeBtn.addEventListener('click', toggleTheme);

  const themeSwitchCheckbox = el('theme-toggle-checkbox');
  if (themeSwitchCheckbox) {
    themeSwitchCheckbox.addEventListener('change', e => {
      setTheme(e.target.checked ? 'dark' : 'light');
    });
  }

  // --- Settings listeners ---
  const setModeSelect = el('settings-search-mode');
  if (setModeSelect) {
    setModeSelect.addEventListener('change', e => {
      const mode = e.target.value;
      if (el('search-mode')) el('search-mode').value = mode;
      try { localStorage.setItem(SEARCH_MODE_KEY, mode); } catch (err) {}
      toast(`Default search mode set to: ${mode}`, 'info');
    });
  }

  const mainSearchMode = el('search-mode');
  if (mainSearchMode) {
    mainSearchMode.addEventListener('change', e => {
      if (el('settings-search-mode')) el('settings-search-mode').value = e.target.value;
      try { localStorage.setItem(SEARCH_MODE_KEY, e.target.value); } catch (err) {}
    });
  }

  const applyPlanBtn = el('settings-apply-plan-btn');
  if (applyPlanBtn) applyPlanBtn.addEventListener('click', applySettingsDefaults);

  const resetSettingsBtn = el('settings-reset-defaults-btn');
  if (resetSettingsBtn) resetSettingsBtn.addEventListener('click', resetAllSettings);

  // --- Hero search & Surprise Me ---
  el('hero-search-btn').addEventListener('click', () => {
    const q = el('hero-search-input').value;
    el('search-query').value = q;
    el('search-field').value = 'all';
    showSection('search');
    if (q.trim()) runSearch(q, 'all');
  });
  el('hero-search-input').addEventListener('keydown', e => {
    if (e.key === 'Enter') el('hero-search-btn').click();
  });
  const heroSurprise = el('hero-surprise-btn');
  if (heroSurprise) heroSurprise.addEventListener('click', triggerSurpriseMe);

  // --- Search ---
  el('search-btn').addEventListener('click', () => {
    runSearch(el('search-query').value, el('search-field').value);
  });
  el('search-query').addEventListener('keydown', e => {
    if (e.key === 'Enter') el('search-btn').click();
  });
  el('search-clear-btn').addEventListener('click', () => {
    el('search-query').value = '';
    setHTML('search-results-area', '');
    const banner = el('search-metrics-banner');
    if (banner) banner.style.display = 'none';
  });
  const searchSurprise = el('search-surprise-btn');
  if (searchSurprise) searchSurprise.addEventListener('click', triggerSurpriseMe);

  // --- KMP Demo toggle ---
  el('kmp-demo-toggle-btn').addEventListener('click', () => {
    const panel = el('kmp-demo-panel');
    const visible = panel.style.display !== 'none';
    panel.style.display = visible ? 'none' : '';
    el('kmp-demo-toggle-btn').textContent = visible ? 'KMP Demo' : 'Hide Demo';
  });

  el('kmp-demo-run-btn').addEventListener('click', () => {
    runKmpDemo(el('kmp-demo-doc').value, el('kmp-demo-pattern').value);
  });
  el('kmp-demo-pattern').addEventListener('keydown', e => {
    if (e.key === 'Enter') el('kmp-demo-run-btn').click();
  });

  // --- Fuzzy Search ---
  el('fuzzy-btn').addEventListener('click', () => {
    runFuzzySearch(el('fuzzy-query').value, el('fuzzy-max-dist').value);
  });
  el('fuzzy-query').addEventListener('keydown', e => {
    if (e.key === 'Enter') el('fuzzy-btn').click();
  });
  el('fuzzy-clear-btn').addEventListener('click', () => {
    el('fuzzy-query').value = '';
    setHTML('fuzzy-results-area', '');
  });

  // --- Compare ---
  el('compare-btn').addEventListener('click', () => {
    runCompare(
      el('compare-doc-a').value,
      el('compare-doc-b').value,
      el('cmp-match').value,
      el('cmp-mismatch').value,
      el('cmp-gap').value
    );
  });
  el('compare-clear-btn').addEventListener('click', () => {
    el('compare-doc-a').value = '';
    el('compare-doc-b').value = '';
    setHTML('compare-results-area', '');
  });

  // --- Allocation ---
  el('alloc-run-btn').addEventListener('click', runAllocation);

  // --- Matching ---
  el('build-matrix-btn').addEventListener('click', buildCompatMatrix);
  el('matching-run-btn').addEventListener('click', runMatching);
  el('matching-clear-btn').addEventListener('click', () => {
    el('matching-users').value = 'Anudeep, Sarvesh, Gautam, Priya, Ravi';
    el('matching-resources').value = 'AlgoBook, AIJournal, NetworksPaper, MLDataset, DBTextbook';
    setHTML('compat-matrix-container', '');
    setHTML('matching-results-area', '');
  });

  // Rebuild matrix when inputs change
  el('matching-users').addEventListener('blur', () => {
    if (el('compat-matrix-container').innerHTML) buildCompatMatrix();
  });
  el('matching-resources').addEventListener('blur', () => {
    if (el('compat-matrix-container').innerHTML) buildCompatMatrix();
  });

  // --- Reading Plan (Module 5) ---
  const genPlanBtn = el('generate-plan-btn');
  if (genPlanBtn) genPlanBtn.addEventListener('click', runGenerateReadingPlan);

  const planRandomBtn = el('plan-random-btn');
  if (planRandomBtn) planRandomBtn.addEventListener('click', triggerSurpriseMe);

  const planSpeedInput = el('plan-speed');
  if (planSpeedInput) {
    planSpeedInput.addEventListener('input', updatePlanEstimatedTimes);
    planSpeedInput.addEventListener('change', updatePlanEstimatedTimes);
  }

  const planFilterInput = el('plan-candidate-filter');
  if (planFilterInput) {
    planFilterInput.addEventListener('input', e => {
      renderReadingPlanCandidates(e.target.value);
    });
  }

  const checkAllBox = el('plan-check-all');
  if (checkAllBox) {
    checkAllBox.addEventListener('change', e => {
      const checked = e.target.checked;
      document.querySelectorAll('.plan-doc-check').forEach(cb => {
        cb.checked = checked;
      });
    });
  }

  // Reading Plan presets
  const presetAll = el('plan-preset-all');
  if (presetAll) presetAll.addEventListener('click', () => applyPlanPreset('all'));

  const presetAlgo = el('plan-preset-algo');
  if (presetAlgo) presetAlgo.addEventListener('click', () => applyPlanPreset('algo'));

  const presetAi = el('plan-preset-ai');
  if (presetAi) presetAi.addEventListener('click', () => applyPlanPreset('ai'));

  const presetClear = el('plan-preset-clear');
  if (presetClear) presetClear.addEventListener('click', () => applyPlanPreset('clear'));

  // --- Documents filters ---
  el('docs-filter-cat').addEventListener('change', applyDocsFilter);
  el('docs-sort').addEventListener('change', applyDocsFilter);

  // Load more
  el('docs-load-more-btn').addEventListener('click', () => renderDocPage(false));

  // --- Document Modal close ---
  el('modal-close-btn').addEventListener('click', closeModal);
  el('doc-modal-overlay').addEventListener('click', e => {
    if (e.target === el('doc-modal-overlay')) closeModal();
  });

  // --- Surprise Modal close ---
  const surpriseClose = el('surprise-modal-close-btn');
  if (surpriseClose) surpriseClose.addEventListener('click', closeSurpriseModal);
  const surpriseOverlay = el('surprise-modal-overlay');
  if (surpriseOverlay) {
    surpriseOverlay.addEventListener('click', e => {
      if (e.target === surpriseOverlay) closeSurpriseModal();
    });
  }

  // Escape key closes open modals
  document.addEventListener('keydown', e => {
    if (e.key === 'Escape') {
      closeModal();
      closeSurpriseModal();
    }
  });

  // --- Init ---
  checkStatus();
  populateDocSelects();
  buildCompatMatrix(); // build default matrix on load

  // Restore saved preferences
  setTheme(getCurrentTheme());
  const savedSearchMode = localStorage.getItem(SEARCH_MODE_KEY);
  if (savedSearchMode && el('search-mode')) {
    el('search-mode').value = savedSearchMode;
    if (el('settings-search-mode')) el('settings-search-mode').value = savedSearchMode;
  }
  const savedSpeed = localStorage.getItem(READING_SPEED_KEY);
  if (savedSpeed && el('plan-speed')) {
    el('plan-speed').value = savedSpeed;
    if (el('settings-reading-speed')) el('settings-reading-speed').value = savedSpeed;
  }
  const savedBudget = localStorage.getItem(READING_BUDGET_KEY);
  if (savedBudget && el('plan-budget')) {
    el('plan-budget').value = savedBudget;
    if (el('settings-time-budget')) el('settings-time-budget').value = savedBudget;
  }
});
