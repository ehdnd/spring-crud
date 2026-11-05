package io.github.ehdnd.crud.web.member.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/// DTO 테스트 - bean validation 규칙 테스트
class MemberPasswordUpdateRequestTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  static void setUp() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void tearDown() {
    factory.close();
  }

  @Test
  void valid() {
    assertThat(validate(new MemberPasswordUpdateRequest("password", "newPassword"))).isEmpty();
  }

  // TODO 비밀번호 길이 제한 추가
  @Test
  void valid_oneCharacter() {
    assertThat(validate(new MemberPasswordUpdateRequest("a", "b"))).isEmpty();
  }

  @ParameterizedTest(name = "currentPassword = [{0}]")
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   ", "\t", "\n"})
  void currentPassword_blank(String currentPassword) {
    assertThat(violations(new MemberPasswordUpdateRequest(currentPassword, "newPassword")))
        .containsExactly(tuple("currentPassword", NotBlank.class));
  }

  @ParameterizedTest(name = "newPassword = [{0}]")
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   ", "\t", "\n"})
  void newPassword_blank(String newPassword) {
    assertThat(violations(new MemberPasswordUpdateRequest("password", newPassword)))
        .containsExactly(tuple("newPassword", NotBlank.class));
  }

  @Test
  void bothBlank() {
    // 하나 실패했다고 검증이 멈추지 않고, 모든 위반을 함께 보고하는지
    assertThat(violations(new MemberPasswordUpdateRequest(" ", " ")))
        .containsExactlyInAnyOrder(
            tuple("currentPassword", NotBlank.class),
            tuple("newPassword", NotBlank.class));
  }

  // === HELPERS ===

  private static Set<ConstraintViolation<MemberPasswordUpdateRequest>> validate(
      MemberPasswordUpdateRequest request) {
    return validator.validate(request);
  }

  /**
   * 위반 목록을 (필드명, 제약 애너테이션 타입) 튜플 목록으로 변환한다.
   */
  private static List<Tuple> violations(MemberPasswordUpdateRequest request) {
    return validate(request).stream()
        .map(v -> tuple(v.getPropertyPath().toString(), annotationType(v)))
        .toList();
  }

  private static Class<? extends Annotation> annotationType(ConstraintViolation<?> violation) {
    return violation.getConstraintDescriptor().getAnnotation().annotationType();
  }
}
