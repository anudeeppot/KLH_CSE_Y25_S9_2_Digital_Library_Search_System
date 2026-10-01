package com.library.search.config;

import com.library.search.model.LibraryDocument;
import com.library.search.repository.DocumentRepository;
import com.library.search.service.CorpusReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DocumentRepository documentRepository;
    private final CorpusReader corpusReader;

    public DataInitializer(DocumentRepository documentRepository, CorpusReader corpusReader) {
        this.documentRepository = documentRepository;
        this.corpusReader = corpusReader;
    }

    @Override
    public void run(String... args) {
        if (documentRepository.count() == 0) {
            System.out.println("Initializing Digital Library Repository from corpus folder...");

            String[] candidatePaths = {
                    "corpus",
                    "../corpus",
                    "../../corpus",
                    "DSA Project-3/corpus"
            };

            List<LibraryDocument> docs = null;
            for (String path : candidatePaths) {
                File dir = new File(path);
                if (dir.exists() && dir.isDirectory()) {
                    docs = corpusReader.loadCorpus(path);
                    if (!docs.isEmpty()) {
                        System.out.println("Loaded " + docs.size() + " documents from: " + dir.getAbsolutePath());
                        break;
                    }
                }
            }

            if (docs != null && !docs.isEmpty()) {
                documentRepository.saveAll(docs);
                System.out.println("Successfully seeded " + documentRepository.count() + " library documents into database.");
            } else {
                System.out.println("No corpus files found at paths. Creating fallback foundational documents...");
                createFallbackDocuments();
            }
        } else {
            System.out.println("Database already contains " + documentRepository.count() + " documents. Skipping seeding.");
        }
    }

    private void createFallbackDocuments() {
        LibraryDocument doc1 = new LibraryDocument("01_kmp_algorithm.txt");
        doc1.setTitle("Knuth Morris Pratt Pattern Matching");
        doc1.setAuthor("Donald Knuth, James H. Morris, Vaughan Pratt");
        doc1.setCategory("String Algorithms");
        doc1.setYear("1977");
        doc1.setIsbn("978-0131103627");
        doc1.setDocumentType("Research Paper");
        doc1.setKeywords("KMP, pattern matching, prefix function, LPS, string search");
        doc1.setAbstractText("The Knuth-Morris-Pratt algorithm solves string matching in linear time by preprocessing the pattern into an LPS table.");
        doc1.setContent("Knuth Morris Pratt is a linear time string matching algorithm. It preprocesses a search pattern using the longest proper prefix that is also a suffix.");
        documentRepository.save(doc1);
    }
}
