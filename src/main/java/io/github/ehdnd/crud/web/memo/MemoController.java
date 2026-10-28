package io.github.ehdnd.crud.web.memo;

import io.github.ehdnd.crud.domain.memo.Memo;
import io.github.ehdnd.crud.domain.memo.MemoRepository;
import io.github.ehdnd.crud.web.memo.dto.MemoSaveRequest;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memos")
@RequiredArgsConstructor
@Slf4j
public class MemoController {

  private final MemoRepository memoRepository;

  @GetMapping()
  List<Memo> memos() {
    return memoRepository.findAll();
  }

  @PostMapping()
  Memo add(@Validated @ModelAttribute MemoSaveRequest memoSaveRequest) {
    log.debug("memoSaveRequest={}", memoSaveRequest);

    Memo memo = new Memo(memoSaveRequest.title(), memoSaveRequest.content());

    return memoRepository.save(memo);
  }

  @GetMapping("/{id}")
  Memo memo(@PathVariable Long id) {
    log.debug("id={}", id);
    return memoRepository.findById(id);
  }

  @PatchMapping("/{id}")
  Memo edit(@PathVariable Long id, @ModelAttribute Memo updateParam) {
    log.debug("updateParam={}", updateParam);
    return memoRepository.update(id, updateParam);
  }

  @PostConstruct
  void init() {
    log.debug("init");

    memoRepository.save(new Memo("A", "AAA"));
    memoRepository.save(new Memo("B", "BBB"));
  }
}
