package com.example.Kolybelka.service;

import com.example.Kolybelka.DTO.News;
import com.example.Kolybelka.DTO.Project;
import com.example.Kolybelka.repository.NewsRepository;
import com.example.Kolybelka.repository.ProjectRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ResponseEntity<?> saveProject(Project project) {
        try {
            projectRepository.save(project);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll(Sort.by(Sort.Direction.DESC, "creationDate"));
    }
}
