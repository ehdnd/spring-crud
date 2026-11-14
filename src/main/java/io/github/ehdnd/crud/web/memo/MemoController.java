package io.github.ehdnd.crud.web.memo;

import io.github.ehdnd.crud.domain.memo.Memo;
import io.github.ehdnd.crud.domain.memo.MemoRepository;
import io.github.ehdnd.crud.web.login.Login;
import io.github.ehdnd.crud.web.memo.dto.MemoSaveRequest;
import io.github.ehdnd.crud.web.memo.dto.MemoUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/memos")
@RequiredArgsConstructor
public class MemoController {

  private final MemoRepository memoRepository;

  @GetMapping
  List<Memo> memos(@Login Long memberId) {
    // 인터셉터가 이미 로그인 사용자 확인함 -> memberId 가 있다.
    return memoRepository.findByMemberId(memberId);
  }

  @PostMapping
  Memo add(@Login Long memberId, @Validated @RequestBody MemoSaveRequest memoSaveRequest) {

    Memo memo = new Memo(memoSaveRequest.title(), memoSaveRequest.content(), memberId);
    return memoRepository.save(memo);
  }

  @GetMapping("/{memoId}")
  Memo memo(@Login Long memberId, @PathVariable Long memoId) {
    // 자신의 메모만 -> 남의 메모는 404 존재 숨김
    return memoRepository.findById(memoId).filter(m -> memberId.equals(m.getMemberId()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  // TODO PutMapping 으로 수정 (검증 규칙을 유지하자)
  // TODO 또는 추후 Patch 로 일부 필드만 수정 가능하게 또는 JsonNullable 등 도입해야.
  @PatchMapping("/{memoId}")
  Memo edit(@Login Long memberId, @PathVariable Long memoId,
      @Validated @RequestBody MemoUpdateRequest memoUpdateRequest) {

    Memo memo = memoRepository.findById(memoId).filter(m -> memberId.equals(m.getMemberId()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    memo.update(memoUpdateRequest.title(), memoUpdateRequest.content());
    return memo;
  }
}
