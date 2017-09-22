/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.contoller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.liusk.common.utils.HttpUtil;
import org.liusk.common.utils.Md5;
import org.liusk.common.utils.XmltoJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author liusk
 * @version $Id: WxController.java, v 0.1 2017/9/22 15:06 liusk Exp $
 */
@Controller
@RequestMapping(value = "/WeiXinPay")
public class WeiXinController {

    private static final Logger LOG = LoggerFactory.getLogger(WeiXinController.class);

    // 微信统一下单接口路径
    private static final String UNIFORMORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    // 微信商户号：*****
    private static final String MCHID     = "********";
    // 微信回调地址
    private static final String NOTIFYURL = "*********";
    // 微信交易类型
    private static final String TRADETYPE = "APP";
    //微信APIKEY
    private static final String APIKEY    = "************";

    /**
     * 微信统一下单
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/uniformorder", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String uniformorder(HttpServletRequest request) throws UnsupportedEncodingException {
        JSONObject resultObj = new JSONObject();
        request.setCharacterEncoding("UTF-8");
        try {
            //APP ID
            String appid = request.getParameter("appid") == null ? null
                : request.getParameter("appid").trim().toUpperCase();
            // 用户访问令牌
            String accessToken = request.getParameter("accessToken") == null ? null
                : request.getParameter("accessToken").trim();
            // 订单编号
            String orderNum = request.getParameter("orderNum") == null ? null
                : request.getParameter("orderNum").trim();// 订单编号
            // 消费金额
            String money = request.getParameter("money") == null ? null
                : request.getParameter("money").trim();// 消费金额
            // 消费主题
            String subject = request.getParameter("subject") == null ? null
                : request.getParameter("subject").trim();// 消费主体

            SortedMap<Object, Object> parame = new TreeMap<Object, Object>();
            parame.put("appid", appid);
            parame.put("mch_id", MCHID);// 商家账号。
            String randomStr = getRandomString(18).toUpperCase();
            parame.put("nonce_str", randomStr);// 随机字符串
            parame.put("body", subject);// 商品描述
            parame.put("out_trade_no", orderNum);// 商户订单编号
            parame.put("total_fee",
                BigDecimal.valueOf(Long.parseLong(money)).multiply(new BigDecimal(100)).toString());// 消费金额
            String ip = getIpAddr(request);
            if (StringUtils.isEmpty(ip)) {
                parame.put("spbill_create_ip", "127.0.0.1");// 消费IP地址
            } else {
                parame.put("spbill_create_ip", ip);// 消费IP地址
            }
            parame.put("notify_url", NOTIFYURL);// 回调地址
            parame.put("trade_type", TRADETYPE);// 交易类型APP
            String sign = createSign(parame);
            parame.put("sign", sign);// 数字签证

            String xml = getRequestXML(parame);

            String content = HttpUtil.httpPost(UNIFORMORDER, xml);
            System.out.println(content);
            JSONObject jsonObject = XmltoJsonUtil.documentToJSONObject(content);
            JSONObject result_xml = jsonObject.getJSONObject("xml");
            JSONArray result_code = result_xml.getJSONArray("result_code");
            String code = (String) result_code.get(0);

            List<String> data = new ArrayList<String>();
            if (code.equalsIgnoreCase("FAIL")) {
                resultObj.put("msg", "微信统一订单下单失败");
                resultObj.put("code", "-1");
                resultObj.put("data", data);
            } else if (code.equalsIgnoreCase("SUCCESS")) {
                JSONArray prepay_id = result_xml.getJSONArray("prepay_id");
                String prepayId = (String) prepay_id.get(0);
                data.add(prepayId);
                resultObj.put("msg", "微信统一订单下单成功");
                resultObj.put("code", "1");
                resultObj.put("data", data);
            }
            return resultObj.toJSONString();

        } catch (Exception e) {
            resultObj.put("msg", e.getMessage());
            resultObj.put("code", "-1");
            return resultObj.toJSONString();
        }

    }

    /**
     * 微信订单回调接口
     *
     * @return
     */
    @RequestMapping(value = "/notify", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public void notify(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultObj = new JSONObject();
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            InputStream in = request.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.close();
            in.close();
            String content = new String(out.toByteArray(), "utf-8");//xml数据

            JSONObject jsonObject = XmltoJsonUtil.documentToJSONObject(content);
            JSONObject result_xml = jsonObject.getJSONObject("xml");
            JSONArray result_code = result_xml.getJSONArray("result_code");
            String code = (String) result_code.get(0);

            if (code.equalsIgnoreCase("FAIL")) {
                resultObj.put("msg", "微信统一订单下单失败");
                resultObj.put("code", "-1");

                response.getWriter().write(setXml("SUCCESS", "OK"));

            } else if (code.equalsIgnoreCase("SUCCESS")) {
                resultObj.put("msg", "微信统一订单下单成功");
                resultObj.put("code", "1");

                // /TODO
                //支付成功的操作

                response.getWriter().write(setXml("SUCCESS", "OK"));
            }

        } catch (Exception e) {
            resultObj.put("msg", e.getMessage());
            resultObj.put("code", "-1");
            return;
        }

    }

    // 返回用IP地址
    public String getIpAddr(HttpServletRequest request) {
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

    //拼接xml 请求路径
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
        buffer.append("key=" + APIKEY);
        String sign = Md5.MD5(buffer.toString()).toUpperCase();
        System.out.println("签名参数：" + sign);
        return sign;
    }

    //返回微信服务
    public static String setXml(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code
               + "]]></return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
    }

    //模拟微信回调接口
    public static String callbakcXml(String orderNum) {
        return "<xml><appid><![CDATA[wx2421b1c4370ec43b]]></appid><attach><![CDATA[支付测试]]></attach><bank_type><![CDATA[CFT]]></bank_type><fee_type><![CDATA[CNY]]></fee_type> <is_subscribe><![CDATA[Y]]></is_subscribe><mch_id><![CDATA[10000100]]></mch_id><nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str><openid><![CDATA[oUpF8uMEb4qRXf22hE3X68TekukE]]></openid> <out_trade_no><![CDATA["
               + orderNum
               + "]]></out_trade_no>  <result_code><![CDATA[SUCCESS]]></result_code> <return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign><sub_mch_id><![CDATA[10000100]]></sub_mch_id> <time_end><![CDATA[20140903131540]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id></xml>";
    }

    public static void main(String[] args) throws Exception {
        String str = HttpUtil.httpPost("http://localhost:8080/WlsqWS/WeiXinPay/notify",
            callbakcXml("a313a907f72f4f70b53a46b9773a9d42"));
        System.out.println("result :" + str);
        //      SortedMap<Object,Object> parame = new TreeMap<Object,Object>();
        //      parame.put("appid", "wxdf26629c37f5b7c1");
        //      parame.put("mch_id", MCHID);
        //      String randomStr = getRandomString(18).toUpperCase();
        //      parame.put("nonce_str", randomStr);
        //      String subject = "余额充值";
        //      parame.put("body", subject);
        //      String orderNum = "3a6154e2a662407c8223971284c372a7";
        //      parame.put("out_trade_no", orderNum);
        //      String money ="1";
        //      parame.put("total_fee", money);
        //      String ip ="127.0.0.1";
        //      parame.put("spbill_create_ip", ip);
        //      parame.put("notify_url", NOTIFYURL);
        //      parame.put("trade_type", TRADETYPE);
        //      String sign =createSign(parame);
        //      parame.put("sign", sign);// 数字签证
        //
        //      String xml = getRequestXML(parame);
        //
        //      String content = HttpUtil.sendPost(UNIFORMORDER, xml);
        //
        //      //成功代码
        //      String result = XmltoJsonUtil.xml2JSON(content);
        //      JSONObject jsonObject = JSONObject.parseObject(result) ;
        //      JSONObject results = jsonObject.getJSONObject("xml");
        //      JSONArray array =  results.getJSONArray("result_code");
        //      String code = (String)array.get(0);
        //      System.out.println(results.toString());
        //      System.out.println(code);

        //失败代码
        //      String result = XmltoJsonUtil.xml2JSON(content);
        //      JSONObject jsonObject = JSONObject.parseObject(result) ;
        //      System.out.println(jsonObject.toString());
    }

}
