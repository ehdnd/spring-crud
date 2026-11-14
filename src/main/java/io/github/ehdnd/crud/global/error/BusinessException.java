package io.github.ehdnd.crud.global.error;

import lombok.Getter;

/**
 * 서버 고장 X 비즈니스 규칙상 실패. 실패 종류는 ErrorCode
 */
@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
