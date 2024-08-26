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
     * 获取类的所有字段名称，用英文逗号隔开
     *
     * @param clazz
     * @return
     */
    public static String getAllFieldsExceptList(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            fieldNames.add(field.getName());
        }

        // 使用逗号将字段名连接起来
        return String.join(",", fieldNames);
    }

    /**
     * 获取类的所有字段内容，用英文逗号隔开
     *
     * @param obj
     * @return
     */
    public static String getAllFieldValuesExceptList(Object obj) {
        List<String> fieldValues = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
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

            // 如果需要，可以遍历父类字段（这取决于你的具体需求）
            // clazz = clazz.getSuperclass();
            // 注意：上面的循环已经修改，现在直接在循环外处理，因为我们通常不需要递归到Object类
            // 当前只关心当前类的字段
            break;
        }

        // 使用逗号将字段值连接起来
        return String.join(",", fieldValues);
    }

    /**
     * 获取指定对象中所有带有DescriptionField注解的字段的值。
     * <p>
     * 该方法遍历指定对象的所有字段，检查每个字段是否带有DescriptionField注解。
     * 如果字段带有DescriptionField注解，则获取该字段的值并添加到一个列表中。
     * 最后，将列表中的所有值用逗号连接成一个字符串并返回。
     *
     * @param obj 要检查的对象。
     * @return 包含所有带有DescriptionField注解的字段的值的字符串，值之间用逗号分隔。
     */
    public static String getAllDescriptionFieldsValue(Object obj) {
        List<String> fieldValues = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(DescriptionField.class)) {
                    field.setAccessible(true); // 确保可以访问私有字段
                    try {
                        Object value = field.get(obj);
                        if (value != null) {
                            fieldValues.add(String.valueOf(value));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            // 如果需要，可以遍历父类字段（这取决于你的具体需求）
            // clazz = clazz.getSuperclass();
            // 注意：上面的循环已经修改，现在直接在循环外处理，因为我们通常不需要递归到Object类
            // 当前只关心当前类的字段
            break;
        }

        // 使用逗号将字段值连接起来
        return String.join(",", fieldValues);
    }

    /**
     * 获取指定类中所有带有DescriptionField注解的字段的描述值。
     * <p>
     * 该方法遍历指定类的所有字段，检查每个字段是否带有DescriptionField注解。
     * 如果字段带有DescriptionField注解，则将注解的值添加到一个列表中。
     * 最后，将列表中的所有值用逗号连接成一个字符串并返回。
     *
     * @param clazz 要检查的类。
     * @return 包含所有带有DescriptionField注解的字段的描述值的字符串，值之间用逗号分隔。
     */
    public static String getAllDescriptionFieldsDesc(Class<?> clazz) {
        List<String> fieldValues = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 检查字段类型是否不是List
            if (field.isAnnotationPresent(DescriptionField.class)) {
                DescriptionField printValue = field.getAnnotation(DescriptionField.class);
                fieldValues.add(printValue.value());
            }
        }

        // 使用逗号连接起来
        return String.join(",", fieldValues);
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
