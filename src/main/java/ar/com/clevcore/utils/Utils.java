package ar.com.clevcore.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Utils {

    private Utils() {
        throw new AssertionError();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Comparator getComparator(final String property, final boolean ascendingOrder) {
        Comparator comparator = new Comparator() {
            public int compare(Object object0, Object object1) {
                int aux = 0;

                try {
                    Comparable comparable0 = (Comparable) getValueFromProperty(object0, property);
                    Comparable comparable1 = (Comparable) getValueFromProperty(object1, property);

                    if (comparable0 != null || comparable1 != null) {
                        if (ascendingOrder) {
                            aux = (comparable0 != null) ? comparable0.compareTo(comparable1 == null ? "" : comparable1)
                                    : 1;
                        } else {
                            aux = (comparable1 != null) ? comparable1.compareTo(comparable0 == null ? "" : comparable0)
                                    : -1;
                        }
                    }
                } catch (Exception e) {
                }
                return aux;
            }
        };

        return comparator;
    }

    public static Map<String, Object> getPropertyValue(Object object, List<String> propertyList, boolean isValueNotNull) {
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
                } catch (Exception e) {
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
            if (propertyList.get(i).equals("*")) {
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
                } catch (Exception e) {
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
        try {
            String[] propertyArray = property.split("\\.");
            for (int i = 0; i < propertyArray.length; i++) {
                String getterMethodString = toGetterMethodString(propertyArray[i]);
                Method method = object.getClass().getMethod(getterMethodString);
                object = method.invoke(object);
            }
        } catch (Exception e) {
        }
        return object;
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
