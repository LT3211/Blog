package com.lt.blog.util;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @program: my-blog
 * @classname: DateUtils
 * @description:
 **/
public class DateUtils {

    /**
     * 获得本地当前时间
     * @param
     */
    public static Timestamp getLocalCurrentDate(){
        return new Timestamp(new Date().getTime());
    }
}
