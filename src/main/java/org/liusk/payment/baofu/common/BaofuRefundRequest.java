/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.baofu.common;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.liusk.payment.baofu.common.constants.BaofuResultConstant;
import org.liusk.payment.baofu.common.models.BaofuResultModel;
import org.liusk.payment.baofu.common.rsa.RsaCodingUtil;
import org.liusk.payment.baofu.common.util.HttpUtil;
import org.liusk.payment.baofu.common.util.JXMConvertUtil;
import org.liusk.payment.baofu.common.util.MapToXMLString;
import org.liusk.payment.baofu.common.util.PathUtil;
import org.liusk.payment.baofu.common.util.SecurityUtil;
import org.liusk.payment.baofu.config.BaoFuConfig;
import org.liusk.payment.baofu.config.BaofuTransferUrls;

import net.sf.json.JSONObject;

/**
 * 退款相关处理
 * @author liusk
 * @version $Id: BaofuRefundRequest.java, v 0.1 2017年10月12日 下午2:43:21 liusk Exp $
 */
public class BaofuRefundRequest {

    /**
     * 请求退款
     * @param arrayData
     * @return
     * @throws UnsupportedEncodingException
     */
    public static BaofuResultModel<Map<String, Object>> refund(Map<String, Object> arrayData) throws UnsupportedEncodingException {
        //验证证书是否存在
        String pfxpath = PathUtil.getRootClassPath() + "//certs/baofu/"
                         + BaoFuConfig.PRIVATE_KEY_CERT_NAME;//宝付私钥
        String cerpath = PathUtil.getRootClassPath() + "//certs/baofu/"
                         + BaoFuConfig.PUBLIC_KEY_CERT_NAME;//宝付公钥
        if (!isExistFile(pfxpath)) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "私钥文件不存在！");
        }
        if (!isExistFile(cerpath)) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "公钥文件不存在！");
        }

        //参数加密成dataContent字符串
        String data_content = encodeDataContent(arrayData, pfxpath);

        //准备支付所需最终参数
        Map<String, String> headparm = new HashMap<String, String>();
        headparm.put("version", BaoFuConfig.VERSION);//版本号
        headparm.put("terminal_id", BaoFuConfig.TERMINAL_ID);//终端号
        headparm.put("txn_type", BaoFuConfig.TXN_TYPE_REFUND);
        headparm.put("txn_sub_type", BaoFuConfig.TXN_SUB_TYPE_REFUND);
        headparm.put("member_id", BaoFuConfig.MEMBER_ID);//商户号
        headparm.put("data_type", BaoFuConfig.DATA_TYPE);//加密报文的数据类型（xml/json）
        headparm.put("data_content", data_content);
        headparm.put("risk_item", "");//风险控制，可不填

        //发送支付请求，并获取返回数据
        String postString = HttpUtil.RequestForm(BaofuTransferUrls.REFUND, headparm);
        if (postString.isEmpty()) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "返回空报文！");
        }
        String resultStr = decodeDataContent(postString);
        JSONObject returnJson = JSONObject.fromObject(resultStr);

        //判断返回数据是否正常解析为json数据
        if (returnJson == null) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "返回参数异常！");
        }
        Map<String, Object> resultMap = JXMConvertUtil.JsonConvertHashMap(returnJson);
        //判断返回码是否正确
        if (!StringUtils.equals(BaoFuConfig.RESP_CODE_SUCCESS,
            resultMap.get("resp_code").toString())) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL,
                resultMap.get("resp_msg").toString());
        }
        return new BaofuResultModel<Map<String, Object>>(BaofuResultConstant.SUCCESS, "退款成功！",
            resultMap);

    }

    /**
     * 退款状态查询
     * 查询接口说明：在系统没有收到异步通知的情况下可通过此接口查询退款订单当前状态
     * 不必依赖系统通知
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    public static BaofuResultModel<Map<String, Object>> QueryRefund(String refundOrderNo) throws IOException {

        //验证证书是否存在
        String pfxpath = PathUtil.getRootClassPath() + "//certs/baofu/"
                         + BaoFuConfig.PRIVATE_KEY_CERT_NAME;//宝付私钥
        String cerpath = PathUtil.getRootClassPath() + "//certs/baofu/"
                         + BaoFuConfig.PUBLIC_KEY_CERT_NAME;//宝付公钥
        if (!isExistFile(pfxpath)) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "私钥文件不存在！");
        }
        if (!isExistFile(cerpath)) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "公钥文件不存在！");
        }

        //准备支付所需最终参数
        Map<String, String> headparm = new HashMap<String, String>();
        headparm.put("version", BaoFuConfig.VERSION);//版本号
        headparm.put("terminal_id", BaoFuConfig.TERMINAL_ID);//终端号
        headparm.put("txn_type", BaoFuConfig.TXN_TYPE_REFUND);
        headparm.put("txn_sub_type", BaoFuConfig.TXN_SUB_TYPE_REFUND_QUERY);
        headparm.put("member_id", BaoFuConfig.MEMBER_ID);//商户号
        headparm.put("data_type", BaoFuConfig.DATA_TYPE);//加密报文的数据类型（xml/json）

        String Refund_TSN = "RefundTSN" + System.currentTimeMillis();//退款商户流水号
        Map<String, Object> arrayData = new HashMap<String, Object>();
        arrayData.put("txn_sub_type", BaoFuConfig.TXN_SUB_TYPE_REFUND_QUERY);
        arrayData.put("terminal_id", BaoFuConfig.TERMINAL_ID);
        arrayData.put("member_id", BaoFuConfig.MEMBER_ID);
        arrayData.put("refund_order_no", refundOrderNo);//商户退款时生成的订单号
        arrayData.put("trans_serial_no", Refund_TSN);//退款流水号
        arrayData.put("additional_info", "附加信息");
        arrayData.put("req_reserved", "保留");
        //参数加密成dataContent字符串
        String data_content = encodeDataContent(arrayData, pfxpath);
        headparm.put("data_content", data_content);

        String postString = HttpUtil.RequestForm(BaofuTransferUrls.REFUND, headparm);

        //结果处理
        String resultStr = decodeDataContent(postString);
        JSONObject returnJson = JSONObject.fromObject(resultStr);
        //判断返回数据是否正常解析为json数据
        if (returnJson == null) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "返回参数异常！");
        }
        Map<String, Object> resultMap = JXMConvertUtil.JsonConvertHashMap(returnJson);
        //判断返回码是否正确
        if (!StringUtils.equals(BaoFuConfig.RESP_CODE_SUCCESS,
            resultMap.get("resp_code").toString())) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL,
                resultMap.get("resp_msg").toString());
        }
        return new BaofuResultModel<Map<String, Object>>(BaofuResultConstant.SUCCESS, "退款查询成功！",
            resultMap);

    }

    /**
     * 异步通方法
     * 退款成功/失败后宝付系统通知商户，商户通过返回的参数得到退款结果，收到通知后输出OK字符。
     * 此方法不得有登陆过虑
     * 宝付处理（成功/失败）后系统异步通知5次，20分钟一次
     * @param dataContent
     * @return
     * @throws IOException
     */
    public static BaofuResultModel<Map<String, Object>> returnurl(String dataContent) throws IOException {

        try {
            //判断参数是否为空
            if (dataContent.isEmpty()) {
                return new BaofuResultModel<>(BaofuResultConstant.FAIL, "返回数据为空！");
            }
            String cerpath = PathUtil.getRootClassPath() + "//certs/baofu/"
                             + BaoFuConfig.PUBLIC_KEY_CERT_NAME;//宝付公钥
            File cerfile = new File(cerpath);
            if (!cerfile.exists()) {//判断宝付公钥是否为空
                return new BaofuResultModel<>(BaofuResultConstant.FAIL, "公钥文件不存在！");
            }
            //解密返回数据
            dataContent = decodeDataContent(dataContent);
            //将回调返回的数据（JSON）转化为Map对象。
            Map<String, Object> dataContentMap = JXMConvertUtil.JsonConvertHashMap(dataContent);
            //检查返回的dataContent解密之后格式和返回码是否正确
            String checkDataContentMapMsg = checkReturnDataContentMap(dataContentMap);
            if (!StringUtils.equals("SUCCESS", checkDataContentMapMsg)) {
                return new BaofuResultModel<>(BaofuResultConstant.FAIL, checkDataContentMapMsg);
            }
            //返回回调数据的map对象
            return new BaofuResultModel<>(BaofuResultConstant.SUCCESS, "支付回调处理成功！", dataContentMap);
        } catch (Exception e) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "支付回调处理出错！");
        }
    }

    /**
     * 解密回调数据
     * @param dataContent
     * @return
     * @throws IOException 
     * @throws UnsupportedEncodingException 
     */
    public static String decodeDataContent(String dataContent) {
        try {
            //判断参数是否为空
            if (dataContent.isEmpty()) {
                return null;
            }
            String cerpath = PathUtil.getRootClassPath() + "//certs/baofu/"
                             + BaoFuConfig.PUBLIC_KEY_CERT_NAME;//宝付公钥
            File cerfile = new File(cerpath);
            if (!cerfile.exists()) {//判断宝付公钥是否为空
                return null;
            }
            //解密返回数据
            dataContent = RsaCodingUtil.decryptByPubCerFile(dataContent, cerpath);
            dataContent = SecurityUtil.Base64Decode(dataContent);
            if (StringUtils.equals("xml", BaoFuConfig.DATA_TYPE)) {
                dataContent = JXMConvertUtil.XmlConvertJson(dataContent);
            }
            return dataContent;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 参数加密成dataContent字符串
     * @param arrayData
     * @param pfxpath
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encodeDataContent(Map<String, Object> arrayData,
                                           String pfxpath) throws UnsupportedEncodingException {
        Map<Object, Object> arrayData1 = new HashMap<Object, Object>();
        String XmlOrJson = "";
        if (StringUtils.equals("json", BaoFuConfig.DATA_TYPE)) {
            JSONObject jsonObjectFromMap = JSONObject.fromObject(arrayData);
            XmlOrJson = jsonObjectFromMap.toString();
        } else {
            arrayData1.putAll(arrayData);
            XmlOrJson = MapToXMLString.converter(arrayData1, "data_content");
        }
        String base64str = SecurityUtil.Base64Encode(XmlOrJson);
        String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str, pfxpath,
            BaoFuConfig.PRIVATE_KEY_CERT_PWD);
        return data_content;
    }

    /**
     * 验证文件是否存在
     * @param filePath
     * @return
     */
    public static boolean isExistFile(String filePath) {
        File pfxfile = new File(filePath);
        if (!pfxfile.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 校验返回的dataContent已经解密的数据
     * @return
     */
    public static String checkReturnDataContentMap(Map<String, Object> dataContentMap) {
        if (!dataContentMap.containsKey("resp_code")) {
            return "返回参数异常！XML解析参数[resp_code]不存在";
        }
        String retCode = dataContentMap.get("resp_code").toString();
        if (!StringUtils.equals(retCode, BaoFuConfig.RESP_CODE_SUCCESS)) {
            return dataContentMap.get("resp_code").toString()
                   + dataContentMap.get("resp_msg").toString();
        }
        return "SUCCESS";
    }

}
