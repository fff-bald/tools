package tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述类字段的含义
 */
@Target(ElementType.FIELD) // 限定注解只能用在字段上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留注解信息
public @interface DescriptionField {
    String value() default ""; // 定义一个返回字符串的value属性，默认为空字符串
}
