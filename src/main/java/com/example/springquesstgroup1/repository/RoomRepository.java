package com.example.springquesstgroup1.repository;

import com.example.springquesstgroup1.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findRoomsByIdBetween(int startId, int endId);
}
