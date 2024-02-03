package com.example.springquesstgroup1.controller;

import com.example.springquesstgroup1.dto.ApiResponse;
import com.example.springquesstgroup1.dto.InitRequest;
import com.example.springquesstgroup1.service.PingpongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class PingpongController {

    private final PingpongService pingpongService;

    @Autowired
    public PingpongController(PingpongService pingpongService) {
        this.pingpongService = pingpongService;
    }

    @GetMapping("/health")
    public ApiResponse healthCheck() {
        return new ApiResponse(200, "API 요청이 성공했습니다.", null);
    }

    @PostMapping("/init")
    public ApiResponse init(@RequestBody InitRequest initRequest) {
        if (pingpongService.init(initRequest)) {
            return new ApiResponse(HttpStatus.OK.value(), "API 요청이 성공했습니다.", null);
        }
        return new ApiResponse(500, "에러가 발생했습니다.", null);
    }


}
