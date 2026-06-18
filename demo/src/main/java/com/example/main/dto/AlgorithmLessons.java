package com.example.main.dto;

import com.example.main.SortAlgorithmPresentApplication;
import com.example.main.service.AlgorithmDataService;

public final class AlgorithmLessons {

    private AlgorithmLessons() {
    }

    public static AlgorithmLesson get(String algorithm) {
        if (SortAlgorithmPresentApplication.context() != null) {
            AlgorithmDataService service = SortAlgorithmPresentApplication.context().getBean(AlgorithmDataService.class);
            return service.getLesson(algorithm);
        }
        return new AlgorithmLesson("No description available.", "-", "-", "-");
    }
}

