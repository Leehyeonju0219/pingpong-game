package com.example.springquesstgroup1.dto;

import com.example.springquesstgroup1.RoomType;
import lombok.Getter;

@Getter
public class CreateRoomRequest {
    private int userId;
    private RoomType roomType;
    private String title;
}
