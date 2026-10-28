package io.github.ehdnd.crud.web.memo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemoUpdateRequest(

    @NotNull
    Long id,

    @NotBlank
    @Size(max = 20)
    String title,

    @NotBlank
    @Size(min = 10)
    String content

) {

}
