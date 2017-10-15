/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.baofu.common;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.liusk.payment.baofu.common.constants.BaofuResultConstant;
import org.liusk.payment.baofu.common.models.BaofuResultModel;
import org.liusk.payment.baofu.common.rsa.RsaCodingUtil;
import org.liusk.payment.baofu.common.util.HttpUtil;
import org.liusk.payment.baofu.common.util.JXMConvertUtil;
import org.liusk.payment.baofu.common.util.MapToXMLString;
import org.liusk.payment.baofu.common.util.ParseStr;
import org.liusk.payment.baofu.common.util.PathUtil;
import org.liusk.payment.baofu.common.util.SecurityUtil;
import org.liusk.payment.baofu.config.BaoFuConfig;
import org.liusk.payment.baofu.config.BaofuTransferUrls;

import net.sf.json.JSONObject;
import org.liusk.payment.baofu.common.constants.BaofuResultConstant;
import org.liusk.payment.baofu.common.models.BaofuResultModel;
import org.liusk.payment.baofu.common.rsa.RsaCodingUtil;
import org.liusk.payment.baofu.common.util.*;
import org.liusk.payment.baofu.config.BaoFuConfig;
import org.liusk.payment.baofu.config.BaofuTransferUrls;

/**
 * 宝付支付处理
 * @author liusk
 * @version $Id: BaofuPayRequest.java, v 0.1 2017年9月30日 下午2:19:01 liusk Exp $
 */
public class BaofuPayRequest {

    /**
     * 宝付的app支付后台处理
     * @return
     * @throws UnsupportedEncodingException
     */
    public static BaofuResultModel<Map<String, String>> sdkBankPay(Map<String, String> arrayData) {
        try {
            String pfxpath = PathUtil.getRootClassPath() + "//certs/baofu/"
                             + BaoFuConfig.PRIVATE_KEY_CERT_NAME;//商户私钥
            File pfxfile = new File(pfxpath);
            if (!pfxfile.exists()) {
                return new BaofuResultModel<>(BaofuResultConstant.FAIL, "私钥文件不存在！");
            }

            //参数加密成dataContent字符串
            String data_content = encodeDataContent(arrayData, pfxpath);

            //准备支付所需最终参数
            Map<String, String> post = new HashMap<String, String>();
            post.put("version", BaoFuConfig.VERSION);
            post.put("input_charset", BaoFuConfig.CHARSET);
            post.put("language", BaoFuConfig.LANGUAGE);
            post.put("terminal_id", BaoFuConfig.TERMINAL_ID);
            post.put("member_id", BaoFuConfig.MEMBER_ID);
            post.put("data_type", BaoFuConfig.DATA_TYPE);
            post.put("data_content", data_content);

            //发送支付请求，并获取返回数据
            String queryResult = HttpUtil.RequestForm(BaofuTransferUrls.SDK_PREORDER, post);
            if (queryResult.isEmpty()) {
                return new BaofuResultModel<>(BaofuResultConstant.FAIL, "返回空报文！");
            }

            //返回的数据转map
            Map<String, String> parseMap = ParseStr.ToMap(queryResult);
            //检查返回数据
            String checkMsg = checkReturnData(parseMap);
            if (!StringUtils.equals("SUCCESS", checkMsg)) {
                return new BaofuResultModel<>(BaofuResultConstant.FAIL, checkMsg);
            }
            return new BaofuResultModel<>(BaofuResultConstant.SUCCESS, "sdk生成交易号调用成功！", parseMap);
        } catch (Exception e) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "sdk生成交易号出错！");
        }
    }

    /**
     * 宝付支付回调处理
     * @param dataContent
     * @return
     */
    public static BaofuResultModel<Map<String, Object>> baofuNotify(String dataContent) {
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
            //检查返回的dataContent解密之后格式和返回码是否正确,然后检查了terminalId和memberId是否是本商户的数据
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
     * 快捷支付交易查询
     * @param orderNo 商户订单号
     * @return
     * @throws IOException
     */
    public static BaofuResultModel<Map<String, Object>> quickSdkPayQuery(String orderNo) {
        try {
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

            //准备查询参数
            Map<String, String> post = packageQuickSdkPayQuery(orderNo, pfxpath);

            //发起请求,获取查询结果数据
            String queryResult = HttpUtil.RequestForm(BaofuTransferUrls.SDK_QUERY, post);
            if (queryResult.isEmpty()) {
                return new BaofuResultModel<>(BaofuResultConstant.FAIL, "返回异常，检查网络通讯！");
            }

            //以下是返回数据的处理......
            BaofuResultModel<Map<String, Object>> resultModel = dealQueryResultData(queryResult,
                cerpath);
            return resultModel;
        } catch (Exception e) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "sdk查询交易状态出错！");
        }
    }

    /**
     * 绑定状态查询
     * @param userId
     * @return
     * @throws IOException
     */
    public static BaofuResultModel<Map<String, Object>> quickPayBindQuery(String userId) {
        try {
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

            //准备绑定查询参数的map集合
            Map<String, String> post = packageQueryBindParams(userId, pfxpath);

            //发起请求
            String queryResult = HttpUtil.RequestForm(BaofuTransferUrls.SDK_QUICK_PAY_BIND_QUERY,
                post);

            //以下是返回数据的处理
            BaofuResultModel<Map<String, Object>> resultModel = dealQueryResultData(queryResult,
                cerpath);

            return resultModel;
        } catch (Exception e) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "绑定状态查询！");
        }
    }

    /**
     * 宝付调用微信支付宝支付
     * @param arrayData 参数
     * @param txnSubType 微信或支付宝支付类型
     * @return
     * @throws UnsupportedEncodingException
     */
    public static BaofuResultModel<Map<String, Object>> appWxAliPay(Map<String, String> arrayData,
                                                                    String txnSubType) throws UnsupportedEncodingException {
        try {
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
            Map<String, String> post = new HashMap<String, String>();
            post.put("version", BaoFuConfig.VERSION);
            post.put("terminal_id", BaoFuConfig.TERMINAL_ID);
            post.put("txn_type", BaoFuConfig.TXN_TYPE_WXALIPAY);//微信支付宝支付，值固定
            post.put("txn_sub_type", txnSubType);
            post.put("member_id", BaoFuConfig.MEMBER_ID);
            post.put("data_type", BaoFuConfig.DATA_TYPE);
            post.put("data_content", data_content);

            //发送支付请求，并获取返回数据
            String postString = HttpUtil.RequestForm(BaofuTransferUrls.WX_ALI_PAY, post);
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
            return new BaofuResultModel<Map<String, Object>>(BaofuResultConstant.SUCCESS,
                "调用微信支付宝获取token成功！", resultMap);
        } catch (Exception e) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "调用微信支付宝获取token出错！");
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

    /**********************************分割线，以下是以上代码的内部方法*********************************/

    /**
     * 准备绑定查询的参数集合
     * @param userId
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static Map<String, String> packageQueryBindParams(String userId,
                                                             String pfxpath) throws UnsupportedEncodingException {
        String trans_serial_no = userId + System.currentTimeMillis();//商户订单号
        Map<String, String> arrayData = new HashMap<String, String>();
        arrayData.put("terminal_id", BaoFuConfig.TERMINAL_ID);//终端号
        arrayData.put("member_id", BaoFuConfig.MEMBER_ID);//商户号
        arrayData.put("user_id", userId);//用户id
        arrayData.put("trans_serial_no", trans_serial_no);//商户流水
        arrayData.put("additional_info", "附加信息");
        arrayData.put("req_reserved", "保留");

        Map<String, String> post = new HashMap<String, String>();
        post.put("version", BaoFuConfig.VERSION);
        post.put("input_charset", BaoFuConfig.CHARSET);
        post.put("terminal_id", BaoFuConfig.TERMINAL_ID);
        post.put("member_id", BaoFuConfig.MEMBER_ID);
        post.put("data_type", BaoFuConfig.DATA_TYPE);

        //参数加密成dataContent字符串
        String data_content = encodeDataContent(arrayData, pfxpath);

        post.put("data_content", data_content);
        return post;
    }

    /**
     * 准备查询交易状态的参数集合
     * @param orderNo
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static Map<String, String> packageQuickSdkPayQuery(String orderNo,
                                                              String pfxpath) throws UnsupportedEncodingException {
        String trade_date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//交易日期
        String trans_serial_no = orderNo + System.currentTimeMillis();//商户订单号
        Map<String, String> arrayData = new HashMap<String, String>();
        arrayData.put("terminal_id", BaoFuConfig.TERMINAL_ID);//终端号
        arrayData.put("member_id", BaoFuConfig.MEMBER_ID);//商户号
        arrayData.put("orig_trans_id", orderNo);//原商户订单号
        arrayData.put("trans_serial_no", trans_serial_no);//商户流水
        arrayData.put("trade_date", trade_date);
        arrayData.put("additional_info", "附加信息");
        arrayData.put("req_reserved", "保留");

        Map<String, String> post = new HashMap<String, String>();
        post.put("version", BaoFuConfig.VERSION);
        post.put("input_charset", BaoFuConfig.CHARSET);
        post.put("terminal_id", BaoFuConfig.TERMINAL_ID);
        post.put("member_id", BaoFuConfig.MEMBER_ID);
        post.put("data_type", BaoFuConfig.DATA_TYPE);

        //参数加密成dataContent字符串
        String data_content = encodeDataContent(arrayData, pfxpath);

        post.put("data_content", data_content);
        return post;
    }

    /**
     * 参数加密成dataContent字符串
     * @param arrayData
     * @param pfxpath
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encodeDataContent(Map<String, String> arrayData,
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
     * 校验返回数据
     * @return
     */
    public static String checkReturnData(Map<?, ?> parseMap) {
        if (!parseMap.containsKey("ret_code")) {
            return "返回参数异常！XML解析参数[ret_code]不存在";
        }
        //如果基础参数错误，如商户号或终端号错，则无需解析参数date_content
        if (!StringUtils.equals(parseMap.get("ret_code").toString(),
            BaoFuConfig.RESP_CODE_SUCCESS)) {
            return parseMap.get("ret_code") + "," + parseMap.get("ret_msg");
        }
        return "SUCCESS";
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
        //获取与验证memberId
        String memberId = dataContentMap.get("member_id").toString();
        if (!StringUtils.equals(memberId, BaoFuConfig.MEMBER_ID)) {
            return "baofuAliWechatNotify memberId not match";
        }
        //获取与验证terminalId
        String terminalId = dataContentMap.get("terminal_id").toString();
        if (!StringUtils.equals(terminalId, BaoFuConfig.TERMINAL_ID)) {
            return "baofuAliWechatNotify terminalId not match";
        }
        return "SUCCESS";
    }

    /**
     * 处理查询返回的数据
     * @return
     * @throws IOException 
     * @throws UnsupportedEncodingException 
     */
    public static BaofuResultModel<Map<String, Object>> dealQueryResultData(String queryResult,
                                                                            String cerpath) throws UnsupportedEncodingException,
                                                                                            IOException {
        Map<?, ?> parseMap = ParseStr.ToMap(queryResult);
        //检查返回数据
        String checkMsg = checkReturnData(parseMap);
        if (!StringUtils.equals("SUCCESS", checkMsg)) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, checkMsg);
        }

        //取出返回数据中的date_content
        String splitDataContent = parseMap.get("data_content").toString();

        //解密dataContent参数中的数据
        String resutldataContent = RsaCodingUtil.decryptByPubCerFile(splitDataContent, cerpath);
        if (resutldataContent.isEmpty()) {//判断解密是否正确。如果为空则宝付公钥不正确
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, "解密出错，检查解密公钥是否正确！");
        }
        resutldataContent = SecurityUtil.Base64Decode(resutldataContent);
        if (StringUtils.equals("xml", BaoFuConfig.DATA_TYPE)) {
            resutldataContent = JXMConvertUtil.XmlConvertJson(resutldataContent);
        }
        //将返回数据中解密之后的dataContent中的JSON数据转化为Map对象。
        Map<String, Object> dataContentMap = JXMConvertUtil.JsonConvertHashMap(resutldataContent);

        //检查返回的dataContent解密之后格式是否正确
        String checkDataContentMapMsg = checkReturnDataContentMap(dataContentMap);
        if (!StringUtils.equals("SUCCESS", checkDataContentMapMsg)) {
            return new BaofuResultModel<>(BaofuResultConstant.FAIL, checkDataContentMapMsg);
        }

        //返回dataContent的map数据
        return new BaofuResultModel<Map<String, Object>>(BaofuResultConstant.SUCCESS, "查询成功",
            dataContentMap);
    }
}
