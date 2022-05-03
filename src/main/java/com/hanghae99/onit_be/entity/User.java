package com.hanghae99.onit_be.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tbl_user")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id")
    private Long id;

    @Column
    private String userNickname;

    @Column
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String profileImg;

    // 일반 회원가입인 경우 kakaoId는 null
    @Column(unique = true)
    private Long kakaoId;

    //수정 한 부분 . 일반 회원 가입 시에는  USER 로 ROLE 등록
    @Enumerated(EnumType.STRING)
    private UserRoleEnum userRole;


//    @OneToMany(mappedBy = "user")
//    @JoinColumn
//    private List<Plan> planList = new ArrayList<>();


    User(Builder builder) {
        this.username = builder.username;
        this.userNickname = builder.nickname;
        this.password = builder.password;
        this.profileImg = builder.profileImg;
        this.kakaoId = builder.kakaoId;
    }

    public User(String username, String password, String userNickname, UserRoleEnum role, String profileImg) {
        this.username = username;
        this.password = password;
        this.userNickname = userNickname;
        this.userRole = role;
        this.profileImg = profileImg;
    }

    // 프로필 이미지 수정 시, 필요한 생성자
    public User(Long id, String imageUrl) {
        this.id = id;
        this.profileImg = imageUrl;
    }

    // 이미지 수정 메서드
    public void update(Long id, String imageKey) {
        this.id = id;
        this.profileImg = imageKey;
    }

    public static class Builder {
        private String username;
        private String nickname;
        private String password;
        private String profileImg;
        private Long kakaoId;

        // 필수적인 필드들
        public Builder(String username, String nickname, String password) {
            this.username = username;
            this.nickname = nickname;
            this.password = password;
        }

        // 선택적인 필드들(null값 들어올수 있음)
        public Builder profileImg(String profileImg) {
            this.profileImg = profileImg;
            return this;
        }

        public Builder kakaoId(Long kakaoId) {
            this.kakaoId = kakaoId;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public void updateNicknameAndProfileImg(String nickname, String imgUrl) {
        this.userNickname = nickname;
        this.profileImg = imgUrl;
    }

    public void updateNickname(String nickname) {
        this.userNickname = nickname;
    }

    public User(String username, String password, String userNickname) {
        this.username = username;
        this.password = password;
        this.userNickname = userNickname;
    }

}
