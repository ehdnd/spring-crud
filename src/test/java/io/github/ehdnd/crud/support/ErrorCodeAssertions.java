package io.github.ehdnd.crud.support;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ehdnd.crud.global.error.BusinessException;
import io.github.ehdnd.crud.global.error.ErrorCode;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;

public final class ErrorCodeAssertions {

  private ErrorCodeAssertions() {
  }

  public static void assertErrorCode(ThrowingCallable action,
      ErrorCode errorCode) {
    assertThatThrownBy(action).isInstanceOf(BusinessException.class).extracting("errorCode")
        .isEqualTo(errorCode);
  }
}
