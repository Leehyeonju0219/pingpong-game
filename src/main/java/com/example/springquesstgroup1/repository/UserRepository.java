package com.example.springquesstgroup1.repository;

import com.example.springquesstgroup1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findUsersByIdBetween(int startId, int endId);
}
