package com.example.springquesstgroup1.repository;

import com.example.springquesstgroup1.entity.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Integer> {
    List<UserRoom> findUserRoomsByUserId(int userId);
    List<UserRoom> findUserRoomsByRoomId(int roomId);
    UserRoom findUserRoomByUserIdAndRoomId(int userId, int roomId);
}
