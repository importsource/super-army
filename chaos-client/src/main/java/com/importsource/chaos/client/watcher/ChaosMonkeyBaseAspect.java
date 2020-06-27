package com.importsource.chaos.client.watcher;

import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author Benjamin Wilms
 */
abstract class ChaosMonkeyBaseAspect {
    @Pointcut("within(com.importsource.chaos.client..*)")
    public void classInChaosMonkeyPackage() {
    }

    @Pointcut("execution(* *.*(..))")
    public void allPublicMethodPointcut() {
    }

    String calculatePointcut(String target) {
        return target
                .replaceAll("\\(\\)", "")
                .replaceAll("\\)", "")
                .replaceAll("\\(", ".");
    }

    String createSignature(MethodSignature signature) {
        return signature.getDeclaringTypeName() + "." + signature.getMethod().getName();
    }
}
