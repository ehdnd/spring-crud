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
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

  // Memo
  MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "메모를 찾을 수 없습니다."),

  // 인증
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

  // Member
  DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
  PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");

  private final HttpStatus status;
  private final String message;
}
