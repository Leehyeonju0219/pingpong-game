package com.example.springquesstgroup1.service;

import com.example.springquesstgroup1.RoomStatus;
import com.example.springquesstgroup1.RoomType;
import com.example.springquesstgroup1.UserStatus;
import com.example.springquesstgroup1.dto.CreateRoomRequest;
import com.example.springquesstgroup1.dto.SelectAllRoomsResponse;
import com.example.springquesstgroup1.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(UserRepository userRepository, UserRoomRepository userRoomRepository, RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.userRoomRepository = userRoomRepository;
        this.roomRepository = roomRepository;
    }

    public Room createRoom(CreateRoomRequest createRoomRequest) {
        int userId = createRoomRequest.getUserId();
        Optional<User> userOptional = userRepository.findById(userId);
        UserRoom userRoom = userRoomRepository.findByUserId(userId);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else return null;

        // user 상태가 ACTIVE가 아닌 경우, user가 현재 참여한 방이 있는 경우
        if (!user.getStatus().equals(UserStatus.ACTIVE) || userRoom != null) {
            return null;
        }

        // 방 생성
        String title = createRoomRequest.getTitle();
        RoomType roomType = createRoomRequest.getRoomType();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Room room = new Room(title, userId, roomType, RoomStatus.WAIT, createdAt, updatedAt);

        return roomRepository.save(room);
    }

    public SelectAllRoomsResponse selectAllRooms(int size, int page) {
        int totalElements = roomRepository.findAll().size();
        int totalPages = (int) Math.ceil((double)totalElements/size);

        if (totalElements == 0) {
            return new SelectAllRoomsResponse(totalElements, totalPages, new ArrayList<>());
        }

        List<Room> roomList = roomRepository.findRoomsByIdBetween(size*page+1, size*(page+1));
        return new SelectAllRoomsResponse(totalElements, totalPages, roomList);
    }
}
