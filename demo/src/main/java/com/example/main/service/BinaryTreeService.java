package com.example.main.service;

import java.util.ArrayList;
import java.util.List;

public class BinaryTreeService {

    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    private Node root;

    public record Result(boolean success, String message) {}

    public record SearchResult(boolean success, String message, List<Integer> path) {}

    public Result insert(int value) {
        if (root == null) {
            root = new Node(value);
            return new Result(true, "Inserted " + value + " as the root.");
        }
        return insertRec(root, value);
    }

    private Result insertRec(Node current, int value) {
        if (value < current.value) {
            if (current.left == null) {
                current.left = new Node(value);
                return new Result(true, "Inserted " + value + " into the left subtree of " + current.value + ".");
            } else {
                return insertRec(current.left, value);
            }
        } else if (value > current.value) {
            if (current.right == null) {
                current.right = new Node(value);
                return new Result(true, "Inserted " + value + " into the right subtree of " + current.value + ".");
            } else {
                return insertRec(current.right, value);
            }
        }
        return new Result(false, "Value " + value + " already exists in the tree.");
    }

    public Result delete(int value) {
        if (!contains(root, value)) {
            return new Result(false, "Value " + value + " was not found for deletion.");
        }
        root = deleteRec(root, value);
        return new Result(true, "Successfully deleted node " + value + ".");
    }

    private Node deleteRec(Node root, int value) {
        if (root == null) return null;
        if (value < root.value) {
            root.left = deleteRec(root.left, value);
        } else if (value > root.value) {
            root.right = deleteRec(root.right, value);
        } else {
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;

            root.value = minValue(root.right);
            root.right = deleteRec(root.right, root.value);
        }
        return root;
    }

    private int minValue(Node root) {
        int minVal = root.value;
        while (root.left != null) {
            minVal = root.left.value;
            root = root.left;
        }
        return minVal;
    }

    public SearchResult search(int value) {
        List<Integer> path = new ArrayList<>();
        Node current = root;

        while (current != null) {
            path.add(current.value);
            if (value == current.value) {
                return new SearchResult(true, "Found node " + value + " in the tree.", path);
            }
            current = value < current.value ? current.left : current.right;
        }
        return new SearchResult(false, "Node " + value + " was not found in the tree.", path);
    }

    private boolean contains(Node current, int value) {
        if (current == null) return false;
        if (value == current.value) return true;
        return value < current.value ? contains(current.left, value) : contains(current.right, value);
    }

    public SearchResult traverse(String type) {
        List<Integer> path = new ArrayList<>();
        if (root == null) {
            return new SearchResult(false, "The tree is empty; traversal is not possible.", path);
        }

        switch (type) {
            case "NLR":
                preOrderRec(root, path);
                return new SearchResult(true, "Pre-order traversal (NLR) complete.", path);
            case "LNR":
                inOrderRec(root, path);
                return new SearchResult(true, "In-order traversal (LNR) complete.", path);
            case "LRN":
                postOrderRec(root, path);
                return new SearchResult(true, "Post-order traversal (LRN) complete.", path);
            default:
                return new SearchResult(false, "Invalid traversal type.", path);
        }
    }

    private void preOrderRec(Node node, List<Integer> path) {
        if (node == null) return;
        path.add(node.value);
        preOrderRec(node.left, path);
        preOrderRec(node.right, path);
    }

    private void inOrderRec(Node node, List<Integer> path) {
        if (node == null) return;
        inOrderRec(node.left, path);
        path.add(node.value);
        inOrderRec(node.right, path);
    }

    private void postOrderRec(Node node, List<Integer> path) {
        if (node == null) return;
        postOrderRec(node.left, path);
        postOrderRec(node.right, path);
        path.add(node.value);
    }

    public Node getRoot() {
        return root;
    }

    public void clear() {
        root = null;
    }
}
