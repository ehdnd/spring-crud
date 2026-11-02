package io.github.ehdnd.crud.web.member.dto;

import io.github.ehdnd.crud.domain.member.Member;

public record MemberResponse(

    // Member 객체 그대로 내보내면 안된다.
    Long id,
    String loginId

) {

  public static MemberResponse from(Member member) {
    return new MemberResponse(member.getId(), member.getLoginId());
  }
}
