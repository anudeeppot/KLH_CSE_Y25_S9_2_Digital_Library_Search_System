package com.library.search.service;

import com.library.search.model.LibraryDocument;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Corpus Reader Service.
 * Integrates the user's original CorpusReader.java and extends it to parse
 * extended metadata such as ISBN, document types, abstract, keywords, and citations.
 */
@Component
public class CorpusReader {

    public List<LibraryDocument> loadCorpus(String folderPath) {
        List<LibraryDocument> documents = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Corpus folder not found: " + folderPath);
            return documents;
        }

        File[] files = folder.listFiles();

        if (files == null) {
            return documents;
        }

        // Sort files for deterministic loading
        Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                LibraryDocument document = readDocument(file);
                if (document != null) {
                    documents.add(document);
                }
            }
        }

        return documents;
    }

    private LibraryDocument readDocument(File file) {
        try {
            String content = java.nio.file.Files.readString(file.toPath());
            return parseDocumentFromString(file.getName(), content);
        } catch (IOException e) {
            System.out.println("Could not read " + file.getName() + ": " + e.getMessage());
            return null;
        }
    }

    public LibraryDocument parseDocumentFromString(String fileName, String fileContent) {
        LibraryDocument document = new LibraryDocument(fileName);
        boolean hasStructuredLabels = false;

        try (BufferedReader reader = new BufferedReader(new java.io.StringReader(fileContent))) {
            String line;
            StringBuilder contentBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Title:")) {
                    document.setTitle(valueAfterLabel(line, "Title:"));
                    hasStructuredLabels = true;
                } else if (line.startsWith("Author:")) {
                    document.setAuthor(valueAfterLabel(line, "Author:"));
                    hasStructuredLabels = true;
                } else if (line.startsWith("Category:")) {
                    document.setCategory(valueAfterLabel(line, "Category:"));
                    hasStructuredLabels = true;
                } else if (line.startsWith("Year:")) {
                    document.setYear(valueAfterLabel(line, "Year:"));
                    hasStructuredLabels = true;
                } else if (line.startsWith("ISBN:")) {
                    document.setIsbn(valueAfterLabel(line, "ISBN:"));
                    hasStructuredLabels = true;
                } else if (line.startsWith("Type:")) {
                    document.setDocumentType(valueAfterLabel(line, "Type:"));
                    hasStructuredLabels = true;
                } else if (line.startsWith("Keywords:")) {
                    document.setKeywords(valueAfterLabel(line, "Keywords:"));
                    hasStructuredLabels = true;
                } else if (line.startsWith("Abstract:")) {
                    document.setAbstractText(valueAfterLabel(line, "Abstract:"));
                    hasStructuredLabels = true;
                } else if (line.startsWith("Citations:")) {
                    String citationStr = valueAfterLabel(line, "Citations:");
                    String[] citArr = citationStr.split(",");
                    List<String> citList = new ArrayList<>();
                    for (String c : citArr) {
                        String trimmed = c.trim();
                        if (!trimmed.isEmpty()) {
                            citList.add(trimmed);
                        }
                    }
                    document.setCitations(citList);
                    document.setCitationCount(citList.size());
                    hasStructuredLabels = true;
                } else if (line.startsWith("Content:")) {
                    contentBuilder.append(valueAfterLabel(line, "Content:"));
                    hasStructuredLabels = true;
                } else {
                    contentBuilder.append(" ").append(line.trim());
                }

                contentBuilder.append(" ");
            }

            if (hasStructuredLabels) {
                document.setContent(contentBuilder.toString().trim());
            } else {
                // Freeform text fallback
                String cleanName = fileName != null ? fileName.replaceFirst("[.][^.]+$", "").replace('_', ' ') : "Uploaded Document";
                document.setTitle(cleanName.substring(0, 1).toUpperCase() + cleanName.substring(1));
                document.setAuthor("Uploaded Document");
                document.setCategory("Computer Science");
                document.setYear("2026");
                document.setDocumentType("Research Paper");
                document.setContent(fileContent.trim());
                if (fileContent.length() > 200) {
                    document.setAbstractText(fileContent.substring(0, 200).trim() + "...");
                } else {
                    document.setAbstractText(fileContent.trim());
                }
            }
            return document;

        } catch (IOException e) {
            System.out.println("Could not parse string content: " + e.getMessage());
            return null;
        }
    }

    private String valueAfterLabel(String line, String label) {
        return line.substring(label.length()).trim();
    }
}
