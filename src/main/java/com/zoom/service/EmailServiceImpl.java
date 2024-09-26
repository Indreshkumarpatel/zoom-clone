package com.zoom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String[] to, String title, String host, LocalDateTime dateTime, int duration) {
        String message="I hope this message finds you well.\n" +
                "\n" +
                "I would like to invite you to a Zoom meeting to discuss about "+title+". Your insights would be greatly appreciated.\n" +
                "\n" +
                "Meeting Details:\n" +
                "Date and Time: "+dateTime +"\n"+
                "Duration: " +duration+" min";
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom(host);
        javaMailSender.send(simpleMailMessage);
        System.out.println("Email has been sent....");
    }
}
