package com.example.main.dto;

public class Step {
    public int[] array;
    public int i, j;
    public String type; // "compare", "swap", "sorted"

    public Step(int[] array, int i, int j, String type) {
        this.array = array;
        this.i = i;
        this.j = j;
        this.type = type;
    }
}