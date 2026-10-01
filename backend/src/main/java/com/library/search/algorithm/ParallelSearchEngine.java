package com.library.search.algorithm;

import com.library.search.model.LibraryDocument;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.*;

/**
 * Module 6: Parallel and Concurrent Processing for High-Throughput Text Search.
 *
 * Compares multi-threaded parallel search using ExecutorService with sequential scanning.
 * Provides empirical timing, speedup factor, and Amdahl's Law analysis.
 */
@Component
public class ParallelSearchEngine {

    private final KMP kmp;
    private final ExecutorService executorService;

    public ParallelSearchEngine(KMP kmp) {
        this.kmp = kmp;
        int cores = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(Math.max(2, cores));
    }

    public static class ParallelBenchmarkResult {
        private final int totalDocuments;
        private final int totalMatches;
        private final long sequentialTimeNanos;
        private final long parallelTimeNanos;
        private final double speedup;
        private final int threadPoolSize;
        private final String analysis;

        public ParallelBenchmarkResult(int totalDocuments, int totalMatches, long sequentialTimeNanos,
                                       long parallelTimeNanos, double speedup, int threadPoolSize, String analysis) {
            this.totalDocuments = totalDocuments;
            this.totalMatches = totalMatches;
            this.sequentialTimeNanos = sequentialTimeNanos;
            this.parallelTimeNanos = parallelTimeNanos;
            this.speedup = speedup;
            this.threadPoolSize = threadPoolSize;
            this.analysis = analysis;
        }

        public int getTotalDocuments() { return totalDocuments; }
        public int getTotalMatches() { return totalMatches; }
        public long getSequentialTimeNanos() { return sequentialTimeNanos; }
        public long getParallelTimeNanos() { return parallelTimeNanos; }
        public double getSpeedup() { return speedup; }
        public int getThreadPoolSize() { return threadPoolSize; }
        public String getAnalysis() { return analysis; }
    }

    public ParallelBenchmarkResult runBenchmark(List<LibraryDocument> documents, String query, int repetitionMultiplier) {
        // Expand dataset virtually if needed to provide measurable multi-core workload
        List<LibraryDocument> workList = new ArrayList<>();
        int repeat = Math.max(1, Math.min(repetitionMultiplier, 50));
        for (int r = 0; r < repeat; r++) {
            workList.addAll(documents);
        }

        // 1. Sequential Search
        long seqStart = System.nanoTime();
        int seqMatches = 0;
        for (LibraryDocument doc : workList) {
            List<Integer> titleMatches = kmp.search(doc.getTitle(), query);
            List<Integer> contentMatches = kmp.search(doc.getContent(), query);
            seqMatches += titleMatches.size() + contentMatches.size();
        }
        long seqTime = System.nanoTime() - seqStart;

        // 2. Parallel Search via ExecutorService
        int cores = Runtime.getRuntime().availableProcessors();
        long parStart = System.nanoTime();
        int parMatches = 0;

        try {
            int chunkSize = Math.max(1, workList.size() / cores);
            List<Callable<Integer>> tasks = new ArrayList<>();

            for (int i = 0; i < workList.size(); i += chunkSize) {
                int start = i;
                int end = Math.min(i + chunkSize, workList.size());
                List<LibraryDocument> subList = workList.subList(start, end);

                tasks.add(() -> {
                    int localCount = 0;
                    for (LibraryDocument doc : subList) {
                        localCount += kmp.search(doc.getTitle(), query).size();
                        localCount += kmp.search(doc.getContent(), query).size();
                    }
                    return localCount;
                });
            }

            List<Future<Integer>> futures = executorService.invokeAll(tasks);
            for (Future<Integer> f : futures) {
                parMatches += f.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
        long parTime = System.nanoTime() - parStart;

        double speedup = (parTime > 0) ? Math.round(((double) seqTime / parTime) * 100.0) / 100.0 : 1.0;

        String analysis = "Sequential scan processed " + workList.size() + " documents in " + (seqTime / 1_000_000.0) + " ms, whereas multi-threaded parallel search (" + cores + " worker threads) completed in " + (parTime / 1_000_000.0) + " ms (Speedup: " + speedup + "x). Speedup is bounded by Amdahl's Law and thread synchronization / cache coherency overhead.";

        return new ParallelBenchmarkResult(workList.size(), parMatches, seqTime, parTime, speedup, cores, analysis);
    }
}
