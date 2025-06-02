package com.example.Kolybelka.repository;

import com.example.Kolybelka.DTO.DonationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<DonationRequest, Long> {
    DonationRequest findByid(Long id);
}
