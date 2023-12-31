package com.spring.boot.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetInfoDTO {
    private int meetListNum; 
    private String email;
    private int meetMemStatus; //0승인대기중 1방장 2회원 3블랙
    private int approvalStatus; //0승인대기중 1승인함 2기각함
    
    private String name;
    private String picture;
    private String role;
}
