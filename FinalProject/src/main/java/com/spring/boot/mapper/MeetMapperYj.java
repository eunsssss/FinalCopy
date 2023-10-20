package com.spring.boot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.boot.dto.GatchiDTO;
import com.spring.boot.dto.MeetCategoryDTO;
import com.spring.boot.dto.MeetInfoDTO;
import com.spring.boot.dto.MeetReviewDTO;

@Mapper
public interface MeetMapperYj {

    public String getMeetMasterName(int meetListNum) throws Exception;

    public List<MeetCategoryDTO> getAllCategories() throws Exception;

    public GatchiDTO getMeetListInfo(int meetListNum) throws Exception;

    public List<MeetInfoDTO> getMeetMembers(int meetListNum) throws Exception;

    public String getMeetMasterEmail(int meetListNum) throws Exception;

    public List<MeetReviewDTO> getReview(int meetListNum) throws Exception;

    public void insertMeetReview(MeetReviewDTO dto) throws Exception;

    public int getReviewNum(int meetListNum) throws Exception;

    public int hasUserReviewed(MeetReviewDTO dto) throws Exception;

    public void deleteMeetReview(MeetReviewDTO dto) throws Exception;

    public Integer getMemberStatus(MeetInfoDTO dto) throws Exception;
    
    public int getMeetHow(int meetListNum) throws Exception;

    public void insertMeetJoinOk(MeetInfoDTO dto) throws Exception;

    public void deleteMeetOut(MeetInfoDTO dto) throws Exception;

    public void updateMeetStatus(GatchiDTO dto) throws Exception;

    public void meetStatusCompletion(GatchiDTO dto) throws Exception;

    public int getMeetStatus(int meetListNum) throws Exception;

    public List<String> getMeetWait(int meetListNum) throws Exception;

    public int acceptToWaitlist(MeetInfoDTO dto) throws Exception;

    public void incrementMeetMemCnt(int meetListNum) throws Exception;

    public void rejectFromWaitlist(MeetInfoDTO dto) throws Exception;

    public List<String> getMeetBlack(int meetListNum) throws Exception;
    
    public void addToBlacklist(MeetInfoDTO dto) throws Exception;

    public void decrementMeetMemCnt(int meetListNum) throws Exception;

    public void releaseFromBlacklist(MeetInfoDTO dto) throws Exception;

}
