package com.hc360.score.utils;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/10/14.
 */
public class EncodeUtil {

    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                return s;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }

        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }

    public static String getEncoding2(String str) {
        Pattern utf8Pattern = Pattern.compile("^([\\x01-\\x7f]|[\\xc0-\\xdf][\\x80-\\xbf]|[\\xe0-\\xef][\\x80-\\xbf]{2}|[\\xf0-\\xf7][\\x80-\\xbf]{3}|[\\xf8-\\xfb][\\x80-\\xbf]{4}|[\\xfc-\\xfd][\\x80-\\xbf]{5})+$");
        Pattern publicPattern = Pattern.compile("^([\\x01-\\x7f]|[\\xc0-\\xdf][\\x80-\\xbf])+$");
        Matcher publicMatcher = publicPattern.matcher(str);
        if(publicMatcher.matches()) {
            return "GBK";
        }

        Matcher matcher = utf8Pattern.matcher(str);
        if (matcher.matches()) {
            return "UTF-8";
        } else {
            return "GBK";
        }
    }


    public static boolean isErrCode(String str) {
        //Pattern utf8Pattern = Pattern.compile("[^\\p{ASCII}]+");
        Pattern utf8Pattern = Pattern.compile("[^\\u0000-\\u007F\\u4e00-\\u9fa5\\u3002\\uff1f\\uff01\\uff0c\\u3001\\uff1b\\uff1a\\u201c\\u201d\\u2018\\u2019\\uff08\\uff09\\u300a\\u300b\\u3008\\u3009\\u3010\\u3011\\u300e\\u300f\\u300c\\u300d\\ufe43\\ufe44\\u3014\\u3015\\u2026\\u2014\\uff5e\\ufe4f\\uffe5]+");
        //Pattern utf8Pattern = Pattern.compile("[\\u3002\\uff1f\\uff01\\uff0c\\u3001\\uff1b\\uff1a\\u201c\\u201d\\u2018\\u2019\\uff08\\uff09\\u300a\\u300b\\u3008\\u3009\\u3010\\u3011\\u300e\\u300f\\u300c\\u300d\\ufe43\\ufe44\\u3014\\u3015\\u2026\\u2014\\uff5e\\ufe4f\\uffe5]+");


        Matcher matcher = utf8Pattern.matcher(str);
        if (matcher.find()) {
            System.out.println(matcher.group());
            return true;
        } else {
            return false;
        }
    }

    public static String guessEncoding(byte[] bytes) {
        //String DEFAULT_ENCODING = "UTF-8";
        String DEFAULT_ENCODING = "GBK";
        org.mozilla.universalchardet.UniversalDetector detector =
                new org.mozilla.universalchardet.UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }
        return encoding;
    }


    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = 0 ;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
                chLength++;
            }
        }
        float result = count / chLength ;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }
    }



    public static void main(String[] args) throws Exception{
        /*System.out.println("你好"+","+getEncoding("你好"));
        System.out.println(new String("你好".getBytes("GBK"),"utf-8")+","+getEncoding(new String("你好".getBytes("GBK"),"utf-8")));
        System.out.println(getEncoding(new String(new String("你好".getBytes("GBK"),"utf-8").getBytes())));*/
        /*System.out.println(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode("你好"));
        System.out.println(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(new String("你好".getBytes("GBK"),"utf-8")));*/

        /*System.out.println(getEncoding2("ss中国dd"));
        System.out.println(getEncoding2(new String("ss中国dd".getBytes("utf-8"),"utf-8")));*/

        String introduce1="<p>\n" +
                "\t<br />\n" +
                "</p>\n" +
                "<p style=\"padding:0px;margin-top:0px;margin-bottom:0px;color:#333333;font-family:微软雅黑;font-size:14px;line-height:25px;white-space:normal;\">\n" +
                "\t<br />\n" +
                "</p>\n" +
                "<p style=\"padding:0px;margin-top:0px;margin-bottom:0px;color:#333333;font-family:微软雅黑;font-size:14px;line-height:25px;white-space:normal;\">\n" +
                "\t<img alt=\"三头焊锡机.jpg\" width=\"500\" height=\"649\" src=\"http://www.shjjzk.com/d/file/Prducts/1/2013-06-26/24c70d52ee4674bacc97848f41b8a319.jpg\" style=\"padding:0px;margin:0px;\" /> \n" +
                "</p>\n" +
                "<table border=\"0\" cellspacing=\"1\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#cccccc\" style=\"padding:0px;margin:0px;color:#333333;font-family:微软雅黑;font-size:14px;line-height:25px;\" class=\"ke-zeroborder\">\n" +
                "\t<colgroup style=\"padding:0px;margin:0px;\"><col span=\"2\" width=\"237\" style=\"padding:0px;margin:0px;\" /></colgroup>\n" +
                "\t<tbody style=\"padding:0px;margin:0px;\">\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">型号（Models):</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;J-SP-4T300\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">X轴 Y轴运行范围mm:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;X=200&nbsp; Y=200\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">Z轴Ｒ Ｒ轴运行范围mm:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;Z=100&nbsp; R=360°\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">重复精度mm:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;±0.01\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">解析能力mm:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;0.001\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">运动速度mm/s:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;500*500*400*360°\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">文件存储容量:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;至少1000组、每组9999字节\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">文件储存方式:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;内存存储\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">显示方式:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;示教盒LCD\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">控制方式:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;高速32位DSP控制\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">编程方式:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;手持式示教编程器\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">温度设定范围:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;0℃-500℃\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">温度控制精度:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;±5℃\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">温控系统功率:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;150W，瞬时180Ｗ\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">锡丝直径范围:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;∮0.3-∮1.2mm\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">烙铁头清洗系统:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;自动气压清洗\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">空气压力:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;4-5kg\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">输入电源:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;380V\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">工作环境温度:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;10-50℃\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">工作环境湿度:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;20%-90%no condensation\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">机器尺寸W×D×Hmm:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" width=\"1103\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;1200X1100X1650\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr height=\"31\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" height=\"31\" width=\"268\" align=\"right\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t<strong style=\"padding:0px;margin:0px;\">机器重量kg:</strong> \n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td bgcolor=\"#ffffff\" style=\"padding:0px;margin:0px;\">\n" +
                "\t\t\t\t&emsp;300\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody>\n" +
                "</table>\n" +
                "<p>\n" +
                "\t<br />\n" +
                "</p>";


        String introduce2="<div><p><span style=\"font-size: small;\"><span style=\"font-size: 12.0pt;\">浜у搧鍚嶇О锛氬叏閾滄礂杞︽按鏋?楂樺帇姘存灙澶?娓呮礂宸ュ叿 楂樺帇姘存灙 姹借溅宸ュ叿30m 濂楄</span></span></p><p><span style=\"font-size: 12.0pt;\">浜у搧閰嶄欢锛氬叏閾滄按鏋?鏋紝閾滈?姘存帴澶?涓紝閾滄姘存帴澶?涓紝浜屼唬閾滀竾鑳芥帴澶?涓紝娴峰叞姘寸30M</span></p><p><span style=\"font-size: 12.0pt;\">缁勫悎閲嶉噺锛?.45Kg</span></p><p><span style=\"font-size: 12.0pt;\">浜у搧鐗圭偣锛氬叏閾滃瑁咃紝楂樺搧璐ㄨ?鐢紝浣跨敤瀵垮懡闀匡紱3.5KG姘存按鍘嬶紝鍠峰皠鍔?0绫冲乏鍙炽?</span></p><p><span style=\"font-size: 12.0pt;\">浜у搧鍔熺敤锛?/span></p><p><span style=\"font-size: 12.0pt;\">1銆佹竻娲楁苯杞︺?鎽╂墭杞︺?鐢靛姩杞︾瓑鍚勭杞︼紱</span></p><p><span style=\"font-size: 12.0pt;\">2銆佸灞呭ぇ鎵櫎锛屾竻娲楀崼鐢熼棿銆佸帹鎴裤?闃冲彴銆佽繃閬撱?绐楁埛銆佸湴鏉跨瓑锛?/span></p><p><span style=\"font-size: 12.0pt;\">3銆佸搴崏鍧?鑺卞洯鍠锋磼銆傞槼鍙扮泦鑺辨穻姘淬?闄嶅皹锛?/span></p><p><span style=\"font-size: 12.0pt;\"></span></p>";

        String introduce3="<div id=\"offer-template-0\"></div><p><img align=\"absMiddle\" src=\"http://img007.hc360.cn/hb/MTQ2OTg2Njc2NDk5MzExNTY4ODM0MTU=.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img001.hc360.cn/hb/MTQ2OTg2NjYyNTI4MjE5ODM1MTM3Nzg=.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img009.hc360.cn/hb/MTQ2OTg2NjUxMTQ2OTEzMjMzNTk4MzQ=.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img006.hc360.cn/hb/MTQ2OTg2NjUyMzA3MTMwNDM5OTg5Nw==.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img000.hc360.cn/hb/MTQ2OTg2NjUxMTc0NC0xMzU5OTYwODE1.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img007.hc360.cn/hb/MTQ2OTg2NjUzNjExNDE2NjEwNDg3NTc=.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img011.hc360.cn/hb/MTQ2OTg2NjUyMzg4NjU4NzM2NjE5OQ==.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img006.hc360.cn/hb/MTQ2OTg2NjUxMjIxNjIxOTc2MTQ4Mg==.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img000.hc360.cn/hb/MTQ2OTg2NjUyMTUxNDE0ODg0MjQ5MTU=.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img002.hc360.cn/hb/MTQ2OTg2NjUwMTAwMS0yMTEyMTYzOTc2.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img011.hc360.cn/hb/MTQ2OTg2NjU2MTc4MS00NzkzNTgzMjM=.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img004.hc360.cn/hb/MTQ2OTg2NjU0MDE2Mi04MTMxMTc3MzA=.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img011.hc360.cn/hb/MTQ2OTg2NjU1MDQyNDE3NTk1MzU0MTA=.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img004.hc360.cn/hb/MTQ2OTg2NjUyNzc5Ni0xMzYyNDMxNjI5.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img000.hc360.cn/hb/MTQ2OTg2NjQ5MzQ0NjIwOTg1ODQ1Mjg=.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img007.hc360.cn/hb/MTQ2OTg2NjU0NzkwMS0xNDA0ODc2MjI1.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img006.hc360.cn/hb/MTQ2OTg2NjU0NTIwMzI1MDAzNDIwNA==.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img011.hc360.cn/hb/MTQ2OTg2NjUzMzk4OC0yMDEyNjU1MjM2.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img006.hc360.cn/hb/MTQ2OTg2NjU1NDY3NC05ODg0NTk5Nw==.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img000.hc360.cn/hb/MTQ2OTg2NjU2Nzg2MjgzNjU3NDA3Ng==.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img004.hc360.cn/hb/MTQ2OTg2NjU3OTkxOS0xNzU1NjcyMDQ5.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img004.hc360.cn/hb/MTQ2OTg2NjU5NDU2MDg2NzkxMjM3Nw==.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img011.hc360.cn/hb/MTQ2OTg2NjUzMTgyOC0xNzExODY2NTgz.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img009.hc360.cn/hb/MTQ2OTg2NjU4NDE0OTE3OTM5NzQ1NTg=.jpg\" style=\"max-width: 750.0px;\" /><img align=\"absMiddle\" src=\"http://img006.hc360.cn/hb/MTQ2OTg2NjU3OTkyMzM4NTU1ODg0Mw==.jpg\" style=\"max-width: 750.0px;\" /></p><p>&nbsp;</p><p>&nbsp;</p></div></div></div></div>\n";

        /*System.out.println(new String("ss中国dd".getBytes("utf-8"),"GBK"));
        System.out.println(isErrCode(introduce5));
        System.out.println(isErrCode("<div><p><span style=\"font-size: small;\"><span style=\"font-size: 12.0pt;\">浜у搧鍚嶇О锛氬叏閾滄礂杞︽按鏋?楂樺帇姘存灙澶?娓呮礂宸ュ叿 楂樺帇姘存灙 姹借溅宸ュ叿30m 濂楄</span></span></p><p><span style=\"font-size: 12.0pt;\">浜у搧閰嶄欢锛氬叏閾滄按鏋?鏋紝閾滈?姘存帴澶?涓紝閾滄姘存帴澶?涓紝浜屼唬閾滀竾鑳芥帴澶?涓紝娴峰叞姘寸30M</span></p><p><span style=\"font-size: 12.0pt;\">缁勫悎閲嶉噺锛?.45Kg</span></p><p><span style=\"font-size: 12.0pt;\">浜у搧鐗圭偣锛氬叏閾滃瑁咃紝楂樺搧璐ㄨ?鐢紝浣跨敤瀵垮懡闀匡紱3.5KG姘存按鍘嬶紝鍠峰皠鍔?0绫冲乏鍙炽?</span></p><p><span style=\"font-size: 12.0pt;\">浜у搧鍔熺敤锛?/span></p><p><span style=\"font-size: 12.0pt;\">1銆佹竻娲楁苯杞︺?鎽╂墭杞︺?鐢靛姩杞︾瓑鍚勭杞︼紱</span></p><p><span style=\"font-size: 12.0pt;\">2銆佸灞呭ぇ鎵櫎锛屾竻娲楀崼鐢熼棿銆佸帹鎴裤?闃冲彴銆佽繃閬撱?绐楁埛銆佸湴鏉跨瓑锛?/span></p><p><span style=\"font-size: 12.0pt;\">3銆佸搴崏鍧?鑺卞洯鍠锋磼銆傞槼鍙扮泦鑺辨穻姘淬?闄嶅皹锛?/span></p><p><span style=\"font-size: 12.0pt;\"></span></p>"));
        System.out.println(isErrCode("∮"));*/
        //匹配这些中文标点符号 。 ？ ！ ， 、 ； ： “ ” ‘ ' （ ） 《 》 〈 〉 【 】 『 』 「 」 ﹃ ﹄ 〔 〕 … — ～ ﹏ ￥
        //var reg = /[\u3002|\uff1f|\uff01|\uff0c|\u3001|\uff1b|\uff1a|\u201c|\u201d|\u2018|\u2019|\uff08|\uff09|\u300a|\u300b|\u3008|\u3009|\u3010|\u3011|\u300e|\u300f|\u300c|\u300d|\ufe43|\ufe44|\u3014|\u3015|\u2026|\u2014|\uff5e|\ufe4f|\uffe5]/
        //System.out.println(isErrCode("уО︽。？！，、；：“”‘’（）《》〈〉【】『』「」﹃﹄〔〕…—～﹏￥"));

        /*System.out.println("org-----------"+introduce1);
        System.out.println(guessEncoding(introduce1.getBytes()));
        System.out.println("gbk-----------"+new String(introduce1.getBytes(), guessEncoding(introduce1.getBytes())));
        System.out.println(guessEncoding(introduce2.getBytes()));
        System.out.println("utf-8-----------"+new String(introduce2.getBytes(),"utf-8"));*/

        /*System.out.println(getEncoding(new String(introduce1.getBytes())));
        System.out.println(getEncoding(new String(introduce2.getBytes())));

        System.out.println(guessEncoding(introduce1.getBytes()));
        System.out.println(guessEncoding(introduce2.getBytes()));*/

        /*System.out.println( System.getProperty("file.encoding"));
        System.out.println(getEncoding(introduce3));
        System.out.println(guessEncoding(introduce3.getBytes()));*/

        //结果不对
        /*System.out.println(Charset.forName("GBK").newEncoder().canEncode("测试"));
        System.out.println(Charset.forName("GBK").newEncoder().canEncode("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<recoveryLog xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><logFileName>error028280_01.xml</logFileName><summary>鍦ㄦ枃浠垛€淓:\\export\\businintro\\busin_intro_20161125_1.xlsx鈥濅腑妫€娴嬪埌閿欒\uE1E4</summary><repairedRecords><repairedRecord>宸蹭慨澶嶇殑璁板綍: /xl/worksheets/sheet1.xml 閮ㄥ垎鐨?瀛楃\uE0C1涓插睘鎬?/repairedRecord></repairedRecords></recoveryLog>"));
        System.out.println(Charset.forName("GBK").newEncoder().canEncode("é??"));*/


        System.out.println(isMessyCode("测试"));
        System.out.println(isMessyCode("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?>\\n\" +\n" +
                "                \"<recoveryLog xmlns=\\\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\\\"><logFileName>error028280_01.xml</logFileName><summary>鍦ㄦ枃浠垛€淓:\\\\export\\\\businintro\\\\busin_intro_20161125_1.xlsx鈥濅腑妫€娴嬪埌閿欒\\uE1E4</summary><repairedRecords><repairedRecord>宸蹭慨澶嶇殑璁板綍: /xl/worksheets/sheet1.xml 閮ㄥ垎鐨?瀛楃\\uE0C1涓插睘鎬?/repairedRecord></repairedRecords></recoveryLog>"));
        System.out.println(isMessyCode("é??"));
        System.out.println(isMessyCode("鍦ㄦ枃浠垛€淓"));


    }

}
