public class LibraryDocument {
    private final String fileName;
    private String title = "";
    private String author = "";
    private String isbn = "";
    private String category = "";
    private String year = "";
    private String content = "";

    public LibraryDocument(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() { return fileName; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getCategory() { return category; }
    public String getYear() { return year; }
    public String getContent() { return content; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setCategory(String category) { this.category = category; }
    public void setYear(String year) { this.year = year; }
    public void setContent(String content) { this.content = content; }

    /** Calculate the total word count of this document's content. */
    public int getWordCount() {
        if (content == null || content.trim().isEmpty()) return 0;
        String[] tokens = content.trim().split("\\s+");
        return tokens.length;
    }

    /**
     * Estimate reading time in minutes based on words per minute (WPM).
     * Formula: max(1, ceil(wordCount / wpm))
     */
    public int getEstimatedReadingMinutes(int readingSpeedWpm) {
        int speed = (readingSpeedWpm > 0) ? readingSpeedWpm : 200;
        int words = getWordCount();
        if (words == 0) return 1;
        return Math.max(1, (int) Math.ceil((double) words / speed));
    }
}
