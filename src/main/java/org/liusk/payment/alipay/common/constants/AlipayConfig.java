package org.liusk.payment.alipay.common.constants;

public class AlipayConfig {
    // 商户appid
    public static String APPID             = "2016082000296547";
    // 私钥 pkcs8格式的
    public static String RSA_PRIVATE_KEY   = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIuNN/Xtfb8cBL2Qz2YsR8eQo3c0kZUexCYZguK8hHNyd3SQ3QXIZWUHsECsfCCDArvP1MGEMunaOrZFn4ogSPuODmxb6MVUZbRAhvNuOeAVjS+3rwcCkt/KUFeTWghYDNa39M6Gw5+ttVjZ32oUibQ957h7bWe/6e6B8e1XhCzLAgMBAAECgYBvPelH/0Ln3tvgiNjrJ40ohiHoWl1as6XEgT2WpJQouyyCHSAzBeXdvV1AkL555DV/pVKB2HIFfFKlDab5onbJ8sJh2YAwF6lAzKG0PT4L01080ZFF+AjtPD84KRQ8wkHl3GhlRAs/LKF6nh22+j+6NlR3hwuM71buzLtRDyAOkQJBAL0kTaSHpiLOX31x2oorcSQo39VZ7eMhipXCWVrKRNL9lIcXjQAabHqrNA8j7QGBqKpN/n1F3bYa+eh7Ne3w8E0CQQC84W8ISF4frc+w2dbAjDBPgFIz8OtUCa+UhPR3yvCypgUNt+DXbLmENFu8WnU0ZhlJiNJAZBtOsnRt+mhKWt13AkBA1a3rxTf03GstNBbmoAeTjpNTrhT2c6vutAUvMwCulpAQUslZjLU8w2z/95+pkYmAf2XKCWX7o3mg8INHAf1NAkA2pvgsqlvpyK4m2vFjp2y3JEck1loRZ/gQ/mOKYgNBff5KWOFLFB+d3FSK7JWrQrugZmGH1i98lvBPfjw8/4vDAkA4M7pQZWd6HECI6KTUmt4KHXd3r8r0V6I0VbGlXrid+i9Kmm7Vwahws5U0OSfu+lFaq21WxglK9XZuMjP8Hi1F";
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url        = "http://dcfca100.ngrok.io/AliPay/notify.do";
    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    public static String return_url        = "http://dcfca100.ngrok.io/AliPay/return.do";
    // 请求网关地址
    public static String URL               = "https://openapi.alipaydev.com/gateway.do";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //"https://openapi.alipay.com/gateway.do";
    // 编码
    public static String CHARSET           = "UTF-8";
    // 返回格式
    public static String FORMAT            = "json";
    // 支付宝公钥
    public static String ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB";
    // 日志记录目录
    public static String log_path          = "/log";
    // RSA2
    public static String SIGNTYPE          = "RSA";
}
