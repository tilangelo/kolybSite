package com.example.Kolybelka.controller;

import com.example.Kolybelka.DTO.*;
import com.example.Kolybelka.model.Admin;
import com.example.Kolybelka.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    private final NewsService newsService;

    private final ProjectService projectService;

    private final ResourceLoader resourceLoader;

/*    @Value("${upload.path}")
    private String uploadPath;*/

    public MainController(PaymentService paymentService,
                          HelpService helpService,
                          UserService userService,
                          NewsService newsService,
                          ProjectService projectService, ResourceLoader resourceLoader) {
        this.paymentService = paymentService;
        this.helpService = helpService;
        this.userService = userService;
        this.newsService = newsService;
        this.projectService = projectService;
        this.resourceLoader = resourceLoader;
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

    //TODO: Редирект на фронте с формы логина на админку после логина
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


    //TODO: Доделать логику существующего логина (false response)
    //TODO: Редирект с формы регистрации в админку
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
    public ResponseEntity<?> createHelpRequest(@RequestBody @Valid HelpRequest helpRequest) {
        try {
            helpService.createHelpRequest(helpRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
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

    //Эндпоинты проектов
    @GetMapping("/projects")
    public String projects(Model model) {
        model.addAttribute("projects", projectService.getAllProjects());
        return "projects";
    }
    @PostMapping("/api/v1/own/kolybPanelka/newProject")
    public ResponseEntity<?> newProject(@ModelAttribute Project project,
                                        @RequestParam("imageFile") MultipartFile file) throws IOException {

        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

            // Убедитесь, что папка существует
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Сохраняем файл
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String filePath = uploadDir + File.separator + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);
            project.setImageUrl("/uploads/" + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка загрузки: " + e.getMessage());
        }

        ResponseEntity response = projectService.saveProject(project);
        if(response.getStatusCode() == HttpStatus.CREATED) {
            return ResponseEntity.ok("Файл сохранён");
        } else {
            return ResponseEntity.badRequest().body("fail");
        }
    }

    //Новостные эндпоинты
    @GetMapping("/news")
    public String news(Model model){
        model.addAttribute("newsList", newsService.getAllNews());
        return "news";
    }
    @PostMapping("/api/v1/own/kolybPanelka/newNews")
    public ResponseEntity<?> newNews(@ModelAttribute News news,
                                     @RequestParam("imageFile") MultipartFile file) throws IOException {

        try {
            // Корневой путь относительно проекта (где pom.xml)
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

            // Убедитесь, что папка существует
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Сохраняем файл
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String filePath = uploadDir + File.separator + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);
            news.setImageUrl("/uploads/" + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка загрузки: " + e.getMessage());
        }

        ResponseEntity response = newsService.saveNews(news);
        if(response.getStatusCode() == HttpStatus.CREATED) {
            return ResponseEntity.ok("Файл сохранен:");
        } else {
            return ResponseEntity.badRequest().body("fail");
        }
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
