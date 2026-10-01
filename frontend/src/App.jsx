import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Footer from './components/Footer';

// Pages
import HomePage from './pages/HomePage';
import SearchPage from './pages/SearchPage';
import DocumentsPage from './pages/DocumentsPage';
import DocumentDetailPage from './pages/DocumentDetailPage';
import AlgorithmsHubPage from './pages/AlgorithmsHubPage';
import KmpDemoPage from './pages/KmpDemoPage';
import RabinKarpDemoPage from './pages/RabinKarpDemoPage';
import BoyerMooreDemoPage from './pages/BoyerMooreDemoPage';
import EditDistanceDemoPage from './pages/EditDistanceDemoPage';
import SuffixArrayDemoPage from './pages/SuffixArrayDemoPage';
import SimilarityDemoPage from './pages/SimilarityDemoPage';
import ComparePage from './pages/ComparePage';
import ParallelBenchmarkPage from './pages/ParallelBenchmarkPage';
import RandomizedSamplingPage from './pages/RandomizedSamplingPage';
import AboutPage from './pages/AboutPage';

function App() {
  return (
    <div className="app-container">
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/documents" element={<DocumentsPage />} />
          <Route path="/documents/:id" element={<DocumentDetailPage />} />
          <Route path="/algorithms" element={<AlgorithmsHubPage />} />
          <Route path="/algorithms/kmp" element={<KmpDemoPage />} />
          <Route path="/algorithms/rabin-karp" element={<RabinKarpDemoPage />} />
          <Route path="/algorithms/boyer-moore" element={<BoyerMooreDemoPage />} />
          <Route path="/algorithms/edit-distance" element={<EditDistanceDemoPage />} />
          <Route path="/algorithms/suffix-array" element={<SuffixArrayDemoPage />} />
          <Route path="/algorithms/similarity" element={<SimilarityDemoPage />} />
          <Route path="/algorithms/parallel" element={<ParallelBenchmarkPage />} />
          <Route path="/algorithms/randomized" element={<RandomizedSamplingPage />} />
          <Route path="/compare" element={<ComparePage />} />
          <Route path="/about" element={<AboutPage />} />
          {/* Fallback to Home */}
          <Route path="*" element={<HomePage />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;
