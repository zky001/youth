package com.handsSwjtu.Objects;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Bundle;

public class SwjtuKnowEntity {

	private int questionId;
	private String title;
	private String content;
	private int category;
	private int viewNum;
	private int replyNum;
	private String questionFrom;
	private boolean isSolved;
	private String ctime;
	private ArrayList<String> questionImgFiles;
	
	
	
	public ArrayList<String> getQuestionImgFiles() {
		return questionImgFiles;
	}
	public void setQuestionImgFiles(ArrayList<String> questionImgFiles) {
		this.questionImgFiles = questionImgFiles;
	}

	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public int getViewNum() {
		return viewNum;
	}
	public void setViewNum(int viewNum) {
		this.viewNum = viewNum;
	}
	public int getReplyNum() {
		return replyNum;
	}
	public void setReplyNum(int replyNum) {
		this.replyNum = replyNum;
	}
	public String getQuestionFrom() {
		return questionFrom;
	}
	public void setQuestionFrom(String questionFrom) {
		this.questionFrom = questionFrom;
	}
	public boolean isSolved() {
		return isSolved;
	}
	public void setSolved(boolean isSolved) {
		this.isSolved = isSolved;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String cTime) {
		this.ctime = cTime;
	}
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	
	
	
	
	
	
}
