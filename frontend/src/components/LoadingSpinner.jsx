import React from 'react';

const LoadingSpinner = ({ message = 'Executing Data Structure & Algorithm operation...' }) => {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '3rem 1.5rem',
      gap: '1rem',
      color: 'var(--text-secondary)',
    }}>
      <div style={{
        width: '44px',
        height: '44px',
        border: '3px solid rgba(56, 189, 248, 0.2)',
        borderTopColor: 'var(--accent-cyan)',
        borderRadius: '50%',
        animation: 'spin 0.8s linear infinite',
      }} />
      <div style={{ fontSize: '0.92rem', fontWeight: 500, color: 'var(--text-secondary)' }}>
        {message}
      </div>
      <style>{`
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};

export default LoadingSpinner;
