package com.example.Kolybelka.repository;

import com.example.Kolybelka.DTO.HelpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelpRepository extends JpaRepository<HelpRequest, Long> {
    HelpRequest findByid(Long id);
}
