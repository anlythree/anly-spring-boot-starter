package com.anly.common.utils;

import java.util.List;

public class AnlyUtil {

    /**
     * 判断数组是否为空
     * @param objects
     * @return
     */
    public static Boolean isArrayEmpty(Object[] objects){
        return objects != null && objects.length >0;
    }

    public static Boolean isArrayEmpty(List<?> list){
        return list != null && !list.isEmpty();
    }

}
