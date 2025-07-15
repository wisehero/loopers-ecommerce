package com.loopers.interfaces.api.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.user.UserFacade;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.domain.user.dto.UserInfo;
import com.loopers.interfaces.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserV1Controller {

	private final UserFacade userFacade;

	@GetMapping("/me")
	public ApiResponse<UserV1Dto.UserResponse> getMyInfo(
		@RequestHeader("X-USER-ID") String userId
	) {
		UserInfo userInfo = userFacade.getUserInfo(userId);
		UserV1Dto.UserResponse response = UserV1Dto.UserResponse.from(userInfo);

		return ApiResponse.success(response);
	}

	@PostMapping
	public ApiResponse<UserV1Dto.UserResponse> signUp(
		@Valid @RequestBody UserV1Dto.SignUpRequest request) {
		UserCommand.Create command = request.toCommand();
		UserInfo userInfo = userFacade.signUp(command);
		UserV1Dto.UserResponse userResponse = UserV1Dto.UserResponse.from(userInfo);

		return ApiResponse.success(userResponse);
	}
}
