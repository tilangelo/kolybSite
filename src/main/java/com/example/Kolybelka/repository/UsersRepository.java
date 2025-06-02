package com.example.Kolybelka.repository;

import com.example.Kolybelka.model.Admin;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Admin, Long> {
    Admin findByid(Long id);
    Admin findByName(@NotNull String name);
}
