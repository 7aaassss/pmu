package com.code.blinchik;

public class User {
    private int id;
    private String username;
    private double weight;
    private double height;


    public User(int id, String username, double weight, double height) {
        this.id = id;
        this.username = username;
        this.weight = weight;
        this.height = height;
    }





    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }


}

