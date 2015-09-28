package com.mk.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * DbTable annotation: 标注Model对应的数据源名称、表名称、主键列名称
 * @author chuaiqing.
 *
 */
/*
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
@Component
*/
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbTable {
    /**
     * Model对应的数据库表名称
     * @return
     */
    public abstract String name();
    
    /**
     * Model的主键列名称：列明可以不是id，但是在建立model的时候那就必须用pkey="xxx"注明
     * @return
     */
    public abstract String pkey() default "id";
}
