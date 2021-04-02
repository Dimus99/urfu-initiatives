package com.example.demo.models;

public enum Permission {
    INITIATIVE_ADD("initiative:add"),
    INITIATIVE_MANAGE("initiative:manage"),
    USERS_MANAGE("users:manage");

    private final String permission;

    public String getPermission() {
        return permission;
    }

    Permission(String permission) {
        this.permission = permission;
    }
}
