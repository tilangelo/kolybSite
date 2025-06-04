package com.example.Kolybelka.service;

import com.example.Kolybelka.DTO.HelpRequest;
import com.example.Kolybelka.repository.HelpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class HelpService {
    private final HelpRepository repository;
    private final JavaMailSender mailSender;
    @Value("${help.recipient}")
    private String recipientEmail;

    public HelpService(HelpRepository repository, JavaMailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
    }

    public String createHelpRequest(HelpRequest helpRequest) {
        try {
            repository.save(helpRequest);
            sendEmailNotification(helpRequest);
            return "Заявка отправлена!";
        } catch (Exception e) {
            return "Не удалось отправить заявку, пожалуйста, воспользуетесь прикрепленными данными";
        }
    }

    private void sendEmailNotification(HelpRequest helpRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Новый запрос от: "+helpRequest.getName());
        message.setText(
                "Имя: " + helpRequest.getName() + "\n" +
                "Email: " + helpRequest.getEmail() + "\n\n" +
                "Сообщение:\n " + helpRequest.getMessage()
        );
        mailSender.send(message);
    }
}
