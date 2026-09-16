package io.github.ehdnd.crud.domain.memo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Memo {

  private Long id;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Memo() {
  }

  public Memo(String title, String content) {
    this.title = title;
    this.content = content;
  }
}
