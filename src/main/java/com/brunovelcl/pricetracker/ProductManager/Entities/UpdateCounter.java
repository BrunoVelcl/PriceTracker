package com.brunovelcl.pricetracker.ProductManager.Entities;

public class UpdateCounter {
    private int alreadyCorrect;
    private int created;
    private int updated;

    public UpdateCounter(){
        this.alreadyCorrect = 0;
        this.created = 0;
        this.updated = 0;
    }

    public int getAlreadyCorrect() {
        return alreadyCorrect;
    }

    public int getCreated() {
        return created;
    }

    public int getUpdated() {
        return updated;
    }

    public void incrementAlreadyCorrect(){
        this.alreadyCorrect++;
    }

    public void incrementCreated(){
        this.created++;
    }

    public void incrementUpdated(){
        this.updated++;
    }
}
