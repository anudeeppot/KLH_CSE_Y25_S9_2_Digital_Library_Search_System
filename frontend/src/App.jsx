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
import CO1SignaturePage from './pages/CO1SignaturePage';
import KmpDemoPage from './pages/KmpDemoPage';
import ZAlgorithmDemoPage from './pages/ZAlgorithmDemoPage';
import RabinKarpDemoPage from './pages/RabinKarpDemoPage';
import BoyerMooreDemoPage from './pages/BoyerMooreDemoPage';
import EditDistanceDemoPage from './pages/EditDistanceDemoPage';
import SuffixArrayDemoPage from './pages/SuffixArrayDemoPage';
import SuffixAutomatonDemoPage from './pages/SuffixAutomatonDemoPage';
import AdvancedDPPage from './pages/AdvancedDPPage';
import NetworkFlowPage from './pages/NetworkFlowPage';
import SimilarityDemoPage from './pages/SimilarityDemoPage';
import ComparePage from './pages/ComparePage';
import SyllabusVivaPage from './pages/SyllabusVivaPage';
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

          {/* CO-1 */}
          <Route path="/algorithms/signature" element={<CO1SignaturePage />} />

          {/* CO-2 */}
          <Route path="/algorithms/kmp" element={<KmpDemoPage />} />
          <Route path="/algorithms/z-algorithm" element={<ZAlgorithmDemoPage />} />
          <Route path="/algorithms/rabin-karp" element={<RabinKarpDemoPage />} />
          <Route path="/algorithms/boyer-moore" element={<BoyerMooreDemoPage />} />
          <Route path="/algorithms/suffix-array" element={<SuffixArrayDemoPage />} />
          <Route path="/algorithms/suffix-automaton" element={<SuffixAutomatonDemoPage />} />

          {/* CO-3 */}
          <Route path="/algorithms/advanced-dp" element={<AdvancedDPPage />} />
          <Route path="/algorithms/edit-distance" element={<EditDistanceDemoPage />} />
          <Route path="/algorithms/similarity" element={<SimilarityDemoPage />} />

          {/* CO-4 */}
          <Route path="/algorithms/network-flow" element={<NetworkFlowPage />} />

          {/* Cross Benchmarking & Viva */}
          <Route path="/compare" element={<ComparePage />} />
          <Route path="/viva-guide" element={<SyllabusVivaPage />} />
          <Route path="/about" element={<AboutPage />} />

          {/* Fallback */}
          <Route path="*" element={<HomePage />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;
