package com.loopers.interfaces.api.point;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.point.PointWalletFacade;
import com.loopers.domain.point.PointWalletCommand;
import com.loopers.domain.point.PointWalletResult;
import com.loopers.interfaces.api.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointV1Controller {

	private final PointWalletFacade pointWalletFacade;

	@GetMapping
	public ApiResponse<PointWalletV1Dto.BalanceResponse> getPointBalance(
		@RequestHeader(name = "X-USER-ID") String loginId) {

		PointWalletResult.BalanceInfo balance = pointWalletFacade.getBalance(loginId);
		return ApiResponse.success(PointWalletV1Dto.BalanceResponse.from(balance));
	}

	@PostMapping
	public ApiResponse<PointWalletV1Dto.DepositResponse> depositPoints(@RequestHeader(name = "X-USER-ID") String loginId,
		@RequestBody PointWalletV1Dto.DepositRequest request) {

		PointWalletCommand.Deposit command = request.toCommand(loginId);
		PointWalletResult.BalanceInfo balanceInfo = pointWalletFacade.deposit(command);

		return ApiResponse.success(PointWalletV1Dto.DepositResponse.from(balanceInfo));
	}
}
