package com.handsSwjtu.Objects;

import android.R.integer;

public class SwjtuChatMsgEntity {
	private String msgContent;
	private String msgFrom;
	private String msgTo;
	private String msgCtime;
	private String msgBelongTo;
	private String stuCode;
	private String stuName;
	private int msgType;
	private long ID;
	private boolean isHaveSend;







	public SwjtuChatMsgEntity(String msgFrom,String msgTo,String msgContent,String stuCode,String stuName,String msgCtime,String msgBelongTo,int msgType,boolean isHaveSend){
		this.msgContent=msgContent;
		this.msgFrom=msgFrom;
		this.msgTo=msgTo;
		this.msgCtime=msgCtime;
		this.msgBelongTo=msgBelongTo;
		this.msgType=msgType;
		this.stuCode=stuCode;
		this.stuName=stuName;
		this.isHaveSend=isHaveSend;
	}
	
	public SwjtuChatMsgEntity(long ID,String msgFrom,String msgTo,String msgContent,String stuCode,String stuName,String msgCtime,String msgBelongTo,int msgType){
		
		this.ID=ID;
		this.msgContent=msgContent;
		this.msgFrom=msgFrom;
		this.msgTo=msgTo;
		this.msgCtime=msgCtime;
		this.msgBelongTo=msgBelongTo;
		this.msgType=msgType;
		this.stuCode=stuCode;
		this.stuName=stuName;
	}

	
	public long getID() {
		return ID;
	}


	public void setID(int iD) {
		ID = iD;
	}

	
	public String getStuCode() {
		return stuCode;
	}


	public void setStuCode(String stuCode) {
		this.stuCode = stuCode;
	}
	
	public String getStuName() {
		return stuName;
	}

	public void setStuName(String stuName) {
		this.stuName = stuName;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getMsgFrom() {
		return msgFrom;
	}

	public void setMsgFrom(String msgFrom) {
		this.msgFrom = msgFrom;
	}

	public String getMsgTo() {
		return msgTo;
	}

	public void setMsgTo(String msgTo) {
		this.msgTo = msgTo;
	}

	public String getMsgCtime() {
		return msgCtime;
	}

	public void setMsgCtime(String msgCtime) {
		this.msgCtime = msgCtime;
	}
	
	public String getMsgBelongTo() {
		return msgBelongTo;
	}

	public void setMsgBelongTo(String msgBelongTo) {
		this.msgBelongTo = msgBelongTo;
	}
	
	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	
	public boolean isHaveSend() {
		return isHaveSend;
	}

	public void setHaveSend(boolean isHaveSend) {
		this.isHaveSend = isHaveSend;
	}

}
