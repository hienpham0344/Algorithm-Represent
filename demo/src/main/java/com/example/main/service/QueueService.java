package com.example.main.service;

import com.example.main.utils.*;
import com.example.main.dto.*;
import com.example.main.enums.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class QueueService {

    private final Deque<Integer> queue = new ArrayDeque<>();

    public record Result(boolean success, String message, Integer value) {}

    public Result enqueue(int value) {
        queue.addLast(value);
        return new Result(true, "Enqueued " + value + " to the end of the queue (REAR).", value);    }

    public Result dequeue() {
        if (queue.isEmpty())
            return new Result(false, "Queue is empty, cannot Dequeue.", null);
        int val = queue.removeFirst(); // Lấy ra từ đầu hàng (FRONT)
        return new Result(true, "Dequeued " + val + " from the front of the queue (FRONT).", val);
    }

    public Result peek() {
        if (queue.isEmpty())
            return new Result(false, "Queue is empty, no front element exists.", null);
        return new Result(true, "Front of queue (peek): " + queue.peekFirst(), queue.peekFirst());
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
