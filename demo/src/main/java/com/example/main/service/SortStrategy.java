package com.example.main.service;

import com.example.main.utils.*;
import com.example.main.dto.*;
import com.example.main.enums.*;
import com.example.main.dto.Step;

import java.util.List;

public interface SortStrategy {
    List<Step> sort(int[] arr, boolean asc);
}

