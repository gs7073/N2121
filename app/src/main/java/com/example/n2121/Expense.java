package com.example.n2121;

/**
 * Model class representing a single Expense object for Firebase Realtime Database.
 */
public class Expense {

    private String id;
    private String desc;
    private double amount;
    private String category;
    private String date;

    /**
     * Default constructor required for Firebase calls.
     */
    public Expense() {
    }

    /**
     * Constructor with all parameters.
     */
    public Expense(String id, String desc, double amount, String category, String date) {
        this.id = id;
        this.desc = desc;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}