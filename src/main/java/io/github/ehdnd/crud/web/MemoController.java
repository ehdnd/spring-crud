package io.github.ehdnd.crud.web;

import io.github.ehdnd.crud.domain.memo.Memo;
import io.github.ehdnd.crud.domain.memo.MemoRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

  @PostConstruct
  void init() {
    log.debug("init");

    LocalDateTime now = LocalDateTime.now();
    memoRepository.save(new Memo("A", "AAA", now, now));
    memoRepository.save(new Memo("B", "BBB", now, now));
  }
}
