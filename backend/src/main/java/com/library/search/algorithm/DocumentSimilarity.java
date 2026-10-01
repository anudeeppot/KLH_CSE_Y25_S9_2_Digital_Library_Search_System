package com.library.search.algorithm;

import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Document Similarity Calculator using TF-IDF (Term Frequency - Inverse Document Frequency)
 * and Cosine Similarity in a Vector Space Model.
 *
 * Used to identify related research papers, books, and articles in the Digital Library.
 *
 * Mathematical Foundations:
 * - TF(t, d) = count(t in d) / total_words(d)
 * - IDF(t, D) = ln(1 + |D| / DF(t))
 * - Vector(d) = [TF-IDF(t1, d), TF-IDF(t2, d), ...]
 * - CosineSimilarity(d1, d2) = (v1 . v2) / (||v1|| * ||v2||)
 */
@Component
public class DocumentSimilarity {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by",
            "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't",
            "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have",
            "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself",
            "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into",
            "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my",
            "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our",
            "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's",
            "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs",
            "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're",
            "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't",
            "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's",
            "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't",
            "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself"
    ));

    public static class SimilarityResult {
        private final double score;
        private final double percentage;
        private final List<String> sharedKeywords;
        private final long executionTime;

        public SimilarityResult(double score, double percentage, List<String> sharedKeywords, long executionTime) {
            this.score = score;
            this.percentage = percentage;
            this.sharedKeywords = sharedKeywords;
            this.executionTime = executionTime;
        }

        public double getScore() { return score; }
        public double getPercentage() { return percentage; }
        public List<String> getSharedKeywords() { return sharedKeywords; }
        public long getExecutionTime() { return executionTime; }
    }

    public List<String> tokenize(String text) {
        if (text == null) return Collections.emptyList();
        String[] rawTokens = text.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", " ").split("\\s+");
        List<String> tokens = new ArrayList<>();
        for (String t : rawTokens) {
            String trimmed = t.trim();
            if (trimmed.length() > 2 && !STOP_WORDS.contains(trimmed)) {
                tokens.add(trimmed);
            }
        }
        return tokens;
    }

    public Map<String, Integer> getTermFrequencies(List<String> tokens) {
        Map<String, Integer> tf = new HashMap<>();
        for (String token : tokens) {
            tf.put(token, tf.getOrDefault(token, 0) + 1);
        }
        return tf;
    }

    public SimilarityResult calculateCosineSimilarity(String text1, String text2) {
        long startTime = System.nanoTime();

        List<String> tokens1 = tokenize(text1);
        List<String> tokens2 = tokenize(text2);

        if (tokens1.isEmpty() || tokens2.isEmpty()) {
            return new SimilarityResult(0.0, 0.0, Collections.emptyList(), System.nanoTime() - startTime);
        }

        Map<String, Integer> tf1 = getTermFrequencies(tokens1);
        Map<String, Integer> tf2 = getTermFrequencies(tokens2);

        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(tf1.keySet());
        allTerms.addAll(tf2.keySet());

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        List<String> sharedKeywords = new ArrayList<>();

        for (String term : allTerms) {
            int freq1 = tf1.getOrDefault(term, 0);
            int freq2 = tf2.getOrDefault(term, 0);

            dotProduct += (double) freq1 * freq2;
            norm1 += (double) freq1 * freq1;
            norm2 += (double) freq2 * freq2;

            if (freq1 > 0 && freq2 > 0) {
                sharedKeywords.add(term);
            }
        }

        double denominator = Math.sqrt(norm1) * Math.sqrt(norm2);
        double cosine = (denominator == 0) ? 0.0 : (dotProduct / denominator);

        // Sort shared keywords by frequency product descending
        sharedKeywords.sort((a, b) -> Integer.compare(
                tf1.get(b) * tf2.get(b),
                tf1.get(a) * tf2.get(a)
        ));
        if (sharedKeywords.size() > 8) {
            sharedKeywords = sharedKeywords.subList(0, 8);
        }

        double percentage = Math.round(cosine * 10000.0) / 100.0;
        long executionTime = System.nanoTime() - startTime;

        return new SimilarityResult(cosine, percentage, sharedKeywords, executionTime);
    }
}
