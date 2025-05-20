package com.example.store.repository;

import com.example.store.entity.Address;
import com.example.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findById(Long id);
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId")
    List<Address> findAllByUser(String userId);
}
