package io.github.ehdnd.crud.web;

import io.github.ehdnd.crud.domain.memo.Memo;
import io.github.ehdnd.crud.domain.memo.MemoRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

  @GetMapping
  List<Memo> memos() {
    log.debug("memos");

    return memoRepository.findAll();
  }

  @GetMapping("/{id}")
  Memo memo(@PathVariable Long id) {
    log.debug("memo");

    return memoRepository.findById(id);
  }

  @GetMapping("/{id}/edit")
  Memo getEdit(@PathVariable Long id) {
    log.debug("getEdit");
    return memoRepository.findById(id);
  }

  @PostMapping("/{id}/edit")
  Memo postEdit(@PathVariable Long id, @ModelAttribute Memo updateParam) {
    log.debug("postEdit");
    log.debug("updateParam: {}", updateParam);
    return memoRepository.update(id, updateParam);
  }

  @GetMapping("/add")
  String getAdd() {
    log.debug("getAdd");
    return "ok";
  }

  @PostMapping("/add")
  Memo postAdd(@ModelAttribute Memo memo) {
    log.debug("postAdd");
    return memoRepository.save(memo);
  }

  @PostConstruct
  void init() {
    log.debug("init");

    memoRepository.save(new Memo("A", "AAA"));
    memoRepository.save(new Memo("B", "BBB"));
  }
}
