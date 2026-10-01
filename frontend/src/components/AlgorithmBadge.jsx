import React from 'react';

const AlgorithmBadge = ({ type, text }) => {
  const norm = (type || text || '').toLowerCase().replace(/[^a-z]/g, '');

  let badgeClass = 'badge-kmp';
  if (norm.includes('rabin')) badgeClass = 'badge-rabin';
  else if (norm.includes('boyer') || norm.includes('bm')) badgeClass = 'badge-bm';
  else if (norm.includes('edit') || norm.includes('fuzzy')) badgeClass = 'badge-edit';
  else if (norm.includes('suffix')) badgeClass = 'badge-suffix';
  else if (norm.includes('graph') || norm.includes('bfs') || norm.includes('dfs')) badgeClass = 'badge-graph';
  else if (norm.includes('auto')) badgeClass = 'badge-auto';
  else if (norm.includes('book')) badgeClass = 'badge-book';
  else if (norm.includes('paper')) badgeClass = 'badge-paper';
  else if (norm.includes('journal')) badgeClass = 'badge-journal';
  else if (norm.includes('magazine')) badgeClass = 'badge-magazine';

  return (
    <span className={`badge ${badgeClass}`}>
      {text || type}
    </span>
  );
};

export default AlgorithmBadge;
