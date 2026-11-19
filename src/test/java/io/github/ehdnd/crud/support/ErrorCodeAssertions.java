package io.github.ehdnd.crud.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ehdnd.crud.global.error.BusinessException;
import io.github.ehdnd.crud.global.error.ErrorCode;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;

public final class ErrorCodeAssertions {

  private ErrorCodeAssertions() {
  }

  // AssertJ 커스텀 Assert 도 있는데 좀 과하더라.

  public static void assertErrorCode(ThrowingCallable action,
      ErrorCode errorCode) {
    assertThatThrownBy(action).isInstanceOfSatisfying(BusinessException.class,
        e -> assertThat(e.getErrorCode()).isEqualTo(errorCode));
  }
}
