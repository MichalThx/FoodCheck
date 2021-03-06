package com.opusdev.foodcheck.recipe;

/**
 * The "Recipe" class creates a structure for a Recipe object.
 * It has following features:
 *
 * @String name = name of the recipe
 * @String image = url of the image
 * @String health = allergy information
 * @String address = address of website that hosts the recipe
 */
public class Recipe {
	private final String name;
	private final String image;
	private final String health;
	private final String address;


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
