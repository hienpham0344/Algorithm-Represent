package com.example.main.service;

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

    // 1. THÊM NODE (INSERT)
    public Result insert(int value) {
        if (root == null) {
            root = new Node(value);
            return new Result(true, "Đã chèn " + value + " làm gốc (Root).");
        }
        return insertRec(root, value);
    }

    private Result insertRec(Node current, int value) {
        if (value < current.value) {
            if (current.left == null) {
                current.left = new Node(value);
                return new Result(true, "Đã chèn " + value + " vào nhánh trái của " + current.value);
            } else {
                return insertRec(current.left, value);
            }
        } else if (value > current.value) {
            if (current.right == null) {
                current.right = new Node(value);
                return new Result(true, "Đã chèn " + value + " vào nhánh phải của " + current.value);
            } else {
                return insertRec(current.right, value);
            }
        }
        return new Result(false, "Giá trị " + value + " đã tồn tại trong cây.");
    }

    // 2. XÓA NODE (DELETE)
    public Result delete(int value) {
        if (!contains(root, value)) {
            return new Result(false, "Không tìm thấy giá trị " + value + " để xóa.");
        }
        root = deleteRec(root, value);
        return new Result(true, "Đã xóa thành công nút " + value + ".");
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

    // 3. TÌM KIẾM NODE (SEARCH)
    public Result search(int value) {
        if (contains(root, value)) {
            return new Result(true, "Tìm thấy nút " + value + " trong cây.");
        }
        return new Result(false, "Không tìm thấy nút " + value + " trong cây.");
    }

    private boolean contains(Node current, int value) {
        if (current == null) return false;
        if (value == current.value) return true;
        return value < current.value ? contains(current.left, value) : contains(current.right, value);
    }

    public Node getRoot() {
        return root;
    }

    public void clear() {
        root = null;
    }
}