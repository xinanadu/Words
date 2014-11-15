package info.zhegui.words;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


public class Utils {
	private static Toast mToast;

	private static final String TAG = "Utils";


	public static String formatMoney(String moneyStr) {
		return formatMoney(Double.parseDouble(moneyStr));
	}

	public static String formatMoney(double parseDouble) {
		DecimalFormat nf = new DecimalFormat("###,##0.00");
		return nf.format(parseDouble);
	}

	public static String formatTime(long timeStamp) {
		Time time = new Time();
		time.set(timeStamp);
		return time.format("%Y-%m-%d %H:%M");
	}

	public static String getPhoneNum(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			String number = tm.getLine1Number();
			if (!TextUtils.isEmpty(number) && number.length() > 11)
				return number.substring(number.length() - 11, number.length());
		}
		return null;
	}

	public static String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	/**
	 * 通过Base64将Bitmap转换成Base64字符串
	 * 
	 * 服务器String2Image，参考：http://www.2cto.com/kf/201402/281840.html
	 * 
	 * @param bit
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String Bitmap2StrByBase64(Bitmap bit) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bit.compress(CompressFormat.PNG, 100, bos);// 参数100表示不压缩
		byte[] bytes = bos.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	public static String Bitmap2StrByBase64(Context context,
			String internalFileName) {
		try {
			Bitmap bm = BitmapFactory.decodeStream(context
					.openFileInput(internalFileName));
			if (bm != null) {
				return Bitmap2StrByBase64(bm);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@SuppressLint("NewApi")
	public static String File2StrByBase64(Context context,
			String internalFileName) {
		try {
			InputStream is = context.openFileInput(internalFileName);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int n = 0;
			while (-1 != (n = is.read(buffer))) {
				output.write(buffer, 0, n);
			}
			return Base64.encodeToString(output.toByteArray(), Base64.DEFAULT);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 此方法不同于Base64.encodeToString(output.toByteArray(), Base64.URL_SAFE)，原因暂不明
	 * 
	 * @param context
	 * @param internalFileName
	 * @return
	 */
	public static String File2StrByBase64AfterEncoded(Context context,
			String internalFileName) {

		try {
			String imageStr = File2StrByBase64(context, internalFileName);
			imageStr = URLEncoder.encode(imageStr, "utf-8");
			return imageStr;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] File2ByteArrayByBase64(Context context,
			String internalFileName) {
		try {
			InputStream is = context.openFileInput(internalFileName);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int n = 0;
			while (-1 != (n = is.read(buffer))) {
				output.write(buffer, 0, n);
			}
			return output.toByteArray();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// @SuppressLint("NewApi")
	// public static String File2StrByApacheBase64(Context context,
	// String internalFileName) {
	// try {
	// InputStream is = context.openFileInput(internalFileName);
	// ByteArrayOutputStream output = new ByteArrayOutputStream();
	// byte[] buffer = new byte[4096];
	// int n = 0;
	// while (-1 != (n = is.read(buffer))) {
	// output.write(buffer, 0, n);
	// }
	// // return
	// //
	// org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(output.toByteArray());
	// String encodedString = new String(
	// org.apache.commons.codec.binary.Base64.encodeBase64(output
	// .toByteArray()));
	// String safeString = encodedString.replace('+', '-').replace('/',
	// '_');
	// return encodedString;
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }

	public static Bitmap getBitmap(Context context, String internalFileName) {
		try {
			Bitmap bm = BitmapFactory.decodeStream(context
					.openFileInput(internalFileName));
			if (bm != null) {
				return bm;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param context
	 * @param urlStr
	 * @return
	 */
	public static final String doHttpGet(Context context, String urlStr) {
		log("doHttpGet(" + context + "," + urlStr + ")");
		if (!isNetworkAvailable(context)) {
			return getNetworkErrorJson();
		}

		String result = Constants.HTTP.NETWORK_ERROR;

		URL url = null;
		HttpURLConnection urlConnection = null;

		try {
			StringBuffer resultBuffer = new StringBuffer();

			url = new URL(urlStr);
			urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.setConnectTimeout(10000);
			urlConnection.setReadTimeout(10000);

			InputStream is = urlConnection.getInputStream();
			InputStreamReader isreader = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isreader);

			String tempLine = null;
			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}

			result = resultBuffer.toString();
			log(result);

			if (is != null) {
				is.close();
			}

			if (isreader != null) {
				isreader.close();
			}

			if (reader != null) {
				reader.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			urlConnection.disconnect();
		}

		return  result;
	}


	public static String getNetworkErrorJson() {
		return Constants.HTTP.NETWORK_ERROR;
	}

	/**
	 * @param context
	 * @param urlStr
	 * @param params
	 * @return
	 */
	public static final String doHttpPost(Context context, String urlStr,
			List<NameValuePair> params) {
		if (!isNetworkAvailable(context)) {
			return getNetworkErrorJson();
		}

		String result = Constants.HTTP.NETWORK_ERROR;
		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams,
					HTTP.DEFAULT_CONTENT_CHARSET);
			HttpProtocolParams.setUseExpectContinue(httpParams, true);
			// 设置连接管理器的超时
			ConnManagerParams.setTimeout(httpParams, 10000);
			// 设置连接超时
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			// 设置socket超时
			HttpConnectionParams.setSoTimeout(httpParams, 10000);
			// 设置http https支持
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpPost httpPost = new HttpPost(urlStr);
			httpPost.addHeader("charset", HTTP.UTF_8);
			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			if (params != null && params.size() > 0) {
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(params,
							HTTP.UTF_8));
					result = EntityUtils.toString(client.execute(httpPost)
							.getEntity());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else {
				try {
					result = EntityUtils.toString(client.execute(httpPost)
							.getEntity());
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return  result;
	}



	/**
	 * 这里的httpclient加https，参考http://blog.csdn.net/lihenair/article/details/
	 * 17441169
	 * 
	 * @param context
	 * @param urlStr
	 * @param params
	 * @return
	 */
	public static final String doHttpsPost(Context context, String urlStr,
			List<NameValuePair> params) {
		if (!isNetworkAvailable(context)) {
			return getNetworkErrorJson();
		}

		String result = Constants.HTTP.NETWORK_ERROR;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); // 允许所有主机的验证
			HttpParams httpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams,
					HTTP.DEFAULT_CONTENT_CHARSET);
			HttpProtocolParams.setUseExpectContinue(httpParams, true);
			// 设置连接管理器的超时
			ConnManagerParams.setTimeout(httpParams, 10000);
			// 设置连接超时
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			// 设置socket超时
			HttpConnectionParams.setSoTimeout(httpParams, 10000);
			// 设置http https支持
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schReg.register(new Scheme("https", sf, 443));
			ClientConnectionManager conManager = new ThreadSafeClientConnManager(
					httpParams, schReg);
			HttpClient client = new DefaultHttpClient(conManager, httpParams);
			HttpPost httpPost = new HttpPost(urlStr);
			httpPost.addHeader("charset", HTTP.UTF_8);
			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			if (params != null && params.size() > 0) {
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(params,
							HTTP.UTF_8));
					result = EntityUtils.toString(client.execute(httpPost)
							.getEntity());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else {
				try {
					result = EntityUtils.toString(client.execute(httpPost)
							.getEntity());
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)

		throws NoSuchAlgorithmException, KeyManagementException,

		KeyStoreException, UnrecoverableKeyException {

			super(truststore);

			TrustManager tm = new X509TrustManager() {

				@Override
				public X509Certificate[] getAcceptedIssuers() {

					return null;

				}

				@Override
				public void checkClientTrusted(

				X509Certificate[] chain, String authType)

				throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(

				X509Certificate[] chain, String authType)

				throws java.security.cert.CertificateException {

				}

			};

			sslContext.init(null, new TrustManager[] { tm }, null);

		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,

		boolean autoClose) throws IOException, UnknownHostException {

			return sslContext.getSocketFactory().createSocket(socket, host,
					port,

					autoClose);

		}

		@Override
		public Socket createSocket() throws IOException {

			return sslContext.getSocketFactory().createSocket();

		}
	}

	public static boolean isNetworkAvailable(Context context) {

		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static void toast(Context context, String text, int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, duration);
		} else {
			mToast.setText(text);
			mToast.setDuration(duration);
		}

		mToast.show();
	}

	public static void toastNetworkError(Context context) {
		toast(context, "请求失败，请检查网络！", Toast.LENGTH_SHORT);
	}

	public static void toastServerError(Context context) {
		toast(context, "请求失败，服务器出错！", Toast.LENGTH_SHORT);
	}

	/** 根据当前状态判断是往onDuty或OffDuty */
	public static void gotoActivityMy(Activity activity) {

	}

	private static void log(String text) {
		Log.e("Utils", text);
	}

	/**
	 * 获取软件信息
	 * 
	 * @return 当前应用的版本号
	 */
	public static PackageInfo getPackageInfo(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void call(Activity context, String number) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number));
		context.startActivity(intent);
	}

	public static String doHttpsGet(Context context, String urlStr) {
		log("doHttpsGet(" + context + "," + urlStr + ")");
		if (!isNetworkAvailable(context)) {
			return getNetworkErrorJson();
		}

		HttpsURLConnection conn = null;
		InputStream in = null;
		ByteArrayOutputStream baos = null;
		String result = null;
		try {
			HttpsURLConnection
					.setDefaultHostnameVerifier(new HostnameVerifier() {

						@Override
						public boolean verify(String hostname,
								SSLSession session) {
							// TODO Auto-generated method stub
							return true;
						}

					});

			X509TrustManager tm = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(
						X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {

				}
			};

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { tm }, null);

			URL url = new URL(urlStr);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(sslContext.getSocketFactory());
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);

			in = conn.getInputStream();
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int read = 0;
			while ((read = in.read(buffer, 0, buffer.length)) != -1) {
				baos.write(buffer, 0, read);
			}
			buffer = baos.toByteArray();
			result = new String(buffer, "utf-8");

			baos.close();
			baos = null;

			in.close();
			in = null;

			conn.disconnect();
			conn = null;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				baos = null;
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}

			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}

		return result;
	}

	/**
	 * 保存在本地（内部存储）
	 * 
	 * @param ctx
	 * @param requesturl
	 * @param internalName
	 *            内部存储的文件名
	 * @return
	 */
	public static boolean doHttpDownload(Context ctx, String requesturl,
			String internalName) {
		log("doHttpDownload(" + ctx + "," + requesturl + "," + internalName
				+ ")");
		URL theurl = null;
		int responseCode = -1;
		InputStream is = null;
		byte[] buffer = new byte[1024];
		HttpURLConnection httpcon = null;
		boolean result = false;
		try {
			int lastsign = requesturl.lastIndexOf("/");
			String filename = requesturl.substring(lastsign + 1);
			requesturl = requesturl.substring(0, lastsign);
			requesturl = requesturl + "/"
					+ URLEncoder.encode(filename, "utf-8");
			theurl = new URL(requesturl);
			httpcon = (HttpURLConnection) theurl.openConnection();
			httpcon.setConnectTimeout(600000);
			httpcon.setReadTimeout(600000);
			httpcon.setUseCaches(false);
			httpcon.setInstanceFollowRedirects(true);
			httpcon.setRequestProperty("Cache-Control",
					"no-store,max-age=0,no-cache");
			httpcon.setRequestProperty("Expires", "0");
			httpcon.setRequestProperty("Pragma", "no-cache");
			httpcon.setRequestProperty("Connection", "close");
			httpcon.setRequestProperty("Charset", "utf-8");
			httpcon.setRequestMethod("GET");
			httpcon.setDoInput(true);
			httpcon.setDoOutput(false);
			responseCode = httpcon.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				int readed = 0;
				is = httpcon.getInputStream();
				FileOutputStream fos = null;
				fos = ctx.openFileOutput(internalName, Context.MODE_PRIVATE);
				do {
					readed = is.read(buffer);
					if (readed > 0) {
						fos.write(buffer, 0, readed);
					}
				} while (readed > 0);
				fos.flush();
				fos.close();
				fos = null;

				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
			}
			is = null;
			if (httpcon != null) {
				try {
					httpcon.disconnect();
				} catch (Exception e) {
				}
			}
			httpcon = null;
			theurl = null;
			buffer = null;
		}

		return result;
	}

	/**
	 * 保存在本地（SDCARD）
	 * 
	 * @param ctx
	 * @param requesturl
	 *            内部存储的文件名
	 * @return
	 */
	public static String doHttpDownload2(Context ctx, String requesturl) {
		log("doHttpDownload2(" + ctx + "," + requesturl + ")");
		URL theurl = null;
		int responseCode = -1;
		InputStream is = null;
		byte[] buffer = new byte[1024];
		HttpURLConnection httpcon = null;
		String result = null;
		try {
			int lastsign = requesturl.lastIndexOf("/");
			String filename = requesturl.substring(lastsign + 1);
			requesturl = requesturl.substring(0, lastsign);
			requesturl = requesturl + "/"
					+ URLEncoder.encode(filename, "utf-8");
			theurl = new URL(requesturl);
			httpcon = (HttpURLConnection) theurl.openConnection();
			httpcon.setConnectTimeout(600000);
			httpcon.setReadTimeout(600000);
			httpcon.setUseCaches(false);
			httpcon.setInstanceFollowRedirects(true);
			httpcon.setRequestProperty("Cache-Control",
					"no-store,max-age=0,no-cache");
			httpcon.setRequestProperty("Expires", "0");
			httpcon.setRequestProperty("Pragma", "no-cache");
			httpcon.setRequestProperty("Connection", "close");
			httpcon.setRequestProperty("Charset", "utf-8");
			httpcon.setRequestMethod("GET");
			httpcon.setDoInput(true);
			httpcon.setDoOutput(false);
			responseCode = httpcon.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				int readed = 0;
				is = httpcon.getInputStream();
				final File dir = ctx.getApplicationContext()
						.getExternalFilesDir(null);
				String path = dir.getAbsolutePath() + File.separator + filename;
				FileOutputStream fos = new FileOutputStream(path);
				do {
					readed = is.read(buffer);
					if (readed > 0) {
						fos.write(buffer, 0, readed);
					}
				} while (readed > 0);
				fos.flush();
				fos.close();
				fos = null;

				result = path;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
			}
			is = null;
			if (httpcon != null) {
				try {
					httpcon.disconnect();
				} catch (Exception e) {
				}
			}
			httpcon = null;
			theurl = null;
			buffer = null;
		}

		return result;
	}

	public static void showInstall(Context context, String filePath) {
		log("showInstall(" + filePath + ")");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File file = new File(filePath);
		if (file.exists()) {
			intent.setDataAndType(Uri.fromFile(new File(filePath)),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		}
	}

	public static void str2FileByBase64(String imgStr, String path) {
		// TODO Auto-generated method stub
		// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null)
			return;
		try {
			// Base64解码
			byte[] b = Base64.decode(imgStr, Base64.DEFAULT);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成Jpeg图片
			OutputStream out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			out.close();
		} catch (Exception e) {
		}
	}

	// public static String httpGet(Context context, String urlStr) {
	// HttpURLConnection conn = null;
	// InputStream in = null;
	// ByteArrayOutputStream baos = null;
	// String result = null;
	// try {
	// URL url = new URL(urlStr);
	// conn = (HttpURLConnection) url.openConnection();
	// conn.setConnectTimeout(10000);
	// conn.setReadTimeout(10000);
	//
	// in = conn.getInputStream();
	// baos = new ByteArrayOutputStream();
	// byte[] buffer = new byte[512];
	// int read = 0;
	// while ((read = in.read(buffer, 0, buffer.length)) != -1) {
	// baos.write(buffer, 0, read);
	// }
	// buffer = baos.toByteArray();
	// result = new String(buffer, "utf-8");
	//
	// baos.close();
	// baos = null;
	//
	// in.close();
	// in = null;
	//
	// conn.disconnect();
	// conn = null;
	//
	// } catch (MalformedURLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// if (baos != null) {
	// try {
	// baos.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// baos = null;
	// }
	// if (in != null) {
	// try {
	// in.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// in = null;
	// }
	//
	// if (conn != null) {
	// conn.disconnect();
	// conn = null;
	// }
	// }
	//
	// log(result);
	// return result;
	// }


	public static SpannableStringBuilder colorString(int start, int end,
			String str, int color) {
		SpannableStringBuilder spannable = new SpannableStringBuilder(str);// 用于可变字符串
		ForegroundColorSpan span = new ForegroundColorSpan(color);
		spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	// /* 上传文件至Server的方法 */
	// public static void uploadFile(File uploadFile, String actionUrl) {
	// String end = "\r\n";
	// String twoHyphens = "--";
	// String boundary = "*****";
	// try {
	// URL url = new URL(actionUrl);
	// HttpURLConnection con = (HttpURLConnection) url.openConnection();
	// /* 允许Input、Output，不使用Cache */
	// con.setDoInput(true);
	// con.setDoOutput(true);
	// con.setUseCaches(false);
	// /* 设置传送的method=POST */
	// con.setRequestMethod("POST");
	// /* setRequestProperty */
	// con.setRequestProperty("Connection", "Keep-Alive");
	// con.setRequestProperty("Charset", "UTF-8");
	// con.setRequestProperty("Content-Type",
	// "multipart/form-data;boundary=" + boundary);
	// /* 设置DataOutputStream */
	// DataOutputStream ds = new DataOutputStream(con.getOutputStream());
	// ds.writeBytes(twoHyphens + boundary + end);
	// ds.writeBytes("Content-Disposition: form-data; "
	// + "name=\"file1\";filename=\"" + newName + "\"" + end);
	// ds.writeBytes(end);
	// /* 取得文件的FileInputStream */
	// FileInputStream fStream = new FileInputStream(uploadFile);
	// /* 设置每次写入1024bytes */
	// int bufferSize = 1024;
	// byte[] buffer = new byte[bufferSize];
	// int length = -1;
	// /* 从文件读取数据至缓冲区 */
	// while ((length = fStream.read(buffer)) != -1) {
	// /* 将资料写入DataOutputStream中 */
	// ds.write(buffer, 0, length);
	// }
	// ds.writeBytes(end);
	// ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
	// /* close streams */
	// fStream.close();
	// ds.flush();
	// /* 取得Response内容 */
	// InputStream is = con.getInputStream();
	// int ch;
	// StringBuffer b = new StringBuffer();
	// while ((ch = is.read()) != -1) {
	// b.append((char) ch);
	// }
	// /* 将Response显示于Dialog */
	// log("上传成功" + b.toString().trim());
	// /* 关闭DataOutputStream */
	// ds.close();
	// } catch (Exception e) {
	// log("上传失败" + e);
	// }
	// }


}
