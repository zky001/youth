package com.handsSwjtu.common;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.handsswjtu.R;

import android.R.bool;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.location.Address;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.handsswjtu.Schedule;
import com.example.handsswjtu.R.color;
import com.example.handsswjtu.SecondClassDetailActivity;
import com.example.handsswjtu.SwjtuChatWithFriendActivity;
import com.handsSwjtu.Objects.NewsChildInfo;
import com.handsSwjtu.Objects.NewsEntity;
import com.handsSwjtu.Objects.NewsListData;
import com.handsSwjtu.Objects.SecondClassDetailEntity;
import com.handsSwjtu.Objects.UserInfoEntity;
import com.handsSwjtu.Objects.SportTimeResult;
import com.handsSwjtu.Objects.SwjtuKnowAnswerReplyEntity;
import com.handsSwjtu.Objects.SwjtuKnowEntity;
import com.handsSwjtu.Objects.SwjtuKnowAnswerEntity;
import com.handsSwjtu.Objects.SwjtuKnowDetailEntity;
import com.handsSwjtu.Objects.SwjtuChatMsgEntity;
import com.handsSwjtu.httpConnect.HttpConnect;

public class DataProvider {

	public static int scheduleProvider(SQLiteDatabase databaseHelper, String username, String password) {

		boolean isAllNull = true;
		String resultString = HttpConnect.getScheduleFromServer(username, password);
		String sql = "insert into schedule(code,name,classNum,teacher,campus,time,location,classroom,dayTime,weekDay)values(?,?,?,?,?,?,?,?,?,?)";
		if (resultString != null) {
			try {
				JSONArray dayTimes = new JSONArray(resultString);
				for (int i = 0; i < dayTimes.length(); i++) {
					JSONArray weekDays = (JSONArray) dayTimes.opt(i);
					for (int j = 0; j < weekDays.length(); j++) {
						JSONObject classInfos = (JSONObject) weekDays.opt(j);
						if (isAllNull) {
							for (int k = 1; k < 9; k++) {
								if (!(classInfos.getString(String.valueOf(k)).equals("null"))) {
									isAllNull = false;
								}
							}
						}

						databaseHelper.execSQL(sql, new Object[] { classInfos.getString("1"),
								classInfos.getString("2"), classInfos.getString("3"), classInfos.getString("4"),
								classInfos.getString("5"), classInfos.getString("6"), classInfos.getString("7"),
								classInfos.getString("8"), i, j });
					}
				}
				if (isAllNull) {
					return 2;
				} else {
					return 1;
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 2;
			}

		} else {
			return 3;
		}

	}

	public static NewsListData loadMoreNewsList(NewsListData newsListData, int pageNo, int newsTypeNow) {

		List<Map<String, Object>> groupLists;
		List<List<Map<String, Object>>> childListsTotal;
		List<NewsChildInfo> newsChildInfos = newsChildInfosFromJson(pageNo, newsTypeNow);
		groupLists = newsListData.getGroupLists();
		childListsTotal = newsListData.getChildListsTotal();
		if (newsChildInfos != null) {
			List<Map<String, Object>> childlList = new ArrayList<Map<String, Object>>();
			Map<String, Object> childMap = new HashMap<String, Object>();
			// if ((newsChildInfos.get(0).getNewsTime()).equals((String)
			// groupLists.get(groupLists.size() - 1).get(
			// "newsTime"))){
			// childlList=newsListData.getChildListsTotal().get(newsListData.getChildListsTotal().size()-1);
			// }
			int i = 0;
			for (; i < newsChildInfos.size(); i++) {
				String a = newsChildInfos.get(i).getNewsTime();
				String bString = (String) groupLists.get(groupLists.size() - 1).get("newsTime");
				if (!(newsChildInfos.get(i).getNewsTime()).equals((String) groupLists.get(groupLists.size() - 1).get(
						"newsTime"))) {
					break;
				} else {
					childMap = new HashMap<String, Object>();
					// String cString = cursor.getString(1);
					childMap.put("newsCode", newsChildInfos.get(i).getNewsCode());
					childMap.put("newsTitle", newsChildInfos.get(i).getNewsTitle());
					// childMap.put("news_time", cursor.getString(2));
					newsListData.childListsTotal.get(newsListData.childListsTotal.size() - 1).add(childMap);
				}
			}

			// newsListData.groupLists.remove(newsListData.groupLists.size()-1);
			boolean flag = true;
			for (; i < newsChildInfos.size(); i++) {
				String a = newsChildInfos.get(i).getNewsTime();
				String bString = (String) groupLists.get(groupLists.size() - 1).get("newsTime");
				if (!(newsChildInfos.get(i).getNewsTime()).equals((String) groupLists.get(groupLists.size() - 1).get(
						"newsTime"))) {
					Map<String, Object> groupMapTemp = new HashMap<String, Object>();
					groupMapTemp.put("newsTime", newsChildInfos.get(i).getNewsTime());
					newsListData.groupLists.add(groupMapTemp);
					if (flag) {
						flag = false;
					} else {
						newsListData.childListsTotal.add(childlList);
					}
					childlList = new ArrayList<Map<String, Object>>();
					childMap = new HashMap<String, Object>();
					childMap.put("newsCode", newsChildInfos.get(i).getNewsCode());
					childMap.put("newsTitle", newsChildInfos.get(i).getNewsTitle());
					// childMap.put("news_time", cursor.getString(2));
					childlList.add(childMap);
					// childlList=new ArrayList<Map<String,Object>>();

				} else {
					childMap = new HashMap<String, Object>();
					// String cString = cursor.getString(1);
					childMap.put("newsCode", newsChildInfos.get(i).getNewsCode());
					childMap.put("newsTitle", newsChildInfos.get(i).getNewsTitle());
					// childMap.put("news_time", cursor.getString(2));
					childlList.add(childMap);
				}

			}
			newsListData.childListsTotal.add(childlList);
			return newsListData;
		} else {
			return null;
		}
		// newsListData.setChildListsTotal(childListsTotal);
		// newsListData.setGroupLists(groupLists);

	}

	public static List<NewsChildInfo> newsChildInfosFromJson(int pageNo, int newsTypeNow) {

		List<NewsChildInfo> newsChildInfos = new ArrayList<NewsChildInfo>();
		String resultString = HttpConnect.getNewsList(String.valueOf(pageNo), newsTypeNow);
		try {
			JSONObject jsonObjectTotal = new JSONObject(resultString);
			JSONArray newsCodeArray = (JSONArray) jsonObjectTotal.getJSONArray("1");
			JSONArray newsTitleArray = (JSONArray) jsonObjectTotal.getJSONArray("2");
			JSONArray newsTimeArray = (JSONArray) jsonObjectTotal.getJSONArray("3");
			for (int i = 0; i < newsCodeArray.length(); i++) {
				NewsChildInfo newsChildInfo = new NewsChildInfo();
				newsChildInfo.setNewsCode(newsCodeArray.optString(i));
				newsChildInfo.setNewsTitle(newsTitleArray.optString(i));
				newsChildInfo.setNewsTime(newsTimeArray.optString(i));
				newsChildInfo.setNewsType(pageNo);
				newsChildInfos.add(newsChildInfo);

			}
			return newsChildInfos;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static boolean newsListToDatabase(Context context, int newsTypeNow) {

		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		String resultString = HttpConnect.getNewsList(String.valueOf(1), newsTypeNow);
		String sqlString = "delete from NewsList where news_type=" + String.valueOf(newsTypeNow);
		if (resultString == null) {
			return false;
		} else {

			databaseHelper.dml(sqlString, new String[] {});
			sqlString = "insert into NewsList(news_code,news_title,news_time,news_type)values(?,?,?,?)";
			// Cursor
			// cursor=databaseHelper.dql("select * from deanOfficeNewsList",
			// new String[]{});
			// boolean a=cursor.moveToFirst();

			try {
				JSONObject jsonObjectTotal = new JSONObject(resultString);
				JSONArray newsCodeArray = (JSONArray) jsonObjectTotal.getJSONArray("1");
				JSONArray newsTitleArray = (JSONArray) jsonObjectTotal.getJSONArray("2");
				JSONArray newsTimeArray = (JSONArray) jsonObjectTotal.getJSONArray("3");

				for (int i = 0; i < newsCodeArray.length(); i++) {
					String newsCode = newsCodeArray.optString(i);
					String newsTitle = newsTitleArray.optString(i);
					String newsTime = newsTimeArray.optString(i);
					databaseHelper.dml(sqlString,
							new String[] { newsCode, newsTitle, newsTime, String.valueOf(newsTypeNow) });
				}

			} catch (Exception e) {
				// TODO: handle exception
				Log.i("json", e.toString());
			}
		}

		return true;
	}

	public static NewsChildInfo newsChildInfoFromDataBase(int newsType, int position) {

		NewsChildInfo newsChildInfo = new NewsChildInfo();
		DatabaseHelper databaseHelper = new DatabaseHelper(ContextUtil.getInstance());
		String sqlString = "select news_code,news_title,news_time from NewsList where news_type=" + newsType;
		Cursor cursor = databaseHelper.dql(sqlString, null);
		cursor.move(position);
		newsChildInfo.setNewsCode(cursor.getString(0));
		newsChildInfo.setNewsTitle(cursor.getString(1));
		newsChildInfo.setNewsTime(cursor.getString(2));
		newsChildInfo.setNewsType(newsType);
		return newsChildInfo;

	}

	public static NewsListData newsListFromDataBase(int newsType) {

		NewsListData newsListData = new NewsListData();
		List<Map<String, Object>> groupLists = new ArrayList<Map<String, Object>>();
		List<List<Map<String, Object>>> childListsTotal = new ArrayList<List<Map<String, Object>>>();
		DatabaseHelper databaseHelper = new DatabaseHelper(ContextUtil.getInstance());
		String sqlString = "select news_code,news_title,news_time from NewsList where news_type=" + newsType;
		Cursor cursor = databaseHelper.dql(sqlString, null);
		if (cursor.moveToFirst()) {
			List<Map<String, Object>> childlList = new ArrayList<Map<String, Object>>();
			Map<String, Object> groupMapFirst = new HashMap<String, Object>();
			groupMapFirst.put("newsTime", cursor.getString(2));
			groupLists.add(groupMapFirst);
			Map<String, Object> childMap = new HashMap<String, Object>();
			do {

				String aString = (String) groupLists.get(groupLists.size() - 1).get("newsTime");
				String bString = cursor.getString(2);
				if (!(cursor.getString(2)).equals((String) groupLists.get(groupLists.size() - 1).get("newsTime"))) {
					Map<String, Object> groupMapTemp = new HashMap<String, Object>();
					groupMapTemp.put("newsTime", cursor.getString(2));
					groupLists.add(groupMapTemp);
					childListsTotal.add(childlList);
					childlList = new ArrayList<Map<String, Object>>();
					childMap = new HashMap<String, Object>();
					childMap.put("newsCode", cursor.getString(0));
					childMap.put("newsTitle", cursor.getString(1));
					// childMap.put("news_time", cursor.getString(2));
					childlList.add(childMap);
				} else {
					childMap = new HashMap<String, Object>();
					String cString = cursor.getString(1);
					childMap.put("newsCode", cursor.getString(0));
					childMap.put("newsTitle", cursor.getString(1));
					// childMap.put("news_time", cursor.getString(2));
					childlList.add(childMap);
				}
			} while (cursor.moveToNext());
			childListsTotal.add(childlList);
		}
		newsListData.setGroupLists(groupLists);
		newsListData.setChildListsTotal(childListsTotal);

		return newsListData;
	}

	public static NewsEntity newsDetailProvider(String newsCode, int newsTypeNow) {

		NewsEntity newsEntity = null;
		try {
			String newsEntityString = HttpConnect.getNewsContent(newsCode, newsTypeNow);
			JSONObject jsonObject = new JSONObject(newsEntityString);
			newsEntity = new NewsEntity();

			newsEntity.setIswebPage(jsonObject.getBoolean("isWebPage"));
			if (newsEntity.isWebPage()) {
				newsEntity.setNewsContent(jsonObject.getString("content"));
				newsEntity.setHaveImg(jsonObject.getBoolean("haveImg"));
				if (newsEntity.isHaveImg()) {
					ArrayList<Bitmap> newsImg = new ArrayList<Bitmap>();
					for (int i = 0; i < jsonObject.getJSONArray("imgString").length(); i++) {
						// String
						// aString=(jsonObject.getJSONArray("imgString")).optString(i);
						Bitmap bitmapTemp = Tools.String2Bitmap(jsonObject.getJSONArray("imgString").optString(i));
						newsImg.add(bitmapTemp);
					}
					;
					newsEntity.setNewsImg(newsImg);
				}

			}

			// newsEntity.setNewsTitle(jsonObject.getString("newsTitle"));

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			newsEntity = null;
		} finally {

			return newsEntity;
		}

	}

	public static boolean sportTimeResultProvider(List<SportTimeResult> sportTimeResults, int weekNo) {
		String resultString = HttpConnect.GetSportsTimeFromServer(String.valueOf(2), String.valueOf(weekNo));
		if (resultString == null) {
			return false;
		}
		try {
			JSONArray jsonArray = new JSONArray(resultString);
			// sportTimeResults = new ArrayList<SportTimeResult>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
				SportTimeResult sportTimeResult = new SportTimeResult();
				sportTimeResult.setSportsPlace(jsonObject.getString("sportsplace"));
				sportTimeResult.setEnterTime(jsonObject.getString("entertime"));
				sportTimeResult.setExitTime(jsonObject.getString("exittime"));
				sportTimeResult.setTotalTime(jsonObject.getString("totaltime"));
				sportTimeResult.setValidTime(jsonObject.getString("validtime"));
				sportTimeResults.add(sportTimeResult);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sportTimeResults = null;
		}
		return true;
	}

	public static boolean swjtuKnowListMapListProvider(List<Map<String, Object>> swjtuKnowEntitiesMapList, int pageNo,
			String userNow) {
		String resultString = HttpConnect.getSwjtuKnowListContent(pageNo, userNow);
		List<Map<String, Object>> swjtuKnowEntitiesMapListTemp = swjtuKnowEntitiesMapList;
		if (resultString == null) {
			return false;
		}
		try {

			JSONObject jsonObject = new JSONObject(resultString);
			JSONArray idArray = (JSONArray) jsonObject.get("id");
			JSONArray titleArray = (JSONArray) jsonObject.get("title");
			JSONArray contentArray = (JSONArray) jsonObject.get("content");
			JSONArray categoryArray = (JSONArray) jsonObject.get("category");
			JSONArray viewNumArray = (JSONArray) jsonObject.get("viewnum");
			JSONArray replyNumArray = (JSONArray) jsonObject.get("replynum");
			JSONArray questionFromArray = (JSONArray) jsonObject.get("question_from");
			JSONArray isSolvedArray = (JSONArray) jsonObject.get("is_solved");
			JSONArray ctimeArray = (JSONArray) jsonObject.get("ctime");
			for (int i = 0; i < titleArray.length(); i++) {
				Map<String, Object> swjtuKnowMap = new HashMap<String, Object>();
				// SwjtuKnowEntity swjtuKnowEntity=new SwjtuKnowEntity();
				int isSolvedImg;
				swjtuKnowMap.put("id", idArray.optInt(i));
				swjtuKnowMap.put("title", titleArray.optString(i));
				swjtuKnowMap.put("content", contentArray.optString(i));
				swjtuKnowMap.put("category", categoryArray.optInt(i));
				swjtuKnowMap.put("viewNum", viewNumArray.optInt(i));
				swjtuKnowMap.put("replyNum", replyNumArray.optInt(i));
				swjtuKnowMap.put("questionFrom", questionFromArray.optString(i));
				if (isSolvedArray.optBoolean(i)) {
					isSolvedImg = R.drawable.green_check_mark;
				} else {
					isSolvedImg = R.drawable.icon_closed;
				}
				swjtuKnowMap.put("isSolved", isSolvedImg);
				swjtuKnowMap.put("ctime", ctimeArray.optString(i));
				// swjtuKnowEntity.setTitle(titleArray.optString(i));
				// swjtuKnowEntity.setContent(contentArray.optString(i));
				// swjtuKnowEntity.setCategory(categoryArray.optInt(i));
				// swjtuKnowEntity.setViewNum(viewNumArray.optInt(i));
				// swjtuKnowEntity.setReplyNum(replyNumArray.optInt(i));
				// swjtuKnowEntity.setQuestionFrom(questionFromArray.optString(i));
				// swjtuKnowEntity.setSolved(isSolvedArray.optBoolean(i));
				// swjtuKnowEntity.setcTime(ctimeArray.optString(i));
				swjtuKnowEntitiesMapList.add(swjtuKnowMap);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			swjtuKnowEntitiesMapList = swjtuKnowEntitiesMapListTemp;
		}
		return true;
	}

	public static boolean swjtuKnowDetailProvider(SwjtuKnowEntity swjtuKnowEntity,
			List<SwjtuKnowAnswerEntity> swjtuKnowAnswerList,
			Map<String, List<SwjtuKnowAnswerReplyEntity>> swjtuKnowAnswerReplyMap, int questionId) {
		String resultString = HttpConnect.getSwjtuKnowDetailContent(questionId);
		SwjtuKnowEntity swjtuKnowEntityTemp = swjtuKnowEntity;
		List<SwjtuKnowAnswerEntity> swjtuKnowAnswerListTemp = swjtuKnowAnswerList;
		Map<String, List<SwjtuKnowAnswerReplyEntity>> swjtuKnowAnswerReplyListsTemp = swjtuKnowAnswerReplyMap;
		if (resultString == null) {
			return false;
		}
		try {

			JSONObject swjtuKnowDetail = new JSONObject(resultString);
			JSONObject questionDataJsonObject = swjtuKnowDetail.getJSONObject("question_data");
			swjtuKnowEntity.setQuestionId(questionDataJsonObject.getInt("id"));
			swjtuKnowEntity.setTitle(questionDataJsonObject.getString("title"));
			swjtuKnowEntity.setContent(questionDataJsonObject.getString("content"));
			swjtuKnowEntity.setCategory(questionDataJsonObject.getInt("category"));
			swjtuKnowEntity.setViewNum(questionDataJsonObject.getInt("viewnum"));
			swjtuKnowEntity.setReplyNum(questionDataJsonObject.getInt("replynum"));
			swjtuKnowEntity.setCtime(questionDataJsonObject.getString("ctime"));
			swjtuKnowEntity.setQuestionFrom(questionDataJsonObject.getString("question_from"));
			swjtuKnowEntity.setSolved(questionDataJsonObject.getBoolean("is_solved"));
			// ArrayList<Bitmap> questionImgBitmaps=new ArrayList<Bitmap>();
			ArrayList<String> questionImgFiles = new ArrayList<String>();
			try {
				JSONArray questionImgArray = questionDataJsonObject.getJSONArray("images");
				for (int i = 0; i < questionImgArray.length(); i++) {
					File file = new File(Environment.getExternalStorageDirectory().toString()
							+ "/handsSwjtu/swjtu_know/thumbs", questionImgArray.optString(i));
					if (!file.exists()) {
						// Bitmap bitmap =
						// Tools.getHttpBitmap(HttpConnect.SWJTU_KNOW_IMAGE_BASE_PATH+"thumbs/"
						// + questionImgArray.optString(i));
						// Tools.writeImg2SdCard(bitmap,
						// "/handsSwjtu/swjtu_know/thumbs",questionImgArray.optString(i));
						Tools.writeHttpFile2SdCard(
								"swjtu_know/swjtuKnowUploadFiles/thumbs/" + questionImgArray.optString(i),
								"/handsSwjtu/swjtu_know/thumbs", questionImgArray.optString(i));
					}

					questionImgFiles.add(questionImgArray.optString(i));

				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			swjtuKnowEntity.setQuestionImgFiles(questionImgFiles);

			JSONObject answerDataJsonObject = swjtuKnowDetail.getJSONObject("answer_data");
			JSONArray answerIdArray = answerDataJsonObject.getJSONArray("answer_id");
			JSONArray answerContentArray = answerDataJsonObject.getJSONArray("answer_content");
			JSONArray answerFromArray = answerDataJsonObject.getJSONArray("answer_from");
			JSONArray answersortIdArray = answerDataJsonObject.getJSONArray("sort_id");
			JSONArray answerCtimeArray = answerDataJsonObject.getJSONArray("ctime");
			JSONArray answerIsBestAnswerArray = answerDataJsonObject.getJSONArray("is_best_answer");

			for (int i = 0; i < answerIdArray.length(); i++) {
				SwjtuKnowAnswerEntity swjtuKnowAnswerEntity = new SwjtuKnowAnswerEntity();
				swjtuKnowAnswerEntity.setAnswerId(answerIdArray.optInt(i));
				swjtuKnowAnswerEntity.setAnswerContent(answerContentArray.optString(i));
				swjtuKnowAnswerEntity.setAnswerFrom(answerFromArray.optString(i));
				swjtuKnowAnswerEntity.setSortId(answersortIdArray.optInt(i));
				swjtuKnowAnswerEntity.setCtime(answerCtimeArray.optString(i));
				swjtuKnowAnswerEntity.setBestAnswer(answerIsBestAnswerArray.optBoolean(i));
				swjtuKnowAnswerList.add(swjtuKnowAnswerEntity);
			}

			JSONObject anReJsonObject = swjtuKnowDetail.getJSONObject("answer_reply_data");
			for (int i = 0; i < swjtuKnowAnswerList.size(); i++) {
				List<SwjtuKnowAnswerReplyEntity> swjtuKnowAnswerReplyEntities = new ArrayList<SwjtuKnowAnswerReplyEntity>();
				if (anReJsonObject.has("reply" + String.valueOf(i))) {
					JSONObject jsonObject = anReJsonObject.getJSONObject("reply" + i);
					JSONArray anReIdArray = jsonObject.getJSONArray("reply_id");
					JSONArray anReContentArray = jsonObject.getJSONArray("reply_content");
					JSONArray anReFromArray = jsonObject.getJSONArray("reply_from");
					JSONArray anReToArray = jsonObject.getJSONArray("reply_to");
					JSONArray anReAnswerIdArray = jsonObject.getJSONArray("answer_id");
					JSONArray anReCtimeArray = jsonObject.getJSONArray("ctime");
					for (int j = 0; j < anReIdArray.length(); j++) {
						SwjtuKnowAnswerReplyEntity swjtuKnowAnswerReplyEntity = new SwjtuKnowAnswerReplyEntity();
						swjtuKnowAnswerReplyEntity.setReplyId(anReIdArray.optInt(j));
						swjtuKnowAnswerReplyEntity.setReplyContent(anReContentArray.optString(j));
						swjtuKnowAnswerReplyEntity.setReplyFrom(anReFromArray.optString(j));
						swjtuKnowAnswerReplyEntity.setReplyTo(anReToArray.optString(j));
						swjtuKnowAnswerReplyEntity.setAnswerId(anReAnswerIdArray.optInt(j));
						swjtuKnowAnswerReplyEntity.setCtime(anReCtimeArray.optString(j));
						swjtuKnowAnswerReplyEntities.add(swjtuKnowAnswerReplyEntity);
					}
				}
				swjtuKnowAnswerReplyMap.put("reply" + i, swjtuKnowAnswerReplyEntities);
				// swjtuKnowAnswerReplyLists.add(swjtuKnowAnswerReplyEntities);

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			swjtuKnowEntity = swjtuKnowEntityTemp;
			swjtuKnowAnswerReplyMap = swjtuKnowAnswerReplyListsTemp;
			swjtuKnowAnswerList = swjtuKnowAnswerListTemp;
		}
		return true;
	}

	public static int searchUserEntityProvider(List<UserInfoEntity> searchUserResultEntities,
			List<NameValuePair> params, String errorMsg) {
		String resultString = HttpConnect.searchUser(params);
		List<UserInfoEntity> searchUserResultEntitiesTemp = searchUserResultEntities;
		if (resultString == null) {
			return 3;
		}
		try {

			JSONObject jsonObject = new JSONObject(resultString);
			errorMsg = jsonObject.getString("errorMsg");
			if (jsonObject.getInt("state") != 0) {
				return 1;
			} else {
				JSONArray stuCodeArray = (JSONArray) jsonObject.get("StuCode");
				JSONArray stuNameArray = (JSONArray) jsonObject.get("StuName");
				JSONArray stuCollegeArray = (JSONArray) jsonObject.get("StuCollege");

				for (int i = 0; i < stuCodeArray.length(); i++) {
					UserInfoEntity searchUserResultEntity = new UserInfoEntity();
					searchUserResultEntity.setStuCode(stuCodeArray.optString(i));
					searchUserResultEntity.setStuName(stuNameArray.optString(i));
					searchUserResultEntity.setStuCollege(stuCollegeArray.optString(i));
					searchUserResultEntities.add(searchUserResultEntity);
				}
				return 0;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			searchUserResultEntities = searchUserResultEntitiesTemp;
			return 2;
		}

	}

	// public static int FriendListProvider(List<Map<String, Object>>
	// friendListMaps, String errorMsg) {
	// List<Map<String, Object>> friendListMapsTemp = friendListMaps;
	// String resultString = HttpConnect.getFriendList();
	// if (resultString == null) {
	// return 3;
	// }
	// try {
	// JSONObject jsonObject = new JSONObject(resultString);
	// errorMsg = jsonObject.getString("errorMsg");
	// if (jsonObject.getInt("state") != 0) {
	// return 1;
	// }
	// JSONArray stuCodeArray =
	// jsonObject.getJSONObject("data").getJSONArray("StuCode");
	// JSONArray stuNameArray =
	// jsonObject.getJSONObject("data").getJSONArray("StuName");
	// JSONArray signatureArray =
	// jsonObject.getJSONObject("data").getJSONArray("Signature");
	// for (int i = 0; i < stuCodeArray.length(); i++) {
	// Map<String, Object> friendListMap = new HashMap<String, Object>();
	// friendListMap.put("stuCode", stuCodeArray.optString(i));
	// friendListMap.put("stuName", stuNameArray.optString(i));
	// friendListMap.put("signature", signatureArray.optString(i));
	// friendListMaps.add(friendListMap);
	//
	// }
	// return 0;
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// friendListMaps = friendListMapsTemp;
	// return 2;
	// }
	//
	// }

	public static int FriendListProvider(List<Map<String, Object>> friendListMaps, String errorMsg) {
		List<Map<String, Object>> friendListMapsTemp = friendListMaps;

		try {

			DatabaseHelper databaseHelper = new DatabaseHelper(ContextUtil.getInstance());
			SharePreferenceHelper sharePreferenceHelper = new SharePreferenceHelper();
			String stuCode = sharePreferenceHelper.getStuCode();
			
			String sqlTemp="select stuCode from swjtu_chat_messages where stuCode='00000000' and msgBelongTo ='"+ stuCode + "'  ";
			Cursor cursorTemp=databaseHelper.dql(sqlTemp, null);
			if (!cursorTemp.moveToFirst()) {
				String sql = "insert into swjtu_chat_messages(msgFrom,msgTo,msgContent,stuCode,stuName,msgCtime,msgBelongTo,msgType) values(?,?,?,?,?,?,?,?)";

				databaseHelper.dml(sql, new Object[] { "00000000", sharePreferenceHelper.getStuCode(), "无聊就给我发消息吧，必回的哦", "00000000", "小黄鸡", Tools.getTimeNow(), sharePreferenceHelper.getStuCode(),
						0 });
				String filePath = SwjtuChatWithFriendActivity.SWJTU_CHAT_ICON_PATH;
				String fileName = "00000000.png";
				//Tools.writeHttpFile2SdCard("swjtu_chat/personalIcons/" + fileName, filePath, fileName);
				Tools.writeImg2SdCard(BitmapFactory.decodeResource(ContextUtil.getInstance().getResources(), R.drawable.xiao_huang_ji), filePath, fileName);
			}

			sqlTemp="select stuCode from swjtu_chat_messages where stuCode='00000001' and msgBelongTo ='"+ stuCode + "'  ";
			cursorTemp=databaseHelper.dql(sqlTemp, null);
			if (!cursorTemp.moveToFirst()) {
				String sql = "insert into swjtu_chat_messages(msgFrom,msgTo,msgContent,stuCode,stuName,msgCtime,msgBelongTo,msgType) values(?,?,?,?,?,?,?,?)";

				databaseHelper.dml(sql, new Object[] { "00000001", sharePreferenceHelper.getStuCode(), "如果您在使用中遇到任何问题，记得给我们发消息哦，我们会在第一时间给您回复！", "00000001", "新青年网络工作室", Tools.getTimeNow(), sharePreferenceHelper.getStuCode(),
						0 });
				
				sql = "insert into swjtu_chat_messages(msgFrom,msgTo,msgContent,stuCode,stuName,msgCtime,msgBelongTo,msgType) values(?,?,?,?,?,?,?,?)";

				databaseHelper.dml(sql, new Object[] { "00000001", sharePreferenceHelper.getStuCode(), "点击会话列表右上角的“+”按钮，可以通过学号或者姓名找到他/她并开始聊天哦！（需要该用户在交大YOUTH客户端登录并且没有手动注销）", "00000001", "新青年网络工作室", Tools.getTimeNow(), sharePreferenceHelper.getStuCode(),
						0 });
				String filePath = SwjtuChatWithFriendActivity.SWJTU_CHAT_ICON_PATH;
				String fileName = "00000001.png";
				//Tools.writeHttpFile2SdCard("swjtu_chat/personalIcons/" + fileName, filePath, fileName);
				Tools.writeImg2SdCard(BitmapFactory.decodeResource(ContextUtil.getInstance().getResources(), R.drawable.the_new_youth), filePath, fileName);
			
			}
			
			
			String sql = "select stuCode,stuName,msgContent,ID,max(msgCtime) as msgCtime from swjtu_chat_messages where msgBelongTo ='"
					+ stuCode + "' group by stuCode order by msgCtime desc";
			Cursor cursor = databaseHelper.dql(sql, null);
			if (cursor.moveToFirst()) {
				do {
					Map<String, Object> friendListMap = new HashMap<String, Object>();
					friendListMap.put("stuCode", cursor.getString(0));
					friendListMap.put("stuName", cursor.getString(1));
					friendListMap.put("signature", cursor.getString(2));
					friendListMap.put("ID", cursor.getString(3));
					String filePath = Environment.getExternalStorageDirectory()
							+ SwjtuChatWithFriendActivity.SWJTU_CHAT_ICON_PATH;
					String fileName = cursor.getString(0) + ".png";
					Bitmap bitmap = BitmapFactory.decodeFile(filePath+"/"+fileName);
					if (bitmap==null) {
						bitmap=BitmapFactory.decodeResource(ContextUtil.getInstance().getResources(), R.drawable.avatar);
					}
					friendListMap.put("stuIcon", bitmap);
					
					
					String timeString=cursor.getString(4);
					String msgTime=Tools.getMsgTime(timeString);
					friendListMap.put("msgTime", msgTime);
					friendListMaps.add(friendListMap);
				} while (cursor.moveToNext());
			}
			cursor.close();

			return 0;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			friendListMaps = friendListMapsTemp;
			return 2;
		}

	}

	public static long swjtuChatMsgToDatabase(SwjtuChatMsgEntity swjtuChatMsgEntity) {
		String msgFrom = swjtuChatMsgEntity.getMsgFrom();
		String msgTo = swjtuChatMsgEntity.getMsgTo();
		String msgContent = swjtuChatMsgEntity.getMsgContent();
		String stuCode = swjtuChatMsgEntity.getStuCode();
		String stuName = swjtuChatMsgEntity.getStuName();
		String msgCtime = swjtuChatMsgEntity.getMsgCtime();
		String msgBelongTo = swjtuChatMsgEntity.getMsgBelongTo();
		DatabaseHelper databaseHelper = new DatabaseHelper(ContextUtil.getInstance());
		int msgType = swjtuChatMsgEntity.getMsgType();
		// String
		// sql="insert into swjtu_chat_messages(msgFrom,msgTo,msgContent,msgCtime,msgBelongTo,msgType) values('"+msgFrom+"','"+msgTo+"','"+msgContent+"','"+msgCtime+"','"+msgBelongTo+"','"+msgType+"')";
		String sql = "insert into swjtu_chat_messages(msgFrom,msgTo,msgContent,stuCode,stuName,msgCtime,msgBelongTo,msgType) values(?,?,?,?,?,?,?,?)";
		SQLiteDatabase db=databaseHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		
		values.put("msgFrom", msgFrom);
		values.put("msgTo", msgTo);
		values.put("msgContent", msgContent);
		values.put("stuCode", stuCode);
		values.put("stuName", stuName);
		values.put("msgCtime", msgCtime);
		values.put("msgBelongTo", msgBelongTo);
		values.put("msgType", msgType);
//		databaseHelper.dml(sql, new Object[] { msgFrom, msgTo, msgContent, stuCode, stuName, msgCtime, msgBelongTo,
//				msgType });
		long lastId=db.insert("swjtu_chat_messages", null, values);


		databaseHelper.close();
		return lastId;


	}

	public static List<SwjtuChatMsgEntity> swjtuChatWithFriendInitProvider(String stuHome, String stuCodeChatNow) {
		String sql = "select msgFrom,msgTo,msgContent,msgCtime,msgBelongTo,msgType,stuCode,stuName,isHaveSend from(select msgFrom,msgTo,msgContent,msgCtime,msgBelongTo,msgType ,stuCode,stuName,isHaveSend from swjtu_chat_messages where stuCode='"+stuCodeChatNow+"' and msgBelongTo='"
				+ stuHome + "' order by msgCtime desc limit 0,10) order by msgCtime";
		DatabaseHelper databaseHelper = new DatabaseHelper(ContextUtil.getInstance());
		Cursor cursor = databaseHelper.dql(sql, null);
		List<SwjtuChatMsgEntity> swjtuChatMsgEntities = new ArrayList<SwjtuChatMsgEntity>();
		if (cursor.moveToFirst())
			do {

				String msgFrom = cursor.getString(0);
				String msgTo = cursor.getString(1);
				String msgContent = cursor.getString(2);
				String msgCtime = cursor.getString(3);
				String msgBelongTo = cursor.getString(4);
				int msgType = cursor.getInt(5);
				String stuCode = cursor.getString(5);
				String stuName = cursor.getString(7);
				boolean isHaveSend=cursor.getInt(8)==1?false:true;
				swjtuChatMsgEntities.add(new SwjtuChatMsgEntity(msgFrom, msgTo, msgContent, stuCode, stuName, msgCtime,
						msgBelongTo, msgType,isHaveSend));
			} while (cursor.moveToNext());
		cursor.close();
		return swjtuChatMsgEntities;
	}

	public static int secondClassListMapsProvider(List<Map<String, Object>> secondClassMapsList, StringBuffer message,
			int index, int category) {

		String resultString = HttpConnect.getSecondClassList(index, category);
		List<Map<String, Object>> secondClassMapsListTemp = secondClassMapsList;
		if (resultString == null) {
			return 3;
		}
		try {

			JSONObject jsonObject = new JSONObject(resultString);
			message = message.append(jsonObject.getString("Message"));
			if (jsonObject.getInt("State") != 0) {
				return 1;
			} else {
				JSONArray ClassArray = (new JSONObject(resultString)).getJSONArray("Data");

				for (int i = 0; i < ClassArray.length(); i++) {
					Map<String, Object> classListMap = new HashMap<String, Object>();
					JSONObject jsonObjectTemp = ClassArray.optJSONObject(i);
					classListMap.put("showId", secondClassMapsList.size() + 1);
					classListMap.put("ID", jsonObjectTemp.getString("ID"));
					classListMap.put("Presenter", jsonObjectTemp.getString("TeachAddress"));
					// classListMap.put("TeachAddress",
					// jsonObjectTemp.getString("teachAddress"));
					classListMap.put("CourseNameEx", jsonObjectTemp.getString("CourseNameEx"));
					classListMap.put("TeachTime",
							jsonObjectTemp.getString("TeachDate") + " " + jsonObjectTemp.getString("TeachTimeSpan"));
					// UserInfoEntity searchUserResultEntity = new
					// UserInfoEntity();
					// searchUserResultEntity.setStuCode(stuCodeArray.optString(i));
					// searchUserResultEntity.setStuName(stuNameArray.optString(i));
					// searchUserResultEntity.setStuCollege(stuCollegeArray.optString(i));
					// searchUserResultEntities.add(searchUserResultEntity);
					secondClassMapsList.add(classListMap);
				}
				return 0;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			secondClassMapsList = secondClassMapsListTemp;

			return 2;
		}

	}

	public static int MySecondClassMapsProvider(List<Map<String, Object>> mySecondClassMapsList, StringBuffer message,
			int index) {

		String resultString = HttpConnect.getMyClassList(index);
		List<Map<String, Object>> mySecondClassMapsListTemp = mySecondClassMapsList;
		if (resultString == null) {
			return 3;
		}
		try {

			JSONObject jsonObject = new JSONObject(resultString);
			message = message.append(jsonObject.getString("Message"));
			if (jsonObject.getInt("State") != 0) {
				return 1;
			} else {

				JSONArray ClassArray= (new JSONObject(resultString)).getJSONArray("Data");

				for (int i = 0; i < ClassArray.length(); i++) {
					Map<String, Object> classListMap = new HashMap<String, Object>();
					JSONObject jsonObjectTemp = ClassArray.optJSONObject(i);
					classListMap.put("showId", mySecondClassMapsList.size() + 1);
					classListMap.put("ID", jsonObjectTemp.getString("CurrentOptionalCoursesID"));
					if (!(jsonObjectTemp.getString("CourseScore")).equals("-1")) {
						classListMap.put("CourseScore", jsonObjectTemp.getString("CourseScore"));
					}else {
						classListMap.put("CourseScore", "");
					}
					
					classListMap.put("Presenter", jsonObjectTemp.getString("TeachAddress"));
					// classListMap.put("TeachAddress",
					// jsonObjectTemp.getString("teachAddress"));
					classListMap.put("CourseNameEx", jsonObjectTemp.getString("CourseNameEx"));
					classListMap.put("TeachTime",
							jsonObjectTemp.getString("TeachDate") + " " + jsonObjectTemp.getString("TeachTimeSpan"));
					// UserInfoEntity searchUserResultEntity = new
					// UserInfoEntity();
					// searchUserResultEntity.setStuCode(stuCodeArray.optString(i));
					// searchUserResultEntity.setStuName(stuNameArray.optString(i));
					// searchUserResultEntity.setStuCollege(stuCollegeArray.optString(i));
					// searchUserResultEntities.add(searchUserResultEntity);
					mySecondClassMapsList.add(classListMap);
				}
				return 0;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			mySecondClassMapsList = mySecondClassMapsListTemp;

			return 2;
		}

	}

public static int chatLogProvider(List<Map<String, Object>> chatLogMaps,String stuCode,String msgBelongTo,int pageNo) {
	DatabaseHelper databaseHelper=new DatabaseHelper(ContextUtil.getInstance());
	String sql="select ID from swjtu_chat_messages where stuCode='"+stuCode+"' and msgBelongTo='"+msgBelongTo+"'";
	Cursor cursor=databaseHelper.dql(sql, new String[]{});

	//String aString=cursor.getCount();
	int totalPage=(int)((cursor.getCount())/(int)10)+1;
	if (pageNo==-1) pageNo=totalPage;
	sql="select ID,msgContent,stuCode,stuName,msgCtime,msgType from (select *  from swjtu_chat_messages  where stuCode='"+stuCode+"' and msgBelongTo='"+msgBelongTo+"' order by msgCtime limit "+(pageNo-1)*10+",10) order by msgCtime";
	cursor=databaseHelper.dql(sql, new String[]{});
	if (cursor.moveToFirst()) {
		do {
			Map<String, Object> chatLogMap=new HashMap<String, Object>();
			chatLogMap.put("ID", cursor.getInt(0));
			chatLogMap.put("msgContent",cursor.getString(1));
			chatLogMap.put("msgTime", cursor.getString(4));
			int a=cursor.getInt(5);
			if (cursor.getInt(5)==1) {
				chatLogMap.put("msgFrom", "我:");
			}else {
				chatLogMap.put("msgFrom", cursor.getString(3)+":");
			}
			chatLogMaps.add(chatLogMap);
		} while (cursor.moveToNext());
	}
	cursor.close();
	databaseHelper.close();
	return totalPage;
	
}

	public static int secondClassDetailProvider(SecondClassDetailEntity secondClassDetailEntity, StringBuffer message,
			String courseId) {

		String resultString = HttpConnect.getSecondClassDetail(courseId);
		SecondClassDetailEntity secondClassDetailEntityTemp = secondClassDetailEntity;
		if (resultString == null) {
			return 3;
		}
		try {

			JSONObject jsonObject = new JSONObject(resultString);
			message = message.append(jsonObject.getString("Message"));
			if (jsonObject.getInt("State") != 0) {
				return 1;
			} else {
				JSONObject classDetailJsonObject = (new JSONObject(resultString)).getJSONObject("Data");
				secondClassDetailEntity.setBelongQiseCode(classDetailJsonObject.getString("BelongQiseCode"));
				secondClassDetailEntity.setCourseDetail(classDetailJsonObject.getString("Remark"));
				secondClassDetailEntity.setCourseNameEx(classDetailJsonObject.getString("CourseNameEx"));
				secondClassDetailEntity.setDepID(classDetailJsonObject.getString("DepID"));
				// secondClassDetailEntity.setID(classDetailJsonObject.getString(""));
				secondClassDetailEntity.setMaxCapacity(classDetailJsonObject.getString("MaxCapacity"));
				secondClassDetailEntity.setPresenter(classDetailJsonObject.getString("Presenter"));
				secondClassDetailEntity.setSelectedCounts(classDetailJsonObject.getString("SelectedCounts"));
				secondClassDetailEntity.setTeachAddress(classDetailJsonObject.getString("TeachAddress"));
				secondClassDetailEntity.setTeachTime(classDetailJsonObject.getString("TeachDate") + " "
						+ classDetailJsonObject.getString("TeachTimeSpan"));
				secondClassDetailEntity.setXHours(classDetailJsonObject.getString("XHours"));

				return 0;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			secondClassDetailEntity = secondClassDetailEntityTemp;

			return 2;
		}

	}

}
