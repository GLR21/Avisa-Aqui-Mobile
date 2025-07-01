package com.example.avisaaqui;

public class SpinnerItem {
    private String value;
    private String description;

    private String regex;
    private String type;

    public SpinnerItem(String value, String description, String regex, String type ) {
        this.value = value;
        this.description = description;
        this.regex       = regex;
        this.type = type;
    }

    public SpinnerItem(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String getRegex()
    {
        return regex;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return description; // This is what will be displayed in the Spinner
    }
}
