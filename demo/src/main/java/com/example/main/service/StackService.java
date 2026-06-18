package com.example.main.service;

import com.example.main.utils.*;
import com.example.main.dto.*;
import com.example.main.enums.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class StackService {

    private final Deque<Integer> stack = new ArrayDeque<>();

    public record Result(boolean success, String message, Integer value) {}

    public Result push(int value) {
        stack.push(value);
        return new Result(true, "Successfully pushed " + value + " onto the stack top.", value);
    }

    public Result pop() {
        if (stack.isEmpty())
            return new Result(false, "Stack is empty, cannot pop.", null);
        int val = stack.pop();
        return new Result(true, "Successfully popped " + val + " from the stack top.", val);
    }

    public Result peek() {
        if (stack.isEmpty())
            return new Result(false, "Stack is empty, no top element exists.", null);
        return new Result(true, "Stack top (peek): " + stack.peek(), stack.peek());
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
