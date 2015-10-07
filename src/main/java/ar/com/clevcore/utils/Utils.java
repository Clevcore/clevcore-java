package ar.com.clevcore.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private Utils() {
        throw new AssertionError();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Comparator getComparator(final String property, final boolean ascendingOrder) {
        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object object0, Object object1) {
                int aux = 0;
                Comparable comparable0 = (Comparable) getValueFromProperty(object0, property);
                Comparable comparable1 = (Comparable) getValueFromProperty(object1, property);

                if (comparable0 != null || comparable1 != null) {
                    if (ascendingOrder) {
                        aux = (comparable0 != null) ? comparable0.compareTo(comparable1 == null ? "" : comparable1) : 1;
                    } else {
                        aux = (comparable1 != null) ? comparable1.compareTo(comparable0 == null ? "" : comparable0)
                                : -1;
                    }
                }
                return aux;
            }
        };
        return comparator;
    }

    public static Map<String, Object> getPropertyValue(Object object, List<String> propertyList,
            boolean isValueNotNull) {
        Class<?> clazz = (Class<?>) object.getClass();
        Map<String, Object> valueMap = new LinkedHashMap<String, Object>(0);

        if (propertyList == null) {
            propertyList = getPropertiesFromObject(clazz);
        } else {
            prepareProperties(propertyList, clazz);
        }

        for (String property : propertyList) {
            Object value = getValueFromProperty(object, property);
            if (isValueNotNull) {
                if (value != null) {
                    valueMap.put(property, value);
                }
            } else {
                valueMap.put(property, value);
            }
        }

        return valueMap;
    }

    public static List<Object> searchObject(String search, List<Object> objectList, List<String> propertyList,
            boolean isCaseSensitive, String patternDate) {
        List<Object> objectResultList = (List<Object>) new ArrayList<Object>(0);

        Class<? extends Object> clazz = objectList.get(0).getClass();

        if (propertyList == null || propertyList.isEmpty()) {
            propertyList = getPropertiesFromObject(clazz);
        } else {
            prepareProperties(propertyList, clazz);
        }

        if (!isCaseSensitive) {
            search = search.toLowerCase();
        }

        for (Object object : objectList) {
            for (String property : propertyList) {
                Object value = getValueFromProperty(object, property);
                if (value != null && !value.toString().isEmpty()) {
                    if (Date.class.equals(value.getClass())) {
                        try {
                            value = DateUtils.getDateFormat((Date) value, patternDate);
                        } catch (ParseException e) {
                            LOG.error("[E] ParseException occurred in [searchObject]", e);
                        }
                    } else if (!isCaseSensitive) {
                        value = value.toString().toLowerCase();
                    }
                    if (value.toString().contains(search)) {
                        objectResultList.add(object);
                        break;
                    }
                }

            }
        }

        return objectResultList;
    }

    public static String toGetterMethodString(String property) {
        return "get" + StringUtils.upperCaseFirst(property);
    }

    public static void prepareProperties(List<String> propertyList, Class<?> clazz) {
        for (int i = 0; i < propertyList.size(); i++) {
            if ("*".equals(propertyList.get(i))) {
                propertyList.remove(i);
                propertyList.addAll(i, getPropertiesFromObject(clazz));
                break;
            }
        }
    }

    public static List<String> getPropertiesFromObject(Class<?> clazz) {
        List<String> propertyList = new ArrayList<String>();
        for (Field field : clazz.getDeclaredFields()) {
            if (isNativeType(field.getType())) {
                propertyList.add(field.getName());
            }
        }
        return propertyList;
    }

    public static List<Method> getMethods(Object object) {
        Class<?> clazz = (Class<?>) object.getClass();
        List<Method> methodList = new ArrayList<Method>(0);

        for (Field field : clazz.getDeclaredFields()) {
            if (isNativeType(field.getType())) {
                try {
                    Method method = clazz.getMethod(toGetterMethodString(field.getName()));
                    methodList.add(method);
                } catch (NoSuchMethodException e) {
                    LOG.error("[E] NoSuchMethodException occurred in [getMethods]", e);
                }
            }
        }

        return methodList;
    }

    public static boolean isNativeType(Class<?> clazzType) {
        return clazzType.equals(Date.class) || clazzType.equals(Double.class) || clazzType.equals(Integer.class)
                || clazzType.equals(Long.class) || clazzType.equals(String.class);
    }

    public static Object getValueFromProperty(Object object, String property) {
        Object methodResult = object;
        try {
            String[] propertyArray = property.split("\\.");
            for (int i = 0; i < propertyArray.length; i++) {
                String getterMethodString = toGetterMethodString(propertyArray[i]);
                Method method = methodResult.getClass().getMethod(getterMethodString);
                methodResult = method.invoke(methodResult);
            }
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            LOG.error("[E] Exception occurred in [getValueFromProperty]", e);
        }
        return methodResult;
    }

    public static List sortList(List list, String property, boolean ascendingOrder) {
        if (list == null || list.size() < 2) {
            return list;
        }
        Collections.sort(list, getComparator(property, ascendingOrder));
        return list;
    }

}
