package com.tensun.serialtool.serialport;


import java.io.UnsupportedEncodingException;
import java.util.Random;

public class GenerateZigCmdHelper {
    public static final String UPDATE_SAME_TIME = "B1";
    public static final String UPDATE_SAME_MSG = "B3";
    public static final String CANCEL_SAME_TASK = "B5";
    public static final String RESPOND_TASK = "B9";

    /**
     * 多地址更新相同时间
     *
     * @param number
     * @param address
     * @param hour
     * @param minute
     * @return
     */
    public static String updateSameTime(int number, String address, int hour, int minute) {
        StringBuilder cmdBuilder = new StringBuilder();
        String numberStr = format2String(number);
        String addressStr = address.replaceAll("\\$", "");
        String hourStr = format2String(hour);
        String minuteStr = format2String(minute);
        String lengthStr = format2String(number * 2 + 5);
        String snStr = generateSn();
        cmdBuilder.append("FE").append(lengthStr).append(UPDATE_SAME_TIME).append(snStr).append(numberStr)
                .append(addressStr).append(hourStr).append(minuteStr).append("55");
        return cmdBuilder.toString();
    }

    /**
     * 多手表发送相同信息
     *
     * @param number
     * @param address
     * @param taskId
     * @param msg
     * @return
     */
    public static String updateSameMsg(int number, String address, String taskId, String msg) {
        StringBuilder cmdBuilder = new StringBuilder();
        String numberStr = format2String(number);
        String addressStr = address.replaceAll("\\$", "");
        String msgStr = null;
        try {
            msgStr = ByteUtil.bytesToHexString(msg.getBytes("gbk"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String lengthStr = format2String(6 + (number * 2) + (msgStr.length() / 2));
        String snStr = generateSn();
        String taskIdStr = taskId;
        cmdBuilder.append("FE").append(lengthStr).append(UPDATE_SAME_MSG).append(snStr).append(numberStr)
                .append(addressStr).append(taskIdStr).append(msgStr).append("55");
        return cmdBuilder.toString();
    }

    /**
     * 多地址取消相同任务ID
     *
     * @param number
     * @param address
     * @param taskId
     * @return
     */
    public static String cancelTask(int number, String address, String taskId) {
        StringBuilder cmdBuilder = new StringBuilder();
        String numberStr = format2String(number);
        String addressStr = address.replaceAll("\\$", "");
        String lengthStr = format2String(6 + (number * 2));
        String snStr = generateSn();
        return cmdBuilder.append("FE").append(lengthStr).append(CANCEL_SAME_TASK)
                .append(snStr).append(numberStr).append(addressStr).append(taskId).append("55").toString();
    }

    /**
     * 响应单只手表信息
     *
     * @param address
     * @param taskId
     * @return
     */
    public static String respondTask(String address, String taskId) {
        StringBuilder cmdBuilder = new StringBuilder();
        return cmdBuilder.append("FE07").append(RESPOND_TASK).append(generateSn())
                .append(address).append(taskId).append("55").toString();
    }

    /**
     * 根据内容计算群发手表个数
     *
     * @param message
     * @return
     */
    public static int calculateWatchCountByMessage(String message) {
        int count = 0;
        try {
            count = (74 - message.getBytes("gbk").length) / 2;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static int calculateWatchCountByCancel() {
        int count = 74 / 2;
        return count;
    }

    /**
     * 数字格式化成2位十六进制字符串，不足补零
     *
     * @param number
     * @return
     */
    public static String format2String(int number) {
        return String.format("%02X", number);
    }

    /**
     * 生成随机序号
     *
     * @return 序号
     */
    public static String generateSn() {
        return String.format("%02X", new Random().nextInt(255));
    }

    /**
     * 生成随机任务
     *
     * @return 序号
     */
    public static String generateTaskId() {
        return String.format("%06X", new Random().nextInt(16777213));
    }
}
