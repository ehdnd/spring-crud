# spring-mvc-1 snapshot

작성: 26-10-28
커밋: d52ec94
진도: MVC-1 수강 이후 api 서버로 구현

## 1. 요약

- CRU 구현. 
- 검증, 예외 처리, 상태 코드 처리 없음

## 2. goal / non-goal

### 2-1. goal

MVC 1 편 내용을 메모장 API에 적용

### 2-2. non-goal

DB/ JPA
배포 인증 인가

## 3. 구조

```plantuml
io.github.ehdnd.crud
├── CrudApplication
├── web
│   └── MemoController        @RestController, /memos
└── domain.memo
    ├── Memo                  도메인 객체 (요청/응답에도 그대로 사용)
    └── MemoRepository        @Repository, HashMap 기반 메모리 저장소
```

컨트롤러 -> 리포지토리

## 4. API


