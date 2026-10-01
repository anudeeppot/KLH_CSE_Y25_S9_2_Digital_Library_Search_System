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

  // DSA Algorithm Demonstration Endpoints
  testKMP: (data) => apiClient.post('/algorithms/kmp', data),
  testRabinKarp: (data) => apiClient.post('/algorithms/rabin-karp', data),
  testBoyerMoore: (data) => apiClient.post('/algorithms/boyer-moore', data),
  testEditDistance: (data) => apiClient.post('/algorithms/edit-distance', data),
  testSuffixArray: (data) => apiClient.post('/algorithms/suffix-array', data),
  testSimilarity: (data) => apiClient.post('/algorithms/similarity', data),
  compareAlgorithms: (data) => apiClient.post('/algorithms/compare', data),
  runParallelBenchmark: (query, multiplier) =>
    apiClient.post(`/algorithms/parallel?query=${encodeURIComponent(query)}&multiplier=${multiplier}`),
  sampleCorpus: (k) => apiClient.get(`/algorithms/randomized?k=${k}`),
};

export default apiClient;
