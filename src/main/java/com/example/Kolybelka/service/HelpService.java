package com.example.Kolybelka.service;

import com.example.Kolybelka.DTO.HelpRequest;
import com.example.Kolybelka.repository.HelpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelpService {
    @Autowired
    private final HelpRepository repository;

    public HelpService(HelpRepository repository) {
        this.repository = repository;
    }

    public String createHelpRequest(HelpRequest helpRequest) {
        try {
            repository.save(helpRequest);
            return "Заявка отправлена!";
        } catch (Exception e) {
            return "Не удалось отправить заявку, пожалуйста, воспользуетесь прикрепленными данными";
        }
    }
}
