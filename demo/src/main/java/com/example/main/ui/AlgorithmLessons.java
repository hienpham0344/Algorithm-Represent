package com.example.main.ui;

import java.util.Map;

public final class AlgorithmLessons {
    private static final Map<String, AlgorithmLesson> LESSONS = Map.of(
            "bubble", new AlgorithmLesson(
                    "So sánh từng cặp phần tử liền kề và đổi chỗ khi sai thứ tự. Sau mỗi lượt, một phần tử lớn nhất hoặc nhỏ nhất được đẩy về cuối vùng chưa sắp xếp.",
                    "O(n²), trường hợp tốt O(n) nếu có tối ưu dừng sớm",
                    "O(1)",
                    "Ổn định"),
            "selection", new AlgorithmLesson(
                    "Tìm phần tử phù hợp nhất trong vùng chưa sắp xếp rồi đưa nó về đầu vùng đó.",
                    "O(n²)",
                    "O(1)",
                    "Không ổn định"),
            "insertion", new AlgorithmLesson(
                    "Lấy từng phần tử làm key, dịch các phần tử lớn hơn hoặc nhỏ hơn sang phải rồi chèn key vào vị trí phù hợp.",
                    "O(n²), trường hợp tốt O(n)",
                    "O(1)",
                    "Ổn định"),
            "heap", new AlgorithmLesson(
                    "Xây dựng heap, liên tục đưa phần tử ưu tiên ở gốc về cuối và khôi phục tính chất heap.",
                    "O(n log n)",
                    "O(log n) do đệ quy",
                    "Không ổn định"),
            "quick", new AlgorithmLesson(
                    "Chọn pivot, chia mảng thành hai vùng theo pivot rồi tiếp tục sắp xếp đệ quy từng vùng.",
                    "Trung bình O(n log n), xấu nhất O(n²)",
                    "Trung bình O(log n)",
                    "Không ổn định"),
            "merge", new AlgorithmLesson(
                    "Chia mảng thành các nửa nhỏ, sắp xếp từng nửa rồi trộn hai dãy đã có thứ tự.",
                    "O(n log n)",
                    "O(n)",
                    "Ổn định")
    );

    private AlgorithmLessons() {
    }

    public static AlgorithmLesson get(String algorithm) {
        return LESSONS.getOrDefault(algorithm,
                new AlgorithmLesson("Chưa có mô tả.", "-", "-", "-"));
    }
}
