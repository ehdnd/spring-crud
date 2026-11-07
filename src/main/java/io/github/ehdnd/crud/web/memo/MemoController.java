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

  @GetMapping()
  List<Memo> memos() {
    return memoRepository.findAll();
  }

  @PostMapping()
  Memo add(@Login Long memberId, @Validated @RequestBody MemoSaveRequest memoSaveRequest) {

    Memo memo = new Memo(memoSaveRequest.title(), memoSaveRequest.content(), memberId);
    return memoRepository.save(memo);
  }

  @GetMapping("/{id}")
  Memo memo(@PathVariable Long id) {
    return memoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  // TODO PutMapping 으로 수정 (검증 규칙을 유지하자)
  @PatchMapping("/{id}")
  Memo edit(@Login Long memberId, @PathVariable Long id,
      @Validated @RequestBody MemoUpdateRequest memoUpdateRequest) {

    Memo memo = memoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!memo.getMemberId().equals(memberId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    memo.update(memoUpdateRequest.title(), memoUpdateRequest.content());
    return memo;
  }
}
