package com.choidh.service.excel.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExcelAopService {
    // 컨트롤러(내부에 뭘 호출해야하는지 저장하고 있어야함)
    // 컨트롤러는 ExcelService를 호출.
    // AOP가 ExcelService 호출 전에 가져와서
    // 파라미터로 넘긴 곳에 있는 ID 값 가지고 와서
    // 그 값을 써서 JPA 조회.
    // 그다음 ExcelService에 조회한 값을 넘겨주고
    // ExcelService에서 그 값을 사용해서 저장 실행

    /**
     * Before: 대상 “메서드”가 실행되기 전에 Advice를 실행합니다.
     */
//    @Before("@annotation(UseExcel)")
//    public void logBefore(JoinPoint joinPoint) {
//        log.info("엑셀 저장하기 전에 동작함.");
//        // 여기에서 ExcelService에 넘겨주는 그 값들을 사용해줘야 할 듯 ?
//        joinPoint.getArgs();
//    }

    /**
     * After : 대상 “메서드”가 실행된 후에 Advice를 실행합니다.
     *
     * @param joinPoint
     */
//    @After("execution(* com.adjh.multiflexapi.controller.*.*(..))")
//    public void logAfter(JoinPoint joinPoint) {
//        log.info("After: " + joinPoint.getSignature().getName());
//    }

    /**
     * AfterReturning: 대상 “메서드”가 정상적으로 실행되고 반환된 후에 Advice를 실행합니다.
     *
     * @param joinPoint
     * @param result
     */
//    @AfterReturning(pointcut = "execution(* com.adjh.multiflexapi.controller.*.*(..))", returning = "result")
//    public void logAfterReturning(JoinPoint joinPoint, Object result) {
//        log.info("AfterReturning: " + joinPoint.getSignature().getName() + " result: " + result);
//    }

    /**
     * AfterThrowing: 대상 “메서드에서 예외가 발생”했을 때 Advice를 실행합니다.
     *
     * @param joinPoint
     * @param e
     */
//    @AfterThrowing(pointcut = "execution(* com.adjh.multiflexapi.controller.*.*(..))", throwing = "e")
//    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
//        log.info("AfterThrowing: " + joinPoint.getSignature().getName() + " exception: " + e.getMessage());
//    }

    /**
     * Around : 대상 “메서드” 실행 전, 후 또는 예외 발생 시에 Advice를 실행합니다.
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
//    @Around("execution(* com.adjh.multiflexapi.controller.*.*(..))")
//    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        log.info("Around before: " + joinPoint.getSignature().getName());
//        Object result = joinPoint.proceed();
//        log.info("Around after: " + joinPoint.getSignature().getName());
//        return result;
//    }
}
