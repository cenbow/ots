package com.mk.ots.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hessian RPC Service.
 * @author chuaiqing.
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HessianService {
    // Hessian service URL
    String value();
    /**
     * Hessian service implement interface.
     * @return
     */
    Class implmentInterface();
}
