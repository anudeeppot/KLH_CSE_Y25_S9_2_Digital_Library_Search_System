public class LibraryDocument {
    private final String fileName;
    private String title = "";
    private String author = "";
    private String category = "";
    private String year = "";
    private String content = "";

    public LibraryDocument(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() { return fileName; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getYear() { return year; }
    public String getContent() { return content; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setCategory(String category) { this.category = category; }
    public void setYear(String year) { this.year = year; }
    public void setContent(String content) { this.content = content; }
}
