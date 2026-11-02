package io.github.ehdnd.crud.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

  private final Map<Long, Member> store = new HashMap<>();
  private long sequence = 0L;

  public Member save(Member member) {
    member.setId(++sequence);
    store.put(member.getId(), member);
    return member;
  }

  public Member findById(Long id) {
    return store.get(id);
  }

  public List<Member> findAll() {
    return new ArrayList<>(store.values());
  }

  public Optional<Member> findByLoginId(Long loginId) {
    return store.values().stream().filter(member -> member.getLoginId().equals(loginId))
        .findFirst();
  }

  public Member update(Long id, Member updateParam) {
    Member findMember = store.get(id);
    findMember.setLoginId(updateParam.getLoginId());
    findMember.setPassword(updateParam.getPassword());
    return findMember;
  }

  public void clear() {
    store.clear();
  }
}
