package io.github.ehdnd.crud.web.memo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemoSaveRequest(

    @NotBlank
    @Size(max = 20)
    String title,

    @NotBlank
    @Size(max = 1000)
    String content

) {

}
