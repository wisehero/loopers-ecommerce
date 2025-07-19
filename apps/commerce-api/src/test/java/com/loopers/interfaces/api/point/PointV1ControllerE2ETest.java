package com.loopers.interfaces.api.point;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointWallet;
import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.infrastructure.point.PointWalletJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointV1ControllerE2ETest {

	private final TestRestTemplate testRestTemplate;
	private final PointWalletJpaRepository pointWalletJpaRepository;
	private final UserJpaRepository userJpaRepository;
	private final DatabaseCleanUp databaseCleanUp;

	@Autowired
	public PointV1ControllerE2ETest(
		TestRestTemplate testRestTemplate,
		PointWalletJpaRepository pointWalletJpaRepository,
		UserJpaRepository userJpaRepository,
		DatabaseCleanUp databaseCleanUp
	) {
		this.testRestTemplate = testRestTemplate;
		this.pointWalletJpaRepository = pointWalletJpaRepository;
		this.userJpaRepository = userJpaRepository;
		this.databaseCleanUp = databaseCleanUp;
	}

	@AfterEach
	void tearDown() {
		databaseCleanUp.truncateAllTables();
	}

	@DisplayName("GET /api/v1/points 사용자 포인트 조회")
	@Nested
	class GetPointBalance {

		private final String ENDPOINT = "/api/v1/points";

		@DisplayName("유효한 X-USER-ID를 제공하면, 사용자 포인트 정보를 반환한다.")
		@Test
		void returnsPointBalance_whenValidUserIdProvided() {
			// given
			UserCommand.Create command = new UserCommand.Create(
				"user123",
				"iop1996@naver.com",
				"MALE",
				"1996-05-04"
			);
			User savedUser = userJpaRepository.save(User.create(command));
			String requestHeaderKey = "X-USER-ID";
			String requestHeaderValue = savedUser.getLoginId().value();
			PointWallet initWallet = PointWallet.create(LoginId.of(savedUser.getLoginId().value()));
			initWallet.deposit(Point.of(1000L));
			pointWalletJpaRepository.save(initWallet);

			// when
			var responseType = new ParameterizedTypeReference<ApiResponse<PointWalletV1Dto.BalanceResponse>>() {
			};
			var response = testRestTemplate.exchange(
				ENDPOINT, HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders() {{
					set(requestHeaderKey, requestHeaderValue);
				}}
				),
				responseType
			);

			// then
			assertAll(
				() -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
				() -> {
					PointWalletV1Dto.BalanceResponse responseData = response.getBody().data();
					assertThat(responseData).isNotNull();
					assertThat(responseData.loginId()).isEqualTo(savedUser.getLoginId().value());
					assertThat(responseData.balance()).isEqualTo(1000L);
				}
			);
		}

		@DisplayName("유효하지 않은 X-USER-ID를 제공하면, 404 NOT FOUND 에러가 발생한다.")
		@Test
		void returnsNotFound_whenInvalidUserIdProvided() {
			// given
			String invalidUserId = "notuser";
			String requestHeaderKey = "X-USER-ID";

			// when
			var responseType = new ParameterizedTypeReference<ApiResponse<PointWalletV1Dto.BalanceResponse>>() {
			};
			var response = testRestTemplate.exchange(
				ENDPOINT, HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders() {{
					set(requestHeaderKey, invalidUserId);
				}}
				),
				responseType
			);

			// then
			assertAll(
				() -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
				() -> assertThat(response.getBody().meta().message()).isEqualTo("지갑을 소유한 사용자가 존재하지 않습니다.")
			);
		}
	}

	@DisplayName("POST /api/v1/points 포인트 충전")
	@Nested
	class DepositPoints {

		private final String ENDPOINT = "/api/v1/points";

		@DisplayName("유효한 X-USER-ID와 포인트 충전 요청을 제공하면, 포인트가 충전되고, 충전된 포인트 정보를 반환한다.")
		@Test
		void depositsPoints_whenValidRequestProvided() {
			// given
			UserCommand.Create command = new UserCommand.Create(
				"user123",
				"iop1996@naver.com",
				"MALE",
				"1996-05-04"
			);
			User savedUser = userJpaRepository.save(User.create(command));
			String requestHeaderKey = "X-USER-ID";
			String requestHeaderValue = savedUser.getLoginId().value();
			PointWallet initWallet = PointWallet.create(LoginId.of(savedUser.getLoginId().value()));
			initWallet.deposit(Point.of(1000L));
			pointWalletJpaRepository.save(initWallet);
			PointWalletV1Dto.DepositRequest depositRequest = new PointWalletV1Dto.DepositRequest(500L);

			// when
			var responseType = new ParameterizedTypeReference<ApiResponse<PointWalletV1Dto.BalanceResponse>>() {
			};
			var response = testRestTemplate.exchange(
				ENDPOINT,
				HttpMethod.POST,
				new HttpEntity<>(depositRequest, new HttpHeaders() {{
					set(requestHeaderKey, requestHeaderValue);
				}}),
				responseType
			);

			// then
			assertAll(
				() -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
				() -> {
					PointWalletV1Dto.BalanceResponse responseData = response.getBody().data();
					assertThat(responseData).isNotNull();
					assertThat(responseData.loginId()).isEqualTo(savedUser.getLoginId().value());
					assertThat(responseData.balance()).isEqualTo(1500L);
				}
			);
		}

		@DisplayName("유효하지 않은 X-USER-ID를 제공하면, 404 NOT FOUND 에러가 발생한다.")
		@Test
		void returnsNotFound_whenInvalidUserIdProvidedForDeposit() {
			// given
			String invalidUserId = "notuser";
			PointWalletV1Dto.DepositRequest depositRequest = new PointWalletV1Dto.DepositRequest(500L);

			// when
			var responseType = new ParameterizedTypeReference<ApiResponse<PointWalletV1Dto.BalanceResponse>>() {
			};
			var response = testRestTemplate.exchange(
				ENDPOINT,
				HttpMethod.POST,
				new HttpEntity<>(depositRequest, new HttpHeaders() {{
					set("X-USER-ID", invalidUserId);
				}}),
				responseType
			);

			// then
			assertAll(
				() -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
				() -> assertThat(response.getBody().meta().message()).isEqualTo("지갑을 소유한 사용자가 존재하지 않습니다.")
			);
		}
	}
}
