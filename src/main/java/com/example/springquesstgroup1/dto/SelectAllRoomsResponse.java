package com.example.springquesstgroup1.dto;

import com.example.springquesstgroup1.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SelectAllRoomsResponse {
    private int totalElements;
    private int totalPages;
    private List<Room> roomList;
}
