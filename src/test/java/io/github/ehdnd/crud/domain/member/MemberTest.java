package io.github.ehdnd.crud.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MemberTest {

  @Test
  void updatePassword() {
    // given
    Member member = new Member("loginId", "password");

    // when
    member.updatePassword("newPassword");

    // then
    assertThat(member.getPassword()).isEqualTo("newPassword");
  }

  @Test
  void matchesPassword() {
    // given
    Member member = new Member("loginId", "password");

    // when, then
    assertThat(member.matchesPassword("password")).isTrue();
  }

  @Test
  void matchesPassword_wrongPassword() {
    // given
    Member member = new Member("loginId", "password");

    // when, then
    assertThat(member.matchesPassword("wrong")).isFalse();
  }

  @Test
  void matchesPassword_null() {
    // given
    Member member = new Member("loginId", "password");

    // when, then
    assertThat(member.matchesPassword(null)).isFalse();
  }
}
