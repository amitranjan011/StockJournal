package com.amit.journal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserBase {
    private String userId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdate;

    private String batchId;
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
//    public UserBase() {
//        this.lastUpdate = LocalDateTime.now();
//    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Override
    public String toString() {
        return "UserBase{" +
                "userId='" + userId + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", batchId='" + batchId + '\'' +
                '}';
    }
}
