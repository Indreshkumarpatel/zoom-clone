package com.zoom.service;

import java.time.LocalDateTime;
import java.util.List;

public interface EmailService {

    void sendEmail(String[] to, String title,String host, LocalDateTime dateTime,int duration);
}
