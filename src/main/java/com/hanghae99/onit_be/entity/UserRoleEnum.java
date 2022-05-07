package com.hanghae99.onit_be.entity;

public enum UserRoleEnum {

    //관리자 권한을 = > 게스트 권한으로 변경.

    USER(Authority.USER), // 사용자 권한
    GUEST(Authority.GUEST); // 게스트 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String GUEST = "ROLE_GUEST";
    }
}