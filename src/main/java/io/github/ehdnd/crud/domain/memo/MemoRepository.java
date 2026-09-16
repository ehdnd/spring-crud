package io.github.ehdnd.crud.domain.memo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class MemoRepository {

  private final Map<Long, Memo> store = new HashMap<>();
  private long sequence = 0L;

  public Memo save(Memo memo) {
    LocalDateTime now = LocalDateTime.now();
    memo.setId(++sequence);
    memo.setCreatedAt(now);
    memo.setUpdatedAt(now);

    store.put(memo.getId(), memo);
    return memo;
  }

  public Memo findById(Long id) {
    return store.get(id);
  }

  public List<Memo> findAll() {
    return new ArrayList<>(store.values());
  }

  public Memo update(Long id, Memo updateParam) {
    Memo findMemo = store.get(id);
    findMemo.setTitle(updateParam.getTitle());
    findMemo.setContent(updateParam.getContent());

    findMemo.setUpdatedAt(LocalDateTime.now());
    return findMemo;
  }

  public void clear() {
    store.clear();
  }
}
