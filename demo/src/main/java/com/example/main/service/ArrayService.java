package com.example.main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArrayService {

    private final List<Integer> array = new ArrayList<>();
    private final Random random = new Random();

    public record Result(boolean success, String message, Integer value, Integer index) {}

    public ArrayService() {
        reset();
    }

    public Result insertEnd(int value) {
        array.add(value);
        return new Result(true, "Inserted " + value + " at the end of the array.", value, array.size() - 1);
    }

    public Result deleteEnd() {
        if (array.isEmpty()) {
            return new Result(false, "The array is empty. Delete End cannot run.", null, null);
        }

        int index = array.size() - 1;
        int removedValue = array.remove(index);
        return new Result(true, "Removed last value " + removedValue + ".", removedValue, index);
    }

    public Result insertAt(int index, int value) {
        if (index < 0 || index > array.size()) {
            return new Result(false, "Invalid index. Insert index must be in [0, " + array.size() + "].", value, index);
        }

        array.add(index, value);
        return new Result(true, "Inserted " + value + " at index " + index + ".", value, index);
    }

    public Result deleteAt(int index) {
        if (array.isEmpty()) {
            return new Result(false, "The array is empty. Delete at Index cannot run.", null, index);
        }

        if (index < 0 || index >= array.size()) {
            return new Result(false, "Invalid index. Delete index must be in [0, " + (array.size() - 1) + "].", null, index);
        }

        int removedValue = array.remove(index);
        return new Result(true, "Removed value " + removedValue + " at index " + index + ".", removedValue, index);
    }

    public Result updateAt(int index, int value) {
        if (array.isEmpty()) {
            return new Result(false, "The array is empty. Update cannot run.", value, index);
        }

        if (index < 0 || index >= array.size()) {
            return new Result(false, "Invalid index. Update index must be in [0, " + (array.size() - 1) + "].", value, index);
        }

        int oldValue = array.set(index, value);
        return new Result(true, "Updated index " + index + " from " + oldValue + " to " + value + ".", value, index);
    }

    public Result search(int value) {
        if (array.isEmpty()) {
            return new Result(false, "The array is empty. Search cannot run.", value, null);
        }

        int index = array.indexOf(value);
        if (index == -1) {
            return new Result(false, "Value " + value + " was not found in the array.", value, null);
        }

        return new Result(true, "Found value " + value + " at index " + index + ".", value, index);
    }

    public Result randomize() {
        array.clear();
        int size = 7;
        for (int i = 0; i < size; i++) {
            array.add(random.nextInt(90) + 10);
        }
        return new Result(true, "Generated a random array with " + size + " values.", null, null);
    }

    public Result reset() {
        array.clear();
        array.add(12);
        array.add(45);
        array.add(8);
        array.add(5);
        array.add(23);
        array.add(19);
        array.add(56);
        return new Result(true, "Reset to the sample array.", null, null);
    }

    public List<Integer> toList() {
        return new ArrayList<>(array);
    }

    public int indexOf(int value) {
        return array.indexOf(value);
    }

    public boolean isEmpty() {
        return array.isEmpty();
    }

    public int size() {
        return array.size();
    }
}
