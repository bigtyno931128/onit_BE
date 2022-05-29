package com.hanghae99.onit_be.noti;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeDto {

    private Long id;
    private String title;
    private String message;
    private String url;
    private boolean isRead;
    private String userName;

    public NoticeDto(Long id, String title, String message, String url, String userName, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.url = url;
        this.userName = userName;
        this.isRead = isRead;
    }
}
