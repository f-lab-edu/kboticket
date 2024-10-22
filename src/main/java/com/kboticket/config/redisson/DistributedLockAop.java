package com.kboticket.config.redisson;

import com.kboticket.common.CustomSpringELParser;
import com.kboticket.config.aop.AopForTransaction;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String lockKey = (String) CustomSpringELParser
            .getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());

        RLock rLock = redissonClient.getLock(lockKey);

        if (rLock.isLocked()) {
            throw new KboTicketException(ErrorCode.ALREADY_SELECTED_SEATS);
        }

        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                throw new KboTicketException(ErrorCode.FAILED_TRY_ROCK);
            }

            return aopForTransaction.proceed(joinPoint);
        } catch (KboTicketException e) {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
            throw new KboTicketException(ErrorCode.FAILED_DURING_TRANSACTION);
        }
     }
}
