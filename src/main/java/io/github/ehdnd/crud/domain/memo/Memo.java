package io.github.ehdnd.crud.domain.memo;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Memo {

  // 리포지토리(같은 패키지)만 세팅. JPA 라면 @Id 생성 / Auditing 이 하는 일
  @Setter(AccessLevel.PACKAGE)
  private Long id;

  private String title;
  private String content;
  private final Long memberId;

  @Setter(AccessLevel.PACKAGE)
  private LocalDateTime createdAt;

  @Setter(AccessLevel.PACKAGE)
  private LocalDateTime updatedAt;

  public Memo(String title, String content, Long memberId) {
    this.title = title;
    this.content = content;
    this.memberId = memberId;
  }

  /**
   * 수정 가능한 값(title, content)은 이 메서드로만 바뀐다.
   */
  public void update(String title, String content) {
    this.title = title;
    this.content = content;
    this.updatedAt = LocalDateTime.now();
  }
}
