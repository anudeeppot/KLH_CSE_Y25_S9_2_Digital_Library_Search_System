import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        LibraryDocument document = new LibraryDocument(file.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder contentBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Title:")) {
                    document.setTitle(valueAfterLabel(line, "Title:"));
                } else if (line.startsWith("Author:")) {
                    document.setAuthor(valueAfterLabel(line, "Author:"));
                } else if (line.startsWith("Category:")) {
                    document.setCategory(valueAfterLabel(line, "Category:"));
                } else if (line.startsWith("Year:")) {
                    document.setYear(valueAfterLabel(line, "Year:"));
                } else if (line.startsWith("Content:")) {
                    contentBuilder.append(valueAfterLabel(line, "Content:"));
                } else {
                    contentBuilder.append(" ").append(line.trim());
                }

                contentBuilder.append(" ");
            }

            document.setContent(contentBuilder.toString().trim());
            return document;

        } catch (IOException e) {
            System.out.println(
                "Could not read " + file.getName() + ": " + e.getMessage()
            );
            return null;
        }
    }

    private String valueAfterLabel(String line, String label) {
        return line.substring(label.length()).trim();
    }
}
