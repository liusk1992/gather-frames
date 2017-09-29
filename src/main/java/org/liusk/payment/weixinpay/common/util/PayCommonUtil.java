/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.weixinpay.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.liusk.payment.weixinpay.config.WeiXinConfig;

import com.alibaba.fastjson.JSONObject;

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

    /**
     * 随机字符串生成
     * @param length 表示生成字符串的长度
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 拼接xml 请求参数
     * @param parame
     * @return
     */
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

    /**
     * 创建md5数字签证
     * @param parame
     * @return
     */
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
        return sign;
    }

    /**
     * 返回微信服务,微信回调接口的返回数据封装
     * @param return_code
     * @param return_msg
     * @return
     */
    public static String setXml(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code
               + "]]></return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
    }

    /**
     * fastjson的对象转sortMap
     * @param jsonObject
     * @return
     */
    public static SortedMap<Object, Object> jsonObjectToSortMap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Set<Entry<String, Object>> joSet = jsonObject.entrySet();
        SortedMap<Object, Object> sortMap = new TreeMap<Object, Object>();
        for (Map.Entry<String, Object> e : joSet) {
            sortMap.put(e.getKey(), e.getValue());
        }
        return sortMap;
    }

    /**
     * 
     * @param url 需要ssl的请求地址
     * @param data 参数
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     */
    @SuppressWarnings("deprecation")
    public static String SSLHttpPost(String url,
                                     String data) throws IOException, NoSuchAlgorithmException,
                                                  CertificateException, KeyStoreException,
                                                  KeyManagementException,
                                                  UnrecoverableKeyException {
        //指定读取证书格式为PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        //读取本机存放的PKCS12证书文件
        FileInputStream instream = new FileInputStream(new File(WeiXinConfig.CERTPATH));
        try {
            //指定PKCS12的密码(商户ID)
            keyStore.load(instream, WeiXinConfig.MCHID.toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts.custom()
            .loadKeyMaterial(keyStore, WeiXinConfig.MCHID.toCharArray()).build();
        //指定TLS版本
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
            new String[] { "TLSv1" }, null,
            SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        //设置httpclient的SSLSocketFactory
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            HttpPost httpost = new HttpPost(url);
            // 设置响应头信息
            httpost.addHeader("Connection", "keep-alive");
            httpost.addHeader("Accept", "*/*");
            httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpost.addHeader("Host", "api.mch.weixin.qq.com");
            httpost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpost.addHeader("Cache-Control", "max-age=0");
            httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
            httpost.setEntity(new StringEntity(data, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httpost);
            try {
                //获取调用接口返回的数据
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                return jsonStr;
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}
