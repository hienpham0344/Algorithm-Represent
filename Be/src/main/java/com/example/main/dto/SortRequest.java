package com.example.main.dto;

public class SortRequest {
    public int[] array;
    public boolean asc = true;

    public SortRequest() {}

    public SortRequest(int[] array, boolean asc) {
        this.array = array;
        this.asc = asc;
    }
}
