package com.mk.ots.common.enums;
//环信的东西
public enum ChatMessageType {
	txt("txt",new String[]{"type","msg"}),
	img("img",new String[]{"type","url","filename","secret"}),
	audio("audio",new String[]{"type","url","filename","length","secret"}),
	loc("loc",new String[]{"type","addr","lat","lng"}),
	video("video",new String[]{"type","url","filename","length","secret"}),
	full_img("img",new String[]{"type","url","filename","secret","thumb","thumb_secret"}),
	full_video("video",new String[]{"type","url","filename","length","secret","file_length","thumb","thumb_secret"}),
	file("file",new String[]{"type","url","filename","length","secret"}),// 环信里没直接说明这个类型
	other("other",new String[]{"type"}),// 环信里没直接说明这个类型
	;
	private String name;
	private String[] info;

	private ChatMessageType(String name, String[] info) {
		this.name = name;
		this.info = info;
	}
	
	public String getName() {
		return name;
	}

	public String[] getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
