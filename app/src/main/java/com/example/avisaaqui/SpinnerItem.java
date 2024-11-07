package com.example.avisaaqui;

public class SpinnerItem {
    private String value;
    private String description;

    private String regex;

    public SpinnerItem(String value, String description, String regex ) {
        this.value = value;
        this.description = description;
        this.regex       = regex;
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

    @Override
    public String toString() {
        return description; // This is what will be displayed in the Spinner
    }
}
