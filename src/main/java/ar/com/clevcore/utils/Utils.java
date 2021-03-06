package ar.com.clevcore.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
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

    @SafeVarargs
    public static <T> T coalesce(T... objects) {
        for (T object : objects) {
            if (object != null) {
                return object;
            }
        }

        return null;
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

        if (propertyList == null) {
            propertyList = getPropertiesFromObject(clazz);
        } else {
            prepareProperties(propertyList, clazz);
        }

        if (!isCaseSensitive) {
            search = search.toLowerCase();
        }

        for (Object object : objectList) {
            for (String property : propertyList) {
                try {
                    Object value = getValueFromProperty(object, property);
                    if (value != null && !value.toString().isEmpty()) {
                        if (Date.class.equals(value.getClass())) {
                            value = DateUtils.getDateFormat((Date) value, patternDate);
                        } else if (!isCaseSensitive) {
                            value = value.toString().toLowerCase();
                        }
                        if (value.toString().contains(search)) {
                            objectResultList.add(object);
                            break;
                        }
                    }
                } catch (ParseException e) {
                    LOG.error("[E] ParseException occurred in [searchObject]", e);
                }
            }
        }
        return objectResultList;
    }

    public static String toGetterMethodString(String property) {
        return "get" + StringUtils.upperCaseFirst(property);
    }

    public static String toSetterMethodString(String property) {
        return "set" + StringUtils.upperCaseFirst(property);
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
        // @formatter:off
        return clazzType.equals(Character.class) ||
               clazzType.equals(String.class) ||

               clazzType.equals(Date.class) ||

               clazzType.equals(Boolean.class) ||
               clazzType.equals(Byte.class) ||
               clazzType.equals(Double.class) ||
               clazzType.equals(Float.class) ||
               clazzType.equals(Integer.class) ||
               clazzType.equals(Long.class) ||
               clazzType.equals(Short.class) ||

               clazzType.equals(BigDecimal.class) ||
               clazzType.equals(BigInteger.class);
        // @formatter:on
    }

    public static Object getValueFromProperty(Object object, String property) {
        try {
            String[] propertyArray = property.split("\\.");
            for (int i = 0; i < propertyArray.length; i++) {
                if (object != null) {
                    String getterMethodString = toGetterMethodString(propertyArray[i]);
                    Method method = object.getClass().getMethod(getterMethodString);
                    object = method.invoke(object);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            LOG.error("[E] Exception occurred in [getValueFromProperty]", e);
        }
        return object;
    }

    public static List<?> sortList(List<?> list, String property) {
        return sortList(list, property, true);
    }

    @SuppressWarnings({ "unchecked" })
    public static List<?> sortList(List<?> list, String property, boolean ascendingOrder) {
        if (list == null || list.size() < 2) {
            return list;
        }

        try {
            Collections.sort(list, getComparator(property, ascendingOrder));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
