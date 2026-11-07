package io.github.ehdnd.crud.domain.memo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  public Optional<Memo> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  public List<Memo> findByMemberId(Long memberId) {
    return store.values().stream().filter(m -> memberId.equals(m.getMemberId())).toList();
  }

  public List<Memo> findAll() {
    return new ArrayList<>(store.values());
  }
  
  public void clear() {
    store.clear();
  }
}
