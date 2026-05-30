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
        return new Result(true, "Enqueue " + value + " vào REAR thành công.", value);
    }

    public Result dequeue() {
        if (queue.isEmpty())
            return new Result(false, "Queue rỗng (Underflow). Không thể Dequeue!", null);
        int val = queue.removeFirst();
        return new Result(true, "Dequeue " + val + " từ FRONT thành công.", val);
    }

    public Result peek() {
        if (queue.isEmpty())
            return new Result(false, "Queue rỗng. Peek = NULL!", null);
        return new Result(true, "FRONT hiện tại: " + queue.peekFirst(), queue.peekFirst());
    }

    public void reset() { queue.clear(); }

    public List<Integer> toList() {
        return new ArrayList<>(queue);
    }

    public boolean isEmpty() { return queue.isEmpty(); }
    public int size()        { return queue.size(); }
}