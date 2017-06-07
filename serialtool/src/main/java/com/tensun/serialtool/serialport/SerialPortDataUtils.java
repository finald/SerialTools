package com.tensun.serialtool.serialport;


import java.util.Iterator;
import java.util.LinkedList;

public class SerialPortDataUtils {

    /**
     * 过滤指定时间内相同数据
     *
     * @param cacheList  缓存List
     * @param str        接收字符串
     * @param time 指定时间 单位毫秒
     * @return false or true
     */
    public static boolean filterData(LinkedList<String> cacheList, String str, int time) {
        Iterator iterator = cacheList.iterator();
        while (iterator.hasNext()) {
            String[] cache = ((String) iterator.next()).split("\\$");
            long now = System.currentTimeMillis();
            long ago = Long.parseLong(cache[1]);
            if (cache[0].equals(str)) {
                if ((now - ago) > time) {
                    iterator.remove();
                    cacheList.addLast(str + "$" + now);
                    return false;
                } else
                    return true;
            }
        }
        cacheList.addLast(str + "$" + System.currentTimeMillis());
        return false;
    }

}
