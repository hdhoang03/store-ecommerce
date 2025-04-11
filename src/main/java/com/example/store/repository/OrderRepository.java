package com.example.store.repository;

import com.example.store.entity.Order;
import com.example.store.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByDeletedFalse(Pageable pageable);
    Page<Order> findByUserAndDeletedFalse(User user, Pageable pageable);
}
