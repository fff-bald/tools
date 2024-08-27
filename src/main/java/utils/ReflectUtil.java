package utils;

import tag.DescriptionField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射工具类
 *
 * @author cjl
 * @since 2024/8/4 13:42
 */
public class ReflectUtil {
    /**
     * 获取指定类中所有字段的名称，并根据指定的注解进行过滤。
     *
     * <p>该方法通过反射获取指定类的所有字段名称，并根据传入的注解类型进行过滤。
     * 如果字段上存在指定的注解，则将该字段名称添加到结果列表中。最终返回以指定分隔符
     * 拼接的字段名称字符串。</p>
     *
     * @param clazz           要处理的类
     * @param annotationClass 要过滤的注解类型，如果为 null 则不过滤
     * @param split           字段名称之间的分隔符
     * @return 以指定分隔符拼接的字段名称字符串
     */
    public static String getAllFieldName(Class<?> clazz, Class<? extends Annotation> annotationClass, String split) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (annotationClass != null && !field.isAnnotationPresent(annotationClass)) {
                continue;
            }
            fieldNames.add(field.getName());
        }

        // 拼接结果
        return String.join(split, fieldNames);
    }

    /**
     * 获取指定对象中所有字段的值，并根据指定的注解进行过滤。
     *
     * <p>该方法通过反射获取指定对象的所有字段值，并根据传入的注解类型进行过滤。
     * 如果字段上存在指定的注解，则将该字段的值添加到结果列表中。最终返回以指定分隔符
     * 拼接的字段值字符串。</p>
     *
     * @param obj             要处理的对象
     * @param annotationClass 要过滤的注解类型，如果为 null 则不过滤
     * @param split           字段值之间的分隔符
     * @return 以指定分隔符拼接的字段值字符串
     */
    public static String getAllFieldValue(Object obj, Class<? extends Annotation> annotationClass, String split) {
        List<String> fieldValues = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (annotationClass != null && !field.isAnnotationPresent(annotationClass)) {
                continue;
            }
            field.setAccessible(true); // 确保可以访问私有字段
            try {
                Object value = field.get(obj);
                if (value != null) {
                    fieldValues.add(value.toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // 拼接结果
        return String.join(split, fieldValues);
    }

    /**
     * 获取类中所有带有 @DescriptionField 注解的字段的注解值，并以指定分隔符拼接成字符串。
     *
     * <p>该方法通过反射获取指定类的所有字段，并检查每个字段是否带有 @DescriptionField 注解。
     * 如果字段带有该注解，则获取注解的值，并将所有注解值以指定的分隔符拼接成一个字符串。</p>
     *
     * @param clazz 要处理的类
     * @param split 字段注解值之间的分隔符
     * @return 以指定分隔符拼接的字段注解值字符串
     */
    public static String getAllDescriptionFieldAnnotationValue(Class<?> clazz, String split) {
        List<String> fieldValues = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(DescriptionField.class)) {
                DescriptionField printValue = field.getAnnotation(DescriptionField.class);
                fieldValues.add(printValue.value());
            }
        }

        // 拼接结果字符串
        return String.join(split, fieldValues);
    }

    /**
     * 格式化对象中所有的double和Double类型字段的值，保留指定的小数位数。
     *
     * <p>此方法会遍历指定对象的所有字段，如果字段类型为double或Double，
     * 则将其值格式化为保留指定小数位数的值。对于无限大（Infinity）、NaN（Not-a-Number）
     * 或null的值，方法将跳过这些字段。</p>
     *
     * @param object 要格式化的对象。该对象的所有double和Double类型字段将被格式化。
     * @param count  保留的小数位数。
     * @throws IllegalArgumentException 如果传入的对象为null。
     */
    public static void formatDoubleField(Object object, int count) {
        if (object == null) {
            throw new IllegalArgumentException("传入的对象不能为null");
        }

        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                // 允许访问私有字段
                field.setAccessible(true);

                // 获取字段的当前值
                Object fieldValue = field.get(object);

                // 检查字段是否为null或不是double/Double类型，则跳过
                if (fieldValue == null || !(field.getType().equals(double.class) || field.getType().equals(Double.class))) {
                    continue;
                }

                // 根据字段类型正确处理double或Double值
                double originalValue;
                if (field.getType().equals(double.class)) {
                    // 如果是基本类型double，直接获取其值（但这里实际上是通过field.get得到的Double再自动拆箱，因为field.get返回Object）
                    // 但由于field.get(object)已经处理了拆箱，我们直接转换是安全的
                    originalValue = (Double) fieldValue; // 注意：这里实际上不会抛出ClassCastException，因为field.get已经处理了
                } else {
                    // 如果是包装类型Double，直接获取其值
                    originalValue = ((Double) fieldValue).doubleValue();
                }

                // 跳过无限大或NaN的值
                if (Double.isInfinite(originalValue) || Double.isNaN(originalValue)) {
                    continue;
                }

                // 格式化值，保留特定位小数
                BigDecimal bd = BigDecimal.valueOf(originalValue); // 使用valueOf确保精度
                bd = bd.setScale(count, RoundingMode.HALF_UP);

                // 将格式化后的值设置回字段
                // 注意：如果字段是double类型，这里会自动装箱为Double；如果字段是Double类型，则直接赋值
                field.set(object, bd.doubleValue());

            } catch (IllegalAccessException e) {
                // 通常这个异常是由于反射访问权限问题，但我们已经设置了setAccessible(true)
                // 因此，这里更可能是其他未知问题，但通常不会抛出
                LogUtil.error("【utils】无法访问或设置字段：{}", ExceptionUtil.getStackTraceAsString(e));
            } catch (IllegalArgumentException e) {
                // 这个异常可能由错误的字段类型或值引起
                LogUtil.error("【utils】字段值类型不匹配或非法：{}", ExceptionUtil.getStackTraceAsString(e));
            }
        }
    }

    /**
     * 将类中所有未被指定注解修饰的字段设置为null或默认值。
     *
     * @param object          要处理的对象
     * @param annotationClass 注解类
     * @throws IllegalAccessException 如果无法访问字段
     */
    public static void resetFieldsWithout(Object object, Class<? extends Annotation> annotationClass) throws IllegalAccessException {
        if (object == null) {
            throw new IllegalArgumentException("The object parameter cannot be null.");
        }

        Class<?> clazz = object.getClass();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                // 检查字段是否被DescriptionField注解修饰
                if (!field.isAnnotationPresent(annotationClass)) {
                    field.setAccessible(true);

                    // 获取字段类型
                    Class<?> fieldType = field.getType();

                    // 根据字段类型设置为null或默认值
                    if (fieldType.isPrimitive()) {
                        // 基础类型设置为默认值
                        if (fieldType.equals(boolean.class)) {
                            field.setBoolean(object, false);
                        } else if (fieldType.equals(char.class)) {
                            field.setChar(object, '\u0000');
                        } else if (fieldType.equals(byte.class)) {
                            field.setByte(object, (byte) 0);
                        } else if (fieldType.equals(short.class)) {
                            field.setShort(object, (short) 0);
                        } else if (fieldType.equals(int.class)) {
                            field.setInt(object, 0);
                        } else if (fieldType.equals(long.class)) {
                            field.setLong(object, 0L);
                        } else if (fieldType.equals(float.class)) {
                            field.setFloat(object, 0.0f);
                        } else if (fieldType.equals(double.class)) {
                            field.setDouble(object, 0.0);
                        }
                    } else {
                        // 对象类型设置为null
                        field.set(object, null);
                    }
                }
            }

            // 如果需要，可以遍历父类字段（这取决于你的具体需求）
            clazz = clazz.getSuperclass();
        }
    }
}
