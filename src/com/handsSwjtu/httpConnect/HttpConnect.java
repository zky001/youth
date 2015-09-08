package com.handsSwjtu.httpConnect;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.conn.params.ConnRoutePNames;

import com.example.handsswjtu.MainActivity;
import com.handsSwjtu.common.ContextUtil;
import com.handsSwjtu.common.DatabaseHelper;
import com.handsSwjtu.common.Tools;

import android.R.bool;
import android.R.integer;
import android.R.string;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class HttpConnect {

	// private static final String
	// BASE_PATH="http://182.18.22.178:80/handsSwjtu/";
	// private static final String BASE_PATH =
	// "http://youth.swjtu.edu.cn/develops/handsSwjtu/";
	// private static final String BASE_PATH =
	// "http://192.168.1.140/develops/handsSwjtu/";
	private static final String BASE_PATH = "http://youth.swjtu.edu.cn/develops/handsSwjtu-v1.0.2/";
	//private static final String BASE_PATH = "http://192.168.1.102/handsSwjtu/";
	private static final String SECOND_CLASS_BASE_PATH = "http://youth.swjtu.edu.cn/tools/";
	//private static final String SECOND_CLASS_BASE_PATH = "http://192.168.1.101/tools/";
	public static final String SWJTU_KNOW_IMAGE_BASE_PATH = "http://youth.swjtu.edu.cn/develops/handsSwjtu/swjtu_know/swjtuKnowUploadFiles/";
	private static final String version = "version=1.0.2";
	private static CookieStore cookieStore;
	private static int isWapFlag;
	private static boolean NETWORKAVAILABLE;

	public HttpConnect() {
	}

	public void setMobileDataEnabled(boolean enabled) {
		Context context = ContextUtil.getInstance();
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// cm.setMobileDataEnabled(enabled);
	}

	public static void isWap(Context ctx) {

		// ConnectivityManager
		// manager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

		ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();
		if (netWorkInfo == null || !netWorkInfo.isAvailable()) {
			isWapFlag = 0;
		} else if (netWorkInfo.getTypeName().equals("mobile") && (netWorkInfo.getExtraInfo().equals("cmwap")||netWorkInfo.getExtraInfo().equals("3gwap")||netWorkInfo.getExtraInfo().equals("uniwap"))) {
			isWapFlag = 1;
		}else if (netWorkInfo.getTypeName().equals("mobile") && netWorkInfo.getExtraInfo().equals("ctwap")) {
			isWapFlag=2;
		}

	}

	public static String netWorkTest() {

		String url = "http://wap.baidu.com";
		isWapFlag = 0;
		return HttpConnect.getData(url);

	}

	public static String getValidateCode() {

		String urlGetValidateCode = BASE_PATH + "getValidateCode.php?" + version;
		return getData(urlGetValidateCode);
		// String result = null;
		// DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		// HttpGet request = new HttpGet(urlGetValidateCode);
		// // request.setHeader("Host", "youth.swjtu.edu.cn");
		// if (isWapFlag) {
		// HttpHost proxy = new HttpHost("10.0.0.172", 80);// 设置cmwap代理
		// defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		// proxy);
		//
		// }
		//
		// try {
		// HttpResponse response = defaultHttpClient.execute(request);
		// if (response.getStatusLine().getStatusCode() == 200) {
		// result = EntityUtils.toString(response.getEntity());
		// cookieStore = ((AbstractHttpClient)
		// defaultHttpClient).getCookieStore();
		// } else {
		// return result;
		// }
		// } catch (Exception e) {
		// Log.d("error", e.toString());
		// return null;
		// }
		// return result;

	}

	public static String Login2SportsCenter(String username, String password, String validateCode) {

		String urlLogin2SportsCenter = BASE_PATH + "sportsCenter.php?" + version + "&username=" + username
				+ "&password=" + password + "&verificationCode=" + validateCode + "&method=login";
		return getData(urlLogin2SportsCenter);
	}

	public static String GetSportsTimeFromServer(String semester, String week) {

		String urlLogin2SportsCenter = BASE_PATH + "sportsCenter.php?" + version + "&semester=" + semester + "&week="
				+ week + "&method=getSportTime";
		return getData(urlLogin2SportsCenter);

	}

	public static String getBookListFromServer(String ddlSearchField, String DropDownList_Sort, String order,
			String txtSearchContent, String ddlDistribution, String ddlBookType, String txtPageIndex) {

		String urlLogin2SportsCenter = BASE_PATH + "Library.php?" + version + "&ddlSearchField=" + ddlSearchField
				+ "&DropDownList_Sort=" + DropDownList_Sort + "&DropDownList_OrderStyle=" + order
				+ "&txtSearchContent=" + txtSearchContent + "&ddlDistribution=" + ddlDistribution + "&ddlBookType="
				+ ddlBookType + "&btnPageIndex=Go%E6%A3%80+%E7%B4%A2&txtPageIndex=" + txtPageIndex + "&order=" + order;
		urlLogin2SportsCenter = urlLogin2SportsCenter.replaceAll("\\s+", "+");
		String result = null;
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlLogin2SportsCenter);
		// request.setHeader("Host", "youth.swjtu.edu.cn");
		if (isWapFlag==1) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80);// 设置cmwap代理
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}else if(isWapFlag==2){
			HttpHost proxy = new HttpHost("10.0.0.200", 80);// 设置cmwap代理
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		if (cookieStore != null) {
			defaultHttpClient.setCookieStore(cookieStore);
		}
		try {
			HttpResponse response = defaultHttpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				cookieStore = ((AbstractHttpClient) defaultHttpClient).getCookieStore();
				Log.i("error", "sagag");

			} else {
				result = null;
			}
		} catch (Exception e) {
			Log.i("error", e.toString());
			result = null;
		}
		return result;

	}

	public static String getBookDetailFromServer(String sysid) {

		String urlLogin2SportsCenter = BASE_PATH + "bookDetail.php?" + version + "&sysid=" + sysid;
		String result = null;
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlLogin2SportsCenter);
		// request.setHeader("Host", "youth.swjtu.edu.cn");
		if (isWapFlag==1) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80);// 设置cmwap代理
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}else if(isWapFlag==2){
			HttpHost proxy = new HttpHost("10.0.0.200", 80);// 设置cmwap代理
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		if (cookieStore != null) {
			defaultHttpClient.setCookieStore(cookieStore);
		}
		try {
			HttpResponse response = defaultHttpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				cookieStore = ((AbstractHttpClient) defaultHttpClient).getCookieStore();
				Log.i("error", "sagag");

			} else {
				result = null;
			}
		} catch (Exception e) {
			Log.i("error", e.toString());
			result = null;
		}
		return result;

	}

	public static String getEmptyClassroomInfoFromServer(String dayOffset, String building) {

		String url = BASE_PATH + "getemPtyClassroom.php?" + version + "&building=" + building + "&dayoffset="
				+ dayOffset;
		return HttpConnect.getData(url);
	}

	public static String getScheduleFromServer(String username, String password) {
		String url = BASE_PATH + "schedule.php?" + version + "&username=" + username + "&password=" + password;
		return HttpConnect.getData(url);
	}

	public static String getNewsList(String pageNo, int newsTypeNow) {
		String url = BASE_PATH + "newsList.php?" + version + "&pageNo=" + pageNo + "&newsTypeNow=" + newsTypeNow;
		return HttpConnect.getData(url);
	}

	public static String getNewsContent(String newsCode, int newsTypeNow) {
		String url = BASE_PATH + "newsDetail.php?" + version + "&newsCode=" + newsCode + "&newsTypeNow=" + newsTypeNow;
		return HttpConnect.getData(url);
	}

	public static String getSwjtuKnowListContent(int pageNo, String userNow) {
		String url = BASE_PATH + "swjtu_know/viewQuestion.php?" + version + "&pageNo=" + String.valueOf(pageNo)
				+ "&userNow=" + userNow;
		return getData(url);
	}

	public static String getSwjtuKnowDetailContent(int questionId) {
		String url = BASE_PATH + "swjtu_know/viewQuestionDetail.php?" + version + "&questionId="
				+ String.valueOf(questionId);
		return getData(url);
	}

	public static String login2IndividualCenter(String stuCode, String password, String validateCode) {
		String url = BASE_PATH + "login.php?" + version + "&stuCode=" + stuCode + "&passWord=" + password
				+ "&validateCode=" + validateCode;
		return getData(url);
	}

	public static String setIcon(List<NameValuePair> params) {
		String url = BASE_PATH + "swjtu_chat/setIcon.php?" + version;
		return postData(url, params);
	}

	public static String addSwjtuKnowQuestion(List<NameValuePair> params) {
		String url = BASE_PATH + "swjtu_know/addQuestion.php?" + version;
		return postData(url, params);
	}

	public static String addSwjtuKnowAnswer(List<NameValuePair> params) {
		String url = BASE_PATH + "swjtu_know/addAnswer.php?" + version;
		return postData(url, params);

	}

	public static String addSwjtuKnowAnswerReply(List<NameValuePair> params) {
		String url = BASE_PATH + "swjtu_know/addAnswerReply.php?" + version;
		return postData(url, params);

	}

	public static String handleMyQuestion(List<NameValuePair> params) {
		String url = BASE_PATH + "swjtu_know/handleMyQuestion.php?" + version;
		return postData(url, params);
	}

	// public static String addFriend(String searchString,) {
	//
	// }

	public static String searchUser(List<NameValuePair> params) {
		String url = BASE_PATH + "swjtu_chat/searchUser.php?" + version;
		return postData(url, params);
	}

	public static String addNewFriend(String stuCode) {
		String url = BASE_PATH + "swjtu_chat/addFriend.php?" + version + "&stuCode=" + stuCode;
		return getData(url);
	}

	public static String getFriendList() {
		String url = BASE_PATH + "swjtu_chat/getFriendList.php?" + version;
		return getData(url);
	}

	public static String registerPush(String pushUserId, String pushChannelId, String userNow) {
		String url = BASE_PATH + "registerPush.php?" + version + "&pushUserId=" + pushUserId + "&pushChannelId="
				+ pushChannelId + "&userNow=" + userNow;
		return getData(url);
	}

	public static String setBaiduPush(String pushSetting, String userNow, String pushUserId, String pushChannelId) {
		String url = BASE_PATH + "pushSetting.php?" + version + "&userNow=" + userNow + "&pushChannelId="
				+ pushChannelId + "&pushUserId=" + pushUserId + "&pushSetting=" + pushSetting;
		return getData(url);
	}

	public static String sendMsg(List<NameValuePair> params) {
		String url = BASE_PATH + "sendMessage.php?" + version;
		return postData(url, params);
	}

	public static String logout() {
		String url = BASE_PATH + "logout.php?" + version;
		return getData(url);
	}
	
	public static String checkUpdate() {
		String url = BASE_PATH + "checkAppUpdate.php?" + version;
		return getData(url);
	}

	public static String login2SecondClassCenter(String stuCode, String passWord, String validateCode) {
		String url = SECOND_CLASS_BASE_PATH + "mobileclientserver.ashx?" + version + "&stuCode=" + stuCode
				+ "&passWord=" + passWord + "&action=login";
		return getData(url);
	}

	public static String getSecondClassList(int index, int cateGory) {
		String url = SECOND_CLASS_BASE_PATH + "mobileclientserver.ashx?" + version + "&action=selc" + "&pagesize=15"
				+ "&index=" + String.valueOf(index);
		return getData(url);
	}

	public static String getMyClassList(int index) {
		String url = SECOND_CLASS_BASE_PATH + "mobileclientserver.ashx?" + version + "&action=myc" + "&pagesize=15"
				+ "&index=" + String.valueOf(index);
		return getData(url);
	}

	public static String getSecondClassDetail(String courseId) {
		String url = SECOND_CLASS_BASE_PATH + "mobileclientserver.ashx?" + version + "&action=detail" + "&cid="
				+ courseId;
		return getData(url);
	}

	public static String chooseCourse(String courseId) {
		String url = SECOND_CLASS_BASE_PATH + "mobileclientserver.ashx?" + version + "&action=sel" + "&cid=" + courseId;
		return getData(url);
	}

	public static String deleteCourse(String courseId) {
		String url = SECOND_CLASS_BASE_PATH + "mobileclientserver.ashx?" + version + "&action=del" + "&cid=" + courseId;
		return getData(url);
	}
	


	public static String postData(String url, List<NameValuePair> params) {
		String result = null;
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		// request.setHeader("Host", "youth.swjtu.edu.cn");
		isWap(ContextUtil.getInstance());
		defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		if (isWapFlag==1) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80);// 设置cmwap代理
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}else if(isWapFlag==2){
			HttpHost proxy = new HttpHost("10.0.0.200", 80);// 设置cmwap代理
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		if (cookieStore != null) {
			defaultHttpClient.setCookieStore(cookieStore);
		}
		try {
			request.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = defaultHttpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				//Toast.makeText(ContextUtil.getInstance(), result, Toast.LENGTH_SHORT).show();
				cookieStore = ((AbstractHttpClient) defaultHttpClient).getCookieStore();
				Log.i("error", result);
			} else {
				result = null;
			}
		} catch (Exception e) {
			Log.i("error", e.toString());
			result = null;
		} finally {
			return result;
		}

	}

	public static String getData(String url) {
		String result = null;
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		// request.setHeader("Host", "youth.swjtu.edu.cn");
		isWap(ContextUtil.getInstance());
		defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		if (isWapFlag==1) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80);// 设置cmwap代理
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}else if(isWapFlag==2){
			HttpHost proxy = new HttpHost("10.0.0.200", 80);// 设置cmwap代理
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		CookieStore cookieStor=cookieStore;
		if (cookieStore != null) {
			defaultHttpClient.setCookieStore(cookieStore);
		}
		try {
			HttpResponse response = defaultHttpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				//Toast.makeText(ContextUtil.getInstance(), result, Toast.LENGTH_SHORT).show();
				cookieStore = ((AbstractHttpClient) defaultHttpClient).getCookieStore();
				Log.i("error", result);
			} else {
				result = null;
			}
		} catch (Exception e) {
			Log.i("error", e.toString());
			result = null;
		} finally {
			return result;
		}

	}

	public static InputStream getHttpFile(String urlWaitToGet) {
		String url = BASE_PATH + urlWaitToGet;
		InputStream result = null;
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		// request.setHeader("Host", "youth.swjtu.edu.cn");
		isWap(ContextUtil.getInstance());
		defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		if (isWapFlag==1) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80);// 设置cmwap代理
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}else if(isWapFlag==2){
			HttpHost proxy = new HttpHost("10.0.0.200", 80);// 设置cmwap代理
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		if (cookieStore != null) {
			defaultHttpClient.setCookieStore(cookieStore);
		}
		try {
			HttpResponse response = defaultHttpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = response.getEntity().getContent();
				cookieStore = ((AbstractHttpClient) defaultHttpClient).getCookieStore();

			} else {
				result = null;
			}
		} catch (Exception e) {
			Log.i("error", e.toString());
			result = null;
		} finally {
			return result;
		}

	}

}
