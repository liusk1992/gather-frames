/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * json和xml相互转化
 * @author liusk
 * @version $Id: XmltoJsonUtil.java, v 0.1 2017/9/22 15:29 liusk Exp $
 */
public class XmltoJsonUtil {

    public static void main(String[] args) throws DocumentException {
        String xmlStr = "<xml><appid><![CDATA[wx2421b1c4370ec43b]]></appid>"
                        + "<attach><![CDATA[支付测试]]></attach><bank_type><![CDATA[CFT]]></bank_type>"
                        + "<fee_type><![CDATA[CNY]]></fee_type>"
                        + "<is_subscribe><![CDATA[Y]]></is_subscribe><mch_id><![CDATA[10000100]]></mch_id>"
                        + "<nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str>"
                        + "<openid><![CDATA[oUpF8uMEb4qRXf22hE3X68TekukE]]></openid>"
                        + "<out_trade_no><![CDATA[1409811653]]></out_trade_no>"
                        + "<result_code><![CDATA[SUCCESS]]></result_code>"
                        + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign>"
                        + "<sub_mch_id><![CDATA[10000100]]></sub_mch_id>"
                        + "<time_end><![CDATA[20140903131540]]></time_end>"
                        + "<total_fee>1</total_fee><trade_type><![CDATA[JSAPI]]></trade_type>"
                        + "<transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id></xml>";
        JSONObject jo = documentToJSONObject(xmlStr);
        Set<Entry<String, Object>> joSet = jo.entrySet();
        for (Map.Entry<String, Object> e : joSet) {
            System.out.println(e.getKey() + "--" + e.getValue());
        }
    }

    /**
     * String 转 org.dom4j.Document
     * @param xml
     * @return
     * @throws DocumentException
     */
    public static Document strToDocument(String xml) throws DocumentException {
        return DocumentHelper.parseText(xml);
    }

    /**
     * org.dom4j.Document 转  com.alibaba.fastjson.JSONObject
     * @param xml
     * @return
     * @throws DocumentException
     */
    public static JSONObject documentToJSONObject(String xml) throws DocumentException {
        return elementToJSONObject(strToDocument(xml).getRootElement());
    }

    /**
     * org.dom4j.Element 转  com.alibaba.fastjson.JSONObject
     * @param node
     * @return
     */
    @SuppressWarnings("unchecked")
    public static JSONObject elementToJSONObject(Element node) {
        JSONObject result = new JSONObject();
        // 当前节点的名称、文本内容和属性
        List<Attribute> listAttr = node.attributes();// 当前节点的所有属性的list
        for (Attribute attr : listAttr) {// 遍历当前节点的所有属性
            result.put(attr.getName(), attr.getValue());
        }
        // 递归遍历当前节点所有的子节点

        List<Element> listElement = node.elements();// 所有一级子节点的list
        if (!listElement.isEmpty()) {
            for (Element e : listElement) {// 遍历所有一级子节点
                if (e.attributes().isEmpty() && e.elements().isEmpty()) // 判断一级节点是否有属性和子节点
                    result.put(e.getName(), e.getTextTrim());// 沒有则将当前节点作为上级节点的属性对待
                else {
                    if (!result.containsKey(e.getName())) // 判断父节点是否存在该一级节点名称的属性
                        result.put(e.getName(), new JSONArray());// 没有则创建
                    ((JSONArray) result.get(e.getName())).add(elementToJSONObject(e));// 将该一级节点放入该节点名称的属性对应的值中
                }
            }
        }
        return result;
    }

}
