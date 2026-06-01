package com.example.main.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class QueueService {

    private final Deque<Integer> queue = new ArrayDeque<>();

    public record Result(boolean success, String message, Integer value) {}

    public Result enqueue(int value) {
        queue.addLast(value);
        return new Result(true, "Đã Enqueue " + value + " vào cuối hàng đợi (REAR).", value);
    }

    public Result dequeue() {
        if (queue.isEmpty())
            return new Result(false, "Hàng đợi đang rỗng, không thể Dequeue.", null);
        int val = queue.removeFirst(); // Lấy ra từ đầu hàng (FRONT)
        return new Result(true, "Đã Dequeue " + val + " ra khỏi đầu hàng đợi (FRONT).", val);
    }

    public Result peek() {
        if (queue.isEmpty())
            return new Result(false, "Hàng đợi đang rỗng, không có phần tử đầu hàng.", null);
        return new Result(true, "Đầu hàng đợi (peek): " + queue.peekFirst(), queue.peekFirst());
    }

    public void reset() {
        queue.clear();
    }


    public List<Integer> toList() {
        return new ArrayList<>(queue);
    }

    public boolean isEmpty() { return queue.isEmpty(); }
    public int size()        { return queue.size(); }
}