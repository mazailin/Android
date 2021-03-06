package com.ruyicai.activity.buy.qxc;

import android.os.Bundle;

import com.ruyicai.activity.buy.BuyActivityGroup;
import com.ruyicai.constant.Constants;

public class QXC extends BuyActivityGroup {
	private String[] titles = { "自选", "机选" };
	private String[] topTitles = { "七星彩", "七星彩" };
	private Class[] allId = { QXCZhiXuan.class, QXCJX.class };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLotno(Constants.LOTNO_QXC);
		init(titles, topTitles, allId);
		setIssue(Constants.LOTNO_QXC);
		setlastbatchcode(Constants.LOTNO_QXC);
	}

}
