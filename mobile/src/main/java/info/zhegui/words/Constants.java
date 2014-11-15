package info.zhegui.words;

/**
 * 本类用来存放常量
 * 
 * @author Administrator
 * 
 */
public class Constants {

    public static class PREFS{
        public static final String DURATION="duration";
        public static final String LESSON="lesson";

        public static final int DEFAULT_DURATION=3;
    }

    public static class ACTIVITY_RESULT{
        public static final int PAUSE=101;
        public static final int STOP=102;
    }

    public static class STATE{
        public static final int PAUSED=101;
        public static final int STOPPED=102;
        public static final int RUNNING=103;
    }

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
