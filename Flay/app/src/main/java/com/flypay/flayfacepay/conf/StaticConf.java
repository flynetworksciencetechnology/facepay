package com.flypay.flayfacepay.conf;

public class StaticConf {
    public static final String RETURN_CODE = "return_code";
    public static final String RETURN_MSG = "return_msg";
    public static enum RESPONSE_CODE{
        SUCCESS("0000","请求成功,响应正常"),
        FAILD("-1111","请求失败,未知异常");
        private String code;
        private String msg;
        private RESPONSE_CODE(String code, String msg) {
            this.code = code;
        }
        public String getMsg() {
            return msg;
        }
        public void setMsg(String msg) {
            this.msg = msg;
        }
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
    }
    public enum ERROR_MSG{
        微信返回异常_调用返回为空,
        调用返回非成功信息;
    }

    public enum BackType{
        BACK,
        DEL;
    }

    public enum EnterType {
        OPEN,
        OHTER;
    }
}
