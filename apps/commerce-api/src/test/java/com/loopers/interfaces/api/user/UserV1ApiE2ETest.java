package com.loopers.interfaces.api.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserId;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserV1ApiE2ETest {

	private final TestRestTemplate testRestTemplate;
	private final UserJpaRepository userJpaRepository;
	private final DatabaseCleanUp databaseCleanUp;

	@Autowired
	public UserV1ApiE2ETest(
		TestRestTemplate testRestTemplate,
		UserJpaRepository userJpaRepository,
		DatabaseCleanUp databaseCleanUp
	) {
		this.testRestTemplate = testRestTemplate;
		this.userJpaRepository = userJpaRepository;
		this.databaseCleanUp = databaseCleanUp;
	}

	@AfterEach
	void tearDown() {
		databaseCleanUp.truncateAllTables();
	}

	@DisplayName("GET /api/v1/users/me 내 정보 조회")
	@Nested
	class GetMyInfo {

		private final String ENDPOINT = "/api/v1/users/me";

		@DisplayName("유효한 X-USER-ID를 제공하면, 사용자 정보를 반환한다.")
		@Test
		void returnUserInfo_whenValidUserIdIsProvided() {
			// given
			UserCommand.Create command = new UserCommand.Create(
				"user123",
				"iop1996@naver.com",
				"MALE",
				"1996-05-04"
			);
			User savedUser = userJpaRepository.save(User.create(command));
			String requestHeaderKey = "X-USER-ID";
			String requestHeaderValue = savedUser.getUserId().value();

			// when
			var responseType = new ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {
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
					UserV1Dto.UserResponse responseData = response.getBody().data();
					assertThat(responseData.userId()).isEqualTo(savedUser.getUserId().value());
					assertThat(responseData.email()).isEqualTo(savedUser.getEmail().getEmailAddress());
					assertThat(responseData.gender()).isEqualTo(savedUser.getGender().name());
					assertThat(responseData.birthDate()).isEqualTo(savedUser.getBirthDate().toString());
				}
			);
		}

		@DisplayName("존재하지 않는 X-USER-ID를 제공하면, null을 반환한다.")
		@Test
		void returnsNotFound_whenUserDoesNotExist() {
			// given
			var nonExistentUserId = "nonUser";
			var headers = new HttpHeaders();
			headers.set("X-USER-ID", nonExistentUserId);
			var requestEntity = new HttpEntity<>(null, headers);

			// when
			var responseType = new ParameterizedTypeReference<ApiResponse<Object>>() {
			};
			var response = testRestTemplate.exchange(ENDPOINT, HttpMethod.GET, requestEntity, responseType);

			// then
			assertThat(response.getBody().data()).isNull();
		}
	}

	@DisplayName("POST /api/v1/users 테스트")
	@Nested
	class SignUp {

		private final String ENDPOINT = "/api/v1/users";

		@DisplayName("POST /api/v1/users 회원가입")
		@Test
		void createsUser_whenRequestIsValid() {
			// given
			var request = new UserV1Dto.SignUpRequest(
				"newUser", "new.user@example.com", "FEMALE", "1996-05-04"
			);
			var requestEntity = new HttpEntity<>(request);

			// when
			var responseType = new ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {
			};
			var response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, requestEntity, responseType);

			// then
			assertAll(
				() -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
				() -> {
					UserV1Dto.UserResponse responseData = response.getBody().data();
					assertThat(responseData.userId()).isEqualTo("newUser");
				},
				() -> assertThat(userJpaRepository.findByUserId(UserId.of("newUser"))).isPresent()
			);
		}

		@DisplayName("요청 DTO의 유효성 검증에 실패하면, 400 BAD_REQUEST 응답을 받는다.")
		@ParameterizedTest(name = "{0}")
		@MethodSource("invalidSignUpRequests")
		void returnsBadRequest_whenRequestIsInvalid(
			String testName,
			UserV1Dto.SignUpRequest request
		) {
			// given
			var signUpRequestHttpEntity = new HttpEntity<>(request);

			// when 
			var response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, signUpRequestHttpEntity, new ParameterizedTypeReference<>() {
			});
		}

		private static Stream<Arguments> invalidSignUpRequests() {
			return Stream.of(
				Arguments.of("ID가 null일 때", new UserV1Dto.SignUpRequest(null, "test@e.com", "MALE", "2000-01-01")),
				Arguments.of("이메일 형식이 틀렸을 때", new UserV1Dto.SignUpRequest("test", "invalid-email", "MALE", "2000-01-01")),
				Arguments.of("성별 형식이 틀렸을 때", new UserV1Dto.SignUpRequest("test", "test@e.com", "OTHER", "2000-01-01")),
				Arguments.of("생년월일 형식이 틀렸을 때", new UserV1Dto.SignUpRequest("test", "test@e.com", "FEMALE", "2000/01/01")));
		}

		@DisplayName("이미 존재하는 ID로 가입을 요청하면, 409 CONFLICT 응답을 받는다.")
		@Test
		void returnsConflict_whenUserIdAlreadyExists() {
			// given
			var initialRequest = new UserV1Dto.SignUpRequest(
				"existing", "existing@example.com", "MALE", "1996-05-04"
			);
			testRestTemplate.postForEntity(ENDPOINT, initialRequest, ApiResponse.class);

			var duplicateRequest = new UserV1Dto.SignUpRequest(
				"existing", "existing@example.com", "MALE", "1996-05-04"
			);
			HttpEntity<UserV1Dto.SignUpRequest> requestEntity = new HttpEntity<>(duplicateRequest);
			// when
			var response =
				testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
				});

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		}
	}
}
