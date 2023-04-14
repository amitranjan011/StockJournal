package com.amit.journal.util;

import com.amit.journal.interceptor.UserContext;
import com.amit.journal.model.UserBase;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ContextUtil {
    public static void populateUserId(UserBase userBase) {
        userBase.setUserId(UserContext.getUserId());
        userBase.setLastUpdate(LocalDateTime.now());
    }

}


