package utils;

import tag.DescriptionField;

import java.lang.reflect.Field;
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
     * 获取类的所有字段名称，用逗号隔开，排除掉List类型
     *
     * @param clazz
     * @return
     */
    public static String getAllFieldsExceptList(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 检查字段类型是否不是List
            if (!List.class.isAssignableFrom(field.getType())) {
                fieldNames.add(field.getName());
            }
        }

        // 使用逗号将字段名连接起来
        return String.join(",", fieldNames);
    }

    /**
     * 获取类的所有字段内容，用逗号隔开，排除掉List类型
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
                if (!List.class.isAssignableFrom(field.getType())) {
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
     * 如果字段带有DescriptionField注解且不是List类型，则获取该字段的值并添加到一个列表中。
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
                if (!List.class.isAssignableFrom(field.getType())
                        && field.isAnnotationPresent(DescriptionField.class)) {
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
}
