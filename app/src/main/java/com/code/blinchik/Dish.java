package com.code.blinchik;


public class Dish {
    private int id;
    private String name;
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    private String date;
    private String mealType;

    public Dish(int id, String name, double calories, double protein, double fat, double carbs, String date, String mealType) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.date = date;
        this.mealType = mealType;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getCalories() { return calories; }

}
