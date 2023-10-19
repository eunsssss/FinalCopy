package com.spring.boot.service;

import com.spring.boot.dto.PaymentInfoDTO;
import com.spring.boot.dto.userPointDTO;

public interface PaymentService {
    public void processPaymentInfo(PaymentInfoDTO paymentInfoDTO);
    // 추가적인 결제 관련 메서드를 필요에 따라 정의할 수 있습니다.
    public void updateUserPoint(userPointDTO userPointDTO);
    public void joinGroupAndDeductPoint(String email, int meetListNum);
    public int getUserPoint(String email);
    public void updateUserUsePoint(userPointDTO userPointDTO);

}