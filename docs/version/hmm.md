# 현 상황에 대해 (Snapshot)

| 항목 | 내용 |
| --- | --- |
| 기준 시점 | 2026-10-28 |
| 기준 커밋 | `d52ec94` (main) |
| 진도 | MVC 1편 적용 완료 · MVC 2편 수강 완료, **적용 전** |
| 목적 | MVC 2편 적용에 앞서 "지금 어디까지 되어 있는가"를 고정해 두고, 나중에 비교·복기할 기준점으로 쓴다 |

## 1. 요약

- 메모 CRUD 중 **C / R / U** 만 있는 REST API 서버. **D(삭제) 없음.**
- 저장소는 메모리(`HashMap`). 서버를 재시작하면 데이터가 초기화된다.
- 검증 · 예외 처리 · 상태 코드 처리가 아직 없다. 잘못된 요청도 대부분 200으로 응답한다.
- Thymeleaf HTML 서빙 → REST API 로 전환을 마쳤다 (`0a6c4e8`, `5d93367`).

## 2. 목표 / 비목표

**목표**
- 김영한 Spring MVC 1·2편에서 배운 내용을 작은 메모장 API 에 직접 적용하며 복습한다.
- 배운 기능을 "왜 쓰는지" 체감할 수 있도록, 먼저 문제가 드러난 상태를 만든 뒤 고친다.

**비목표** (하려면 할 수 있지만, 지금은 일부러 하지 않는 것)
- DB / JPA 연동 — DB 관련 강의 범위
- 화면(HTML) — API 서버로 전환했으므로 Thymeleaf 관련 내용은 적용하지 않는다
- 배포, 인증 / 인가 고도화(Spring Security)

## 3. 기술 스택 & 실행

| 구분 | 버전 / 내용 |
| --- | --- |
| Java | 21 (Gradle toolchain) |
| Spring Boot | 4.1.1 (`spring-boot-starter-webmvc`) |
| 기타 | Lombok, DevTools, Thymeleaf(현재 미사용) |
| 개발 환경 | `.devcontainer` (Java 21 이미지) |

```bash
sh ./gradlew bootRun   # 서버 실행 (localhost:8080)
sh ./gradlew test      # 테스트
```

- 시작 시 `MemoController#init()` (`@PostConstruct`)이 샘플 메모 2개(A, B)를 넣는다.
- 요청 예시는 `../../http/MemoController.http` 에 있다.
- `gradlew` 에 실행 권한이 없어 `./gradlew` 는 permission denied 가 뜬다. `sh ./gradlew` 로 실행하거나 `chmod +x gradlew`.

## 4. 구조

```
io.github.ehdnd.crud
├── CrudApplication
├── web
│   └── MemoController        @RestController, /memos
└── domain.memo
    ├── Memo                  도메인 객체 (요청/응답에도 그대로 사용)
    └── MemoRepository        @Repository, HashMap 기반 메모리 저장소
```

- 계층: `Controller → Repository` (Service 계층 없음)
- 요청 바인딩: 전부 `@ModelAttribute` (쿼리 파라미터 / `x-www-form-urlencoded`)
- 응답: `@RestController` 라서 `Memo` 객체가 JSON 으로 직렬화된다 (HTTP 메시지 컨버터)

## 5. API 현황

| Method | URI | 설명 | 정상 응답 |
| --- | --- | --- | --- |
| GET | `/memos` | 전체 목록 | 200, `Memo[]` |
| GET | `/memos/{id}` | 단건 조회 | 200, `Memo` |
| POST | `/memos` | 생성 (form: `title`, `content`) | 200, 생성된 `Memo` |
| PATCH | `/memos/{id}` | 수정 (form: `title`, `content`) | 200, 수정된 `Memo` |
| DELETE | `/memos/{id}` | — | **미구현 (405)** |

응답 예시:

```json
{"id":1,"title":"A","content":"AAA","createdAt":"2026-10-28T21:13:35.586281","updatedAt":"2026-10-28T21:13:35.586281"}
```

### 비정상 요청 시 실제 동작 (2026-10-28 curl 로 직접 확인)

| 요청 | 실제 결과 | 기대하는 결과 |
| --- | --- | --- |
| `GET /memos/999` (없는 id) | **200, 빈 body** | 404 |
| `PATCH /memos/999` (없는 id) | **500**, `NullPointerException` (`MemoRepository.update`) | 404 |
| `POST /memos` (파라미터 없음) | **200**, `title`/`content` 가 `null` 인 메모 생성 | 400 |
| `POST /memos` JSON body | **200**, 값이 바인딩되지 않아 `null` 메모 생성 | JSON 을 받으려면 `@RequestBody` 필요 |
| `PATCH /memos/1` 에 `title` 만 전송 | `content` 가 **`null` 로 덮어써짐** | PATCH 라면 보낸 필드만 수정 |
| `DELETE /memos/1` | 405 Method Not Allowed | 삭제 |

- 500 응답 body 에 **스택 트레이스 전체가 노출**된다. DevTools 가 개발 중 `server.error.include-stacktrace` 등을 켜기 때문이다. 운영에서는 나오면 안 되는 정보다.

## 6. 도메인 & 저장소

**Memo**: `id`, `title`, `content`, `createdAt`, `updatedAt`
- `@Getter @Setter`, 기본 생성자 + `(title, content)` 생성자
- `equals/hashCode` 없음

**MemoRepository**
- `save` : id 발급(`++sequence`) + `createdAt`/`updatedAt` 설정 후 저장. 넘겨받은 객체를 그대로 수정해서 저장한다.
- `findById` : 없으면 `null` 반환
- `findAll` : `new ArrayList<>(store.values())` (방어적 복사)
- `update` : `title`, `content` 를 무조건 덮어쓰고 `updatedAt` 갱신. 없는 id 면 NPE.
- `clear` : 테스트용
- `HashMap` + `long sequence` 라서 **동시 요청에 안전하지 않다** (강의에서는 실무라면 `ConcurrentHashMap`, `AtomicLong` 을 쓰라고 함).

## 7. 테스트 현황

| 테스트 | 내용 | 상태 |
| --- | --- | --- |
| `CrudApplicationTests#contextLoads` | 스프링 컨텍스트 로딩 | 통과 |
| `MemoRepositoryTest#save` | 저장 후 조회 | 통과 |

- `findAll`, `update` 테스트는 없고 컨트롤러(API) 테스트도 없다.
- `save` 테스트는 같은 인스턴스를 비교하기 때문에 실질적인 필드 검증을 하지 못한다 → `../확장방안.md` 6번 참고.

## 8. 지금까지 내린 결정

| 결정 | 이유 | 대가 / 남은 숙제 | 커밋 |
| --- | --- | --- | --- |
| HTML 서빙 → REST API 서버로 전환 | 실무 형태에 가깝게, 화면 없이 HTTP 자체에 집중 | 폼용 엔드포인트(`/add`, `/{id}/edit`) 제거. 요청은 여전히 form 방식 | `d029c6c`, `5d93367` |
| URI 를 리소스 중심으로 (`/memos`, `/memos/{id}`) | HTTP 메서드로 행위 구분 | 수정에 PATCH 를 골랐지만 동작은 전체 덮어쓰기(PUT 에 가까움) | `0321050`, `../uri.md` |
| Repository 의 `static` 필드 제거 | 스프링 빈은 싱글톤이라 불필요하고, 테스트마다 `new` 할 때 상태가 공유됨 | — | `083a598` |
| 시간 설정을 생성자에서 `save` 로 옮김 | 객체 생성 시점이 아니라 저장 시점이 생성 시각 | 시간 책임이 Repository 에 있음 | `6390a10` |
| 도메인 `Memo` 를 요청/응답에 그대로 사용 | 강의 Item 예제와 같은 구조, 지금은 단순함이 우선 | `id`, `createdAt` 도 클라이언트가 보낼 수 있음 → DTO 분리 후보 | — |
| Service 계층 생략 | 비즈니스 로직이 아직 없음 | 로직이 생기면 분리 | — |

## 9. 남아 있는 잔재

- `spring-boot-starter-thymeleaf` 의존성: API 전환 후 사용하지 않음
- `static/css/bootstrap.min.css`: HTML 시절 파일, 사용하지 않음
- `templates/` 는 이미 없음

## 10. 다음 단계: MVC 2편 적용 후보

5절에서 확인한 문제를 MVC 2편 주제와 연결해 둔다. 순서는 적용하면서 정한다.

| MVC 2편 주제 | 이 프로젝트에서 해결할 문제 | 관련 |
| --- | --- | --- |
| 검증 (Bean Validation, `@Validated`) | 빈 제목/내용으로 생성·수정됨 → 400 | 5절 |
| API 예외 처리 (`@ExceptionHandler`, `@RestControllerAdvice`) | 없는 id 에 200 빈 body / 500 NPE → 404, 일관된 에러 응답 | 5절 |
| 스프링 타입 컨버터 / 포맷터 | 날짜 응답 포맷 (`createdAt` 의 나노초 등) | 5절 응답 예시 |
| 쿠키 · 세션 · 로그인 | "내 메모" 개념 도입 (작성자) | — |
| 필터 / 인터셉터 | 로그인 체크, 요청 로그 | — |
| 파일 업로드 | 메모 첨부파일 (선택) | — |

MVC 2편과 별개로 남은 것:
- [ ] DELETE `/memos/{id}` 구현
- [ ] JSON 요청 받기 (`@RequestBody`) 로 바꿀지 결정
- [ ] PATCH 를 부분 수정으로 바꿀지, PUT 으로 바꿀지 결정
- [ ] 요청/응답 DTO 분리, Service 계층 — `../확장방안.md` 3·4번
- [ ] 미사용 Thymeleaf 의존성, bootstrap 정리

## 관련 문서

- `../uri.md` — URI 설계 메모
- `../확장방안.md` — 확장 방향 / 개선 아이디어
