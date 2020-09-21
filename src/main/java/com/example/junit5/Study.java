package com.example.junit5;

import lombok.Getter;

@Getter
public class Study {
    private StudyStatus status = StudyStatus.DRAFT;
    private int limit;
    private String name;

    public Study(int limit) {
        if(limit<0) {
            throw new IllegalArgumentException("limit는 0보다 커야 한다");
        }
        this.limit = limit;

    }

    public StudyStatus getStatus() {
        return this.status;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "Study{" +
                "status=" + status +
                ", limit=" + limit +
                ", name='" + name + '\'' +
                '}';
    }
}
