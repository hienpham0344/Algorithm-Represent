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
        return new Result(true, "Đã thêm " + value + " vào cuối mảng.", value, array.size() - 1);
    }

    public Result deleteEnd() {
        if (array.isEmpty()) {
            return new Result(false, "Mảng đang rỗng, không thể xóa cuối.", null, null);
        }

        int index = array.size() - 1;
        int removedValue = array.remove(index);
        return new Result(true, "Đã xóa phần tử cuối: " + removedValue + ".", removedValue, index);
    }

    public Result insertAt(int index, int value) {
        if (index < 0 || index > array.size()) {
            return new Result(false, "Index không hợp lệ. Index insert phải nằm trong đoạn [0, " + array.size() + "].", value, index);
        }

        array.add(index, value);
        return new Result(true, "Đã chèn " + value + " vào vị trí index " + index + ".", value, index);
    }

    public Result deleteAt(int index) {
        if (array.isEmpty()) {
            return new Result(false, "Mảng đang rỗng, không thể xóa.", null, index);
        }

        if (index < 0 || index >= array.size()) {
            return new Result(false, "Index không hợp lệ. Index delete phải nằm trong đoạn [0, " + (array.size() - 1) + "].", null, index);
        }

        int removedValue = array.remove(index);
        return new Result(true, "Đã xóa phần tử " + removedValue + " tại index " + index + ".", removedValue, index);
    }

    public Result updateAt(int index, int value) {
        if (array.isEmpty()) {
            return new Result(false, "Mảng đang rỗng, không thể cập nhật.", value, index);
        }

        if (index < 0 || index >= array.size()) {
            return new Result(false, "Index không hợp lệ. Index update phải nằm trong đoạn [0, " + (array.size() - 1) + "].", value, index);
        }

        int oldValue = array.set(index, value);
        return new Result(true, "Đã cập nhật index " + index + " từ " + oldValue + " thành " + value + ".", value, index);
    }

    public Result search(int value) {
        if (array.isEmpty()) {
            return new Result(false, "Mảng đang rỗng, không thể tìm kiếm.", value, null);
        }

        int index = array.indexOf(value);

        if (index == -1) {
            return new Result(false, "Không tìm thấy giá trị " + value + " trong mảng.", value, null);
        }

        return new Result(true, "Tìm thấy giá trị " + value + " tại index " + index + ".", value, index);
    }

    public Result randomize() {
        array.clear();
        int size = 7;
        for (int i = 0; i < size; i++) {
            array.add(random.nextInt(90) + 10);
        }
        return new Result(true, "Đã tạo mảng ngẫu nhiên gồm " + size + " phần tử.", null, null);
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
        return new Result(true, "Đã khởi tạo lại mảng mẫu.", null, null);
    }

    public List<Integer> toList() {
        return new ArrayList<>(array);
    }

    public boolean isEmpty() {
        return array.isEmpty();
    }

    public int size() {
        return array.size();
    }
}