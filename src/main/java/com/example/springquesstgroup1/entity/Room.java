package com.example.springquesstgroup1.entity;

import com.example.springquesstgroup1.RoomStatus;
import com.example.springquesstgroup1.RoomType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int host;

    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    private RoomStatus status;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public Room(String title, int host, RoomType roomType, RoomStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.host = host;
        this.roomType = roomType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
