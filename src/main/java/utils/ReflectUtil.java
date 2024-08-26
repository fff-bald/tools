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
     * 格式化对象中所有的Double类型字段的值，保留指定的小数位数。
     *
     * <p>该方法会遍历指定对象的所有字段，如果字段类型为Double或double，
     * 则将其值格式化为保留指定小数位数的值。</p>
     *
     * @param object 要格式化的对象。该对象的所有Double类型字段将被格式化。
     * @param count  保留的小数位数。
     * @throws IllegalArgumentException 如果传入的对象为null。
     */
    public static void formatDoubleField(Object object, int count) {
        Class<?> clazz = object.getClass();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                try {
                    // 检查字段是否为Double类型，Double类型保留到小数点后两位
                    if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
                        // 获取字段的原始值
                        double originalValue = field.getDouble(object);

                        // 修改值，保留两位小数
                        BigDecimal bd = new BigDecimal(Double.toString(originalValue));
                        bd = bd.setScale(count, RoundingMode.HALF_UP);

                        // 将修改后的值设置回字段
                        field.set(object, bd.doubleValue());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            // 如果需要，可以遍历父类字段（这取决于你的具体需求）
            // clazz = clazz.getSuperclass();
            // 注意：上面的循环已经修改，现在直接在循环外处理，因为我们通常不需要递归到Object类
            // 当前只关心当前类的字段
            break;
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
