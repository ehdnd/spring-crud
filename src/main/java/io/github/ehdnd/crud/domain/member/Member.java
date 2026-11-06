package io.github.ehdnd.crud.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class Member {

  @Setter(AccessLevel.PACKAGE)
  private Long id;

  @Setter
  private String loginId;
  @Setter
  @ToString.Exclude
  private String password;

  public Member() {
  }

  public Member(String loginId, String password) {
    this.loginId = loginId;
    this.password = password;
  }

  public boolean matchesPassword(String password) {
    return this.password.equals(password);
  }
}
