package csx.com.annotation_lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 2020/5/6 create by cuishuxiang description:
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface AptTestAnnotation {
  String getName() default "";
}
