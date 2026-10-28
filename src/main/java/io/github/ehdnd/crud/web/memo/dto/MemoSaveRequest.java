package io.github.ehdnd.crud.web.memo.dto;

import jakarta.validation.constraints.NotBlank;

public record MemoSaveRequest(

    @NotBlank
    String title,

    @NotBlank
    String content
    
) {

}
