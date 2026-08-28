# Digital Library Search System — KMP Module

A Java console application that indexes a small corpus of library documents (books, journals, magazines, research papers) and lets users search them using the **Knuth–Morris–Pratt (KMP)** string-matching algorithm.

This module is the KMP-based search component of the larger *Digital Library Search System* project (Data Structures and Algorithms course, CO2 outcome).

## Features

- Loads all `.txt` documents from a `corpus/` folder automatically — no source changes needed to add new documents.
- Parses structured fields from each document: `Title`, `Author`, `Category`, `Year`, `Content`.
- Full-corpus search across all fields (title, author, category, content) using KMP.
- Standalone KMP demonstration mode: pick a document, enter a pattern, and see the LPS (Longest Proper Prefix which is also Suffix) array, match positions, and time/space complexity.
- Case-insensitive matching.
- Corpus can be reloaded at runtime without restarting the app.

## Project Structure

```
DigitalLibrarySearch_KMP_Codebase/
├── Main.java                 # Entry point, console menu, user interaction
├── KMP.java                  # Core KMP algorithm: buildLPS() and search()
├── LibraryDocument.java      # Data model for a single document
├── CorpusReader.java         # Loads and parses .txt files from corpus/
├── LibrarySearchEngine.java  # Applies KMP across all documents/fields
├── corpus/                   # 20 sample .txt documents
│   ├── 01_kmp_algorithm.txt
│   ├── 02_digital_library_overview.txt
│   ├── ...
│   └── 20_kmp_library_application.txt
└── README.md
```

## How It Works

1. **`Main`** starts the program and loads every `.txt` file from the `corpus/` folder.
2. **`CorpusReader`** reads each file line by line, extracting `Title:`, `Author:`, `Category:`, `Year:`, and `Content:` fields into a `LibraryDocument`.
3. **`LibraryDocument`** is a simple data holder for one document's metadata and text.
4. **`KMP`** implements the algorithm from scratch:
   - `buildLPS(pattern)` — builds the prefix-suffix table in O(m) time.
   - `search(text, pattern)` — scans the text in O(n + m) time using the LPS table, returning every match position.
5. **`LibrarySearchEngine`** runs a query against the `Title`, `Author`, `Category`, and `Content` of every document and collects the results.
6. **`Main`** also calls `KMP` directly for a single-document demonstration, printing the LPS array and complexity notes.

**Note:** KMP is the only pattern-matching algorithm used here. No `contains()`, `indexOf()`, regex, Rabin-Karp, Boyer-Moore, Z-function, suffix array, or LCP-based search is used — this keeps the module focused purely on demonstrating KMP.

## Requirements

- Java JDK 8 or later
- (Optional) VS Code with the "Extension Pack for Java", or any IDE that runs plain Java

## Running the Project

### VS Code
1. Open the `DigitalLibrarySearch_KMP_Codebase` folder in VS Code.
2. Make sure the Java JDK and "Extension Pack for Java" are installed.
3. Open `Main.java` and click **Run**.
4. Keep the `corpus/` folder in the same directory as the `.java` files.

### Command line
```bash
cd DigitalLibrarySearch_KMP_Codebase
javac *.java
java Main
```

## Usage

On launch, the app loads the corpus and shows a menu:

```
1. Search corpus
2. Demonstrate KMP on one document
3. Reload corpus
4. Exit
```

- **Search corpus** — enter a keyword (e.g. `algorithm`, `library`, `KMP`) to search across all documents' title, author, category, and content. Results show the matched field and every match position.
- **Demonstrate KMP on one document** — pick a document and a pattern to see the LPS array, match positions, and KMP's time/space complexity (`O(m)` for LPS construction, `O(n + m)` for search, `O(m)` extra space).
- **Reload corpus** — re-reads the `corpus/` folder, picking up any newly added `.txt` files.
- **Exit** — closes the program.

### Example searches
```
algorithm
library
research
KMP
data
```

## Adding Documents

Drop a new `.txt` file into `corpus/` using this format, then choose **Reload corpus**:

```
Title: <document title>
Author: <author name>
Category: <e.g. Book, Journal, Magazine, Research Paper>
Year: <year>
Content: <document text>
```

No Java code changes are required.
