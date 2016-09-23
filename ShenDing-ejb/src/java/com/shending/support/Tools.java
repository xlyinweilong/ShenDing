/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support;

import com.shending.config.Config;
import flexjson.JSONSerializer;
import java.awt.Color;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 工具类
 *
 * @author yin.weilong
 */
public class Tools {

    public static void toMp3(String webroot, String sourcePath) {
        //File file = new File(sourcePath);  
        String targetPath = sourcePath + ".mp3";//转换后文件的存储地址，直接将原来的文件名后加mp3后缀名  
        Runtime run = null;
        try {
            run = Runtime.getRuntime();
            long start = System.currentTimeMillis();
            Process p = run.exec(webroot + "files/ffmpeg -i " + webroot + sourcePath + " -acodec libmp3lame " + webroot + targetPath);//执行ffmpeg.exe,前面是ffmpeg.exe的地址，中间是需要转换的文件地址，后面是转换后的文件地址。-i是转换方式，意思是可编码解码，mp3编码方式采用的是libmp3lame  
            //释放进程    
            p.getOutputStream().close();
            p.getInputStream().close();
            p.getErrorStream().close();
            p.waitFor();
            long end = System.currentTimeMillis();
            System.out.println(sourcePath + " convert success, costs:" + (end - start) + "ms");
            //删除原来的文件    
            //if(file.exists()){    
            //file.delete();    
            //}    
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //run调用lame解码器最后释放内存    
            run.freeMemory();
        }
    }

    /**
     * 解析请求的body
     *
     * @param request
     * @return
     */
    public static String parsingRequest(HttpServletRequest request) {
        int size = request.getContentLength();
        InputStream is = null;
        try {
            is = request.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] reqBodyBytes = readBytes(is, size);
        String res = new String(reqBodyBytes);
        return res;
    }

    /**
     * 读取InputStream流
     *
     * @param is
     * @param contentLen
     * @return
     */
    public static final byte[] readBytes(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;
            int readLengthThisTime = 0;
            byte[] message = new byte[contentLen];
            try {
                while (readLen != contentLen) {
                    readLengthThisTime = is.read(message, readLen, contentLen
                            - readLen);
                    if (readLengthThisTime == -1) {// Should not happen.
                        break;
                    }
                    readLen += readLengthThisTime;
                }
                return message;
            } catch (IOException e) {
                // Ignore
                e.printStackTrace();
            }
        }
        return new byte[]{};
    }

    /**
     * 通过HttpServletRequest解析请求是否来自手机
     *
     * @param request
     * @return
     */
    public static boolean parsingRequestIsFromMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        System.out.println(userAgent);
        if (Tools.isBlank(userAgent)) {
            return false;
        }
        Pattern pattern = Pattern.compile(".*(iPad|iPhone|Android).*");
        Matcher matcher = pattern.matcher(userAgent);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 判断浏览器内核是否为8.9
     *
     * @param request
     * @return
     */
    public static boolean parsingRequestIsFromLowIe9(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (Tools.isBlank(userAgent)) {
            return false;
        }
        Pattern pattern = Pattern.compile(".*(Trident/4.0|Trident/5.0).*");
        Matcher matcher = pattern.matcher(userAgent);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 生成随机颜色
     *
     * @param fc
     * @param bc
     * @return
     */
    public static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public static String getBASE64(String s) {
        if (s == null) {
            return null;
        }
        return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
    }

// 将 BASE64 编码的字符串 s 进行解码 
    public static String getFromBASE64(String s) {
        if (s == null) {
            return null;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * trim to null
     *
     * @param str
     * @return
     */
    public static String trimToNull(String str) {
        return StringUtils.trimToNull(str);
    }

    public static String getString(String str) {
        return str == null ? "" : str;
    }

    /**
     * 将字符串转为图片
     *
     * @param imgStr
     * @return
     */
    public static boolean generateImage(String imgStr, String imgFile) throws Exception {// 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) {// 图像数据为空
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            // 生成jpeg图片
            String imgFilePath = imgFile;// 新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取一个有默认值的MAP
     *
     * @return
     */
    public static Map<String, Object> getDMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("success", "0");
        map.put("msg", null);
        return map;
    }

    /**
     * 计算时间差，返回中文
     *
     * @param date
     * @return
     */
    public static String calculateDate(Date date) {
        Date now = new Date();
        long between = (now.getTime() - date.getTime()) / 1000;//除以1000是为了转换成秒
        long year = between / (24 * 60 * 60 * 30 * 12);
        long month = between / (24 * 60 * 60 * 30);
        long day = between / (24 * 3600);
        long hour = between % (24 * 3600) / 3600;
        long minute = between % 3600 / 60;
        long second = between % 60 / 60;
        if (year > 0) {
            return year + "年前";
        } else if (month > 0) {
            return month + "个月前";
        } else if (day > 0) {
            return day + "天前";
        } else if (hour > 0) {
            return hour + "小时前";
        } else if (minute > 0) {
            return minute + "分钟前";
        } else if (second >= 0) {
            return second + " 秒前";
        } else {
            return "未来信息";
        }
    }

    /**
     * 计算时间差，返回中文
     *
     * @param date
     * @return
     */
    public static String calculateDistance(Date date) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTime(now);
        long second = (now.getTime() - date.getTime()) / 1000;//时间差多少秒
        long hour = second / (60 * 60);
        long minute = second / 60;
        if (second == 0) {
            return "刚刚";
        } else if (minute < 1) {
            return second + " 秒前";
        } else if (hour < 1) {
            return minute + "分钟前";
        } else if (calendar.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR)) {
            if (calendar.get(Calendar.DAY_OF_YEAR) + 1 == calendarNow.get(Calendar.DAY_OF_YEAR)) {
                return "昨天 " + Tools.formatDate(date, "HH:mm");
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == calendarNow.get(Calendar.DAY_OF_YEAR)) {
                return Tools.formatDate(date, "HH:mm");
            } else {
                return Tools.formatDate(date, "yyyy-MM-dd");
            }
        } else {
            //昨天，跨年
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            if (calendar.get(Calendar.DAY_OF_YEAR) == calendarNow.get(Calendar.DAY_OF_YEAR)) {
                return "昨天 " + Tools.formatDate(date, "HH:mm");
            } else {
                return Tools.formatDate(date, "yyyy-MM-dd");
            }
        }
    }

    /**
     * 判断是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (Tools.isBlank(str)) {
            return false;
        }
        return StringUtils.isNumeric(str.trim());
    }

    /**
     * 输出MAP为JSON格式
     *
     * @param map
     * @return
     */
    public static String getJsonStringByMap(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean flag = false;
        for (Entry<String, String> entry : map.entrySet()) {
            if (flag) {
                sb.append(",");
            } else {
                flag = true;
            }
            sb.append("\"").append(entry.getKey()).append("\":" + "\"").append(entry.getValue().toString()).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 深度转化object为json
     *
     * @param o
     * @return
     */
    public static String caseObjectToJson(Object o) {
        JSONSerializer serializer = new JSONSerializer();
        return serializer.deepSerialize(o);
    }

    public static void outputServletResponse(HttpServletResponse response, String text) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println(text);
        } finally {
            out.close();
        }
    }

    public static void outputServletResponse(HttpServletResponse response, Throwable ex) throws IOException {
        String stackTrace = getStackTrace(ex);
        outputServletResponse(response, stackTrace);
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }
    //String tools

    public static String reverse(String str) {
        StringBuilder sb = new StringBuilder(str);
        sb.reverse();
        return sb.toString();
    }

    public static String[] getSplit(String src, String split) {
        return src.split(split);
    }

    public static void generateHTMLFile(String fileFullPath, String content) throws IOException {
        File file = new File(fileFullPath);
        FileUtils.writeStringToFile(file, content, "UTF-8");
    }
    //Format tools

    public static String getEscapedBrHtml(String str) {
        str = StringEscapeUtils.escapeHtml(str);
        str = StringUtils.replace(str, "\n", " <br/>");
        return str;
    }

    public static String getEscapedHtml(String str) {
        return StringEscapeUtils.escapeHtml(str);
    }

    public static String getUnEscapedHtml(String str) {
        return StringEscapeUtils.unescapeHtml(str);
    }

    public static String formatCurrency(Number number) {
        DecimalFormat dFormat = new DecimalFormat();
        dFormat.applyPattern("#,##0.##");
        return dFormat.format(number);
    }

    public static Number parseCurrency(String str) throws ParseException {
        DecimalFormat dFormat = new DecimalFormat();
        dFormat.applyPattern("#,##0.##");
        return dFormat.parse(str);
    }

    public static String formatCurrency(Number number, Locale locale) {
        DecimalFormat df = new DecimalFormat("¤#,###", DecimalFormatSymbols.getInstance(locale));
        return df.format(number);
    }

    /**
     * 格式化货币： 123456 --> ￥123,456
     *
     * @param number
     * @return
     */
    public static String formatRMBCurrency(Number number) {
        return formatCurrency(number, Locale.CHINA);
    }

    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static BigDecimal round(BigDecimal bd, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal one = new BigDecimal("1");
        return bd.divide(one, scale, BigDecimal.ROUND_HALF_UP);
    }
    //Validate tools

    public static boolean isNotNullAndNotEmpty(List list) {
        if (list != null && !list.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String[] src) {
        if (null == src) {
            return false;
        }
        return src.length > 0 ? true : false;
    }

    public static boolean isNotBlank(String src) {
        return StringUtils.isNotBlank(src);
    }

    public static boolean isBlank(String src) {
        return StringUtils.isBlank(src);
    }

    public static boolean isNotBlankAndEqTrue(String src) {
        if (StringUtils.isBlank(src)) {
            return false;
        }
        if ("true".equalsIgnoreCase(src)) {
            return true;
        }
        return false;
    }

    public static boolean isNotNullAndGtZero(Integer target) {
        if (null == target || target.intValue() <= 0) {
            return false;
        }
        return true;
    }

    public static boolean isNotBlankAndEqStr(String src, String eq) {
        boolean result = StringUtils.isNotBlank(src);
        if (result) {
            if (!src.trim().equalsIgnoreCase(eq)) {
                result = false;
            }
        }
        return result;
    }

    public static boolean isNotBlankAndNotEqStr(String src, String eq) {
        boolean result = StringUtils.isNotBlank(src);
        if (result) {
            if (src.trim().equalsIgnoreCase(eq)) {
                result = false;
            }
        }
        return result;
    }

    public static boolean isEmail(String src) {
        return EmailValidator.getInstance().isValid(src);
    }

    public static boolean isMobilePhoneNumber(String src) {
        if (Tools.isBlank(src)) {
            return false;
        }
        src = src.trim();
        Matcher m1 = MOBILE_PHONE_LOOSE_PATTERN.matcher(src);
        if (!m1.matches()) {
            return false;
        } else {
            String realPhoneNumber = src.replaceAll("^\\+86|^86|[\\D]*", "");
            Matcher m2 = MOBILE_PHONE_PATTERN.matcher(realPhoneNumber);
            if (!m2.matches()) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static String formatMobilePhoneNumber(String src) {
        if (Tools.isBlank(src)) {
            return null;
        }
        return src.trim().replaceAll("^\\+86|^86|[\\D]*", "");

    }

    public static boolean isEqual(String src, String... targets) {
        for (String target : targets) {
            if (src.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }
    //Date Utils

    /**
     * 月里哪日
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回月
     *
     * @param date
     * @return
     */
    public static String getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        switch (month) {
            case 0:
                return "一月";
            case 1:
                return "二月";
            case 2:
                return "三月";
            case 3:
                return "四月";
            case 4:
                return "五月";
            case 5:
                return "六月";
            case 6:
                return "七月";
            case 7:
                return "八月";
            case 8:
                return "九月";
            case 9:
                return "十月";
            case 10:
                return "十一月";
            case 11:
                return "十二月";

        }
        return null;
    }

    public static Date mergeDate(Date date, String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String[] arrayTime = time.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrayTime[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(arrayTime[1]));
        return calendar.getTime();
    }

    public static Date subMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -minute);
        return calendar.getTime();
    }

    public static Date addMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    public static Date addHour(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    public static Date addMonth(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static Date addDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static Date addYear(Date date, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    static public Date getBeginOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date begin = cal.getTime();
        return begin;
    }

    static public Date getEndOfDay(Date date) {//date下一天的开始
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 1);
        Date end = cal.getTime();
        return end;
    }

    public static Date[] getThisIssueDate(Date date) {
        Calendar cal = Calendar.getInstance();
        Date[] dates = new Date[2];
        cal.setTime(date);
        if (cal.get(Calendar.DAY_OF_MONTH) > 15) {
            //返回15到月底
            cal.set(Calendar.DAY_OF_MONTH, 15);
            dates[0] = cal.getTime();
            dates[1] = Tools.getEndofMonth(date);
        } else {
            //返回月初到15号
            dates[0] = Tools.getBeginOfMonth(date);
            cal.set(Calendar.DAY_OF_MONTH, 15);
            dates[1] = cal.getTime();
        }
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date d = cal.getTime();
        return dates;
    }

    public static Date getBeginOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date d = cal.getTime();
        return getBeginOfDay(d);
    }

    public static Date getEndofMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        Date end = getBeginOfMonth(date);
        cal.setTime(end);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MILLISECOND, -1);
        return cal.getTime();
    }

    public static Date parseDate(String dStr, String style) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(style);
            return sdf.parse(dStr);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static String formatDate(Date date, String style) {
        SimpleDateFormat sdf = new SimpleDateFormat(style);
        return sdf.format(date);
    }

    public static String formatDate(Date date, String style, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(style, locale);
        return sdf.format(date);
    }

    public static String formatDateYmdOnly(Date date, Locale locale) {
        String style = "MMMM d, yyyy";
        if (locale.getLanguage().equals("zh")) {
            style = "yyyy年M月d日";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(style, locale);
        return sdf.format(date);
    }

    public static String formatDate(Date date, String style, DateFormatSymbols dfs) {
        SimpleDateFormat sdf = new SimpleDateFormat(style, dfs);
        return sdf.format(date);
    }

    public static boolean isSameYear(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    public static boolean isSameYearDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static int getPeriodYearDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR);
    }

    //Codec Utils
    public static String md5(String s) {
        try {
            s = s.toUpperCase().trim() + "ALADING";
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            s = new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
        }
        return s;
    }

    //Random Utils
    public static String generateRandom10Chars() {
        return generateRandomNumber(10);
    }

    public static String generateRandom8Chars() {
        return generateRandomNumber(8);
    }

    public static String generateRandomAlpha(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static String generateRandomNumber(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    /**
     * 使用Id随即生成唯一且内容为字母数字的字符串(Random Unique Alpha Number)
     *
     * @param id
     * @return
     */
    public static String generateRandom8Chars(Long oid) {
        StringBuilder result = new StringBuilder();
        String idStr = String.valueOf(oid);
        int totalLength = 8;
        int idStrLength = idStr.length();
        int randomStrLength = totalLength - idStrLength;
        String randomStr = generateRandomAlpha(randomStrLength);
        int repate = idStrLength > randomStrLength ? randomStrLength : idStrLength;
        for (int i = 0; i < repate; i++) {
            result.append(idStr.charAt(i)).append(randomStr.charAt(i));
        }
        if (idStrLength > randomStrLength) {
            result.append(idStr.substring(repate));
        } else {
            result.append(randomStr.substring(repate));
        }
        return result.toString();
    }

    //Email Utils
    /**
     * 从一段文字中解析出email地址
     *
     * @param emails
     * @param orgiText
     */
    public static void parseEmails(Set<String> emails, String orgiText) {
        Pattern emailer = Pattern.compile("[\\w[.-]]+@[\\w[.-]]+\\.[\\w]+");
        Matcher matchr = emailer.matcher(orgiText);
        while (matchr.find()) {
            String email = matchr.group();
            emails.add(email.toLowerCase());
        }
    }

    /**
     * 获取email前缀
     *
     * @前
     *
     * @param email
     * @return
     */
    public static String getEmailPrefix(String email) {
        return email.substring(0, email.indexOf("@"));
    }

    /**
     * 屏蔽email地址
     *
     * @后内容
     *
     * @param email
     * @return
     */
    public static String shieldingEmailSuffix(String email) {
        return email.substring(0, email.indexOf("@")) + "@******";
    }

    /**
     * 银行卡号，留下前4位和后3位，其余屏蔽
     *
     * @param BankCardNumber
     * @return
     */
    public static String shieldingBankCardNumber(String BankCardNumber) {
        if (BankCardNumber.length() < 8) {
            return BankCardNumber;
        }
        return StringUtils.left(BankCardNumber, 4) + StringUtils.repeat("*", BankCardNumber.length() - 7) + StringUtils.right(BankCardNumber, 3);
    }

    /**
     * 一段的字符串留下后3位，其他全部屏蔽
     *
     * @param mobilePhone
     * @return
     */
    public static String shieldingStringForepart(String str) {
        str.trim();
        if (str.length() < 4) {
            return str;
        }
        return StringUtils.repeat("*", str.length() - 3) + StringUtils.right(str, 3);
    }

    /**
     * 获取邮件内容
     *
     * @param part
     * @param bodytext
     * @throws MessagingException
     * @throws IOException
     */
    public static void getEmailContent(Part part, StringBuilder bodytext) throws MessagingException, IOException {
        if (part.isMimeType("text/plain") && bodytext.length() < 1) {
            bodytext.append((String) part.getContent());
        } else if (part.isMimeType("text/html") && bodytext.length() < 1) {
            bodytext.append((String) part.getContent());
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            for (int i = 0; i < count; i++) {
                getEmailContent(multipart.getBodyPart(i), bodytext);
            }
        } else if (part.isMimeType("message/rfc822")) {
            getEmailContent((Part) part.getContent(), bodytext);
        }
    }

    public static String getEmailFromAddress(Message msg) throws MessagingException {
        InternetAddress[] address = (InternetAddress[]) msg.getFrom();
        String from = address[0].getAddress();
        if (from == null) {
            from = "";
        }
        return from;
    }

    public static boolean isWeekOldEmail(Message message) throws MessagingException {
        Date sendDate = message.getSentDate();
        if (sendDate == null) {
            return false;
        }
        Date nowDate = new Date();
        long d1 = sendDate.getTime();
        long d2 = nowDate.getTime();
        long diff = d2 - d1;
        long week = 7 * 24 * 3600 * 1000;
        if (diff > week) {
            return true;
        } else {
            return false;
        }
    }

    public static void deleteEmail(Message message) throws MessagingException {
        message.setFlag(Flag.DELETED, true);
    }

    public static void zip(List<String> sourceList, String destZip) throws IOException {
        List<File> list = new LinkedList<File>();
        for (int i = 0; i < sourceList.size(); i++) {
            String filename = sourceList.get(i);
            File file = new File(filename);
            list.add(file);
        }
        File destFile = new File(destZip);
        zip(list, destFile);
    }

    public static void zip(List<File> sourceList, File destZipFile) throws IOException {
        final int BUFFER = 2048;

        BufferedInputStream origin = null;
        FileOutputStream dest = new FileOutputStream(destZipFile);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
        out.setMethod(ZipOutputStream.DEFLATED);
        byte data[] = new byte[BUFFER];

        for (int i = 0; i < sourceList.size(); i++) {
            File file = sourceList.get(i);
            FileInputStream fi = new FileInputStream(file);
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(file.getName());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
        }
        out.close();
    }

    public static boolean isInstanceof(Object sourceObject, String theClassName) {
        try {
            Class c = Class.forName(theClassName);
            if (sourceObject.getClass().equals(c)) {
                return true;
            }
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 保存上传的文件
     *
     * @param item
     * @param src
     * @param filename
     */
    public static void setUploadFile(FileUploadItem item, String src, String filename) {
        try {
            String fullPath = src + filename;
            FileUtils.copyFile(new File(item.getUploadFullPath()), new File(fullPath));
            FileUtils.deleteQuietly(new File(item.getUploadFullPath()));
        } catch (Exception e) {
        }
    }

    /**
     * 获取上传文件
     *
     * @param request
     * @param maxFileSizeMB
     * @param fileNamePrefix
     * @param uploadPath
     * @param fileType
     * @return
     * @throws FileUploadException
     */
    public static FileUploadObj uploadFile(HttpServletRequest request, double maxFileSizeMB, String fileNamePrefix, String uploadPath, String fileType) throws FileUploadException {
        try {
            request.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        //上传数据封装到上传对象中
        FileUploadObj fileUploadObj = new FileUploadObj();
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            throw new FileUploadException("文件上传失败");
        }
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = null;
        try {
            items = upload.parseRequest(request);
        } catch (FileUploadException ex) {
            throw new FileUploadException("文件上传失败");
        }
        //循环上传Items
        Iterator<FileItem> iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
                //普通表单域(非文件域)
                try {
                    String fieldName = item.getFieldName();
                    if (fieldName != null && fieldName.length() > 0) {
                        if (fileUploadObj.containsKey(fieldName)) {
                            fileUploadObj.putToFormFieldArray(fieldName, item.getString("UTF-8"));
                        } else {
                            fileUploadObj.putFormField(fieldName, item.getString("UTF-8"));
                        }
                    }
                    if (maxFileSizeMB == 0 && fieldName.equals("max_size")) {
                        Double maxSize = Double.valueOf(fileUploadObj.getFormField("max_size"));
                        maxFileSizeMB = maxSize.doubleValue();
                        fileUploadObj.setMaxSizeMB(maxFileSizeMB);
                    }
                } catch (UnsupportedEncodingException ex) {
                }
                continue;
            }
            //文件域
            //上传前的文件名
            String fileName = item.getName();
            if (fileName == null || fileName.trim().equals("")) {
                continue;
            }

            //文件大小控制
            Long size = item.getSize();
            if (size > maxFileSizeMB * 1024 * 1024) {
                throw new FileUploadException("文件大小不能超过" + maxFileSizeMB + "MB");
            }

            // 创建临时上传目录
            String targetDir = Config.FILE_UPLOAD_TEMP_DIR;

            if (uploadPath != null) {
                targetDir = uploadPath;
            }
            File uploadedFileDir = new File(targetDir);
            if (!uploadedFileDir.exists()) {
                try {
                    FileUtils.forceMkdir(uploadedFileDir);
                } catch (IOException ex) {
                    throw new FileUploadException("文件上传失败");
                }
            }

            // 上传前文件基名和扩展名
            String baseName = FilenameUtils.getBaseName(fileName);
            String extension = FilenameUtils.getExtension(fileName);

            //设置上传后文件名前缀
            if (fileNamePrefix != null && fileNamePrefix.length() > 0) {
                fileNamePrefix = fileNamePrefix + "_";
            } else {
                fileNamePrefix = "Upload_";
            }
            if (extension == null) {
                extension = "";
            }
            if (extension.length() > 0) {
                extension = "." + extension;
            }
            //校验文件类型
            if (fileType != null) {
                String contentType = item.getContentType();
                boolean flag = false;
                if (fileType.indexOf("image") != -1) {
                    if (contentType.equalsIgnoreCase("image/pjpeg") || contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/gif") || contentType.equalsIgnoreCase("image/png") || contentType.equalsIgnoreCase("image/x-png")) {
                        flag = true;
                    }
                }
                if (fileType.indexOf("text") != -1) {
                    if (contentType.equalsIgnoreCase("text/plain") || contentType.equalsIgnoreCase("application/msword") || contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                        flag = true;
                    }
                }
                if (!flag) {
                    throw new FileUploadException("请上传正确的文件格式");
                }
            }
            //生成上传后文件名
            String tmpName = fileNamePrefix + Tools.md5(fileName + size + System.currentTimeMillis());
            String filename = tmpName + extension;

            // 保存文件
            File savedFile = new File(uploadedFileDir, filename);
            try {
                item.write(savedFile);
                item.delete();
            } catch (Exception ex) {
                throw new FileUploadException("文件上传失败");
            }
            //success upload
            FileUploadItem fileitem = new FileUploadItem();
            fileitem.setOrigFileBaseName(baseName);
            fileitem.setOrigFileExtName(extension);  //包含.
            fileitem.setOrigFileName(baseName + extension);
            fileitem.setFileSize(size);
            fileitem.setUploadFileName(filename);
            fileitem.setUploadFullPath(savedFile.getAbsolutePath());

            String fieldName = item.getFieldName();
            if (fieldName != null && fieldName.length() > 1) {
                fileitem.setFieldName(fieldName);
                fileUploadObj.putFileField(fieldName, fileitem);
            }
            fileUploadObj.addFileUploadItem(fileitem);
        }
        return fileUploadObj;
    }

    /**
     * SHA1加密
     *
     * @param decript
     * @return
     */
    public static String sha1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 是否为保留的阿拉丁号码
     *
     * @param code
     * @return
     */
    public static boolean isReservedALDCode(String code) {
        return RESERVED_ALD_CODE_PATTERN.matcher(code).matches();
    }

    /**
     * 是否包含数字
     *
     * @param str
     * @return
     */
    public static boolean isContainNumber(String str) {
        return CONTAIN_NUMBER_PATTERN.matcher(str).matches();
    }

    private static final Pattern CONTAIN_NUMBER_PATTERN = Pattern.compile(".*\\d+.*");
    private static final Pattern MOBILE_PHONE_PATTERN = Pattern.compile("^(1[0-9][0-9])\\d{8}$");
    private static final Pattern MOBILE_PHONE_LOOSE_PATTERN = Pattern.compile("^.{11,}$");
    //保留的阿拉丁号码正则表达式  
    private static final Pattern RESERVED_ALD_CODE_PATTERN = Pattern.compile("^//d*521$|^//d*520$|^521//d*$|520//d*$|^\\d*(\\d)\\1{2}$|^(\\d)\\1{2}\\d*$|\\d*(\\d)\\1{2,}(\\d)\\2{1,}\\d*|\\d*(\\d)\\1{1,}(\\d)\\2{2,}(\\d)\\3{1,}\\d*|\\d*(\\d)\\1{1,}(\\d)\\2{1,}(\\d)\\3{1,}\\d*|\\d*(\\d)\\1{2,}(\\d)\\2{2,}\\d*|\\d*(\\d)\\1{3,}\\d*|((?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){3,}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){3,})\\d)");
//    private static final Pattern RESERVED_ALD_CODE_PATTERN = Pattern.compile("^");

    public static void main(String[] args) throws Exception {
        String si = "长春1123123";
        System.out.println(Tools.isContainNumber(si));
//        toMp3("E:/workspace/ReportWeb/WebRoot/", "audio/REC_20150126_175835.amr");
//        Date date = Tools.parseDate("2015-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
//        Date date2 = Tools.parseDate("2014-12-30 23:59:50", "yyyy-MM-dd HH:mm:ss");
//        System.out.println(Tools.isReservedALDCode("520111"));
//        System.out.println(Tools.calculateDate(date2, date));
    }

//    public static void main(String[] args) {
//        //匹配4位顺增  
//        String pattern = "^\\d*(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){3}\\d\\d*$";
//        Pattern pa = Pattern.compile(pattern);
//        String mc = "99456789555";
//        Matcher ma = pa.matcher(mc);
//        System.out.println("6位顺增 ：" + ma.matches());
//        System.out.println("*******分割线*******");
//
//        //匹配4位顺降  
//        pattern = "\\d*(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){3}\\d*$";
//        pa = Pattern.compile(pattern);
//        mc = "654321";
//        ma = pa.matcher(mc);
//        System.out.println("6位顺降 ：" + ma.matches());
//        System.out.println("*******分割线*******");
//
//        //匹配3位以上的重复数字  
//        pattern = "(\\d*([\\d])\\1{3})\\d*$";
//        pa = Pattern.compile(pattern);
//        mc = "8888888888";
//        ma = pa.matcher(mc);
//        System.out.println("3位以上的重复数字 ：" + ma.matches());
//        System.out.println("*******分割线*******");
//
//
//        //匹配2233类型  
//        pattern = "(\\d*[\\d])\\1{1,}([\\d])\\2{1,}\\d*$";
//        pa = Pattern.compile(pattern);
//        mc = "822339";
//        ma = pa.matcher(mc);
//        System.out.println("2233类型 ：" + ma.matches());
//        System.out.println("*******分割线*******");
//
//    }
}
