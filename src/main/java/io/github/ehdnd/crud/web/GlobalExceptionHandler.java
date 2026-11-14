package io.github.ehdnd.crud.web;

import io.github.ehdnd.crud.global.error.BusinessException;
import io.github.ehdnd.crud.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 전역 예외 핸들러로, 예외를 ProblemDetail 응답으로 바꾼다.
 *
 * <ul>
 *   <li>어드바이스(@RestControllerAdvice): 이 클래스의 @ExceptionHandler 를 모든 컨트롤러에 적용</li>
 *   <li>동작: 예외가 DispatcherServlet 까지 올라오면 스프링의 리졸버가 예외 타입에 맞는
 *       `@ExceptionHandler` 메서드를 여기서 찾아 실행한다</li>
 *   <li>상속(ResponseEntityExceptionHandler): 스프링 MVC 표준 예외(검증 실패, 405,
 *       ResponseStatusException 등)를 잡아 ProblemDetail 로 응답하는 핸들러를 물려받는다</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  ProblemDetail handleBusinessException(BusinessException e) {

    ErrorCode errorCode = e.getErrorCode();
    log.warn("business exception: code={}, message={}", errorCode.name(), errorCode.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.getStatus(),
        errorCode.getMessage());
    problemDetail.setProperty("code", errorCode.name());
    return problemDetail;
  }

  // 부모가 처리하는 스프링 표준 예외는 전부 응답 직전에 이 메서드를 지남 -> 여기서 code 만 추가
  @Override
  protected @Nullable ResponseEntity<Object> handleExceptionInternal(Exception ex,
      @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

    ResponseEntity<Object> response = super.handleExceptionInternal(ex, body, headers,
        statusCode, request);

    if (response != null && response.getBody() instanceof ProblemDetail problemDetail) {
      problemDetail.setProperty("code", HttpStatus.valueOf(statusCode.value()).name());
    }
    return response;
  }
}
