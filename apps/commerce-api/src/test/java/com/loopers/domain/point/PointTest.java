package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

@DisplayName("Point 값 객체 테스트")
class PointTest {

	@DisplayName("포인트를 생성할 때, ")
	@Nested
	class CreatePoint {
		@DisplayName("0 이상의 값을 입력하면, 정상적으로 생성된다.")
		void createSuccessfully_whenAmountIsNonNegative() {
			// when
			Point point = Point.of(1000);
			Point zeroPoint = Point.of(0);

			// then
			assertThat(point.getAmount()).isEqualTo(point.getAmount());
			assertThat(point.getAmount()).isEqualTo(zeroPoint.getAmount());
		}

		@DisplayName("음수를 입력하면, BAD_REQUEST 예외가 발생한다.")
		@Test
		void throwsException_whenAmountIsNegative() {
			// when & then
			assertThatThrownBy(() -> Point.of(-1000))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
				.hasMessage("포인트는 0보다 작을 수 없습니다.");
		}
	}

	@DisplayName("포인트를 연산할 때, ")
	@Nested
	class Operation {

		@DisplayName("plus 메서드는 포인트를 더한 후 새로운 Point 객체를 반환한다.")
		@Test
		void plus_returnsNewPointWithAmount() {
			// given
			Point p1 = Point.of(1000);
			Point p2 = Point.of(500);

			// when
			Point result = p1.plus(p2);

			// then
			assertThat(result.getAmount()).isEqualTo(1500);
		}

		@DisplayName("minus 메서드는 포인트를 정확하게 뺀 새로운 Point 객체를 반환한다.")
		@Test
		void minus_returnsNewPointWithAmount() {
			// given
			Point p1 = Point.of(1000);
			Point p2 = Point.of(500);

			// when
			Point result = p1.minus(p2);

			// then
			assertThat(result.getAmount()).isEqualTo(500);
		}

		@DisplayName("minus 연산 결과가 음수이면(포인트 부족), BAD_REQUEST 예외가 발생한다.")
		@Test
		void minus_throwsException_whenResultIsNegative() {
			// given
			Point p1 = Point.of(500);
			Point p2 = Point.of(1000);

			// when & then
			assertThatThrownBy(() -> p1.minus(p2))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
				.hasMessage("보유 포인트가 부족합니다.");
		}
	}
}
