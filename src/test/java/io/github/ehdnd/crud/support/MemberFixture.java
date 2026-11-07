package io.github.ehdnd.crud.support;

import io.github.ehdnd.crud.domain.member.Member;
import org.springframework.test.util.ReflectionTestUtils;

public final class MemberFixture {

  private MemberFixture() {
  }

  public static Member createMember(Long id, String loginId, String password) {
    Member member = new Member(loginId, password);
    ReflectionTestUtils.setField(member, "id", id);
    return member;
  }
}
