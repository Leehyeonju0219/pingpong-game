package com.example.springquesstgroup1.controller;

import com.example.springquesstgroup1.dto.*;
import com.example.springquesstgroup1.service.RoomService;
import com.example.springquesstgroup1.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "user", description = "사용자 API")
@RestController
public class UserController {

    private final UserService userService;

    private final RoomService roomService;

    @Autowired
    public UserController(UserService userService, RoomService roomService) {
        this.userService = userService;
        this.roomService = roomService;
    }

    @Operation(summary = "get all users", description = "모든 사용자 정보를 조회하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "API 요청이 성공했습니다."),
    })
    @Parameters({
            @Parameter(name = "size", description = "페이지 크기", example = "100"),
            @Parameter(name = "page", description = "페이지 번호", example = "0")
    })
    @GetMapping("/user")
    public ApiResponse selectAllUsers(@RequestParam(name = "size") int size,
                                      @RequestParam(name = "page") int page) {
        return new ApiResponse<>(200, "API 요청이 성공했습니다.", userService.selectAllUsers(size, page));
    }

}
