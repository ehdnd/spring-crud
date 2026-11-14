# spring-mvc-2

작성: 26-10-28
업데이트: 26-11-14
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
        - [X] MemberController Test
        - [X] 도메인 / 웹
            - [X] dto 생성
                - [X] MemberResponse (id, loginId)
                - [X] MemberPasswordUpdateRequest
            - [X] findByLoginId 추가
    - [X] Controller Test
    - [X] 인터셉터 인증 확인
        - [X] 로그인하지 않으면 401로 막자 (`LoginCheckInterceptor`)
        - [X] `@Login` ArgumentResolver 도입 (`@Login Long memberId`)
        - [X] 현재 엔드포인트 별로 로그인 여부 결정 → [4. API](#4-api)
        - [X] `PATCH /members/{id}/password` → `PATCH /members/me/password`
    - [X] 세션
    - [X] (CURRENT) 메모가 memberId 를 가지자
        - [X] MemoResponse DTO 생성
        - [X] 작성자는 Login 에서 꺼내쓰는 값이다.
        - [X] Setter 막아둘 곳 막아두기
        - [X] 본인 메모만 조회/수정 (인가) - 남의 메모면 403? 404? -> 404 존재 사실을 숨기자
        - [X] 테스트 고치기
            - [X] 지금 수정한 건 '필드 하나 추가' + '누가 수정하는가 Memo 스스로 / 리포지토리가' 두 가지의 변경이 하나로 섞인 것이다.
            - [X] edit 없는 아이디 / edit 남의 메모 검증 등
        - [X] 내 메모만 보기? 공유하기? 일단 내 메모만 보고 추후 공개 메모 도입.
        - [X] 로그인 적용하기
        - [X] 메모의 멤버 아이디 노출 여부 결정. 안보이고 추후에 멤버 닉네임 도입 시 적용.
    - [X] 아이디 중복 체크 -> MemberService 추가
    - [ ] 메모 삭제 delete 추가 - 일단 뒤로 미룸
    - [ ] MemoService 추가
        - [ ] memo.isOwnedBy 추가
        - [ ] 찾기, 주인확인, 수정, 저장 옮기기
        - [ ] memo.isOwnedBy - 서비스는 판단을 memo에게 물어보면 된다.
        - [ ] Test 수정
        - [ ] 에러는 responseStatusException 일단 들고 가자

- [ ] 예외 처리
    - [ ] Memo에 적용
    - [ ] Member에 적용
    - [ ] 에러 응답 DTO
    - [ ] MemberController 에서 비밀번호 에러 처리 구분 - code 필드로 바꾸자
    - [ ] getById
    - [ ] 401 응답 형식 통일 (인터셉터는 빈 본문, 컨트롤러 예외는 에러 JSON)

- [ ] 타입 컨버터 JSON ?

- [ ] 파일 업로드

### 2-2. non-goal

- DB/ JPA
- 배포 인증 인가 -> 스프링 시큐리티
- dto 중 password 해싱은 추후 spring-security-crypto 의 BCryptPasswordEncoder 사용하기
- spring rest docs 도 있다는데 일단 구현에 집중하자

## 3. 구조

## 4. API

### 엔드포인트별 로그인 필요 여부

요청한 사람이 누군지 알아야 동작하는가? (인증)

+) 본인 것만 허용 = 인가 -> 인터셉터가 아니라 컨트롤러/서비스에서 따로 처리

| 메서드 | 경로                   | 로그인   | 이유                                                                    |
|--------|------------------------|----------|-------------------------------------------------------------------------|
| POST   | `/members`             | X        | 회원가입. 아직 누군지 모르는 상태에서 호출                              |
| GET    | `/members`             | X (임시) | `POST /members`와 경로가 같아서 함께 열림. 전체 회원 목록 공개는 재검토 |
| GET    | `/members/{id}`        | X        | 공개 프로필. 응답에 loginId가 노출되는 건 재검토                        |
| PATCH  | `/members/me/password` | O        | 대상 회원을 세션에서 꺼냄. path id를 안 받으니 남의 비밀번호 변경 불가  |
| POST   | `/login`               | X        | 로그인                                                                  |
| POST   | `/logout`              | X        | 세션이 없어도 해가 없음. 있으면 무효화, 없으면 그냥 204                 |
| GET    | `/me`                  | O        | 누군지 모르면 응답을 만들 수 없음                                       |
| *      | `/memos/**`            | O        | 작성자(memberId)가 필요                                                 |
| *      | `/error`               | X        | 에러 응답까지 인터셉터에 걸리지 않도록                                  |

#### 구현 메모 (일부)

- `excludePathPatterns` HTTP 메서드 구분 불가 → URL 설계(`/me/...`)로 회피
- 401(Unauthorized 인증 실패) 2군데
    - 로그인 X: 인터셉터 (`setStatus(401)` + `return false`, 본문 없음)
    - 세션 O 회원 X (stale session): 컨트롤러 (`ResponseStatusException`)
- `@Login Long memberId`
    - 로그인 안 했으면 null (로그인은 선택)
    - 인터셉터 걸린 경로는 null 아님 보장 → 컨트롤러 null 체크 X

## +)

### 지금 상태 체크리스트

TBD.

#### 컨트롤러 테스트에서 하지 않는 것

- 실제로 저장/수정됐는지 → `MemoRepositoryTest`에서
- 1L, 2L, 3L처럼 같은 코드를 지나가는 값을 여러 개 넣어보는 것

### 부분 적용 / Member 와 Memo 가 서로 다른 것

| 항목                             | Member                                                                  | Memo                                                                       |
|----------------------------------|-------------------------------------------------------------------------|----------------------------------------------------------------------------|
| Bean Validation 경계값 검증 위치 | DTO 테스트 (`MemberSaveRequestTest`, `MemberPasswordUpdateRequestTest`) | 컨트롤러 테스트 (`add_invalid`, `edit_invalid` 각 4케이스 + `*_maxLength`) |
| 컨트롤러의 400 테스트            | 대표 1케이스 (`add_invalid`, `editPassword_invalid`)                    | 파라미터 테스트로 전부                                                     |
| 없는 id → 404                    | 있음 (`member_notFound`)                                                | 없음 (보류 항목)                                                           |
| GET 테스트                       | 있음 (`members`, `member`)                                              | 없음                                                                       |

- [ ] Memo도 Member 방식으로 맞출지 결정
