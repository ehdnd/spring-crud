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

  // Memo
  MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMO_001", "메모를 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
