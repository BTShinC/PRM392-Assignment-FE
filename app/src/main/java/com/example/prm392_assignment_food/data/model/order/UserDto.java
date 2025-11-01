package com.example.prm392_assignment_food.data.model.order;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDto {
    private UUID userId;

    private String name;

    private String phone;

    private String email;

    private UserRole role;

    private String address;

    private UserStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
