package com.tensun.serialtool;

import java.util.Arrays;
import java.util.Locale;


public class ByteUtil {


    /**
     * 设定长度的byte数组转换成16进制字符串
     *
     * @param bytes  byte[]
     * @param length int
     * @return null or string
     */
    public static String bytesToHexString(byte[] bytes, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase(Locale.ENGLISH);
    }


    /**
     * byte数组转换成字符串
     *
     * @param bytes byte[]
     * @return null or string
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase(Locale.ENGLISH);
    }

    /**
     * 字符0-F转换成字节
     *
     * @param ch char
     * @return -1 or byte
     */
    private static byte charToByte(char ch) {
        return (byte) "0123456789ABCDEF".indexOf(ch);
    }


    /**
     * 16进制字符串转换成byte数组
     *
     * @param hexString String
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase(Locale.ENGLISH);
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            bytes[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return bytes;
    }


    /**
     * 拼接字节数组
     *
     * @param one byte[]
     * @param two byte[]
     * @return byte[]
     */
    public static byte[] appendByte(byte[] one, byte[] two) {
        if (one == null)
            one = new byte[0];
        byte[] bytes = new byte[one.length + two.length];
        System.arraycopy(one, 0, bytes, 0, one.length);
        System.arraycopy(two, 0, bytes, one.length, two.length);
        return bytes;
    }

    /**
     * 一字节拼接到字节数组里
     *
     * @param bytes byte[]
     * @param abyte byte
     * @return byte[]
     */
    public static byte[] appendByte(byte[] bytes, byte abyte) {
        if (bytes == null)
            bytes = new byte[0];
        bytes = Arrays.copyOf(bytes, bytes.length + 1);
        bytes[bytes.length - 1] = abyte;
        return bytes;
    }
}
