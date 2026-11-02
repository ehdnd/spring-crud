package io.github.ehdnd.crud.web.member.dto;

public record MemberResponse(

    // Member 객체 그대로 내보내면 안된다.
    Long id,
    String loginId

) {

}
