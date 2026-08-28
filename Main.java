import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        String corpusPath = "corpus";

        CorpusReader corpusReader = new CorpusReader();
        List<LibraryDocument> documents =
                corpusReader.loadCorpus(corpusPath);

        KMP kmp = new KMP();
        LibrarySearchEngine searchEngine =
                new LibrarySearchEngine(kmp);

        System.out.println("==========================================");
        System.out.println("      DIGITAL LIBRARY SEARCH SYSTEM");
        System.out.println("      CO2 - KMP PATTERN MATCHING");
        System.out.println("==========================================");
        System.out.println("Corpus documents loaded: " + documents.size());

        if (documents.isEmpty()) {
            System.out.println("Add .txt files to the corpus folder and run again.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Search corpus");
            System.out.println("2. Demonstrate KMP on one document");
            System.out.println("3. Reload corpus");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Enter search query: ");
                String query = scanner.nextLine();

                if (query.trim().isEmpty()) {
                    System.out.println("Query cannot be empty.");
                    continue;
                }

                List<LibrarySearchEngine.SearchMatch> results =
                        searchEngine.search(documents, query);

                System.out.println("\n========== SEARCH RESULTS ==========");

                if (results.isEmpty()) {
                    System.out.println("No matching documents found.");
                } else {
                    for (LibrarySearchEngine.SearchMatch result : results) {
                        LibraryDocument document = result.getDocument();

                        System.out.println("\nFile     : " + document.getFileName());
                        System.out.println("Title    : " + document.getTitle());
                        System.out.println("Author   : " + document.getAuthor());
                        System.out.println("Category : " + document.getCategory());
                        System.out.println("Matched  : " + result.getFieldName());
                        System.out.println("Positions: " + result.getPositions());
                        System.out.println("Matches  : " + result.getPositions().size());
                    }
                }

            } else if (choice.equals("2")) {
                System.out.println("\nAvailable documents:");

                for (int i = 0; i < documents.size(); i++) {
                    System.out.println(
                        (i + 1) + ". " + documents.get(i).getFileName() +
                        " - " + documents.get(i).getTitle()
                    );
                }

                System.out.print("Enter document number: ");

                try {
                    int number = Integer.parseInt(scanner.nextLine().trim());

                    if (number < 1 || number > documents.size()) {
                        System.out.println("Invalid document number.");
                        continue;
                    }

                    LibraryDocument document = documents.get(number - 1);

                    System.out.print("Enter pattern: ");
                    String pattern = scanner.nextLine();

                    if (pattern.trim().isEmpty()) {
                        System.out.println("Pattern cannot be empty.");
                        continue;
                    }

                    // Direct KMP call from Main for CO2 demonstration.
                    List<Integer> positions =
                            kmp.search(document.getContent(), pattern);

                    int[] lps = kmp.buildLPS(pattern);

                    System.out.println("\n========== KMP DEMONSTRATION ==========");
                    System.out.println("Document : " + document.getFileName());
                    System.out.println("Pattern  : " + pattern);

                    System.out.print("LPS      : ");
                    for (int value : lps) {
                        System.out.print(value + " ");
                    }
                    System.out.println();

                    System.out.println("Found    : " + (!positions.isEmpty() ? "YES" : "NO"));
                    System.out.println("Positions: " + positions);
                    System.out.println("Matches  : " + positions.size());

                    System.out.println("\nComplexity:");
                    System.out.println("LPS construction : O(m)");
                    System.out.println("KMP search       : O(n + m)");
                    System.out.println("Extra space      : O(m)");

                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid number.");
                }

            } else if (choice.equals("3")) {
                documents = corpusReader.loadCorpus(corpusPath);
                System.out.println(
                    "Corpus reloaded. Documents loaded: " + documents.size()
                );

            } else if (choice.equals("4")) {
                System.out.println("Exiting Digital Library Search System.");
                scanner.close();
                return;

            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
