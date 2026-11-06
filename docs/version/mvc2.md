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

- [ ] Memo


- [ ] 로그인
    - [ ] 멤버 만들기
        - [ ] Member + Repository + Test
            - [X] Member (id - 불변, loginId, password, 등등)
            - [ ] 닉네임, 소개도 받을 수 있다.
        - [X] loginId 로 검색수행? DB id로 검색 수행? - id
        - [ ] MemberController Test 작성
        - [ ] 도메인 / 웹
            - [ ] dto 생성
                - [X] MemberResponse (id, loginId)
                - [X] MemberPasswordUpdateRequest
            - [ ] findByLoginId 추가
    - [ ] Controller Test
    - [ ] 인터셉터 인증 확인
    - [ ] 세션
    - [ ] 메모가 memberId 를 가지자
        - [ ] MemoResponse DTO 생성
        - [ ] Setter 막아둘 곳 막아두기
        - [ ] 
    - [ ] 아이디 중복 체크 -> MemberService 추가

- [ ] 예외 처리
    - [ ] 에러 응답 DTO
    - [ ] 앞서 미뤄둔 service 계층 도입
    - [ ] 앞서 미뤄둔 테스트 수정
    - [ ] MemberController 에서 비밀번호 에러 처리 구분 - code 필드로 바꾸자
    - [ ] getById

- [ ] 타입 컨버터 JSON ?

- [ ] 파일 업로드

### 2-2. non-goal

- DB/ JPA
- 배포 인증 인가 -> 스프링 시큐리티
- dto 중 password 해싱은 추후 spring-security-crypto 의 BCryptPasswordEncoder 사용하기

## 3. 구조

컨트롤러 -> 리포지토리

## 4. API

TBD.

## +)

### 지금 상태 체크리스트

TBD.

#### 보류: 설계를 먼저 정할 것

- [ ] 없는 id로 조회/수정하면 어떻게 할지 (지금 Memo 쪽은 처리 코드가 없음). 404로 정하고 예외 처리를 만든 뒤에 테스트.

#### 컨트롤러 테스트에서 하지 않는 것

- 실제로 저장/수정됐는지 → `MemoRepositoryTest`에서
- 1L, 2L, 3L처럼 같은 코드를 지나가는 값을 여러 개 넣어보는 것

### 부분 적용 / Member 와 Memo 가 서로 다른 것

| 항목                             | Member                                                                  | Memo                                                                       |
|----------------------------------|-------------------------------------------------------------------------|----------------------------------------------------------------------------|
| Bean Validation 경계값 검증 위치 | DTO 테스트 (`MemberSaveRequestTest`, `MemberPasswordUpdateRequestTest`) | 컨트롤러 테스트 (`add_invalid`, `edit_invalid` 각 4케이스 + `*_maxLength`) |
| 컨트롤러의 400 테스트            | 대표 1케이스 (`add_invalid`, `editPassword_invalid`)                    | 파라미터 테스트로 전부                                                     |
| 없는 id → 404                    | 있음 (`member_notFound`, `editPassword_notFound`)                       | 없음 (보류 항목)                                                           |
| GET 테스트                       | 있음 (`members`, `member`)                                              | 없음                                                                       |

- [ ] Memo도 Member 방식으로 맞출지 결정
