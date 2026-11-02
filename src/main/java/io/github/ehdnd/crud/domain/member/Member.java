package io.github.ehdnd.crud.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Member {

  @Setter(AccessLevel.PACKAGE)
  private Long id;

  @Setter
  private String loginId;
  @Setter
  private String password;

  public Member() {
  }

  public Member(String loginId, String password) {
    this.loginId = loginId;
    this.password = password;
  }
}
