package com.handsSwjtu.Objects;

import android.R.integer;

public class SwjtuKnowAnswerEntity {

	private int answerId;
	private String answerContent;
	private String answerFrom;
	private int sortId;
	private String ctime;
	private boolean isBestAnswer;
	public int getAnswerId() {
		return answerId;
	}
	public void setAnswerId(int answerId) {
		this.answerId = answerId;
	}
	public String getAnswerContent() {
		return answerContent;
	}
	public void setAnswerContent(String answerData) {
		this.answerContent = answerData;
	}
	public String getAnswerFrom() {
		return answerFrom;
	}
	public void setAnswerFrom(String answerFrom) {
		this.answerFrom = answerFrom;
	}
	public int getSortId() {
		return sortId;
	}
	public void setSortId(int sortId) {
		this.sortId = sortId;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public boolean isBestAnswer() {
		return isBestAnswer;
	}
	public void setBestAnswer(boolean isBestAnswer) {
		this.isBestAnswer = isBestAnswer;
	}
	
	
}
