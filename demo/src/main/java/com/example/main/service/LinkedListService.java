package com.example.main.service;

import java.util.ArrayList;
import java.util.List;

public class LinkedListService {

    private Node head;

    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    public void addHead(int value) {
        Node newNode = new Node(value);
        newNode.next = head;
        head = newNode;
    }

    public void addTail(int value) {
        Node newNode = new Node(value);

        if (head == null) {
            head = newNode;
            return;
        }

        Node current = head;
        while (current.next != null) {
            current = current.next;
        }

        current.next = newNode;
    }

    public boolean deleteHead() {
        if (head == null) {
            return false;
        }

        head = head.next;
        return true;
    }

    public boolean deleteTail() {
        if (head == null) {
            return false;
        }

        if (head.next == null) {
            head = null;
            return true;
        }

        Node current = head;
        while (current.next.next != null) {
            current = current.next;
        }

        current.next = null;
        return true;
    }

    public int search(int value) {
        Node current = head;
        int index = 0;

        while (current != null) {
            if (current.value == value) {
                return index;
            }

            current = current.next;
            index++;
        }

        return -1;
    }

    public List<Integer> getValues() {
        List<Integer> values = new ArrayList<>();

        Node current = head;
        while (current != null) {
            values.add(current.value);
            current = current.next;
        }

        return values;
    }

    public void reset() {
        head = null;
    }

    public boolean isEmpty() {
        return head == null;
    }
}