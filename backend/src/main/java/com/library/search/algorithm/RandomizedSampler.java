package com.library.search.algorithm;

import com.library.search.model.LibraryDocument;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Module 6: Randomized Algorithm Demonstration for Digital Library Systems.
 *
 * Implements Reservoir Sampling (Algorithm R by Alan G. Waterman).
 * Selects an unbiased, uniform random sample of k documents from a stream or collection of size N
 * in a single O(N) pass without requiring all items to fit in memory.
 *
 * Mathematical Guarantee:
 * Every document has exactly k/N probability of appearing in the sample reservoir.
 */
@Component
public class RandomizedSampler {

    public static class SamplingResult {
        private final int sampleSize;
        private final int totalPopulation;
        private final List<SampledDocumentSummary> sampledDocuments;
        private final long executionTimeNanos;
        private final String mathematicalProof;

        public SamplingResult(int sampleSize, int totalPopulation, List<SampledDocumentSummary> sampledDocuments,
                              long executionTimeNanos, String mathematicalProof) {
            this.sampleSize = sampleSize;
            this.totalPopulation = totalPopulation;
            this.sampledDocuments = sampledDocuments;
            this.executionTimeNanos = executionTimeNanos;
            this.mathematicalProof = mathematicalProof;
        }

        public int getSampleSize() { return sampleSize; }
        public int getTotalPopulation() { return totalPopulation; }
        public List<SampledDocumentSummary> getSampledDocuments() { return sampledDocuments; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public String getMathematicalProof() { return mathematicalProof; }
    }

    public static class SampledDocumentSummary {
        private final Long id;
        private final String title;
        private final String author;
        private final String category;
        private final String documentType;

        public SampledDocumentSummary(Long id, String title, String author, String category, String documentType) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.category = category;
            this.documentType = documentType;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getCategory() { return category; }
        public String getDocumentType() { return documentType; }
    }

    public SamplingResult sampleDocuments(List<LibraryDocument> population, int k) {
        long startTime = System.nanoTime();
        int n = population.size();
        int targetK = Math.min(Math.max(1, k), n);

        List<LibraryDocument> reservoir = new ArrayList<>(targetK);
        Random random = new Random();

        // Step 1: Fill the reservoir with the first k items
        for (int i = 0; i < targetK; i++) {
            reservoir.add(population.get(i));
        }

        // Step 2: Iterate through the remaining items (from k to n-1)
        for (int i = targetK; i < n; i++) {
            int j = random.nextInt(i + 1); // Random index between 0 and i inclusive
            if (j < targetK) {
                reservoir.set(j, population.get(i));
            }
        }

        List<SampledDocumentSummary> summaries = new ArrayList<>();
        for (LibraryDocument doc : reservoir) {
            summaries.add(new SampledDocumentSummary(doc.getId(), doc.getTitle(), doc.getAuthor(), doc.getCategory(), doc.getDocumentType()));
        }

        long executionTime = System.nanoTime() - startTime;
        String proof = "Reservoir Sampling Proof: For any item i > k, probability of being selected at step i is k/(i+1). For an item already in the reservoir, the probability of surviving subsequent replacements from step i+1 to N telescopes to exactly k/N. Hence, every document has an identical k/N probability of inclusion.";

        return new SamplingResult(targetK, n, summaries, executionTime, proof);
    }
}
