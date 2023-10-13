package com.spring.boot.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.spring.boot.dto.MeetDTOYj;
import com.spring.boot.service.MeetServiceYj;

@RestController
public class MeetControllerYj {
    
	@Autowired
	private MeetServiceYj meetServiceYj;

	@GetMapping("/listYj.action")
	public ModelAndView listYj() throws Exception {
		List<MeetDTOYj> listYj = meetServiceYj.getAllCategories();

		ModelAndView mav = new ModelAndView();
		mav.addObject("listYj", listYj);
		mav.setViewName("bbs/listYj");

		return mav;
	}

	@GetMapping("/articleYj.action")
	public ModelAndView articleYj(HttpServletRequest request) throws Exception {

		MeetDTOYj meetListInfo = meetServiceYj.getMeetListInfo(Integer.parseInt(request.getParameter("meetListNum")));
		List<String> meetMembers = meetServiceYj.getMeetMembers(Integer.parseInt(request.getParameter("meetListNum")));
		List<MeetDTOYj> meetReview = meetServiceYj.getReview(Integer.parseInt(request.getParameter("meetListNum")));
		ModelAndView mav = new ModelAndView();

		mav.addObject("meetListNum", request.getParameter("meetListNum"));
		mav.addObject("meetListInfo", meetListInfo);
		mav.addObject("meetMembers", meetMembers);
		mav.addObject("meetReview", meetReview);
		mav.setViewName("bbs/articleYj");
		
		return mav;
		
	}
	
	//리뷰 올리기
	@PostMapping("/upload-review")
    public ModelAndView uploadReview(HttpServletRequest request,
            @RequestParam("meetListNum") int meetListNum,
            @RequestParam("meetReviewContent") String meetReviewContent,
            @RequestParam("meetReviewImg") MultipartFile meetReviewImg) throws Exception {
        
        String uploadDir = "C:\\VSCode\\Final\\FinalProject\\src\\main\\resources\\static\\image\\reviewImage";

        MeetDTOYj dto = new MeetDTOYj();

        if (!meetReviewImg.isEmpty()) {
            String originalFilename = meetReviewImg.getOriginalFilename();
			File destFile = new File(uploadDir, originalFilename);
			meetReviewImg.transferTo(destFile);

			dto.setMeetReviewImg(originalFilename); // 원본 이미지 파일 이름을 저장
			dto.setMeetListNum(meetListNum);
			dto.setEmail("kim"); // TODO : 세션에서 memid 가져와야됨
            dto.setMeetReviewContent(meetReviewContent);
            dto.setMeetReviewDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

			int meetReviewNum = meetServiceYj.getReviewNum(meetListNum);
			dto.setMeetReviewNum(meetReviewNum);

			meetServiceYj.insertMeetReview(dto);
		}
        // 기존 articleYj 화면으로 리다이렉트
        return new ModelAndView("redirect:/articleYj.action?meetListNum=" + meetListNum);
    }
	
	
	// 방장이 관리할 수 있는 곳
	@GetMapping("/managerYj.action")
	public ModelAndView manageYj(HttpServletRequest request) throws Exception {

		int meetListNum = Integer.parseInt(request.getParameter("meetListNum"));

		List<String> meetMembers = meetServiceYj.getMeetMembers(meetListNum);
		List<String> meetBlack = meetServiceYj.getMeetBlack(meetListNum);

		ModelAndView mav = new ModelAndView();
		mav.addObject("meetMembers", meetMembers);
		mav.addObject("meetBlack", meetBlack);
		mav.setViewName("bbs/managerYj");
		
		return mav;
		
	}

	// 방 가입
	@PostMapping("/join-meet")
	public ModelAndView  joinMeet(HttpServletRequest request,
			@RequestParam("meetListNum") int meetListNum) throws Exception {

		ModelAndView mav = new ModelAndView();

		//String email = (String) request.getSession().getAttribute("email"); //세션
	
		MeetDTOYj dto = new MeetDTOYj();
		dto.setMeetListNum(meetListNum);
		dto.setEmail("kim"); // TODO : 세션에서 email 가져와야됨
		dto.setMeetMemStatus(0); //승인대기
	
		meetServiceYj.insertMeetJoinOk(dto);
	
		mav.addObject("message", "가입 신청이 완료되었습니다.");
		// mav.setViewName("joinSuccessPage"); // 가입 완료 페이지 뷰로 이동

		return new ModelAndView("redirect:/articleYj.action?meetListNum=" + meetListNum);
	}

	// 블랙리스트에 추가 또는 해제
	@PostMapping("/update-meet-blacklist")
	public ModelAndView updateMeetBlacklist(
			@RequestParam("meetListNum") int meetListNum,
			@RequestParam("email") String email,
			@RequestParam("action") String action) throws Exception {

		if ("add".equals(action)) {
			meetServiceYj.addToBlacklist(meetListNum, email);
		} else if ("release".equals(action)) {
			meetServiceYj.releaseFromBlacklist(meetListNum, email);
		}

		return new ModelAndView("redirect:/managerYj.action?meetListNum=" + meetListNum);
	}
		
	// // 블랙리스트에 추가
	// @PostMapping("/add-to-blacklist")
	// public ModelAndView addToBlacklist(@RequestParam("meetListNum") int meetListNum, @RequestParam("email") String email) throws Exception {

	// 	meetServiceYj.addToBlacklist(meetListNum, email);

	// 	return new ModelAndView("redirect:/managerYj.action?meetListNum=" + meetListNum);
	// }

	// // 블랙리스트 해제
	// @PostMapping("/release-from-blacklist")
	// public ModelAndView releaseFromBlacklist(@RequestParam("meetListNum") int meetListNum, @RequestParam("email") String email) throws Exception {

	// 	meetServiceYj.releaseFromBlacklist(meetListNum, email);

	// 	return new ModelAndView("redirect:/managerYj.action?meetListNum=" + meetListNum);
	// }

}
