/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.weixinpay.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.liusk.common.utils.HttpUtil;
import org.liusk.common.utils.XmltoJsonUtil;
import org.liusk.payment.alipay.common.constants.AlipayConfig;
import org.liusk.payment.weixinpay.common.constants.PayApi;
import org.liusk.payment.weixinpay.common.constants.WeiXinConfig;
import org.liusk.payment.weixinpay.common.util.PayCommonUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * @author liusk
 * @version $Id: WxPayRequest.java, v 0.1 2017/9/26 16:35 liusk Exp $
 */
public class WxPayRequest {

    /**
     * 微信统一下单
     * @param request
     * @param orderNum 订单号
     * @param money 订单金额（分）
     * @param subject 订单描述
     * @return
     * @throws DocumentException 
     * @throws UnsupportedEncodingException 
     */
    public static JSONObject appUniformorder(HttpServletRequest request, String orderNo,
                                             String money, String subject, String tradeType) {
        JSONObject resultObj = new JSONObject();
        try {
            request.setCharacterEncoding("UTF-8");
            //sortMap自带字典排序光环
            SortedMap<Object, Object> parame = new TreeMap<Object, Object>();
            parame.put("appid", WeiXinConfig.APPID);
            parame.put("mch_id", WeiXinConfig.MCHID);//商户号
            String randomStr = PayCommonUtil.getRandomString(18).toUpperCase();
            parame.put("nonce_str", randomStr);// 随机字符串
            parame.put("body", subject);// 商品描述
            parame.put("out_trade_no", orderNo);// 商户订单编号
            parame.put("total_fee",
                BigDecimal.valueOf(Long.parseLong(money)).multiply(new BigDecimal(100)).toString());// 消费金额
            String ip = PayCommonUtil.getIpAddr(request);
            if (StringUtils.isEmpty(ip)) {
                parame.put("spbill_create_ip", "127.0.0.1");// 消费IP地址
            } else {
                parame.put("spbill_create_ip", ip);// 消费IP地址
            }
            parame.put("notify_url", AlipayConfig.notify_url);// 回调地址
            parame.put("trade_type", tradeType);// 交易类型APP
            //获取签名字符串
            String sign = PayCommonUtil.createSign(parame);
            parame.put("sign", sign);// 数字签证
            //map转xml参数字符串
            String xml = PayCommonUtil.getRequestXML(parame);

            //请求微信统一下单接口，获取返回数据
            String content = HttpUtil.httpPost(PayApi.PAY_UNIFORMORDER, xml);
            JSONObject jsonObject = XmltoJsonUtil.documentToJSONObject(content);
            String code = (String) jsonObject.get("result_code");

            //微信统一下单接口返回数据的code不等于SUCCESS时，下单失败
            if (!"SUCCESS".equalsIgnoreCase(code)) {
                resultObj.put("msg", "微信统一订单下单失败" + jsonObject.get("return_msg"));
                resultObj.put("code", "FAIL");
                return resultObj;
            }
            //获取prepayid
            String prepayId = (String) jsonObject.get("prepay_id");
            //获取到prepayid后对以下字段进行签名最终发送给app
            SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();
            finalpackage.put("appid", WeiXinConfig.APPID);
            finalpackage.put("timestamp", PayCommonUtil.getTimeStamp());
            finalpackage.put("noncestr", randomStr);
            finalpackage.put("partnerid", WeiXinConfig.MCHID);
            finalpackage.put("package", "Sign=WXPay");
            finalpackage.put("prepayid", prepayId);
            String finalsign = PayCommonUtil.createSign(finalpackage);
            finalpackage.put("sign", finalsign);

            //封装返回给前端的数据
            resultObj.put("msg", "微信统一订单下单成功");
            resultObj.put("code", "SUCCESS");
            resultObj.put("data", finalpackage);
            return resultObj;
        } catch (Exception e) {
            resultObj.put("msg", "微信统一订单下单失败");
            resultObj.put("code", "FAIL");
            return resultObj;
        }
    }

    /**
     * 微信支付回调处理
     * 该方法在微信支付回调接口里使用，根据该方法返回的信息判断是否回调验证成功，进行自己的逻辑处理之后记得response返回微信处理结果
     * 一般情况下成功或失败都要返回处理成功表示接收到微信的回调了：response.getWriter().write(setXml("SUCCESS", "OK"));
     * @param request
     * @param response
     * @return
     */
    public static JSONObject notify(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultObj = new JSONObject();
        try {
            //设置响应头的编码格式，放开跨域请求
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

            //获取微信回调返回的支付结果数据
            JSONObject jsonObject = XmltoJsonUtil.documentToJSONObject(content);
            String result_code = (String) jsonObject.get("result_code");
            String sign = (String) jsonObject.get("sign");

            //如果微信回调进来的code不是success，微信下单失败
            if (!"SUCCESS".equalsIgnoreCase(result_code)) {
                resultObj.put("msg", "微信统一订单下单失败");
                resultObj.put("code", "FAIL");
                return resultObj;
            }

            //验证签名
            SortedMap<Object, Object> sortMap = PayCommonUtil.jsonObjectToSortMap(jsonObject);
            String signCallBack = PayCommonUtil.createSign(sortMap);
            if (!StringUtils.equals(signCallBack, sign)) {
                resultObj.put("msg", "签名验证失败");
                resultObj.put("code", "FAIL");
                return resultObj;
            }

            resultObj.put("msg", "微信统一订单下单成功");
            resultObj.put("code", "SUCCESS");
            resultObj.put("data", jsonObject);
            return resultObj;
        } catch (Exception e) {
            resultObj.put("msg", e.getMessage());
            resultObj.put("code", "FAIL");
            return resultObj;
        }
    }

    /**
     * 微信退款处理
     * @param refundNo 商户退款单号（自定义）
     * @param orderNo 支付时传给微信的商户订单号
     * @param totalFee 订单总价
     * @param refundFee 退款总价
     * @return
     */
    public static JSONObject refund(String refundNo, String orderNo, String totalFee,
                                    String refundFee) {
        JSONObject resultObj = new JSONObject();
        try {
            //生成预支付订单需要的的package数据
            SortedMap<Object, Object> parame = new TreeMap<Object, Object>();
            parame.put("appid", WeiXinConfig.APPID);
            parame.put("mch_id", WeiXinConfig.MCHID);
            String randomStr = PayCommonUtil.getRandomString(18).toUpperCase();
            parame.put("nonce_str", randomStr);// 随机字符串
            parame.put("out_trade_no", orderNo);
            parame.put("out_refund_no", refundNo);
            parame.put("total_fee", totalFee);
            parame.put("refund_fee", refundFee);
            //获取签名字符串
            String sign = PayCommonUtil.createSign(parame);
            parame.put("sign", sign);// 数字签证
            //map转xml参数字符串
            String xml = PayCommonUtil.getRequestXML(parame);

            //调用微信退款接口
            String resultStr = PayCommonUtil.SSLHttpPost(PayApi.PAY_REFUND, xml);
            //获取微信回调返回的支付结果数据
            JSONObject jsonObject = XmltoJsonUtil.documentToJSONObject(resultStr);
            resultObj.put("code", "SUCCESS");
            resultObj.put("msg", "退款请求处理失败");
            resultObj.put("data", jsonObject);
        } catch (Exception e) {
            resultObj.put("code", "FAIL");
            resultObj.put("msg", "退款请求处理失败");
            resultObj.put("error", e.getMessage());
        }
        return resultObj;
    }

}
