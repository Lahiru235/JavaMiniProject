package model;

import java.util.List;

// -------------------------------------------------------
// Report – Abstract base class for all report types
// OOP: ABSTRACTION – every report must implement generate()
// -------------------------------------------------------
public abstract class Report {

    protected String title;

    public Report(String title) {
        this.title = title;
    }

    // Every report type must implement how it generates data
    public abstract List<String> generate();

    public String getTitle() { return title; }
}
