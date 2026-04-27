package com.fsad.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    ADMIN,
    TEACHER,
    STUDENT;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static UserRole fromJson(String value) {
        return UserRole.valueOf(value.toUpperCase());
    }
}
