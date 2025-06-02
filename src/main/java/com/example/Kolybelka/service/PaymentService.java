package com.example.Kolybelka.service;

import com.example.Kolybelka.DTO.DonationRequest;
import com.example.Kolybelka.repository.PaymentRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {
    private static Dotenv dotenv = Dotenv.load();
    @Autowired
    private final PaymentRepository repository;
    private static final String SHOP_ID = "123456";
    private static final String SECRET_KEY = dotenv.get("MY_API_KEY");

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public String createPayment(DonationRequest donationRequest) {
        repository.save(donationRequest);
        RestTemplate restTemplate = new RestTemplate();

        // Заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(SHOP_ID, SECRET_KEY);

        // Тело запроса
        Map<String, Object> paymentBody = new HashMap<>();
        Map<String, String> amount = new HashMap<>();
        amount.put("value", String.valueOf(donationRequest.getAmount()));
        amount.put("currency", "RUB");

        Map<String, String> confirmation = new HashMap<>();
        confirmation.put("type", "redirect");
        confirmation.put("return_url", "https://!!!!!!!!!!!!!!!!/thankYou");

        paymentBody.put("amount", amount);
        paymentBody.put("confirmation", confirmation);
        paymentBody.put("capture", true);
        paymentBody.put("description", "Пожертвование волонтёру: " + donationRequest.getVolunteerName());
        paymentBody.put("receipt", Map.of(
                "customer", Map.of("email", donationRequest.getEmail()),
                "items", List.of(Map.of(
                        "description", "Пожертвование",
                        "quantity", 1,
                        "amount", amount,
                        "vat_code", 1 // Без НДС
                ))
        ));

        HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(paymentBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    //"https://api.yookassa.ru/v3/payments",
                    "http://localhost:8081/mock-yookassa",
                    httpRequest,
                    Map.class
            );

            Map responseBody = response.getBody();
            if (responseBody != null) {
                //Map confirmationResponse = (Map) responseBody.get("confirmation");
                //return (String) confirmationResponse.get("confirmation_url");
                return (String) responseBody.get("confirmation_url");
            } else {
                throw new RuntimeException("Empty response from YooKassa");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create payment: " + e.getMessage(), e);
        }
    }
}
