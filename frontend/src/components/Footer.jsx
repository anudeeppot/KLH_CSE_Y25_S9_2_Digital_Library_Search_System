import React from 'react';
import { Link } from 'react-router-dom';
import { BookOpen, Code2, Database, Terminal, Cpu, ShieldCheck } from 'lucide-react';

const Footer = () => {
  return (
    <footer style={{
      backgroundColor: 'var(--footer-bg)',
      borderTop: '1px solid var(--border-subtle)',
      padding: '3rem 1.5rem 2rem',
      color: 'var(--text-secondary)',
      fontSize: '0.88rem',
      marginTop: 'auto',
      transition: 'background-color 0.25s ease, border-color 0.25s ease',
    }}>
      <div style={{
        maxWidth: '1280px',
        margin: '0 auto',
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
        gap: '2.5rem',
        marginBottom: '2.5rem',
      }}>
        {/* Brand & Project Info */}
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.8rem', color: 'var(--text-primary)', fontWeight: 700, fontSize: '1.1rem' }}>
            <BookOpen size={20} color="var(--accent-cyan)" />
            Digital Library Search System
          </div>
          <p style={{ lineHeight: 1.6, color: 'var(--text-muted)', marginBottom: '1rem' }}>
            A rigorous B.Tech CSE DSA-3 Capstone project demonstrating advanced string algorithms, dynamic programming, suffix indexing, and vector similarity.
          </p>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(56, 189, 248, 0.1)', color: 'var(--accent-cyan)', padding: '0.3rem 0.75rem', borderRadius: '6px', fontSize: '0.78rem', fontWeight: 600 }}>
            <ShieldCheck size={14} /> Academic Review Ready
          </div>
        </div>

        {/* DSA Algorithms */}
        <div>
          <h4 style={{ color: 'var(--text-primary)', fontSize: '0.95rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
            <Cpu size={16} color="var(--accent-indigo)" /> Implemented Algorithms
          </h4>
          <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.45rem' }}>
            <li><Link to="/algorithms/kmp" style={{ color: 'var(--text-secondary)' }}>Knuth-Morris-Pratt (KMP)</Link></li>
            <li><Link to="/algorithms/rabin-karp" style={{ color: 'var(--text-secondary)' }}>Rabin-Karp Rolling Hash</Link></li>
            <li><Link to="/algorithms/suffix-array" style={{ color: 'var(--text-secondary)' }}>Suffix Arrays & LCP Binary Search</Link></li>
          </ul>
        </div>

        {/* Quick Links */}
        <div>
          <h4 style={{ color: 'var(--text-primary)', fontSize: '0.95rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
            <Terminal size={16} color="var(--accent-emerald)" /> System Navigation
          </h4>
          <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.45rem' }}>
            <li><Link to="/" style={{ color: 'var(--text-secondary)' }}>Library Dashboard</Link></li>
            <li><Link to="/search" style={{ color: 'var(--text-secondary)' }}>Interactive Search Engine</Link></li>
            <li><Link to="/documents" style={{ color: 'var(--text-secondary)' }}>Document Repository</Link></li>
            <li><Link to="/compare" style={{ color: 'var(--text-secondary)' }}>Algorithm Benchmark</Link></li>
            <li><Link to="/about" style={{ color: 'var(--text-secondary)' }}>Architecture & Viva Guide</Link></li>
          </ul>
        </div>

        {/* Technology Stack */}
        <div>
          <h4 style={{ color: 'var(--text-primary)', fontSize: '0.95rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
            <Database size={16} color="var(--accent-amber)" /> Tech Architecture
          </h4>
          <p style={{ color: 'var(--text-muted)', marginBottom: '0.6rem' }}>
            <strong>Backend:</strong> Java 17/21/25, Spring Boot 3, Maven, REST APIs
          </p>
          <p style={{ color: 'var(--text-muted)', marginBottom: '0.6rem' }}>
            <strong>Frontend:</strong> React, Vite, React Router DOM, Axios
          </p>
          <p style={{ color: 'var(--text-muted)' }}>
            <strong>Storage:</strong> Embedded H2 DB & MySQL Schema DDL
          </p>
        </div>
      </div>

      <div style={{
        maxWidth: '1280px',
        margin: '0 auto',
        paddingTop: '1.5rem',
        borderTop: '1px solid rgba(255, 255, 255, 0.05)',
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'space-between',
        alignItems: 'center',
        gap: '1rem',
        fontSize: '0.8rem',
        color: 'var(--text-muted)',
      }}>
        <div>
          © 2026 Digital Library Search System. Built for B.Tech Computer Science & Engineering (DSA-3).
        </div>
        <div style={{ display: 'flex', gap: '1.5rem' }}>
          <span>DSA Course Outcome: CO2, CO3, CO4</span>
          <span>Zero Mock Data • Verified Native Java Implementations</span>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
