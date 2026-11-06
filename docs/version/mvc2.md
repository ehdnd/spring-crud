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
            - [ ] 메모가 memberId 를 가지자
            - [ ] findByLoginId 추가
    - [ ] Controller Test
    - [ ] 인터셉터 인증 확인
    - [ ] 세션도 있어야하고 . .
    - [ ] 아이디 중복 체크 -> MemberService 추가

- [ ] 예외 처리
    - [ ] 에러 응답 DTO

- [ ] 타입 컨버터 JSON ?

- [ ] 파일 업로드

### 2-2. non-goal

- DB/ JPA
- 배포 인증 인가 -> 스프링 시큐리티
- dto 중 password 해싱은 추후 spring-security-crypto 의 BCryptPasswordEncoder 사용하기

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

## +)

### 엔드포인트마다 던질 질문 5개

#### 1.

질문: 정상 요청을 보내면 원하는 응답이 오나?
확인 방법: `status().isOk()` + `jsonPath(...)`

#### 2.

질문: 요청 값이 repository까지 제대로 전달되나?
확인 방법: `then(repo).should().xxx(eq(..), argThat(..))` 또는 `ArgumentCaptor` + `usingRecursiveComparison()`

#### 3.

질문: (body가 있으면) 규칙을 어기면 400이고, repository는 호출되지 않나?
확인 방법: `isBadRequest()` + `should(never())`

#### 4.

질문: (body가 있으면) 경계값 (딱 최대 길이)은 통과하나?
확인 방법: `isOk()`

#### 5.

질문: ({id}가 있으면) 숫자가 아닌 id면 400인가?
확인 방법: `/memos/abc` → `isBadRequest()`

### 지금 상태 체크리스트 (MemoControllerTest, 26-11-05 기준)

#### POST /memos

- [x] ① 정상 → 200 + JSON (`add`)
- [x] ② repository에 넘긴 title/content 확인 (`add`의 `ArgumentCaptor`)
- [x] ③ 잘못된 입력 → 400 (`add_invalid`)
- [x] ④ 최대 길이 통과 (`add_maxLength`)
- ⑤ 해당 없음 ({id} 없음)

#### PATCH /memos/{id}

- [x] ① 정상 → 200 + JSON (`edit`)
- [x] ② id, title, content 전달 확인 (`edit`의 `ArgumentCaptor`)
- [x] ③ 잘못된 입력 → 400 (`edit_invalid`)
- [x] ④ 최대 길이 통과 (`edit_maxLength`)
- [ ] ⑤ `PATCH /memos/abc` → 400

#### GET /memos

- [ ] ① 정상 → 200 + 목록 JSON (`$.length()`, `$[0].title` 등)
- ②~⑤ 해당 없음 (입력이 없음)

#### GET /memos/{id}

- [ ] ① 정상 → 200 + JSON
- [ ] ② `findById`가 그 id로 호출되는지 확인
- ③④ 해당 없음 (body 없음)
- [ ] ⑤ `GET /memos/abc` → 400

#### 보류: 설계를 먼저 정할 것

- [ ] 없는 id로 조회/수정하면 어떻게 할지 (지금 Memo 쪽은 처리 코드가 없음). 404로 정하고 예외 처리를 만든 뒤에 테스트.

#### 컨트롤러 테스트에서 하지 않는 것

- 실제로 저장/수정됐는지 → `MemoRepositoryTest`에서
- 1L, 2L, 3L처럼 같은 코드를 지나가는 값을 여러 개 넣어보는 것

### 부분 적용 / Member 와 Memo 가 서로 다른 것

Member 쪽은 이미 다음 방식으로 바뀌었는데, Memo 쪽은 아직 예전 방식이다. 둘 중 하나로 맞추기.

| 항목 | Member | Memo |
|------|--------|------|
| Bean Validation 경계값 검증 위치 | DTO 테스트 (`MemberSaveRequestTest`, `MemberPasswordUpdateRequestTest`) | 컨트롤러 테스트 (`add_invalid`, `edit_invalid` 각 4케이스 + `*_maxLength`) |
| 컨트롤러의 400 테스트 | 대표 1케이스 (`add_invalid`, `editPassword_invalid`) | 파라미터 테스트로 전부 |
| 없는 id → 404 | 있음 (`member_notFound`, `editPassword_notFound`) | 없음 (보류 항목) |
| GET 테스트 | 있음 (`members`, `member`) | 없음 |

- [ ] Memo도 Member 방식으로 맞출지 결정
    - 맞춘다면: `MemoSaveRequestTest`, `MemoUpdateRequestTest` 추가 → 컨트롤러의 `*_invalid`는 대표 1케이스로 줄이고 `*_maxLength`는 DTO 테스트로 이동
    - 안 맞춘다면: 왜 다르게 두는지 여기에 한 줄 남기기

### 그 밖에 확인할 것

- [ ] `MemoControllerTest`의 `invalidRequests()` 주석 갱신: "(설명, title, content) 순서로 파라미터에 들어간다" → 지금은 `argumentSet` 이라 이름은 `{argumentSetName}`, 파라미터는 `(title, content)`
- [ ] 4. API 표의 POST/PATCH 설명이 `form: title, content` 로 되어 있음 → 실제로는 JSON body (`@RequestBody`)

#### 추천 순서

1. `GET /memos/{id}` ①②
2. `GET /memos` ①
3. ⑤ 두 개 (`GET`, `PATCH` 의 `/memos/abc`)
4. Member 방식으로 맞출지 결정 → 맞춘다면 Memo DTO 테스트 추가
5. 404 예외 처리 설계 후 `memo_notFound`, `edit_notFound`
