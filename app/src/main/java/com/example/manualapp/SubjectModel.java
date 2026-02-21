package com.example.manualapp;

public class SubjectModel {
    private final String subjectName;
    private final String customFilePath;

    public SubjectModel(String subjectName) {
        this(subjectName, null);
    }

    public SubjectModel(String subjectName, String customFilePath) {
        this.subjectName = subjectName;
        this.customFilePath = customFilePath;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getCustomFilePath() {
        return customFilePath;
    }

    public boolean isCustom() {
        return customFilePath != null && !customFilePath.isEmpty();
    }
}
