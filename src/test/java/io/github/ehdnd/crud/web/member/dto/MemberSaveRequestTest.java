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
class MemberSaveRequestTest {

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
    assertThat(validate(new MemberSaveRequest("loginId", "password"))).isEmpty();
  }

  @Test
  void valid_oneCharacter() {
    // @NotBlank 는 "공백이 아닌 문자 1개 이상" 이면 통과한다. (길이 제한 규칙이 생기면 이 테스트가 깨지며 알려준다)
    assertThat(validate(new MemberSaveRequest("a", "b"))).isEmpty();
  }

  @ParameterizedTest(name = "loginId = [{0}]")
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   ", "\t", "\n"})
  void loginId_blank(String loginId) {
    assertThat(violations(new MemberSaveRequest(loginId, "password")))
        .containsExactly(tuple("loginId", NotBlank.class));
  }

  @ParameterizedTest(name = "password = [{0}]")
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   ", "\t", "\n"})
  void password_blank(String password) {
    assertThat(violations(new MemberSaveRequest("loginId", password)))
        .containsExactly(tuple("password", NotBlank.class));
  }

  @Test
  void bothBlank() {
    // 하나 실패했다고 검증이 멈추지 않고, 모든 위반을 함께 보고하는지
    assertThat(violations(new MemberSaveRequest(" ", " ")))
        .containsExactlyInAnyOrder(
            tuple("loginId", NotBlank.class),
            tuple("password", NotBlank.class));
  }

  // === HELPERS ===

  private static Set<ConstraintViolation<MemberSaveRequest>> validate(MemberSaveRequest request) {
    return validator.validate(request);
  }

  /**
   * 위반 목록을 (필드명, 제약 애너테이션 타입) 튜플 목록으로 변환한다.
   */
  private static List<Tuple> violations(MemberSaveRequest request) {
    return validate(request).stream()
        .map(v -> tuple(v.getPropertyPath().toString(), annotationType(v)))
        .toList();
  }

  private static Class<? extends Annotation> annotationType(ConstraintViolation<?> violation) {
    return violation.getConstraintDescriptor().getAnnotation().annotationType();
  }
}
