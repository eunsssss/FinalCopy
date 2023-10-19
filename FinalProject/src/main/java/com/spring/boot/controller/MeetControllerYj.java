package com.spring.boot.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.spring.boot.dto.GatchiDTO;
import com.spring.boot.dto.MeetDTOYj;
import com.spring.boot.dto.PointHistoryDTO;
import com.spring.boot.dto.userPointDTO;
import com.spring.boot.model.Users;
import com.spring.boot.service.GatchiService;
import com.spring.boot.service.MeetServiceYj;
import com.spring.boot.service.PaymentService;
import com.spring.boot.service.PointHistoryService;

@RestController
public class MeetControllerYj {
    
	@Autowired
	private MeetServiceYj meetServiceYj;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private PointHistoryService pointHistoryService;

	@Autowired
	private GatchiService gatchiService;

	@GetMapping("/listYj.action")
	public ModelAndView listYj() throws Exception {
		List<MeetDTOYj> listYj = meetServiceYj.getAllCategories();

		ModelAndView mav = new ModelAndView();
		if(listYj != null){
			listYj = new ArrayList<>();
		}
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
		MeetDTOYj dto = new MeetDTOYj();
		
		mav.addObject("loginEmail", "kim");  // TODO : 로그인된 email

		dto.setMeetListNum(Integer.parseInt(request.getParameter("meetListNum")));
		dto.setEmail("kim"); // TODO : 세션에서 email 가져와야됨

		int memberStatus = -1;
		Integer ret = meetServiceYj.getMemberStatus(dto);
		if (ret != null) memberStatus = ret.intValue();

		if (memberStatus == 1) {
			String masterEmail = meetServiceYj.getMeetMasterEmail(Integer.parseInt(request.getParameter("meetListNum")));
			mav.addObject("masterEmail", masterEmail);
		}

		mav.addObject("meetListNum", request.getParameter("meetListNum"));
		mav.addObject("meetListInfo", meetListInfo);
		mav.addObject("meetMembers", meetMembers);
		mav.addObject("meetReview", meetReview);
		mav.addObject("meetMemStatus", memberStatus);
		mav.setViewName("bbs/articleYj");
		
		return mav;
		
	}
	
	//리뷰 올리기
	@PostMapping("/upload-review")
    public String uploadReview(HttpServletRequest request,
            @RequestParam("meetListNum") int meetListNum,
            @RequestParam("meetReviewContent") String meetReviewContent,
            @RequestParam("meetReviewImg") MultipartFile meetReviewImg) throws Exception {
        
		Resource resource = new ClassPathResource("static");
      	String resourcePath = resource.getFile().getAbsolutePath() + "/image/reviewImage";

        MeetDTOYj dto = new MeetDTOYj();
		
		dto.setMeetListNum(meetListNum);
		dto.setEmail("kim"); // TODO : 세션에서 email 가져와야됨

		// 중복 리뷰 작성 여부 확인
		int hasReviewed = meetServiceYj.hasUserReviewed(dto);
		String response = "";

		if (hasReviewed<=0) {

			if (!meetReviewImg.isEmpty()) {
				String originalFilename = meetReviewImg.getOriginalFilename();
				String saveFileName = originalFilename + UUID.randomUUID();
				System.out.println(saveFileName);
				Path filePath = Paths.get(resourcePath, saveFileName);
            	// 파일 저장
            	Files.write(filePath, meetReviewImg.getBytes());
				// File destFile = new File(resourcePath, saveFileName);
				// meetReviewImg.transferTo(destFile);

				dto.setMeetReviewImg(saveFileName); // 원본 이미지 파일 이름을 저장
				dto.setMeetReviewContent(meetReviewContent);
				dto.setMeetReviewDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

				int meetReviewNum = meetServiceYj.getReviewNum(meetListNum);
				dto.setMeetReviewNum(meetReviewNum);
				
				meetServiceYj.insertMeetReview(dto);
				response = "success";
				return response; // 리뷰 작성 성공 시 success 페이지로 리다이렉트
        } 

		}else{
			response = "already-reviewed";
            return response; // 이미 리뷰를 작성한 경우 already-reviewed 페이지로 리다이렉트
		}
		return "already-reviewed";
    }

	// 리뷰 삭제
	@PostMapping("/delete-review")
	public ModelAndView deleteReview(HttpServletRequest request,
			@RequestParam("meetListNum") int meetListNum,
			@RequestParam("meetReviewNum") int meetReviewNum,
			@RequestParam("email") String email) throws Exception {

		MeetDTOYj dto = new MeetDTOYj();
		
		dto.setMeetListNum(meetListNum);
		dto.setEmail(email);
		dto.setMeetReviewNum(meetReviewNum);

		meetServiceYj.deleteMeetReview(dto);		


		return new ModelAndView("redirect:/articleYj.action?meetListNum=" + meetListNum);
	}

	
	// 방장이 관리할 수 있는 곳
	@GetMapping("/managerYj.action")
	public ModelAndView manageYj(HttpServletRequest request) throws Exception {

		int meetListNum = Integer.parseInt(request.getParameter("meetListNum"));

		List<String> meetMembers = meetServiceYj.getMeetMembers(meetListNum);
		List<String> meetBlack = meetServiceYj.getMeetBlack(meetListNum);
		List<String> meetWait = meetServiceYj.getMeetWait(meetListNum);

		ModelAndView mav = new ModelAndView();
		mav.addObject("meetListNum", meetListNum);
		mav.addObject("meetMembers", meetMembers);
		mav.addObject("meetWait", meetWait);
		mav.addObject("meetBlack", meetBlack);

		// 방장의 이메일을 모델에 추가
		String masterEmail = meetServiceYj.getMeetMasterEmail(meetListNum);
		mav.addObject("masterEmail", masterEmail);

		mav.setViewName("bbs/managerYj");
		
		return mav;
		
	}

	// 방 가입
	@GetMapping("/join-meet")
	public ModelAndView  joinMeet(HttpServletRequest request,
			@RequestParam("meetListNum") int meetListNum) throws Exception {

		ModelAndView mav = new ModelAndView("redirect:/articleYj.action?meetListNum=" + meetListNum);

		//String email = (String) request.getSession().getAttribute("email"); //세션
		System.out.println("join-meet 들어옴");

		HttpSession session = request.getSession();
		Users user = (Users) session.getAttribute("user1");
		String useremail = user.getEmail();
		
		// 사용자가 현재 가지고 있는 포인트
		int userPoint = paymentService.getUserPoint(useremail);
		
		GatchiDTO gatchiDto = gatchiService.getReadData(meetListNum);
		
		// 유저 포인트 감소
		userPointDTO userpointDTO = new userPointDTO();
		userpointDTO.setUseremail(useremail);
		userpointDTO.setPointBalance(gatchiDto.getMeetMoney());
		paymentService.updateUserUsePoint(userpointDTO);

		// 히스토리 업데이트(추가)
		PointHistoryDTO pointDto = new PointHistoryDTO();
		pointDto.setUseremail(useremail);
		pointDto.setUseType(1);
		pointDto.setUsePoint(gatchiDto.getMeetMoney());
		pointDto.setPointUseHistory(gatchiDto.getMeetTitle());
		pointDto.setAfterPoint(userPoint);
		pointDto.setBeforPoint(userPoint - gatchiDto.getMeetMoney());
		pointHistoryService.insertPointHistory(pointDto);

		MeetDTOYj dto = new MeetDTOYj();
		dto.setMeetListNum(meetListNum);
		dto.setEmail("kim"); // TODO : 세션에서 email 가져와야됨
		dto.setMeetMemStatus(0); //승인대기
		meetServiceYj.insertMeetJoinOk(dto);
	
		return mav;
	}

	// 승인대기 수락
	@PostMapping("/accept-to-waitlist")
	public ModelAndView acceptToWaitlist(
			@RequestParam("meetListNum") int meetListNum,
			@RequestParam("email") String email) throws Exception {

		MeetDTOYj dto = new MeetDTOYj();

		dto.setMeetListNum(meetListNum);
		dto.setEmail(email);

		meetServiceYj.acceptToWaitlist(dto); //멤버 상태(2로)변경
		meetServiceYj.incrementMeetMemCnt(meetListNum);//방인원수 증가
		
		return new ModelAndView("redirect:/managerYj.action?meetListNum=" + meetListNum);
	}

	// 승인대기 거절
	@PostMapping("/reject-from-waitlist")
	public ModelAndView rejectFromWaitlist(
			@RequestParam("meetListNum") int meetListNum,
			@RequestParam("email") String email) throws Exception {

		MeetDTOYj dto = new MeetDTOYj();
		dto.setMeetListNum(meetListNum);
		dto.setEmail(email);
		meetServiceYj.rejectFromWaitlist(dto);

		return new ModelAndView("redirect:/managerYj.action?meetListNum=" + meetListNum);
	}
		
	// 블랙리스트에 추가
	@PostMapping("/add-to-blacklist")
	public ModelAndView addToBlacklist(
			@RequestParam("meetListNum") int meetListNum,
			@RequestParam("email") String email) throws Exception {

		MeetDTOYj dto = new MeetDTOYj();

		dto.setMeetListNum(meetListNum);
		dto.setEmail(email);

		meetServiceYj.addToBlacklist(dto);
		meetServiceYj.decrementMeetMemCnt(meetListNum);

		return new ModelAndView("redirect:/managerYj.action?meetListNum=" + meetListNum);
	}


	// 블랙리스트 해제
	@PostMapping("/release-from-blacklist")
	public ModelAndView releaseFromBlacklist(
			@RequestParam("meetListNum") int meetListNum,
			@RequestParam("email") String email) throws Exception {

		MeetDTOYj dto = new MeetDTOYj();
		dto.setMeetListNum(meetListNum);
		dto.setEmail(email);
		meetServiceYj.releaseFromBlacklist(dto);

		return new ModelAndView("redirect:/managerYj.action?meetListNum=" + meetListNum);
	}

	// 포인트 있는지 확인
	@PostMapping("/checkuserpoint")
	public Boolean checkUserMoney(HttpServletRequest req, @RequestParam("meetMoney") String meetMoney){
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user1");
		String useremail = user.getEmail();
		System.out.println("유저 이메일 : " + useremail);
		System.out.println("포인트 체크 하는 곳");
		int userPoint = paymentService.getUserPoint(useremail);
		int money = Integer.parseInt(meetMoney);

		System.out.println("유저 포인트 : " + userPoint);
		System.out.println("meetMoney = " + meetMoney);

		if(userPoint < money){
			return false;
		}
		return true;
	}

	// 가입 취소시 
	@PostMapping("/joincancel")
	public ModelAndView joinCancel(){

		

		return null;
	}

}
