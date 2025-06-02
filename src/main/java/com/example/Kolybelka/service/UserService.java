package com.example.Kolybelka.service;

import com.example.Kolybelka.DTO.JwtResponse;
import com.example.Kolybelka.DTO.LoginResponse;
import com.example.Kolybelka.Exceptions.LoginAlreadyExists;
import com.example.Kolybelka.Exceptions.LoginNotFoundException;
import com.example.Kolybelka.Exceptions.PasswordException;
import com.example.Kolybelka.config.JwtUtil;
import com.example.Kolybelka.model.Admin;
import com.example.Kolybelka.repository.PaymentRepository;
import com.example.Kolybelka.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public boolean UserRegister(Admin requestAdmin) {
        if(usersRepository.findByName(requestAdmin.getName()) == null) {
            Admin admin = new Admin();
            admin.setName(requestAdmin.getName());
            admin.setPassword(passwordEncoder.encode(requestAdmin.getPassword()));
            usersRepository.save(admin);
            return true;
        }else {
            return false;
        }
    }

    public LoginResponse UserLogin(Admin requestAdmin) throws Exception {
        //проверка существования логина
        if(usersRepository.findByName(requestAdmin.getName()) == null) {
            throw new LoginNotFoundException("Такого логина не существует");
        }
        //чекаю пароли
        Admin admin = usersRepository.findByName(requestAdmin.getName());
        System.out.println(admin.getPassword());
        boolean passwordMatch = passwordEncoder.matches(requestAdmin.getPassword(), admin.getPassword());
        System.out.println(requestAdmin.getPassword());
        System.out.println(passwordMatch);
        if(!passwordMatch) {
            throw new PasswordException("Invalid password");
        }
        //Генерация токена
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestAdmin.getName(), requestAdmin.getPassword())
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);

        return new LoginResponse(jwt);
    }
}
