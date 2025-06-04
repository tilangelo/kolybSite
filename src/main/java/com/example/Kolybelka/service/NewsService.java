package com.example.Kolybelka.service;

import com.example.Kolybelka.DTO.News;
import com.example.Kolybelka.repository.NewsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public ResponseEntity<?> saveNews(News news){
        try {
            newsRepository.save(news);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public List<News> getAllNews() {
        return newsRepository.findAll(Sort.by(Sort.Direction.DESC, "creationDate"));
    }
}
