package com.example.springquesstgroup1.dto;

import com.example.springquesstgroup1.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SelectAllUsersResponse {
    private int totalElements;
    private int totalPages;
    private List<User> userList;
}
