package com.example.springquesstgroup1.controller;

import com.example.springquesstgroup1.dto.InitRequest;
import com.example.springquesstgroup1.dto.NoDataApiResponse;
import com.example.springquesstgroup1.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "init", description = "초기 설정 API")
@RequiredArgsConstructor
public class InitController {
    private final UserService userService;

    @Operation(summary = "health check", description = "서버 상태를 체크하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 요청이 성공했습니다."),
            @ApiResponse(responseCode = "500", description = "에러가 발생했습니다.")
    })
    @GetMapping("/health")
    public NoDataApiResponse healthCheck() {
        NoDataApiResponse noDataApiResponse;
        try {
            noDataApiResponse = new NoDataApiResponse(200, "API 요청이 성공했습니다.");
        } catch (Exception e) {
            noDataApiResponse = new NoDataApiResponse(500, "에러가 발생했습니다.");
        }

        return noDataApiResponse;
    }

    @Operation(summary = "user info init", description = "기존에 있던 모든 회원 정보 및 방 정보를 삭제하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 요청이 성공했습니다."),
            @ApiResponse(responseCode = "500", description = "에러가 발생했습니다.")
    })
    @PostMapping("/init")
    public NoDataApiResponse init(@RequestBody InitRequest initRequest) {
        if (userService.init(initRequest)) {
            return new NoDataApiResponse(HttpStatus.OK.value(), "API 요청이 성공했습니다.");
        }
        return new NoDataApiResponse(500, "에러가 발생했습니다.");
    }
}
