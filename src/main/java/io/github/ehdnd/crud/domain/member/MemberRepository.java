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

  public Optional<Member> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  public List<Member> findAll() {
    return new ArrayList<>(store.values());
  }

  public Optional<Member> findByLoginId(String loginId) {
    return store.values().stream().filter(member -> member.getLoginId().equals(loginId))
        .findFirst();
  }

  public Member updatePassword(Long id, String newPassword) {
    Member findMember = store.get(id);
    findMember.setPassword(newPassword);
    return findMember;
  }

  public void clear() {
    store.clear();
  }
}
