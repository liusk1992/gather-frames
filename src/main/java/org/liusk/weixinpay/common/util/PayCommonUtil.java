/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.weixinpay.common.util;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import org.liusk.common.utils.Md5;
import org.liusk.weixinpay.common.constants.WeiXinConfig;

/**
 * 微信支付的一些工具方法
 * @author liusk
 * @version $Id: PayCommonUtil.java, v 0.1 2017年9月25日 下午3:19:57 liusk Exp $
 */
public class PayCommonUtil {

    /**
     * 返回用IP地址
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader(" x-forwarded-for ");
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getHeader(" Proxy-Client-IP ");
        }
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getHeader(" WL-Proxy-Client-IP ");
        }
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取10位时间戳
     * @return
     */
    public static String getTimeStamp() {
        //时间戳
        Date date = new Date();
        long time = date.getTime();
        //时间戳只有10位 要做处理
        String timeStamp = time + "";
        timeStamp = timeStamp.substring(0, 10);
        return timeStamp;
    }

    // 随机字符串生成
    public static String getRandomString(int length) { // length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    //拼接xml 请求参数
    @SuppressWarnings("rawtypes")
    public static String getRequestXML(SortedMap<Object, Object> parame) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<xml>");
        Set set = parame.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            //过滤相关字段sign
            if ("sign".equalsIgnoreCase(key)) {
                buffer.append("<" + key + ">" + "<![CDATA[" + value + "]]>" + "</" + key + ">");
            } else {
                buffer.append("<" + key + ">" + value + "</" + key + ">");
            }
        }
        buffer.append("</xml>");
        return buffer.toString();
    }

    //创建md5 数字签证
    @SuppressWarnings("rawtypes")
    public static String createSign(SortedMap<Object, Object> parame) {
        StringBuffer buffer = new StringBuffer();
        Set set = parame.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            Object value = (String) entry.getValue();
            if (null != value && !"".equals(value) && !"sign".equals(key) && !"key".equals(key)) {
                buffer.append(key + "=" + value + "&");
            }
        }
        buffer.append("key=" + WeiXinConfig.APIKEY);
        String sign = Md5.MD5(buffer.toString()).toUpperCase();
        System.out.println("签名参数：" + sign);
        return sign;
    }

    //返回微信服务
    public static String setXml(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code
               + "]]></return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
    }

}
