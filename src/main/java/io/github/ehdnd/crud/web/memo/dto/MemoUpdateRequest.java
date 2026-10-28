package io.github.ehdnd.crud.web.memo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemoUpdateRequest(

    @NotNull
    Long id,

    @NotBlank
    String title,

    @NotBlank
    String content

) {

}
