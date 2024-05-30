package com.choidh.service.attachment.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class AttachmentServiceImplTest {

    @Test
    public void dateTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        System.out.println("year = " + year);
        int month = now.getMonthValue();
        System.out.println("month = " + month);
        int dayOfMonth = now.getDayOfMonth();
        System.out.println("dayOfMonth = " + dayOfMonth);
    }
}