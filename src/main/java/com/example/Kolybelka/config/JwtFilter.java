package com.example.Kolybelka.config;

import com.example.Kolybelka.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    public JwtFilter(JwtUtil jwtUtil, UsersRepository usersRepository) {
        this.jwtUtil = jwtUtil;
        this.usersRepository = usersRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        String jwt = null;

        // 1. Получаем JWT из куки
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                }
            }
        }

        // 2. Валидируем и устанавливаем пользователя в контекст
        if (jwt != null && jwtUtil.validateToken(jwt)) {
            String username = jwtUtil.extractUsername(jwt);

            // 3. Получаем пользователя из БД
            UserDetails userDetails = usersRepository.findByName(username);
            if (userDetails != null) {
                // 4. Создаём токен аутентификации
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                // 5. Устанавливаем в контекст
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Продолжаем выполнение цепочки фильтров
        filterChain.doFilter(request, response);
    }
}
