/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.weixinpay;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.liusk.common.utils.HttpUtil;
import org.liusk.common.utils.XmltoJsonUtil;
import org.liusk.weixinpay.common.constants.PayApi;
import org.liusk.weixinpay.common.constants.WeiXinConfig;
import org.liusk.weixinpay.common.util.PayCommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 * 微信app支付处理
 * @author liusk
 * @version $Id: WxController.java, v 0.1 2017/9/22 15:06 liusk Exp $
 */
@Controller
@RequestMapping(value = "/WeiXinAppPay")
public class WeiXinAppPayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeiXinAppPayController.class);

    // 微信回调地址
    public static final String NOTIFYURL = "***/WeiXinAppPay/notify";
    // 微信交易类型
    public static final String TRADETYPE = "APP";

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
            parame.put("appid", WeiXinConfig.APPID);
            parame.put("mch_id", WeiXinConfig.MCHID);// 商家账号。
            String randomStr = PayCommonUtil.getRandomString(18).toUpperCase();
            parame.put("nonce_str", randomStr);// 随机字符串
            parame.put("body", subject);// 商品描述
            parame.put("out_trade_no", orderNum);// 商户订单编号
            parame.put("total_fee",
                BigDecimal.valueOf(Long.parseLong(money)).multiply(new BigDecimal(100)).toString());// 消费金额
            String ip = PayCommonUtil.getIpAddr(request);
            if (StringUtils.isEmpty(ip)) {
                parame.put("spbill_create_ip", "127.0.0.1");// 消费IP地址
            } else {
                parame.put("spbill_create_ip", ip);// 消费IP地址
            }
            parame.put("notify_url", NOTIFYURL);// 回调地址
            parame.put("trade_type", TRADETYPE);// 交易类型APP
            String sign = PayCommonUtil.createSign(parame);
            parame.put("sign", sign);// 数字签证

            //map转xml参数字符串
            String xml = PayCommonUtil.getRequestXML(parame);

            String content = HttpUtil.httpPost(PayApi.PAY_UNIFORMORDER, xml);
            JSONObject jsonObject = XmltoJsonUtil.documentToJSONObject(content);
            String code = (String) jsonObject.get("result_code");

            if (code.equalsIgnoreCase("FAIL")) {
                resultObj.put("msg", "微信统一订单下单失败");
                resultObj.put("code", "-1");
                LOGGER.error("微信支付失败，原因：{}", (String) jsonObject.get("return_msg"));
            } else if (code.equalsIgnoreCase("SUCCESS")) {
                String prepayId = (String) jsonObject.get("prepay_id");
                //获取到prepayid后对以下字段进行签名最终发送给app  
                SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();
                finalpackage.put("appid", WeiXinConfig.APPID);
                finalpackage.put("timestamp", PayCommonUtil.getTimeStamp());
                finalpackage.put("noncestr", randomStr);
                finalpackage.put("partnerid", WeiXinConfig.MCHID);//微信支付分配的商户号
                finalpackage.put("package", "Sign=WXPay");
                finalpackage.put("prepayid", prepayId);
                String finalsign = PayCommonUtil.createSign(finalpackage);
                finalpackage.put("sign", finalsign);

                //封装返回给前端的数据
                resultObj.put("msg", "微信统一订单下单成功");
                resultObj.put("code", "1");
                resultObj.put("data", finalpackage);
            }
            return resultObj.toJSONString();

        } catch (Exception e) {
            resultObj.put("msg", e.getMessage());
            resultObj.put("code", "-1");
            return resultObj.toJSONString();
        }

    }

    /**
     * 微信支付回调接口
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

            //获取微信返回的支付结果部分数据
            JSONObject jsonObject = XmltoJsonUtil.documentToJSONObject(content);
            String result_code = (String) jsonObject.get("result_code");
            //String out_trade_no = (String) jsonObject.get("out_trade_no");
            //String total_fee = (String) jsonObject.get("total_fee"); //金额（分） 
            //String transaction_id = (String) jsonObject.get("transaction_id");//微信订单号

            if (result_code.equalsIgnoreCase("FAIL")) {
                resultObj.put("msg", "微信统一订单下单失败");
                resultObj.put("code", "-1");
                //返回给微信处理结果，一般情况下都返回成功
                response.getWriter().write(PayCommonUtil.setXml("SUCCESS", "OK"));
            } else if (result_code.equalsIgnoreCase("SUCCESS")) {
                resultObj.put("msg", "微信统一订单下单成功");
                resultObj.put("code", "1");

                //TODO
                //支付成功的操作，处理之前最好先查询一下数据库中订单的状态是否是已支付，已支付的就不用处理

                response.getWriter().write(PayCommonUtil.setXml("SUCCESS", "OK"));
            }

        } catch (Exception e) {
            resultObj.put("msg", e.getMessage());
            resultObj.put("code", "-1");
            return;
        }
    }
}
