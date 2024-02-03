package com.example.springquesstgroup1.controller;

import com.example.springquesstgroup1.dto.ApiResponse;
import com.example.springquesstgroup1.dto.CreateRoomRequest;
import com.example.springquesstgroup1.dto.NoDataApiResponse;
import com.example.springquesstgroup1.dto.RoomRequest;
import com.example.springquesstgroup1.entity.Room;
import com.example.springquesstgroup1.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "room", description = "방 API")
@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "create room", description = "방 생성 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "API 요청이 성공했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "불가능한 요청입니다.")
    })
    @PostMapping("/room")
    public NoDataApiResponse createRoom(@RequestBody CreateRoomRequest createRoomRequest) {
        if (roomService.createRoom(createRoomRequest) == null) {
            return new NoDataApiResponse(201, "불가능한 요청입니다.");
        }
        return new NoDataApiResponse(HttpStatus.OK.value(), "API 요청이 성공했습니다.");
    }

    @Operation(summary = "get all rooms", description = "방 전체 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "API 요청이 성공했습니다.")
    })
    @GetMapping("/room")
    public ApiResponse selectAllRooms(@RequestParam(name = "size") int size,
                                      @RequestParam(name = "page") int page) {
        return new ApiResponse<>(200, "API 요청이 성공했습니다.", roomService.selectAllRooms(size, page));
    }

    @Operation(summary = "get room detail", description = "방 상세 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "API 요청이 성공했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "불가능한 요청입니다.")
    })
    @Parameters({
            @Parameter(name = "roomId", description = "방 고유 번호", example = "1")
    })
    @GetMapping("/room/{roomId}")
    public ApiResponse roomDetail(@PathVariable(name = "roomId") int roomId) {
        Room room = roomService.roomDetail(roomId);
        if (room == null) return new ApiResponse(201, "불가능한 요청입니다.", null);
        else return new ApiResponse<>(200, "API 요청이 성공했습니다.", room);
    }

    @Operation(summary = "join room", description = "방 참가 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "API 요청이 성공했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "불가능한 요청입니다.")
    })
    @Parameters({
            @Parameter(name = "roomId", description = "방 고유 번호", example = "1")
    })
    @PostMapping("/room/attention/{roomId}")
    public NoDataApiResponse joinRoom(@PathVariable(name = "roomId") int roomId,
                                      @RequestBody RoomRequest roomRequest) {
        if (roomService.joinRoom(roomId, roomRequest.getUserId())) {
            return new NoDataApiResponse(200, "API 요청이 성공했습니다.");
        } else return new NoDataApiResponse(201, "불가능한 요청입니다.");
    }

    @Operation(summary = "out room", description = "방 퇴장 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "API 요청이 성공했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "불가능한 요청입니다.")
    })
    @Parameters({
            @Parameter(name = "roomId", description = "방 고유 번호", example = "1")
    })
    @PostMapping("/room/out/{roomId}")
    public NoDataApiResponse outRoom(@PathVariable(name = "roomId") int roomId,
                                     @RequestBody RoomRequest roomRequest) {
        if (roomService.outRoom(roomId, roomRequest.getUserId())) {
            return new NoDataApiResponse(200, "API 요청이 성공했습니다.");
        } else return new NoDataApiResponse(201, "불가능한 요청입니다.");
    }

    @Operation(summary = "start game", description = "게임 시작 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "API 요청이 성공했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "불가능한 요청입니다.")
    })
    @Parameters({
            @Parameter(name = "roomId", description = "방 고유 번호", example = "1")
    })
    @PutMapping("/room/start/{roomId}")
    public NoDataApiResponse startGame(@PathVariable(name = "roomId") int roomId,
                                       @RequestBody RoomRequest roomRequest) {
        boolean result = roomService.startGame(roomId, roomRequest.getUserId());
        if (!result) return new NoDataApiResponse(201, "불가능한 요청입니다.");

        return new NoDataApiResponse(200, "API 요청이 성공했습니다.");
    }

    @Operation(summary = "change team", description = "팀 변경 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "API 요청이 성공했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "불가능한 요청입니다.")
    })
    @Parameters({
            @Parameter(name = "roomId", description = "방 고유 번호", example = "1")
    })
    @PutMapping("/team/{roomId}")
    public NoDataApiResponse changeTeam(@PathVariable(name = "roomId") int roomId,
                                        @RequestBody RoomRequest roomRequest) {
        boolean result = roomService.changeTeam(roomId, roomRequest.getUserId());
        if (!result) return new NoDataApiResponse(201, "불가능한 요청입니다.");

        return new NoDataApiResponse(200, "API 요청이 성공했습니다.");
    }

}
