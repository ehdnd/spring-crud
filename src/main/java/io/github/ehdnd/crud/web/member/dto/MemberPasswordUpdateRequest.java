package io.github.ehdnd.crud.web.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberPasswordUpdateRequest(

    @NotBlank
    String currentPassword,

    @NotBlank
    String newPassword
) {

}
