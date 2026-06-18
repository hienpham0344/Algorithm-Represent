package com.example.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "code_snippets")
public class CodeSnippetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "algorithm_code", nullable = false, length = 100)
    private String algorithmCode;

    @Column(name = "line_order", nullable = false)
    private int lineOrder;

    @Column(name = "line_id", length = 255)
    private String lineId;

    @Column(name = "line_text", nullable = false, length = 1000)
    private String lineText;

    public CodeSnippetEntity() {
    }

    public CodeSnippetEntity(String algorithmCode, int lineOrder, String lineId, String lineText) {
        this.algorithmCode = algorithmCode;
        this.lineOrder = lineOrder;
        this.lineId = lineId;
        this.lineText = lineText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlgorithmCode() {
        return algorithmCode;
    }

    public void setAlgorithmCode(String algorithmCode) {
        this.algorithmCode = algorithmCode;
    }

    public int getLineOrder() {
        return lineOrder;
    }

    public void setLineOrder(int lineOrder) {
        this.lineOrder = lineOrder;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getLineText() {
        return lineText;
    }

    public void setLineText(String lineText) {
        this.lineText = lineText;
    }
}
