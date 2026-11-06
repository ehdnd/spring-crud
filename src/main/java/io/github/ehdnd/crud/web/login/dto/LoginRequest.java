package io.github.ehdnd.crud.web.login.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

    @NotBlank
    String loginId,

    @NotBlank
    String password

) {

}
