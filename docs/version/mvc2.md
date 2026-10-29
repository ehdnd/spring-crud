# spring-mvc-1 snapshot

작성: 26-10-28
업데이트: 26-10-29
커밋:
진도: MVC-2 수강 이후 api 서버로 구현

## 1. 요약

## 2. goal / non-goal

### 2-1. goal

MVC2 편 내용을 적용

- [ ] 국제화?

- [X] Bean Validation
    - [X] 요청 DTO에 Validation 적용
    - [X] 검증 실패 응답 통일

- [ ] 로그인 - 쿠키, 세션
    - [ ] 세션 로그인

- [ ] 로그인 - 필터, 인터셉터
    - [ ] 인터셉터 인증 확인

- [ ] 예외 처리
    - [ ] 에러 응답 DTO

- [ ] 타입 컨버터 JSON ?

- [ ] 파일 업로드

### 2-2. non-goal

DB/ JPA
배포 인증 인가 -> 스프링 시큐리티

## 3. 구조

컨트롤러 -> 리포지토리

## 4. API

| Method | URI           | 설명                            | 정상 응답          |
|--------|---------------|---------------------------------|--------------------|
| GET    | `/memos`      | 전체 목록                       | 200, `Memo[]`      |
| GET    | `/memos/{id}` | 단건 조회                       | 200, `Memo`        |
| POST   | `/memos`      | 생성 (form: `title`, `content`) | 200, 생성된 `Memo` |
| PATCH  | `/memos/{id}` | 수정 (form: `title`, `content`) | 200, 수정된 `Memo` |
| DELETE | `/memos/{id}` | —                               | **미구현 (405)**   |

