import axios from 'axios';

// Centralized API Base URL configuration
const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000,
});

// Centralized error handling interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response ? error.response.data : error.message);
    return Promise.reject(error);
  }
);

export const searchAPI = {
  // Search endpoints
  search: (params) => apiClient.get('/search', { params }),
  searchPost: (data) => apiClient.post('/search', data),

  // Document endpoints
  getAllDocuments: () => apiClient.get('/documents'),
  getDocumentById: (id) => apiClient.get(`/documents/${id}`),
  createDocument: (data) => apiClient.post('/documents', data),
  uploadDocumentFile: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  updateDocument: (id, data) => apiClient.put(`/documents/${id}`, data),
  deleteDocument: (id) => apiClient.delete(`/documents/${id}`),
  getSimilarDocuments: (id) => apiClient.get(`/documents/${id}/similar`),
  getDocumentStats: () => apiClient.get('/documents/stats'),

  // CO-1: Problem-Class Signature Evaluator
  evaluateSignature: (profile) => apiClient.post('/algorithms/evaluate-signature', profile),

  // CO-2: Linear-Time String Algorithms & Suffix Structures
  testKMP: (data) => apiClient.post('/algorithms/kmp', data),
  testZAlgorithm: (data) => apiClient.post('/algorithms/z-algorithm', data),
  testRabinKarp: (data) => apiClient.post('/algorithms/rabin-karp', data),
  testBoyerMoore: (data) => apiClient.post('/algorithms/boyer-moore', data),
  testSuffixArray: (data) => apiClient.post('/algorithms/suffix-array', data),
  testSuffixAutomaton: (data) => apiClient.post('/algorithms/suffix-automaton', data),
  compareAlgorithms: (data) => apiClient.post('/algorithms/compare', data),

  // CO-3: Advanced Dynamic Programming Suite
  testIntervalDP: (data) => apiClient.post('/algorithms/interval-dp', data || {}),
  testBitmaskDP: (data) => apiClient.post('/algorithms/bitmask-dp', data || {}),
  getTreeDP: () => apiClient.get('/algorithms/tree-dp'),
  testSequenceAlignment: (data) => apiClient.post('/algorithms/sequence-alignment', data),
  testEditDistance: (data) => apiClient.post('/algorithms/edit-distance', data),
  testSimilarity: (data) => apiClient.post('/algorithms/similarity', data),

  // CO-4: Network Flow & Max-Flow / Min-Cut Duality
  getReservationFlow: (algo = 'EDMONDS_KARP') => apiClient.get(`/algorithms/flow/reservation?algorithm=${algo}`),
  getCdnFlow: (algo = 'DINIC') => apiClient.get(`/algorithms/flow/cdn?algorithm=${algo}`),
};

export default apiClient;
