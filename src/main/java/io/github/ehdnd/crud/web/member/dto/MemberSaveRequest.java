package io.github.ehdnd.crud.web.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberSaveRequest(

    @NotBlank
    String loginId,

    @NotBlank
    String password

) {

}
