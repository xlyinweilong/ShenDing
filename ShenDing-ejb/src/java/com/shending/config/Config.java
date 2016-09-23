package com.shending.config;


/**
 * 配置信息
 *
 * @author yin
 */
public class Config {

    private Config() {
    }
    public static final String IPHONE_APP_URL = "https://itunes.apple.com/cn/app/a-la-ding-bangta/id986225845?l=zh&ls=1&mt=8";

    public static final String ANDROID_APP_URL = "/app/alading_bangta.apk";

    public static final String JPUSH_APP_KEY = "109350a90248e2c2988826e9";

    public static final String JPUSH_MASTER_SECRET = "157d555f9285cf18470665ae";

    public static final String RONG_YUN_APP_KEY = "e5t4ouvptx6ra";

    public static final String RONG_YUN_SECRET_KEY = "xXeYCUvNtKY";

    public static final String BAI_DU_PUSH_API_KEY = "QWKALGla1IzWUDQpr6LNms5j";

    public static final String BAI_DU_PUSH_SECRET_KEY = "C3dlQdq8zC9vSASZfwL9X1KdDMBwIwTP";

    public static final String SMS_SIGNUP = "您好，欢迎加入啊啦叮，验证码：%，有效时间为10分钟，请填写验证码并完成注册，啊啦叮提醒您为了保护您的账号安全，请勿转告他人。";

    public static final String SMS_FORGOTPASSWD = "您好，您当前正在努力找回密码，验证码：%，有效时间为10分钟，请输入验证码并完成密码重置，啊啦叮提醒您为了保护您的账号安全，请勿转告他人。";

    public static final String SMS_MODIFY_ACCOUNT = "您好，您现在更换绑定手机号码，验证码：%，有效时间为1分钟，啊啦叮提醒您为了保护您的账号安全，请勿转告他人。";

    public static final String SMS_SIGN = "【啊啦叮】";

    public static final String ILLEGAL_KEYS_FATH = "/home/yinweilong/alading/illegal_keys.txt";
//    public static final String ILLEGAL_KEYS_FATH = "d:/illegal_keys.txt";

    public static final Integer SEEK_LENGTH = 5;

    public static final Integer ACCOUNT_LENGTH = 6;

    public static final Integer GROUP_LENGTH = 5;

//    public static final String FILE_UPLOAD_DIR = "f:/cbrd/";
    public static final String FILE_UPLOAD_DIR = "/data/file_data/";
    public static final String HTML_EDITOR_UPLOAD = "html_editor_upload";

    public static final String FILE_UPLOAD_TEMP = "temp";

    public static final String FILE_UPLOAD_PLATE = "plate";

    public static final String FILE_UPLOAD_ACCOUNT = "account_upload";

    public static final String FILE_UPLOAD_CIRCLE = "circle_upload";

    public static final String FILE_UPLOAD_ACCOUNT_HEAD_IMAGE = "account_head_image";

//    public static final String FILE_UPLOAD_TEMP_DIR = "f:/test";
    public static final String FILE_UPLOAD_TEMP_DIR = "/data/file_data/";
    public static final String HTTP_URL_BASE = "http://www.aladingren.com";

    public static final String MESSAGE_REPLY = "回答了您提出的追问，快去看看是否满意。";

    public static final String MESSAGE_ASK = "对您提出了追问，赶紧回复TA吧。";

    public static final String MESSAGE_COMMENT = "回答了您提出的求助，快去看看是否满意。";

    public static final String MESSAGE_ADOPT = "采纳了您的帮助，快去看看吧。";

}
