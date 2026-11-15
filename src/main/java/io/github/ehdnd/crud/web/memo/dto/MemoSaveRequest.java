package io.github.ehdnd.crud.web.memo.dto;

import io.github.ehdnd.crud.domain.memo.Memo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemoSaveRequest(

    @NotBlank
    @Size(max = Memo.TITLE_MAX_LENGTH)
    String title,

    @NotBlank
    @Size(max = Memo.CONTENT_MAX_LENGTH)
    String content

) {

}
