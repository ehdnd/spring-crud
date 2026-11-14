# 에러 규칙

## 응답 본문

ProblemDetail (RFC 9457). Content-Type 은 `application/problem+json`

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "메모를 찾을 수 없습니다.",
  "instance": "/memos/1",
  "code": "MEMO_NOT_FOUND"
}
```

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "입력값이 올바르지 않습니다.",
  "instance": "/memos",
  "code": "INVALID_INPUT",
  "errors": [
    {
      "field": "title",
      "reason": "공백일 수 없습니다"
    }
  ]
}
```

표준 필드

- `type`, `title`, `instance`: 스프링 기본값. 직접 설정 X
- `status`: HTTP 상태와 동일
- `detail`: 사람이 읽는 용도. 변경 가능.

확장 필드

- `code`: 클라이언트 분기용. 변경 금지. 모든 에러 응답에 존재
    - 비즈니스 실패: `ErrorCode` 이름
    - 검증 실패: `INVALID_INPUT`
    - 스프링이 처리한 예외: HTTP 상태 이름 (`METHOD_NOT_ALLOWED`)
- `errors`: 검증 실패 때만 존재

## 코드 이름

1. 형식: `{도메인}_{사유}`, 대문자 스네이크. enum 상수 이름이 code
2. 도메인: 리소스 단수 명사 (`MEMO`, `MEMBER`, `LOGIN`). 공통 코드는 접두사 없음
3. 사유: 실패한 API (X) 잘못된 상태 작성
   `JOIN_FAILED` (X) → `MEMBER_LOGIN_ID_DUPLICATED` (O)
4. 사유 어휘 고정
    - 없음: `_NOT_FOUND`
    - 중복: `_DUPLICATED`
    - 불일치: `_MISMATCH`
    - 등등. .
5. 필드명은 API 필드명 그대로 작성
