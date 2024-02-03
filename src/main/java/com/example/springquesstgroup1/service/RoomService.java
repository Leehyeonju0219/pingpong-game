package com.example.springquesstgroup1.service;

import com.example.springquesstgroup1.RoomStatus;
import com.example.springquesstgroup1.RoomType;
import com.example.springquesstgroup1.Team;
import com.example.springquesstgroup1.UserStatus;
import com.example.springquesstgroup1.dto.CreateRoomRequest;
import com.example.springquesstgroup1.dto.SelectAllRoomsResponse;
import com.example.springquesstgroup1.entity.*;
import com.example.springquesstgroup1.repository.RoomRepository;
import com.example.springquesstgroup1.repository.UserRepository;
import com.example.springquesstgroup1.repository.UserRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        List<UserRoom> userRoomList = userRoomRepository.findUserRoomsByUserId(userId);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else return null;

        // user 상태가 ACTIVE가 아닌 경우, user가 현재 참여한 방이 있는 경우
        if (!user.getStatus().equals(UserStatus.ACTIVE) || userRoomList.size() > 0) {
            return null;
        }

        // 방 생성
        String title = createRoomRequest.getTitle();
        RoomType roomType = createRoomRequest.getRoomType();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Room room = new Room(title, userId, roomType, RoomStatus.WAIT, createdAt, updatedAt);
        room = roomRepository.save(room);

        UserRoom userRoom = new UserRoom(room.getId(), userId, Team.RED);
        userRoomRepository.save(userRoom);

        return room;
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

    public Room roomDetail(int roomId) {
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
            for (UserRoom userRoom : userRoomList) {
                if (userRoom.getTeam().equals(Team.RED)) redTeam += 1;
            }
            if (redTeam < 2) team = Team.RED;
            else team = Team.BLUE;
        }

        UserRoom userRoom = new UserRoom(roomId, userId, team);

        userRoomRepository.save(userRoom);
        return true;
    }

    public boolean outRoom(int roomId, int userId) {
        UserRoom userRoom = userRoomRepository.findUserRoomByUserIdAndRoomId(userId, roomId);
        if (userRoom == null) return false;

        Optional<Room> roomOptional = roomRepository.findById(roomId);
        Room room;
        if (roomOptional.isPresent()) {
            room = roomOptional.get();
        } else return false;
        if (room.getStatus().equals(RoomStatus.PROGRESS) || room.getStatus().equals(RoomStatus.FINISH)) {
            return false;
        }

        Optional<User> userOptional = userRepository.findById(userId);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else return false;

        if (room.getHost() == userId) {
            List<UserRoom> userRoomList = userRoomRepository.findUserRoomsByRoomId(roomId);
            for (UserRoom obj : userRoomList) {
                userRoomRepository.delete(obj);
            }
            room.setStatus(RoomStatus.FINISH);
        } else {
            userRoomRepository.delete(userRoom);
        }

        return true;
    }

    public boolean startGame(int roomId, int userId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        Room room;
        if (roomOptional.isPresent()) {
            room = roomOptional.get();
        } else return false;

        Optional<User> userOptional = userRepository.findById(userId);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else return false;

        if (room.getHost() != userId) return false;

        List<UserRoom> userRoomList = userRoomRepository.findUserRoomsByRoomId(roomId);
        if (room.getRoomType().equals(RoomType.SINGLE)) {
            if (userRoomList.size() < 2) return false;
        } else if (userRoomList.size() < 4) return false;

        if (!room.getStatus().equals(RoomStatus.WAIT)) return false;

        room.setStatus(RoomStatus.PROGRESS);
        roomRepository.save(room);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> endGame(roomId), 1, TimeUnit.MINUTES);

        return true;
    }

    public void endGame(int roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null && room.getStatus().equals(RoomStatus.PROGRESS)) {
            room.setStatus(RoomStatus.FINISH);
            roomRepository.save(room);
        }
        List<UserRoom> userRoomList = userRoomRepository.findUserRoomsByRoomId(roomId);
        for (UserRoom userRoom : userRoomList) {
            userRoomRepository.delete(userRoom);
        }
    }

    public boolean changeTeam(int roomId, int userId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (room == null || user == null) return false;

        UserRoom userRoom = userRoomRepository.findUserRoomByUserIdAndRoomId(userId, roomId);
        if (userRoom == null) return false;

        if (!room.getStatus().equals(RoomStatus.WAIT)) return false;

        List<UserRoom> userRoomList = userRoomRepository.findUserRoomsByRoomId(roomId);
        if (room.getRoomType().equals(RoomType.SINGLE)) {
            if (userRoom.getTeam().equals(Team.RED)) {
                int blueSize = 0;
                for (UserRoom obj : userRoomList) {
                    if (obj.getTeam().equals(Team.BLUE)) blueSize += 1;
                }
                if (blueSize >= 1) return false;
                else userRoom.setTeam(Team.BLUE);
            } else {
                int redSize = 0;
                for (UserRoom obj : userRoomList) {
                    if (obj.getTeam().equals(Team.RED)) redSize += 1;
                }
                if (redSize >= 1) return false;
                else userRoom.setTeam(Team.RED);
            }
        } else {
            if (userRoom.getTeam().equals(Team.RED)) {
                int blueSize = 0;
                for (UserRoom obj : userRoomList) {
                    if (obj.getTeam().equals(Team.BLUE)) blueSize += 1;
                }
                if (blueSize >= 2) return false;
                else userRoom.setTeam(Team.BLUE);
            } else {
                int redSize = 0;
                for (UserRoom obj : userRoomList) {
                    if (obj.getTeam().equals(Team.RED)) redSize += 1;
                }
                if (redSize >= 2) return false;
                else userRoom.setTeam(Team.RED);
            }
        }

        userRoomRepository.save(userRoom);
        return true;
    }
}
