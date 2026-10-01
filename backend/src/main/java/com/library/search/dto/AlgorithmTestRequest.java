package com.library.search.dto;

public class AlgorithmTestRequest {
    private String text;
    private String pattern;
    private String target; // For edit distance target string

    public AlgorithmTestRequest() {}

    public AlgorithmTestRequest(String text, String pattern) {
        this.text = text;
        this.pattern = pattern;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
}
