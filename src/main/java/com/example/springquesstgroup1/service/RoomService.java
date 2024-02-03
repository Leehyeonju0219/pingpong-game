package com.example.springquesstgroup1.service;

import com.example.springquesstgroup1.RoomStatus;
import com.example.springquesstgroup1.RoomType;
import com.example.springquesstgroup1.Team;
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
        List<UserRoom> userRoom = userRoomRepository.findUserRoomsByUserId(userId);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else return null;

        // user 상태가 ACTIVE가 아닌 경우, user가 현재 참여한 방이 있는 경우
        if (!user.getStatus().equals(UserStatus.ACTIVE) || userRoom.size() > 0) {
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

    public Room selectRoom(int roomId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        Room room;
        if (roomOptional.isPresent()) {
            room = roomOptional.get();
            return room;
        } else return null;
    }

    public boolean joinRoom(int roomId, int userId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        Optional<User> userOptional = userRepository.findById(userId);
        Room room;
        User user;
        if (roomOptional.isPresent() && userOptional.isPresent()) {
            room = roomOptional.get();
            user = userOptional.get();
        } else return false;

        if (!room.getStatus().equals(RoomStatus.WAIT)) return false;
        if (!user.getStatus().equals(UserStatus.ACTIVE)) return false;

        List<UserRoom> userRoomList = userRoomRepository.findUserRoomsByRoomId(roomId);
        for (UserRoom userRoom : userRoomList) {
            if (userRoom.getUserId() == userId) return false;
        }

        if (room.getRoomType().equals(RoomType.SINGLE)) {
            if (userRoomList.size() >= 2) return false;
        } else if (room.getRoomType().equals(RoomType.DOUBLE)) {
            if (userRoomList.size() >= 4) return false;
        } else return false;

        // 팀 배정
        Team team = Team.RED;
        if (room.getRoomType().equals(RoomType.SINGLE)) {
            for (UserRoom userRoom : userRoomList) {
                if (userRoom.getTeam().equals(Team.RED)) team = Team.BLUE;
                else team = Team.RED;
            }
        } else {
            int redTeam = 0;
            int blueTeam = 0;
            for (UserRoom userRoom : userRoomList) {
                if (userRoom.getTeam().equals(Team.RED)) redTeam += 1;
                else blueTeam += 1;
            }
            if (redTeam < 2) team = Team.RED;
            else team = Team.BLUE;
        }

        UserRoom userRoom = new UserRoom(roomId, userId, team);

        userRoomRepository.save(userRoom);
        return true;
    }
}
