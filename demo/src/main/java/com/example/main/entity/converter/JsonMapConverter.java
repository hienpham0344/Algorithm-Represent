package com.example.main.entity.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.LinkedHashMap;
import java.util.Map;

@Converter(autoApply = false)
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to serialize map to JSON", exception);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            return new LinkedHashMap<>(MAPPER.readValue(dbData, new TypeReference<>() { }));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to parse JSON map", exception);
        }
    }
}
