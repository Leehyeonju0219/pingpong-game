package com.example.springquesstgroup1.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Integer> {
    List<UserRoom> findUserRoomsByUserId(int userId);
    List<UserRoom> findUserRoomsByRoomId(int roomId);
    UserRoom findUserRoomByUserIdAndRoomId(int userId, int roomId);
}
