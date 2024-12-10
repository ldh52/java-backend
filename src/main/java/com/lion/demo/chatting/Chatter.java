package com.lion.demo.chatting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chatter {
    private String friendUid;
    private String friendName;
    private String profileUrl;
    private String message;
    private String timeStr;
    private int newCount;
}
