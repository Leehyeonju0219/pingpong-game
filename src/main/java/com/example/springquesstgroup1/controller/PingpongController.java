package com.example.springquesstgroup1.controller;

import com.example.springquesstgroup1.dto.ApiResponse;
import com.example.springquesstgroup1.dto.CreateRoomRequest;
import com.example.springquesstgroup1.dto.InitRequest;
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
    public ApiResponse healthCheck() {
        return new ApiResponse(200, "API 요청이 성공했습니다.", null);
    }

    @PostMapping("/init")
    public ApiResponse init(@RequestBody InitRequest initRequest) {
        if (userService.init(initRequest)) {
            return new ApiResponse(HttpStatus.OK.value(), "API 요청이 성공했습니다.", null);
        }
        return new ApiResponse(500, "에러가 발생했습니다.", null);
    }

    @GetMapping("/user")
    public ApiResponse selectAllUsers(@RequestParam(name = "size") int size,
                                            @RequestParam(name = "page") int page) {
        return new ApiResponse<>(200, "API 요청이 성공했습니다.", userService.selectAllUsers(size, page));
    }

    @PostMapping("/room")
    public ApiResponse createRoom(@RequestBody CreateRoomRequest createRoomRequest) {
        if (roomService.createRoom(createRoomRequest) == null) {
            return new ApiResponse<>(201, "불가능한 요청입니다.", null);
        }
        return new ApiResponse<>(HttpStatus.OK.value(), "API 요청이 성공했습니다.", null);
    }


}
