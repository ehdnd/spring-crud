package io.github.ehdnd.crud.web.memo.dto;

import io.github.ehdnd.crud.domain.memo.Memo;
import java.time.LocalDateTime;

public record MemoResponse(

    Long id,

    String title,
    String content,

    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {

  public static MemoResponse from(Memo memo) {
    return new MemoResponse(memo.getId(), memo.getTitle(), memo.getContent(), memo.getCreatedAt(),
        memo.getUpdatedAt());
  }
}
