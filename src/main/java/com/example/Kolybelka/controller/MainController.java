package com.example.Kolybelka.controller;

import com.example.Kolybelka.DTO.DonationRequest;
import com.example.Kolybelka.DTO.HelpRequest;
import com.example.Kolybelka.DTO.LoginResponse;
import com.example.Kolybelka.DTO.PaymentResponse;
import com.example.Kolybelka.model.Admin;
import com.example.Kolybelka.service.HelpService;
import com.example.Kolybelka.service.PaymentService;
import com.example.Kolybelka.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping
public class MainController {

    private final HelpService helpService;

    private final PaymentService paymentService;

    private final UserService userService;

    public MainController(PaymentService paymentService, HelpService helpService, UserService userService) {
        this.paymentService = paymentService;
        this.helpService = helpService;
        this.userService = userService;
    }

    @PostMapping("/mock-yookassa")
    public ResponseEntity<Map<String, Object>> mockYooKassaPayment(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", UUID.randomUUID().toString());
        response.put("status", "pending");
        response.put("confirmation_url", "https://mock-confirmation.url/confirm");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/v1/donation")
    @ResponseBody
    public ResponseEntity<PaymentResponse> createDonation(@RequestBody @Valid DonationRequest donationRequest) {
        String paymentUrl = paymentService.createPayment(donationRequest);
        return ResponseEntity.ok(new PaymentResponse(paymentUrl));
    }

    @PostMapping("/api/v1/own/kolybPanelka/loginPanelSubmit")
    @ResponseBody
    public ResponseEntity<LoginResponse> panelLogin(@RequestBody @Valid Admin admin, HttpServletResponse response) throws Exception {
        LoginResponse loginResponse = userService.UserLogin(admin); //Возвращает токен jwt
        loginResponse.setUsername(admin.getName()); //Вставляю в респонс юзернейм

        ResponseCookie cookie = ResponseCookie.from("token", loginResponse.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString()); //Куки уходит юзеру

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/api/v1/own/kolybPanelka/loginPanel")
    public String panelLogin() {
        return "loginPanel";
    }

    @GetMapping("/api/v1/own/kolybPanelka")
    public String panelKolybPanelka() {
        return "kolybPanelka";
    }


    //Доделать логику существующего логина (false response)
    @PostMapping("/api/v1/own/kolybPanelka/registerNewSubmit")
    @ResponseBody
    public ResponseEntity<?> registerNewSubmit(@RequestBody @Valid Admin admin) {
        boolean result = userService.UserRegister(admin);
        if (result) {
            return ResponseEntity.ok("success");
        }else {
            return ResponseEntity.badRequest().body("fail");
        }
    }

    @GetMapping("/api/v1/own/kolybPanelka/registerNew")
    public String registerNew(){
        return "registerNew";
    }

    @PostMapping("/api/v1/help-request")
    public ResponseEntity<PaymentResponse> createHelpRequest(@RequestBody @Valid HelpRequest helpRequest) {
        helpService.createHelpRequest(helpRequest);
        return ResponseEntity.ok(new PaymentResponse("https://shikimori.one/"));
    }

    @GetMapping("/donate")
    public String donateRequest(){
        return "donate";
    }

    @GetMapping("/help-request")
    public String helpRequest(){
        return "help-request";
    }

    @GetMapping("/infoAboutUs")
    public String infoAboutUs(){
        return "infoAboutUs";
    }

    @GetMapping("projects")
    public String projects(){
        return "projects";
    }

    @GetMapping("/news")
    public String news(){
        return "news";
    }

    @GetMapping("/contacts")
    public String contacts(){
        return "contacts";
    }

    @GetMapping("/thankYou")
    public String thankYou(){
        return "ThankYouForDonate";
    }
}
