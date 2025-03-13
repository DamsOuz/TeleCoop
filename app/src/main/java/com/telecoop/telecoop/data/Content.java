package com.telecoop.telecoop.data;

public class Content {
    private final String title;
    private final String content;
    private final Integer imageResId; // peut être null si aucune image

    public Content(String title, String content, Integer imageResId) {
        this.title = title;
        this.content = content;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer getImageResId() {
        return imageResId;
    }
}
