package com.example.springquesstgroup1.controller;

import com.example.springquesstgroup1.dto.*;
import com.example.springquesstgroup1.entity.Room;
import com.example.springquesstgroup1.service.RoomService;
import com.example.springquesstgroup1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
public class PingpongController {

    private final UserService userService;

    private final RoomService roomService;

    @Autowired
    public PingpongController(UserService userService, RoomService roomService) {
        this.userService = userService;
        this.roomService = roomService;
    }

    @GetMapping("/health")
    public NoDataApiResponse healthCheck() {
        return new NoDataApiResponse(200, "API 요청이 성공했습니다.");
    }

    @PostMapping("/init")
    public NoDataApiResponse init(@RequestBody InitRequest initRequest) {
        if (userService.init(initRequest)) {
            return new NoDataApiResponse(HttpStatus.OK.value(), "API 요청이 성공했습니다.");
        }
        return new NoDataApiResponse(500, "에러가 발생했습니다.");
    }

    @GetMapping("/user")
    public ApiResponse selectAllUsers(@RequestParam(name = "size") int size,
                                      @RequestParam(name = "page") int page) {
        return new ApiResponse<>(200, "API 요청이 성공했습니다.", userService.selectAllUsers(size, page));
    }

    @PostMapping("/room")
    public NoDataApiResponse createRoom(@RequestBody CreateRoomRequest createRoomRequest) {
        if (roomService.createRoom(createRoomRequest) == null) {
            return new NoDataApiResponse(201, "불가능한 요청입니다.");
        }
        return new NoDataApiResponse(HttpStatus.OK.value(), "API 요청이 성공했습니다.");
    }

    @GetMapping("/room")
    public ApiResponse selectAllRooms(@RequestParam(name = "size") int size,
                                      @RequestParam(name = "page") int page) {
        return new ApiResponse<>(200, "API 요청이 성공했습니다.", roomService.selectAllRooms(size, page));
    }

    @GetMapping("/room/{roomId}")
    public ApiResponse selectRoom(@PathVariable(name = "roomId") int roomId) {
        Room room = roomService.selectRoom(roomId);
        if (room == null) return new ApiResponse(201, "불가능한 요청입니다.", null);
        else return new ApiResponse<>(200, "API 요청이 성공했습니다.", room);
    }

    @PostMapping("/room/attention/{roomId}")
    public NoDataApiResponse joinRoom(@PathVariable(name = "roomId") int roomId,
                                @RequestBody RoomRequest roomRequest) {
        if (roomService.joinRoom(roomId, roomRequest.getUserId())) {
            return new NoDataApiResponse(200, "API 요청이 성공했습니다.");
        } else return new NoDataApiResponse(201, "불가능한 요청입니다.");
    }

    @PostMapping("/room/out/{roomId}")
    public NoDataApiResponse outRoom(@PathVariable(name = "roomId") int roomId,
                               @RequestBody RoomRequest roomRequest) {
        if (roomService.outRoom(roomId, roomRequest.getUserId())) {
            return new NoDataApiResponse(200, "API 요청이 성공했습니다.");
        } else return new NoDataApiResponse(201, "불가능한 요청입니다.");
    }

    @PutMapping("/room/start/{roomId}")
    public NoDataApiResponse startGame(@PathVariable(name = "roomId") int roomId,
                                 @RequestBody RoomRequest roomRequest) {
        boolean result = roomService.startGame(roomId, roomRequest.getUserId());
        if (!result) return new NoDataApiResponse(201, "불가능한 요청입니다.");

        return new NoDataApiResponse(200, "API 요청이 성공했습니다.");
    }

    @PutMapping("/team/{roomId}")
    public NoDataApiResponse changeTeam(@PathVariable(name = "roomId") int roomId,
                                  @RequestBody RoomRequest roomRequest) {
        boolean result = roomService.changeTeam(roomId, roomRequest.getUserId());
        if (!result) return new NoDataApiResponse(201, "불가능한 요청입니다.");

        return new NoDataApiResponse(200, "API 요청이 성공했습니다.");
    }

}
