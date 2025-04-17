package com.jihun.myshop.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // 서비스 계층 메소드 실행 전/후 로깅
    @Around("execution(* com.jihun.myshop..service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String shortClassName = className.substring(className.lastIndexOf('.') + 1);

        // 파라미터 로깅 - 파라미터 이름과 값을 함께 표시
        String params = getParameterNamesAndValues(joinPoint);
        log.info("┌── [SERVICE] {} - {}", shortClassName, methodName);
        log.info("├── Params: {}", params);

        // 실행 시간 측정
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;
        try {
            // 메소드 실행
            result = joinPoint.proceed();
            stopWatch.stop();

            // 결과 및 실행시간 로깅 - 성공
            log.info("├── Execution Time: {}ms", stopWatch.getTotalTimeMillis());
            log.info("└── Result: {}", result != null ? "SUCCESS" : "NULL");
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            // 예외 로깅 - 실패
            log.error("├── Execution Time: {}ms", stopWatch.getTotalTimeMillis());
            log.error("├── Status: FAILED");
            log.error("├── Error: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            log.error("└── Stack: ", e);
            throw e;
        }
    }

    // Controller 계층 메소드 진입 시 로깅
    @Before("execution(* com.jihun.myshop..controller..*.*(..))")
    public void logBeforeControllers(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String shortClassName = className.substring(className.lastIndexOf('.') + 1);

        // HTTP 메소드와 엔드포인트 정보 추가 (가능한 경우)
        log.info("┌── [API] {} - {}", shortClassName, methodName);
        log.info("└── Params: {}", getParameterNamesAndValues(joinPoint));
    }

    // 예외 발생 시 로깅
    @AfterThrowing(pointcut = "execution(* com.jihun.myshop..*.*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String shortClassName = className.substring(className.lastIndexOf('.') + 1);

        log.error("┌── [ERROR] {} - {}", shortClassName, methodName);
        log.error("├── Exception: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());
        log.error("└── Stack: ", exception);
    }

    // 파라미터 이름과 값을 함께 표시하는 헬퍼 메소드
    private String getParameterNamesAndValues(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parameterNames.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(parameterNames[i]).append("=");

                // 민감한 데이터는 마스킹 처리
                if (parameterNames[i].toLowerCase().contains("password") ||
                        parameterNames[i].toLowerCase().contains("token")) {
                    sb.append("*****");
                } else {
                    sb.append(args[i]);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            // 파라미터 정보를 가져올 수 없는 경우 기본 toString 사용
            return Arrays.toString(joinPoint.getArgs());
        }
    }
}