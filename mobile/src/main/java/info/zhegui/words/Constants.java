package info.zhegui.words;

/**
 * 本类用来存放常量
 * 
 * @author Administrator
 * 
 */
public class Constants {

    public static class BAIDU_FANYI{
        public static final String API_KEY = "xddWAYzYYrxOsvQkdRfdrKt6";
        public static final String SECRET_KEY = "2CrVFFSGGVtkBawF0ig1sPtUgzsGobHf";
    }
	
	public static class BAIDU_VOICE{
	    public static final String API_KEY = "Gm67p14lkm4PreFqYbPquuwR";
	    public static final String SECRET_KEY = "8D1Igdmp6lIShcnYqsudqL260cKqZ4nL";
	}

	//0 :待调度， 1 :正在调度，2：正在接送 3:订单成功完成，4:订单承接前乘客取消 5:订单承接后乘客取消 6:订单承接后司机取消 
	//，0~2为未完成，3为完成，4~6为已取消
	
	public static class ORDER_STATUS{
		/**0 :待调度*/
		public static final int WAIT_2_ARRANGEMNT=0;
		
		/**1 :正在调度*/
		public static final int IN_ARRANGEMENT=1;
		/**2：正在接送*/
		public static final int ON_THE_WAY=2;
		/**3:订单成功完成*/
		public static final int DONE=3;
		
		/**4:订单承接前乘客取消*/
		public static final int CANCELED_BY_PASSENGER_BEFORE_ARRANGEDMENT=4;
		/**5:订单承接后乘客取消*/
		public static final int CANCELED_BY_PASSENGER_AFTER_ARRANGEDMENT=5;
		/**6:订单承接后司机取消*/
		public static final int CANCELED_BY_DRIVER=6;
	}
	
	/**
	 * 司机审核状态
	 * 
	 * @author ASUS
	 * 
	 */
	public static class DRIVER_VERIFY_STATUS {
		/** 审核中 */
		public static final int ING = 1;
		/** 未通过 */
		public static final int NO = 2;
		/** 审核通过 */
		public static final int OK = 3;
	}

	/** SharedPreferences相关常量 */
	public static class PREFS {
		/** 启动次数 */
		public static final String START_COUNT = "start_count";

		/** 上班时间 */
		public static final String ON_DUTY_TIME = "on_duty_time";

		public static final String DRIVER_UNIQUE_CODE = "driver_unique_code";
		public static final String DRIVER_STAR = "driver_star";
		public static final String DRIVER_BALANCE = "driver_balance";
		public static final String DRIVER_VERIFY_STATUS = "driver_verify_status";

		public static final String LOGIN_LAST_PHONE_NUM = "login_last_phone_num";
		/** 登录过的手机号码，用逗号连成字符串 */
		public static final String LOGINED_PHONE_NUM = "logined_phone_num";
		public static final String REGISTER_PHONE_NUM = "register_phone_num";
		public static final String REGISTER_PWD = "register_pwd";
		public static final String REGISTER_NAME = "register_name";
		public static final String REGISTER_YYZ_NUMBER = "register_yyz_number";
		public static final String REGISTER_PLATE_NUMBER = "register_plate_number";
		public static final String REGISTER_ID_NUMBER = "register_id_number";
		public static final String REGISTER_COMPANY_NAME = "register_company_name";
		public static final String REGISTER_COMPANY_ID = "register_company_id";
		public static final String REGISTER_TERMINAL = "register_terminal";
		/** 头像保存在内部存储 */
		public static final String REGISTER_PORTRAIT_IMAGE = "register_portrait_image";
		/** 头像服务器地址 */
		public static final String REGISTER_PORTRAIT_IMAGE_URL = "register_portrait_image_url";
		/** 营运证保存在内部存储 */
		public static final String REGISTER_YYZ_IMAGE = "register_yyz_image";
		/** 营运证服务器地址 */
		public static final String REGISTER_YYZ_IMAGE_URL = "register_yyz_image_url";

		public static final String DEFAULT_DRIVER_STAR = "1";

		public static final String WALLET_BANK_TYPE = "wallet_bank_type";
		public static final String WALLET_BANK_NUMBER = "wallet_bank_number";
		public static final String WALLET_ALIPAY = "wallet_alipay";

		public static final String SETTINGS_OFFCAR_ENABLED = "settings_offcar_enabled";
		public static final String SETTINGS_OFFCAR_PHONE_NUM = "settings_offcar_phone_num";
		public static final String SETTINGS_RETURN_ENABLED = "settings_return_enabled";
		public static final String SETTINGS_RETURN_ADDR = "settings_return_addr";
		public static final String SETTINGS_RETURN_RADIUS = "settings_return_radius";
		public static final String SETTINGS_CHANGE_SHIFTS_ENABLED = "settings_change_shifts_enabled";
		public static final String SETTINGS_CHANGE_SHIFTS_ADDR = "settings_change_shifts_addr";
		public static final String SETTINGS_CHANGE_SHIFTS_TIME = "settings_change_shifts_time";
		public static final String SETTINGS_CHANGE_SHIFTS_TIME_IN_ADVANCE = "settings_change_shifts_time_in_advance";

		public static final String CAR_SETTINGS_DEVICE_NUMBER = "car_settings_device_number";
		public static final String CAR_SETTINGS_SERVER_IP = "car_settings_server_ip";
		public static final String CAR_SETTINGS_SERVER_PORT = "car_settings_server_port";
		public static final String CAR_SETTINGS_DIAL_ACCOUNT = "car_settings_dial_account";
		public static final String CAR_SETTINGS_DIAL_PWD = "car_settings_dial_pwd";

		public static final String LOCATION_LAST_LATITUDE = "location_last_latitude";
		public static final String LOCATION_LAST_LONGITUDE = "location_last_longtitude";
	}

	public static class HTTP {
		public static final String NETWORK_ERROR = "network_error";
		public static final String SERVER_ERROR = "server_error";
		public static final String OK = "OK";

		public static final String RESULT = "Result";
		public static final String MSG = "Msg";

		public static final String COMMON_URL = "api.i952169.com.cn:8065/BKingAPI.ashx?__from=android";
		public static final String COMMON_URL_HTTPS = "https://" + COMMON_URL;
		public static final String COMMON_URL_HTTP = "http://" + COMMON_URL;
		public static final String COMMON_IMG_URL_PREFIX = "http://api.i952169.com.cn:8061/UpDoc/Image/";
	}

	public static class DIALOG {
		public static final float RATIO_WIDTH = 0.8f;
		public static final float RATIO_HEIGHT = 0.8F;
	}

	public static class Driver_Intent {
		public static final String ACTION_LOCATION_RECEIVED = "action_location_received";
		public static final String ACTION_ORDER_DATA_CHANGED = "action_order_data_changed";
		public static final String BUNDLE_LOCATION = "bundle_location";
	}

	public static class SocketBroadCast {
		/** 打车相关 */
		public static final String NEW_ORDER_BROADCAST = "new_order";// 收到中心下发的订单广播
		public static final String ACCEPT_ORDER_REPLY_BROADCAST = "accpt_order_result";// 抢单结果广播
		public static final String PASSENGER_CANCEL_ORDER_BROADCAST = "passenger_cancel_order_notice";// 乘客取消订单广播
		public static final String RECEIVE_ORDER_PRICE_BROADCAST = "receive_order_price";//中心下发车费广播
		
		public static final String COMMON_BROADCAST = "common_reply";// 收到中心下发的通用回复

	}

}
