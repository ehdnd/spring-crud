package io.github.ehdnd.crud.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 클라이언트에게 알려줄 실패 목록
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // 공통
  INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

  // Memo
  MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "메모를 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String message;
}
