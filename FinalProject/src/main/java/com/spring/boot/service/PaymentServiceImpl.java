package com.spring.boot.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.boot.dto.PaymentInfoDTO;
import com.spring.boot.dto.userPointDTO;
import com.spring.boot.mapper.PaymentMapper;

@Service
public class PaymentServiceImpl implements PaymentService{

	@Autowired //의존성 주입
	private PaymentMapper paymentMapper;

	@Override
	public void insertPaymentInfo(PaymentInfoDTO paymentInfoDTO) throws Exception {
		paymentMapper.insertPaymentInfo(paymentInfoDTO);
	}

	@Override
	public void updateOrInsertUserPointWithMap(Map<String, Object> params) throws Exception {
		paymentMapper.updateOrInsertUserPointWithMap(params);
	}

	@Override
	public int getUserPoint(String email) throws Exception {
		return paymentMapper.getUserPoint(email);
	}

    @Override
    public List<PaymentInfoDTO> findByEmail(String email) throws Exception {
        return paymentMapper.findByEmail(email);
    }

	@Override
    public void updateUserPoint(userPointDTO userPointDTO) {
        paymentMapper.updateUserPoint(userPointDTO);
    }

	@Override
    public void updateUserUsePoint(userPointDTO userPointDTO){
        paymentMapper.updateUserUsePoint(userPointDTO);
    }

	@Override
    public void insertUserAfterSignUp(String email) throws Exception {
        paymentMapper.insertUserAfterSignUp(email);
    }
	
	// 이후 필요한 추가적인 비즈니스 로직 메서드의 구현을 여기에 추가할 수 있습니다.
}