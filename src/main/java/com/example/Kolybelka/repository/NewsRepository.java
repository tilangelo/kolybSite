package com.example.Kolybelka.repository;

import com.example.Kolybelka.DTO.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}
