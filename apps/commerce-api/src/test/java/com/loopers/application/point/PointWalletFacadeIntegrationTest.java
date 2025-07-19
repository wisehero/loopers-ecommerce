package com.loopers.application.point;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointWallet;
import com.loopers.domain.point.PointWalletCommand;
import com.loopers.domain.point.PointWalletResult;
import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.infrastructure.point.PointWalletJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.IntegrationTestSupport;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

class PointWalletFacadeIntegrationTest extends IntegrationTestSupport {

	@Autowired
	private PointWalletFacade pointWalletFacade;

	@Autowired
	private UserJpaRepository userJpaRepository;
	@Autowired
	private PointWalletJpaRepository pointWalletJpaRepository;

	@DisplayName("사용자 ID로 포인트 조회 시, ")
	@Nested
	class GetBalance {

		@DisplayName("지갑이 존재하면, 해당 지갑의 포인트를 조회할 수 있다.")
		@Test
		void getBalanceSuccessfully_whenWalletExists() {
			// given
			User user = User.create(new UserCommand.Create(
				"testuser", "test@example.com", "MALE", "2000-01-01"
			));
			User savedUser = userJpaRepository.save(user);

			PointWallet pointWallet = PointWallet.create(savedUser.getLoginId());
			pointWallet.deposit(Point.of(500L));
			pointWalletJpaRepository.save(pointWallet);

			// when
			PointWalletResult.BalanceInfo result = pointWalletFacade.getBalance(savedUser.getLoginId().value());

			// then
			assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.loginId()).isEqualTo(savedUser.getLoginId().value()),
				() -> assertThat(result.balance()).isEqualTo(500L)
			);
		}

		@DisplayName("사용자가 존재하지 않으면, 사용자를 찾지 못했다는 예외가 발생한다.")
		@Test
		void throwsNotFoundException_whenUserDoesNotExist() {
			// given
			LoginId nonExistentLoginId = LoginId.of("nouser");

			// when & then
			assertThatThrownBy(() -> pointWalletFacade.getBalance(nonExistentLoginId.value()))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.NOT_FOUND)
				.hasMessage("지갑을 소유한 사용자가 존재하지 않습니다.");
		}
	}

	@DisplayName("포인트를 충전 시, ")
	@Nested
	class Deposit {

		@DisplayName("지갑을 소유한 사용자는 정상적으로 포인트가 충전된다.")
		@Test
		void depositPointsSuccessfully_whenUserExists() {
			// given
			User user = User.create(new UserCommand.Create(
				"testuser", "test@example.com", "MALE", "2000-01-01"
			));
			User savedUser = userJpaRepository.save(user);
			PointWalletCommand.Deposit command = new PointWalletCommand.Deposit(savedUser.getLoginId(),
				Point.of(1000L));

			// when
			PointWalletResult.BalanceInfo result = pointWalletFacade.deposit(command);

			// then
			assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.loginId()).isEqualTo(savedUser.getLoginId().value()),
				() -> assertThat(result.balance()).isEqualTo(1000L)
			);
		}

		@DisplayName("사용자가 존재하지 않을 경우 예외가 발생한다.")
		@Test
		void throwsNotFoundException_whenUserExists() {
			// given
			LoginId nonExistentLoginId = LoginId.of("nouser");
			PointWalletCommand.Deposit command = new PointWalletCommand.Deposit(
				nonExistentLoginId,
				Point.of(1000L)
			);

			// when & then
			assertThatThrownBy(() -> pointWalletFacade.deposit(command))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.NOT_FOUND)
				.hasMessage("지갑을 소유한 사용자가 존재하지 않습니다.");
		}
	}
}
