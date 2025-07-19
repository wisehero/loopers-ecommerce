package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.infrastructure.point.PointWalletJpaRepository;
import com.loopers.support.IntegrationTestSupport;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

class PointWalletServiceIntegrationTest extends IntegrationTestSupport {

	@Autowired
	private PointWalletService pointWalletService;

	@Autowired
	private UserRepository userJpaRepository;

	@Autowired
	private PointWalletJpaRepository pointWalletJpaRepository;

	@MockitoSpyBean
	private PointWalletRepository pointWalletRepository;

	@DisplayName("사용자 ID로 포인트 조회 시, ")
	@Nested
	class GetWalletByLoginId {

		@DisplayName("지갑이 이미 존재하면, 새로 생성하지 않고 기존 지갑을 반환한다.")
		@Test
		void returnsExistingWallet_whenWalletExists() {
			// given
			User user = userJpaRepository.save(User.create(new UserCommand.Create(
				"existing", "exist@example.com", "MALE", "2000-01-01"
			)));
			PointWallet existingWallet = pointWalletJpaRepository.save(PointWallet.create(user.getLoginId()));

			// when
			PointWallet foundWallet = pointWalletService.getWalletByLoginId(existingWallet.getLoginId());

			// then
			assertAll(
				() -> assertThat(foundWallet.getId()).isEqualTo(existingWallet.getId()),
				() -> verify(pointWalletRepository, times(1)).findByLoginId(existingWallet.getLoginId()),
				() -> verify(pointWalletRepository, never()).save(any(PointWallet.class))
			);
		}

		@DisplayName("지갑이 존재하지 않으면, 새로 생성하여 저장하고 반환한다.")
		@Test
		void createsAndReturnsNewWallet_whenWalletDoesNotExists() {
			// given
			User user = userJpaRepository.save(User.create(new UserCommand.Create(
				"existing", "exist@example.com", "MALE", "2000-01-01"
			)));

			// when
			PointWallet newWallet = pointWalletService.getWalletByLoginId(user.getLoginId());

			// then
			assertAll(
				() -> assertThat(newWallet).isNotNull(),
				() -> assertThat(newWallet.getBalance().value()).isEqualTo(0L),
				// 행위 검증: orElseGet 블록 내부의 save가 호출되었는지 확인
				() -> verify(pointWalletRepository, times(1)).findByLoginId(user.getLoginId()),
				() -> verify(pointWalletRepository, times(1)).save(any(PointWallet.class))
			);
		}
	}

	@DisplayName("포인트 충전 시,")
	@Nested
	class Deposit {

		@DisplayName("신규 사용자면, 새 지갑을 생성하고 충전과 거래 내역 저장을 수행한다.")
		@Test
		void createsWalletAndDeposit_forNewUser() {
			// given
			User user = User.create(new UserCommand.Create(
				"testuser", "test@example.com", "MALE", "2000-01-01"
			));
			User savedUser = userJpaRepository.save(user);
			Point amountToDeposit = Point.of(1000L);

			// when
			pointWalletService.deposit(savedUser.getLoginId(), amountToDeposit);

			// then
			verify(pointWalletRepository, times(1)).findByLoginId(savedUser.getLoginId());
			verify(pointWalletRepository, times(1)).save(any(PointWallet.class));
			verify(pointWalletRepository, times(1)).saveWithHistory(any(PointWallet.class),
				any(PointTransactionHistory.class));

			PointWallet foundWallet = pointWalletRepository.findByLoginId(savedUser.getLoginId()).get();
			assertThat(foundWallet.getBalance().value()).isEqualTo(1000L);
		}
	}

	@DisplayName("기존 사용자이면, 지갑을 조회하고 충전과 거래내역 저장을 수행한다.")
	@Test
	void depositsPoints_forExistingUser() {
		// given
		User user = User.create(new UserCommand.Create(
			"testuser", "test@example.com", "MALE", "2000-01-01"
		));
		User savedUser = userJpaRepository.save(user);

		PointWallet pointWallet = PointWallet.create(savedUser.getLoginId());
		pointWallet.deposit(Point.of(500L));
		pointWalletJpaRepository.save(pointWallet);

		Point additionalAmount = Point.of(1000L);

		// when
		PointWallet updatedWallet = pointWalletService.deposit(savedUser.getLoginId(), additionalAmount);

		// then
		verify(pointWalletRepository, never()).save(any(PointWallet.class));
		verify(pointWalletRepository, times(1)).saveWithHistory(any(PointWallet.class),
			any(PointTransactionHistory.class));
		assertThat(updatedWallet.getBalance().value()).isEqualTo(1500L);
	}

	@DisplayName("포인트 사용 시, ")
	@Nested
	class Use {

		@DisplayName("잔액이 충분하면, 사용한 만큼 잔액을 차감하고, 거래내역을 저장한다.")
		@Test
		void usesPoints_whenBalanceIsSufficient() {
			// given
			User user = User.create(new UserCommand.Create(
				"testuser", "test@example.com", "MALE", "2000-01-01"
			));
			User savedUser = userJpaRepository.save(user);

			PointWallet pointWallet = PointWallet.create(savedUser.getLoginId());
			pointWallet.deposit(Point.of(1000L));
			pointWalletJpaRepository.save(pointWallet);

			// when
			PointWallet updatedWallet = pointWalletService.use(savedUser.getLoginId(), Point.of(300L));

			// then
			verify(pointWalletRepository, times(1)).saveWithHistory(any(PointWallet.class),
				any(PointTransactionHistory.class));
			assertThat(updatedWallet.getBalance().value()).isEqualTo(700L);
		}
	}

	@DisplayName("잔액이 부족하면, 예외가 발생하고 데이터는 변경되지 않는다.")
	@Test
	void throwsException_whenBalanceIsNotSufficient() {
		// given
		User user = User.create(new UserCommand.Create(
			"testuser", "test@example.com", "MALE", "2000-01-01"
		));
		User savedUser = userJpaRepository.save(user);

		PointWallet pointWallet = PointWallet.create(savedUser.getLoginId());
		pointWallet.deposit(Point.of(1000L));
		pointWalletJpaRepository.save(pointWallet);

		// when & then
		assertThatThrownBy(() -> pointWalletService.use(savedUser.getLoginId(), Point.of(1100L)))
			.isInstanceOf(CoreException.class)
			.hasMessage("보유 포인트가 부족합니다.");

		verify(pointWalletRepository, never()).saveWithHistory(any(PointWallet.class),
			any(PointTransactionHistory.class));
	}

	@DisplayName("사용하려는 지갑의 소유자가 존재하지 않을 경우, 예외가 발생한다.")
	@Test
	void throwsException_whenUserDoesNotOwnWallet() {
		// given
		LoginId notExisting = LoginId.of("noWallet");
		Point amountToUse = Point.of(100L);

		// whne & then
		assertThatThrownBy(() -> pointWalletService.use(notExisting, amountToUse))
			.isInstanceOf(CoreException.class)
			.hasFieldOrPropertyWithValue("errorType", ErrorType.CONFLICT)
			.hasMessage("지갑을 소유한 사용자가 존재하지 않습니다.");
	}
}
