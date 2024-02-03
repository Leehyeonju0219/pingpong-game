package com.example.springquesstgroup1.entity;

import com.example.springquesstgroup1.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user_room")
public class UserRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "room_id", nullable = false)
    private int roomId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(nullable = false)
    private Team team;

    public UserRoom(int roomId, int userId, Team team) {
        this.roomId = roomId;
        this.userId = userId;
        this.team = team;
    }

}
