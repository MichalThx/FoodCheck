package com.opusdev.foodcheck;

public class Recipe {
    private String name;
    private String image;
    private String health;
    private String address;


    public Recipe(String name, String image, String health, String address) {
        this.name = name;
        this.image = image;
        this.health = health;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getHealth() {
        return health;
    }

    public String getAddress() {
        return address;
    }
}
