package com.example.main.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class StackService {

    private final Deque<Integer> stack = new ArrayDeque<>();

    public record Result(boolean success, String message, Integer value) {}

    public Result push(int value) {
        stack.push(value);
        return new Result(true, "Đã push " + value + " vào đỉnh stack.", value);
    }

    public Result pop() {
        if (stack.isEmpty())
            return new Result(false, "Stack đang rỗng, không thể pop.", null);
        int val = stack.pop();
        return new Result(true, "Đã pop " + val + " ra khỏi đỉnh stack.", val);
    }

    public Result peek() {
        if (stack.isEmpty())
            return new Result(false, "Stack đang rỗng, không có phần tử đỉnh.", null);
        return new Result(true, "Đỉnh stack (peek): " + stack.peek(), stack.peek());
    }

    public void reset() {
        stack.clear();
    }

    /** Trả về list từ đỉnh xuống đáy để vẽ UI */
    public List<Integer> toList() {
        return new ArrayList<>(stack); // ArrayDeque iterator = top → bottom
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }
}