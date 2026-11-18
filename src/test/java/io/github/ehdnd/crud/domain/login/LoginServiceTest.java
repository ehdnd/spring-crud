package io.github.ehdnd.crud.domain.login;


import static org.assertj.core.api.Assertions.*;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class LoginServiceTest {

  MemberRepository memberRepository = new MemberRepository();
  LoginService loginService = new LoginService(memberRepository);

  @Test
  void login() {
    // given
    Member member = memberRepository.save(new Member("loginId", "password"));

    // when
    Member loggedIn = loginService.login("loginId", "password");

    // then
    assertThat(member.getLoginId()).isEqualTo(loggedIn.getLoginId());
    assertThat(member.getPassword()).isEqualTo(loggedIn.getPassword());
  }

  @Test
  @Disabled
  void login_idNotFound() {
  }

  @Test
  @Disabled
  void login_wrongPassword() {
  }

  @Test
  @Disabled("TODO")
  void me() {
  }

  @Test
  @Disabled
  void me_memberNotFound() {
  }
}