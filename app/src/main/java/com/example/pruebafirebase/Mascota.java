package com.example.pruebafirebase;

public class Mascota {

    private String petName;
    private String species;
    private int age;
    private String owner;

    public Mascota() {
    }

    public Mascota(String petName, String species, int age, String owner) {
        this.petName = petName;
        this.species = species;
        this.age = age;
        this.owner = owner;
    }

    public String getPetName() {
        return petName;
    }

    public String getSpecies() {
        return species;
    }

    public int getAge() {
        return age;
    }

    public String getOwner() {
        return owner;
    }
}

