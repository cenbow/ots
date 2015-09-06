package com.mk.framework.util;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @Title Cast.java 动态实体
 * @author zhangliang
 * @Description: 在平台主要作为方法间进行数据传输.
 * @version $Rev: 2643 $
 * @LastModify: $Id: Cast.java 2643 2012-11-23 05:29:36Z zhangl $
 */
public class Cast {
    public static final String MODULE = Cast.class.getName();

    public static long to(Object obj, long def){
    	if (!CommonUtils.isNull(obj)) {
            long rtnVal = def;
            if (obj instanceof Number) {
                rtnVal = ((Number)obj).longValue();
            } else if (obj instanceof Boolean) {
                rtnVal = ((Boolean)obj).booleanValue() ? 1 : 0;
            } else if (obj instanceof Date) {
                rtnVal = (int)((Date)obj).getTime();
            } else {
                String str = obj.toString();
                if (CommonUtils.isNullOrEmpty(str)) {
                    return def;
                }
                try {
                    rtnVal = Long.parseLong(str);
                } catch (NumberFormatException e) {
                    try {
                        rtnVal = (new Long(Long.parseLong(str))).longValue();
                    } catch (NumberFormatException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return rtnVal;
        } else {
            return def;
        }
    }
    /**
     * 转型为整型
     * 
     * @param obj 原始对象
     * @param def 缺省值
     * @return 整型
     */
    public static int to(Object obj, int def) {
        if (!CommonUtils.isNull(obj)) {
            int rtnVal = def;
            if (obj instanceof Number) {
                rtnVal = ((Number)obj).intValue();
            } else if (obj instanceof Boolean) {
                rtnVal = ((Boolean)obj).booleanValue() ? 1 : 0;
            } else if (obj instanceof Date) {
                rtnVal = (int)((Date)obj).getTime();
            } else {
                String str = obj.toString();
                if (CommonUtils.isNullOrEmpty(str)) {
                    return def;
                }
                try {
                    rtnVal = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    try {
                        rtnVal = (new Double(Double.parseDouble(str))).intValue();
                    } catch (NumberFormatException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return rtnVal;
        } else {
            return def;
        }
    }

    /**
     * 转型为双精度浮点型
     * 
     * @param obj 原始对象
     * @param def 缺省值
     * @return 双精度浮点型
     */
    public static double to(Object obj, double def) {
        if (!CommonUtils.isNull(obj)) {
            if (obj instanceof Double) {
                return ((Double)obj).doubleValue();
            } else if (obj instanceof Float) {
                return ((Float)obj).doubleValue();
            } else if (obj instanceof Integer) {
                return ((Integer)obj).intValue();
            } else if (obj instanceof Long) {
                return ((Long)obj).doubleValue();
            } else if (obj instanceof Boolean) {
                return ((Boolean)obj).booleanValue() ? 1 : 0;
            } else if (obj instanceof Date) {
                return ((Date)obj).getTime();
            } else {
                try {
                    return Double.parseDouble(obj.toString());
                } catch (Exception e) {
                    return def;
                }
            }
        } else {
            return def;
        }
    }

    /**
     * 转型为布尔值
     * 
     * @param obj 原始对象
     * @param def 缺省值
     * @return 布尔值
     */
    public static boolean to(Object obj, boolean def) {
        if (!CommonUtils.isNull(obj)) {
            if (obj instanceof Boolean) {
                return ((Boolean)obj).booleanValue();
            } else if (obj instanceof Integer) {
                return ((Integer)obj).intValue() == 1;
            } else if (obj instanceof Long) {
                return ((Long)obj).longValue() == 1;
            } else if (obj instanceof Double) {
                return ((Double)obj).doubleValue() == 1;
            } else if (obj instanceof Date) {
                return ((Date)obj).getTime() == 1;
            } else {
                String str = obj.toString().toUpperCase();
                return str.equalsIgnoreCase("TRUE") || str.equalsIgnoreCase("YES")
                       || str.equals("1");
            }
        } else {
            return def;
        }
    }

    /**
     * 转型为字符串
     * 
     * @param obj 原始对象
     * @param def 缺省值
     * @return 字符串
     */
    public static String to(Object obj, String def) {
        if (!CommonUtils.isNull(obj) && !CommonUtils.isNullOrEmpty(obj.toString())) {
            return obj.toString();
        } else {
            return def;
        }
    }

    /**
     * 将数据实体转化为指定的类对象实例
     * 
     * @param result 数据
     * @param classType 要转成的类
     * @return 转化后的类对象实例
     */
    @SuppressWarnings("unchecked")
    public static Object to(Object result, Class classType) {
        Object rtnObj = null;
        // 1.获取参数接口类或自身类
        Class[] interFace = result.getClass().getInterfaces();
        Class[] argsCls = new Class[interFace.length + 1];
        for (int i = 0; i < argsCls.length; i++ ) {
            if (i == 0) {
                argsCls[i] = result.getClass();
            } else {
                argsCls[i] = interFace[i - 1];
            }
            // 2.根据类名称进行类实例化

            try {
                Constructor<?> constructor = classType.getConstructor(argsCls[i]);
                rtnObj = constructor.newInstance(result);
                break;
            } catch (SecurityException e) {
                continue;
            } catch (IllegalArgumentException e) {
                continue;
            } catch (NoSuchMethodException e) {
                continue;
            } catch (InstantiationException e) {
                continue;
            } catch (IllegalAccessException e) {
                continue;
            } catch (InvocationTargetException e) {
                continue;
            }

        }
        return rtnObj;
    }

    /**
     * 将数据实体集合转化为指定的类对象实例
     * 
     * @param resultList 数据集合
     * @param classType 要转成的类
     * @return 转化后的类对象实例
     */
    @SuppressWarnings("unchecked")
    public static List transfer(List resultList, Class classType) {
        List rtnList = null;
        if (!CommonUtils.isNullOrEmpty(resultList)) {
            rtnList = new ArrayList();
            for (Object obj : resultList) {
                rtnList.add(to(obj, classType));
            }
        }
        return rtnList;
    }
}