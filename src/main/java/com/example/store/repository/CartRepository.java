package com.example.store.repository;

import com.example.store.entity.Cart;
import com.example.store.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Cart> findByUser(User user);
}
/*
* đang dùng @ManyToOne(fetch = FetchType.LAZY) cho product và cart, điều này là tốt về hiệu năng
* nhưng nếu mapper được gọi ở ngoài transaction (ví dụ trong controller), có thể gặp LazyInitializationException.
* */