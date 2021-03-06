package com.palmdream.RuyicaiAndroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.palmdream.RuyicaiAndroid.BallTable;
import com.palmdream.RuyicaiAndroid.OneBallView;
import com.palmdream.RuyicaiAndroid.R;

import com.palmdream.netintface.BettingInterface;
import com.palmdream.netintface.iHttp;
import com.palmdream.netintface.jrtLot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class JinQC extends Activity implements OnClickListener,
		SeekBar.OnSeekBarChangeListener {
	int lieNum;
	private Vibrator vibrator;

	public void JinQC() {
		this.vibrator = null;

	}

	public final static String TEAM1 = "TEAM1";
	public final static String TEAM2 = "TEAM2";
	public final static String SCORES1 = "SCORES1";
	public final static String SCORES2 = "SCORES2";
	String inflater = Context.LAYOUT_INFLATER_SERVICE;
	/** 小球起始id */
	public static final int JINQC_START_ID = 0x84000001;
	public int iAllBallWidth;
	LayoutInflater layoutInflater;
	ListViewDemo listViewDemo;
	ScrollView mHScrollView;
	LinearLayout buyView;
	String str[] = new String[4];// 存放期号，截止时间，彩种
	ListView mlist;
	TextView mTextSumMoney;
	List<Map<String, Object>> list;
	private int iretrytimes = 2;
	SeekBar mSeekBarBeishu;
	TextView mTextBeishu;
	int iProgressBeishu;
	Vector<BallTable> ballTables = new Vector<BallTable>();
	ImageButton jinqc_btn_touzhu;
	private JSONObject obj;
	// 进度条
	private static final int DIALOG1_KEY = 0;
	private ProgressDialog progressdialog;
	private int mTimesMoney = 2;
	String batchCode;
	String re;
	Vector<TeamInfo> teamInfos = new Vector<TeamInfo>();
	private int position;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** ----- 设置全屏 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.jinqc);

		/** 顶部返回按钮 */
		ImageButton returnBtn = (ImageButton) findViewById(R.id.goucaitouzhu_title_return);
		returnBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}

		});
		TextView title = (TextView) findViewById(R.id.goucaitouzhu_title);
		title.setText(getResources().getString(R.string.jinqiucai));
		str = getLotno("information13");
		batchCode = str[0];
		createVeiw();
		getData();
	}

	/**
	 * 获得当前期数
	 * 
	 * @param string
	 * @return
	 */
	public String[] getLotno(String string) {
		String error_code;
		String str[] = new String[4];
		// ShellRWSharesPreferences
		ShellRWSharesPreferences shellRW = new ShellRWSharesPreferences(this,
				"addInfo");
		String notice = shellRW.getUserLoginInfo(string);
		PublicMethod.myOutput("------------------lotnossq" + notice);
		// 判断取值是否为空 cc 2010/7/9
		if (!notice.equals("") || notice != null) {
			try {
				JSONObject obj = new JSONObject(notice);
				error_code = obj.getString("error_code");
				if (error_code.equals("000000")) {
					JSONArray value = obj.getJSONArray("value");
					JSONObject re = value.getJSONObject(0);
					str[0] = re.getString("BATCHCODE");
					str[1] = re.getString("ENDTIME");
					str[2] = re.getString("LOTNO");

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	public void initList() {
		mlist = (ListView) findViewById(R.id.jinqiucai_xuan);
		list = getListForMainListAdapter();
		listViewDemo = new ListViewDemo(this, list);
		mlist.setAdapter(listViewDemo);
		Drawable drawable = getResources().getDrawable(
				R.drawable.list_selector_red);
		mlist.setSelector(drawable);
	}

	public class ListViewDemo extends BaseAdapter {

		private Context context;
		private List<Map<String, Object>> mList;
		private LayoutInflater mInflater; // 扩充主列表布局

		public ListViewDemo(Context context, List<Map<String, Object>> list) {
			this.context = context;

			mInflater = LayoutInflater.from(context);
			mList = list;
		}

		/*
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		/*
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		/*
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public Dialog getFenxi(int position, Dialog dialog) {

			Dialog fenxidialog;

			return dialog;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int[] aResId = { R.drawable.grey, R.drawable.red };
			int ballNum = 4;
			int liuCBBallFieldWidth;
			int liuCBBallWidth = 30;
			liuCBBallFieldWidth = (liuCBBallWidth + 2) * 5;
			int START_ID;
			START_ID = JINQC_START_ID + position * 4;
			final int index = position;
			String team1 = (String) mList.get(position).get("TEAM1");
			String team2 = (String) mList.get(position).get("TEAM2");
			String scores1 = (String) mList.get(position).get("SCORES1");
			String scores2 = (String) mList.get(position).get("SCORES2");
			ViewHolder holder = null;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.jinqiucai, null);

				holder = new ViewHolder();
				holder.lie = ((TextView) convertView
						.findViewById(R.id.jinqiu_lienum));
				holder.teamnamerank1 = (TextView) convertView
						.findViewById(R.id.jinqiu_teamrank1);
				holder.teamnamerank2 = (TextView) convertView
						.findViewById(R.id.jinqiu_teamrank2);
				holder.layout = (LinearLayout) convertView
						.findViewById(R.id.jinqiucai_layout);
				holder.info = (ImageView) convertView
						.findViewById(R.id.jinqiucai_fenxi);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			BallTable jinqcRow1 = makeBallTable((LinearLayout) convertView,
					R.id.jinqiucai_ball_1, liuCBBallFieldWidth, ballNum,
					liuCBBallWidth, aResId, START_ID);
			ballTables.add(jinqcRow1);
			Vector<OneBallView> BallViews1 = jinqcRow1.getBallViews();
			for (int i = 0; i < BallViews1.size(); i++) {
				final OneBallView ball = BallViews1.get(i);
				ball.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// shengfcRow.changeBallState(3, 1);
						ball.startAnim();
						ball.showNextId();
						changeTextSumMoney();

						// 测试注数的值
						int iZhuShu = getZhuShu();
					}
				});
			}
			BallTable jinqcRow2 = makeBallTable((LinearLayout) convertView,
					R.id.jinqiucai_ball_2, liuCBBallFieldWidth, ballNum,
					liuCBBallWidth, aResId, START_ID);
			ballTables.add(jinqcRow2);
			Vector<OneBallView> BallViews2 = jinqcRow2.getBallViews();
			for (int i = 0; i < BallViews2.size(); i++) {
				final OneBallView ball2 = BallViews2.get(i);
				ball2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// shengfcRow.changeBallState(3, 1);
						ball2.startAnim();
						ball2.showNextId();
						changeTextSumMoney();

						// 测试注数的值
						int iZhuShu = getZhuShu();
					}
				});
			}

			holder.lie.setText((String.valueOf(position + 1)));

			if (scores1 == null || scores2.equals(null)) {
				holder.teamnamerank1.setText(team1);
				holder.teamnamerank2.setText(team2);

			} else {
				holder.teamnamerank1.setText(team1 + "[" + scores1 + "]");
				holder.teamnamerank2.setText(team2 + "[" + scores2 + "]");
			}
			holder.info.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//
					getInfo(index);
				}
			});
			return convertView;

		}

		class ViewHolder {
			TextView lie;
			TextView teamnamerank1;
			TextView teamnamerank2;
			LinearLayout layout;
			ImageView info;
		}

	}

	/**
	 * 创建BallTable
	 * 
	 * @param LinearLayout
	 *            aParentView 上一级Layout
	 * 
	 * @param int aLayoutId 当前BallTable的LayoutId
	 * 
	 * @param int aFieldWidth BallTable区域的宽度（如屏幕宽度）
	 * 
	 * @param int aBallNum 小球个数
	 * 
	 * @param int aBallViewWidth 小球视图的宽度（图片宽度）
	 * 
	 * @param int[] aResId 小球图片Id
	 * 
	 * @param int aIdStart 小球Id起始数值
	 * 
	 * @return BallTable
	 */
	private BallTable makeBallTable(LinearLayout aParentView, int aLayoutId,
			int aFieldWidth, int aBallNum, int aBallViewWidth, int[] aResId,
			int aIdStart) {

		BallTable iBallTable = new BallTable(aParentView, aLayoutId, aIdStart);
		// BallTable iBallTable=new BallTable(aLayoutId,aIdStart);
		int iBallNum = aBallNum;
		int iBallViewWidth = aBallViewWidth;
		int iFieldWidth = aFieldWidth;
		/** 滚动条的宽度 */
		int scrollBarWidth = 6;
		/** 每一行的小球数量 */
		int viewNumPerLine = (iFieldWidth - scrollBarWidth)
				/ (iBallViewWidth + 2);
		/** 行的数量 */
		int lineNum = iBallNum / viewNumPerLine;
		/** 最后一行的view的数目 */
		int lastLineViewNum = iBallNum % viewNumPerLine;
		/** 空白的大小 */
		int margin = (iFieldWidth - scrollBarWidth - (aBallViewWidth + 2)
				* viewNumPerLine) / 2;
		int iBallViewNo = 0;

		for (int row = 0; row < lineNum; row++) {
			TableRow tableRow = new TableRow(aParentView.getContext());
			for (int col = 0; col < viewNumPerLine; col++) {
				/** 设置显示的数字 */

				String iStrTemp = "" + (iBallViewNo + 1);
				if (iStrTemp.equals("1") || iStrTemp.equals("5")) {
					iStrTemp = "0";
				} else if (iStrTemp.equals("2") || iStrTemp.equals("6")) {
					iStrTemp = "1";
				} else if (iStrTemp.equals("3") || iStrTemp.equals("7")) {
					iStrTemp = "2";
				} else {
					iStrTemp = "3+";
				}
				/** 实例化一个BallView对象 */
				OneBallView tempBallView = new OneBallView(aParentView
						.getContext());
				/** 为这个tempView设置一个Id */
				tempBallView.setId(aIdStart + iBallViewNo);
				/** 将这个小球初始化出来 */
				tempBallView.initBall(iBallViewWidth, iBallViewWidth, iStrTemp,
						aResId);

				/** 为初始化的小球设置监听 */
				tempBallView.setOnClickListener(this);
				/*** 将小球tempView添加到Table中 */
				iBallTable.addBallView(tempBallView);

				TableRow.LayoutParams lp = new TableRow.LayoutParams();
				if (col == 0) {
					/** 设置TableRow四个方向的空白像素 */
					lp.setMargins(margin + 1, 1, 1, 1);
				} else if (col == viewNumPerLine - 1) {
					lp.setMargins(1, 1, margin + scrollBarWidth + 1, 1);
				} else
					lp.setMargins(1, 1, 1, 1);
				tableRow.addView(tempBallView, lp);
				/** iBallViewNo自增，循环设置小球的属性 */
				iBallViewNo++;
			}
			/** 新建的TableRow添加到TableLayout */
			iBallTable.tableLayout
					.addView(tableRow, new TableLayout.LayoutParams(
							PublicConst.FP, PublicConst.WC));
		}
		if (lastLineViewNum > 0) {
			TableRow tableRow = new TableRow(this);
			for (int col = 0; col < lastLineViewNum; col++) {
				// PublicMethod.myOutput("-----------"+iBallViewNo);
				String iStrTemp = "" + (iBallViewNo + 1);
				OneBallView tempBallView = new OneBallView(aParentView
						.getContext());
				tempBallView.setId(aIdStart + iBallViewNo);
				tempBallView.initBall(iBallViewWidth, iBallViewWidth, iStrTemp,
						aResId);
				tempBallView.setOnClickListener(this);
				iBallTable.addBallView(tempBallView);
				TableRow.LayoutParams lp = new TableRow.LayoutParams();
				if (col == 0) {
					lp.setMargins(margin + 1, 1, 1, 1);
				} else
					lp.setMargins(1, 1, 1, 1);
				tableRow.addView(tempBallView, lp);
				iBallViewNo++;
			}
			// 新建的TableRow添加到TableLayout
			iBallTable.tableLayout
					.addView(tableRow, new TableLayout.LayoutParams(
							PublicConst.FP, PublicConst.WC));
		}
		// PublicMethod.myOutput("-----w:"+iBallTable.tableLayout.getWidth()+"   h:"+iBallTable.tableLayout.getHeight());
		return iBallTable;
	}

	/**
	 * 主列表中相应的数据
	 */
	private List<Map<String, Object>> getListForMainListAdapter() {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();

		// zlm 7.16 代码修改：添加开奖日期

		for (int i = 0; i < teamInfos.size(); i++) {
			map = new HashMap<String, Object>();
			map.put(TEAM1, teamInfos.get(i).hTeam);
			map.put(TEAM2, teamInfos.get(i).vTeam);
			map.put(SCORES1, teamInfos.get(i).hRankNum);
			map.put(SCORES2, teamInfos.get(i).vRankNum);
			list.add(map);
		}

		return list;
	}

	/**
	 * 获取对阵矩阵
	 * 
	 * @作者：
	 * @日期：
	 * @参数：
	 * @返回值：
	 * @修改人：
	 * @修改内容：
	 * @修改日期：
	 * @版本：
	 */
	public void getData() {
		showDialog(DIALOG1_KEY);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String error_code = "00";
				while (iretrytimes < 3 && iretrytimes > 0) {
					re = jrtLot.getZCData(str[2], str[0]);
					try {
						obj = new JSONObject(re);
						error_code = obj.getString("error_code");
						if (error_code.equals("000000")) {
							JSONArray value = obj.getJSONArray("value");
							for (int i = 0; i < value.length(); i++) {
								JSONObject reee = value.getJSONObject(i);
								TeamInfo team = new TeamInfo();
								team.hTeam = reee.getString("HTeam");
								team.vTeam = reee.getString("VTeam");
								// String rank = reee.getString("leagueRank");
								team.num = reee.getString("num");
								// if (!rank.equals("")) {
								//
								// String str[] = rank.split("\\|");
								// team.hRankNum = str[0];
								// team.vRankNum = str[1];
								// }
								teamInfos.add(team);
							}
						}
						iretrytimes = 3;
					} catch (Exception e) {
						iretrytimes--;

					}

					if (iretrytimes == 0 && iHttp.whetherChange == false) {
						iHttp.whetherChange = true;
						if (iHttp.conMethord == iHttp.CMWAP) {
							iHttp.conMethord = iHttp.CMNET;
						} else {
							iHttp.conMethord = iHttp.CMWAP;
						}
						iretrytimes = 2;
						while (iretrytimes < 3 && iretrytimes > 0) {

							re = jrtLot.getZCData(str[2], str[0]);
							try {
								obj = new JSONObject(re);
								error_code = obj.getString("error_code");
								if (error_code.equals("000000")) {
									JSONArray value = obj.getJSONArray("value");
									for (int i = 0; i < value.length(); i++) {
										JSONObject reeee = value
												.getJSONObject(i);
										TeamInfo team = new TeamInfo();
										team.hTeam = reeee.getString("HTeam");
										team.vTeam = reeee.getString("VTeam");
										String rank = reeee
												.getString("leagueRank");
										team.num = reeee.getString("num");
										if (!rank.equals("")) {

											String str[] = rank.split("\\|");
											team.hRankNum = str[0];
											team.vRankNum = str[1];
										}
										teamInfos.add(team);
									}

								}
								iretrytimes = 3;
							} catch (Exception e) {

								iretrytimes--;
								error_code = "000";

							}
						}
					}
				}
				iretrytimes = 2;
				if (error_code.equals("00")) {
					Message msg = new Message();
					msg.what = 0;
					handler.sendMessage(msg);

				} else if (error_code.equals("000000")) {
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				} else if (error_code.equals("100000")) {
					Message msg = new Message();
					msg.what = 2;
					handler.sendMessage(msg);
				} else if (error_code.equals("200001")) {
					Message msg = new Message();
					msg.what = 3;
					handler.sendMessage(msg);
				} else if (error_code.equals("200002")) {
					Message msg = new Message();
					msg.what = 4;
					handler.sendMessage(msg);
				} else if (error_code.equals("200003")) {
					Message msg = new Message();
					msg.what = 5;
					handler.sendMessage(msg);
				} else if (error_code.equals("200005")) {
					Message msg = new Message();
					msg.what = 6;
					handler.sendMessage(msg);
				} else if (error_code.equals("200008")) {
					Message msg = new Message();
					msg.what = 7;
					handler.sendMessage(msg);
				}

			}

		}).start();
	}

	/**
	 * 消息处理函数
	 */
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			String result = msg.getData().getString("get");
			switch (msg.what) {
			case 0:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "网络异常！", Toast.LENGTH_LONG)
						.show();
				break;
			case 1:
				progressdialog.dismiss();
				initList();
				break;
			case 2:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "发生异常！", Toast.LENGTH_LONG)
						.show();
				break;
			case 3:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "传入参数不是JSON格式！",
						Toast.LENGTH_LONG).show();
				break;

			case 4:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "传入彩种错误！", Toast.LENGTH_LONG)
						.show();
				break;
			case 5:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "传入期号错误！", Toast.LENGTH_LONG)
						.show();
				break;

			case 6:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "返回期号对应的对阵记录为空！",
						Toast.LENGTH_LONG).show();
				break;
			case 7:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "转码异常！", Toast.LENGTH_LONG)
						.show();
				break;
			case 8:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "网络异常！", Toast.LENGTH_LONG)
						.show();
				break;
			case 9:
				progressdialog.dismiss();
				Toast
						.makeText(getBaseContext(), "投注失败余额不足！",
								Toast.LENGTH_LONG).show();
				// //需要添加AlertDialog提示注册成功
				break;
			case 10:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "该期已结束！", Toast.LENGTH_LONG)
						.show();
				// //需要添加AlertDialog提示用户已注册
				break;
			case 11:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "彩票投注中！", Toast.LENGTH_LONG)
						.show();
				// //需要添加AlertDialog提示系统结算，请稍后
				break;
			case 12:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "投注成功，出票成功！",
						Toast.LENGTH_LONG).show();

				for (int i = 0; i < ballTables.size(); i++) {
					ballTables.get(i).clearAllHighlights();
				}

				break;
			case 14:
				// //需要添加AlertDialog提示用户登录成功
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "投注已受理！", Toast.LENGTH_LONG)
						.show();
				// 投注成功后清除小球

				for (int i = 0; i < ballTables.size(); i++) {
					ballTables.get(i).clearAllHighlights();
				}
				break;
			case 15:
				progressdialog.dismiss();

				// 30分钟后再次登录 陈晨 20100719
				Intent intentSession = new Intent(JinQC.this, UserLogin.class);
				startActivity(intentSession);
				break;
			case 16:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "网络异常！", Toast.LENGTH_LONG)
						.show();
				// //需要添加AlertDialog提示登录失败
				break;
			case 17:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "投注失败！", Toast.LENGTH_LONG)
						.show();
				break;
			case 18:
				// 将图片的背景设置回原来的图案表示可点击
				jinqc_btn_touzhu.setImageResource(R.drawable.imageselecter);

				break;
			case 19:

				progressdialog.dismiss();
				Toast
						.makeText(getBaseContext(), "没有期号可以投注！",
								Toast.LENGTH_LONG).show();
				break;
			case 20:

				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "传入场次错误！", Toast.LENGTH_LONG)
						.show();
				break;
			case 21:

				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "返回对阵为空！", Toast.LENGTH_LONG)
						.show();
				break;
			case 22:

				progressdialog.dismiss();
				alertZC(re);
				break;
			case 23:
				progressdialog.dismiss();
				Toast.makeText(getBaseContext(), "投注期号不存在，或已过期！",
						Toast.LENGTH_LONG).show();
				break;

			}
		}
	};

	// 提示框1 用来提醒选球规则
	// fqc delete 删除取消按钮 7/14/2010
	private void alertZC(String re) {
		// 解析数据
		JSONObject re1;
		JSONObject obj;
		String hTeam8 = "";
		String vTeam8 = "";
		String avgOdds = "";
		String title = "";
		try {
			re1 = new JSONObject(re);
			obj = re1.getJSONObject("value");
			hTeam8 = obj.getString("HTeam8");
			vTeam8 = obj.getString("VTeam8");
			avgOdds = obj.getString("avgOdds");
			// title += obj.getString("num");
			title += position;
			title += obj.getString("HTeam");
			title += "VS";
			title += obj.getString("VTeam");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LayoutInflater factory = LayoutInflater.from(this);
		View view = factory.inflate(R.layout.fenxi, null);
		if (!hTeam8.equals("") && !vTeam8.equals("") && !avgOdds.equals("")) {

			String avg[] = avgOdds.split("\\|");
			String hteam[] = hTeam8.split("\\|");
			String vteam[] = vTeam8.split("\\|");
			TextView row1_1 = (TextView) view.findViewById(R.id.sheng_zhishu);
			TextView row1_2 = (TextView) view
					.findViewById(R.id.zhudui_jinshi_sheng);
			TextView row1_3 = (TextView) view
					.findViewById(R.id.kedui_jinshi_sheng);
			TextView row2_1 = (TextView) view.findViewById(R.id.ping_zhishu);
			TextView row2_2 = (TextView) view
					.findViewById(R.id.zhudui_jinshi_ping);
			TextView row2_3 = (TextView) view
					.findViewById(R.id.kedui_jinshi_ping);
			TextView row3_1 = (TextView) view.findViewById(R.id.fu_zhishu);
			TextView row3_2 = (TextView) view
					.findViewById(R.id.zhudui_jinshi_fu);
			TextView row3_3 = (TextView) view
					.findViewById(R.id.kedui_jinshi_fu);
			TextView row4_1 = (TextView) view
					.findViewById(R.id.zhudui_jinshi_jinqiu);
			TextView row4_2 = (TextView) view
					.findViewById(R.id.kedui_jinshi_jinqiu);
			row1_1.setText(avg[0].substring(1));
			row1_2.setText(hteam[0]);
			row1_3.setText(vteam[0]);
			row2_1.setText(avg[1].substring(1));
			row2_2.setText(hteam[1]);
			row2_3.setText(vteam[1]);
			row3_1.setText(avg[2].substring(1));
			row3_2.setText(hteam[2]);
			row3_3.setText(vteam[2]);
			row4_1.setText(hteam[3] + "|" + hteam[4]);
			row4_2.setText(vteam[3] + "|" + vteam[4]);
		}
		Builder dialog = new AlertDialog.Builder(this).setTitle(title).setView(
				view).setPositiveButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}

				});
		dialog.show();

	}

	/**
	 * 方法编译时改为胜负彩期数的计算方法 双色球期数的计算方法
	 * 
	 * @return int 返回注数
	 */
	private int getQiShu() {
		int iReturnValue = 0;

		return iReturnValue;
	}

	public void createVeiw() {

		mTextSumMoney = (TextView) findViewById(R.id.jinqc_text_sum_money);
		mTextSumMoney.setText(getResources().getString(
				R.string.please_choose_number));
		mSeekBarBeishu = (SeekBar) findViewById(R.id.jinqc_seek_beishu);
		mSeekBarBeishu.setOnSeekBarChangeListener(this);
		iProgressBeishu = 1;
		mSeekBarBeishu.setProgress(iProgressBeishu);
		mTextBeishu = (TextView) findViewById(R.id.jinqc_text_beishu);
		mTextBeishu.setText("" + iProgressBeishu);
		setSeekWhenAddOrSub(R.id.jinqc_seekbar_subtract_beishu, null, -1,
				mSeekBarBeishu, true);
		setSeekWhenAddOrSub(R.id.jinqc_seekbar_add_beishu, null, 1,
				mSeekBarBeishu, true);

		jinqc_btn_touzhu = (ImageButton) findViewById(R.id.jinqc_btn_touzhu);
		jinqc_btn_touzhu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				beginTouZhu(); // 1表示当前为单式

			}
		});
	}

	/**
	 * 网络连接提示框
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG1_KEY: {
			progressdialog = new ProgressDialog(this);
			progressdialog.setMessage("网络连接中...");
			progressdialog.setIndeterminate(true);
			progressdialog.setCancelable(true);
			return progressdialog;
		}
		}
		return null;

	}

	/**
	 * 投注提示框中的信息
	 */
	private String getTouzhuAlert() {
		int iZhuShu = getZhuShu();
		return "第" + batchCode + "期\n" + "截止日期：" + str[1] + "\n" + "选号结果：\n"
				+ getZhuMa() + "\n" + "注数：" + iZhuShu
				/ mSeekBarBeishu.getProgress() + "注" + "\n"
				+ // 注数不能算上倍数 陈晨 20100713
				"倍数：" + mSeekBarBeishu.getProgress() + "倍" + "\n" + "金额："
				+ (iZhuShu * 2) + "元" + "\n" + "确认支付吗？";

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	/**
	 * 进球彩的注数计算方法
	 * 
	 * */
	private int getZhuShu() {
		int iReturnValue = 0;

		int beishu = mSeekBarBeishu.getProgress();
		for (int i = 0; i < ballTables.size(); i++) {
			if (i != 0) {
				iReturnValue *= ballTables.get(i).getHighlightBallNums();
			} else {
				iReturnValue = ballTables.get(i).getHighlightBallNums();
			}
		}
		return iReturnValue * beishu;
	}

	/**
	 * 获取注码
	 * 
	 * @作者：
	 * @日期：
	 * @参数：
	 * @返回值：
	 * @修改人：
	 * @修改内容：
	 * @修改日期：
	 * @版本：
	 */
	public String getZhuMa() {
		String t_str = "";
		for (int i = 0; i < ballTables.size(); i++) {
			int balls[] = ballTables.get(i).getHighlightBallNOs();
			for (int j = 0; j < balls.length; j++) {
				if (balls[j] == 1) {
					t_str += 0;
				} else if (balls[j] == 2) {
					t_str += 1;
				} else if (balls[j] == 3) {
					t_str += 2;
				} else if (balls[j] == 4) {
					t_str += 3;
				}

			}
			if (i < ballTables.size() - 1) {
				t_str += ",";
			}
		}
		return t_str;

	}

	/**
	 * 提示信息
	 * 
	 * @作者：
	 * @日期：
	 * @参数：
	 * @返回值：
	 * @修改人：
	 * @修改内容：
	 * @修改日期：
	 * @版本：
	 */
	public void changeTextSumMoney() {

		int iZhuShu = getZhuShu();
		if (iZhuShu != 0) {
			String iTempString = "共" + iZhuShu + "注，共" + (iZhuShu * 2) + "元";
			mTextSumMoney.setText(iTempString);
		} else {
			mTextSumMoney.setText(getResources().getString(
					R.string.please_choose_number));
		}
	}

	public void beginTouZhu() {
		int iZhuShu = getZhuShu();
		ShellRWSharesPreferences pre = new ShellRWSharesPreferences(JinQC.this,
				"addInfo");
		String sessionIdStr = pre.getUserLoginInfo("sessionid");
		if (sessionIdStr.equals("")) {
			Intent intentSession = new Intent(JinQC.this, UserLogin.class);
			startActivityForResult(intentSession, 0);
			jinqc_btn_touzhu.setClickable(true);
		} else {

			if (isTouZhu()) {
				alert1("请至少选择一注！");
			} else if (iZhuShu * 2 > 20000) {
				DialogExcessive();
			} else if (jrtLot.ifPerfectIfo(this, sessionIdStr)) {

				String sTouzhuAlert = "";
				sTouzhuAlert = getTouzhuAlert();
				alert(sTouzhuAlert);

			}
		}
	}

	//
	public boolean isTouZhu() {
		for (int i = 0; i < ballTables.size(); i++) {
			if (ballTables.get(i).getHighlightBallNums() == 0) {
				return true;
			}
		}
		return false;
	}

	// /**
	// * 单笔投注大于2万元时的对话框
	// */
	private void DialogExcessive() {
		AlertDialog.Builder builder = new AlertDialog.Builder(JinQC.this);
		builder.setTitle("投注失败");
		builder.setMessage("单笔投注不能大于2万元");
		// 确定
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}

				});
		builder.show();
	}

	/**
	 * 加减按钮事件监听方法
	 * 
	 * @作者：
	 * @日期：
	 * @参数：
	 * @返回值：
	 * @修改人：
	 * @修改内容：
	 * @修改日期：
	 * @版本：
	 */
	private void setSeekWhenAddOrSub(int idFind, View iV, final int isAdd,
			final SeekBar mSeekBar, final boolean isBeiShu) {
		ImageButton subtractBeishuBtn = (ImageButton) findViewById(idFind);
		subtractBeishuBtn.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isBeiShu) {
					if (isAdd == 1) {
						iProgressBeishu++;
						if (iProgressBeishu > 99)
							iProgressBeishu = 99;
						mSeekBar.setProgress(iProgressBeishu);
					} else {
						iProgressBeishu--;
						if (iProgressBeishu < 1)
							iProgressBeishu = 1;
						mSeekBar.setProgress(iProgressBeishu);
					}
				}
			}
		});
	}

	// // 提示框1 用来提醒选球规则
	// // fqc delete 删除取消按钮 7/14/2010
	private void alert1(String string) {
		Builder dialog = new AlertDialog.Builder(this).setTitle("请选择号码")
				.setMessage(string).setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}

						});
		dialog.show();

	}

	/**
	 * 投注确认提示框
	 * 
	 * @作者：
	 * @日期：
	 * @参数：
	 * @返回值：
	 * @修改人：
	 * @修改内容：
	 * @修改日期：
	 * @版本：
	 */
	private void alert(String string) {
		Builder dialog = new AlertDialog.Builder(this).setTitle("您选择的是")
				.setMessage(string).setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								jinqc_btn_touzhu.setClickable(false);
								jinqc_btn_touzhu
										.setImageResource(R.drawable.touzhuup_n);
								iHttp.whetherChange = false;
								showDialog(DIALOG1_KEY); // 显示网络提示框 2010/7/4
								// TODO Auto-generated method stub
								Thread t = new Thread(new Runnable() {
									int iZhuShu = getZhuShu();
									String[] strCode = null;
									int iBeiShu = mSeekBarBeishu.getProgress();
									String zhuma = getZhuMa();

									@Override
									public void run() {

										if (mTimesMoney == 2) {
											strCode = payNew(zhuma, ""
													+ iBeiShu,
													iZhuShu * 2 + "", "1" + "");
										} else if (mTimesMoney == 3) {
											strCode = payNew(zhuma, ""
													+ iBeiShu,
													iZhuShu * 3 + "", "1" + "");
										}
										jinqc_btn_touzhu.setClickable(true);

										Message msg1 = new Message();
										msg1.what = 18;
										handler.sendMessage(msg1);

										Log.v("+++++strCode[]++JinQC++++",
												strCode[0] + "<>" + strCode[1]);
										if (strCode[0].equals("0000")
												&& strCode[1].equals("000000")) {
											Message msg = new Message();
											msg.what = 14;
											handler.sendMessage(msg);
										} else if (strCode[0].equals("070002")) {
											Message msg = new Message();
											msg.what = 15;
											handler.sendMessage(msg);
										} else if (strCode[0].equals("0000")
												&& strCode[1].equals("000001")) {
											Message msg = new Message();
											msg.what = 12;
											handler.sendMessage(msg);
										} else if (strCode[0].equals("1007")) {
											Message msg = new Message();
											msg.what = 10;
											handler.sendMessage(msg);
										} else if (strCode[1].equals("002002")) {
											Message msg = new Message();
											msg.what = 11;
											handler.sendMessage(msg);
										} else if (strCode[0].equals("040006")
												|| strCode[0].equals("201015")) {
											Message msg = new Message();
											msg.what = 9;
											handler.sendMessage(msg);
										} else if (strCode[0].equals("00")
												&& strCode[1].equals("00")) {
											Message msg = new Message();
											msg.what = 13;
											handler.sendMessage(msg);
										} else if (strCode[0].equals("040003")) {
											Message msg = new Message();
											msg.what = 19;
											handler.sendMessage(msg);
										} else if (strCode[0].equals("00")
												&& strCode[1].equals("1002")) {
											Message msg = new Message();
											msg.what = 23;
											handler.sendMessage(msg);
										} else {
											Message msg = new Message();
											msg.what = 17;
											handler.sendMessage(msg);
										}
									}

								});
								t.start();
							}
						}).setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}

						});
		dialog.show();

	}

	// 投注新接口 20100711陈晨
	protected String[] payNew(String betCode, String lotMulti, String amount,
			String qiShu) {
		// TODO Auto-generated method stub
		String[] error_code = null;
		BettingInterface betting = new BettingInterface();

		ShellRWSharesPreferences shellRW = new ShellRWSharesPreferences(this,
				"addInfo");
		String sessionid = shellRW.getUserLoginInfo("sessionid");
		String phonenum = shellRW.getUserLoginInfo("phonenum");
		PublicMethod.myOutput("-------------touzhusessionid" + sessionid);
		PublicMethod.myOutput("-------------phonenum" + phonenum
				+ "----lotMulti---" + lotMulti + "----amount----" + amount
				+ "---qiShu---" + qiShu);
		if (mTimesMoney == 2) {
			error_code = betting.BettingTC(phonenum, str[2], betCode, lotMulti,
					amount, "2", qiShu, sessionid, batchCode);
		} else if (mTimesMoney == 3) {
			error_code = betting.BettingTC(phonenum, str[2], betCode, lotMulti,
					amount, "3", qiShu, sessionid, batchCode);
		}

		return error_code;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (progress < 1)
			seekBar.setProgress(1);
		int iProgress = seekBar.getProgress();

		switch (seekBar.getId()) {
		case R.id.jinqc_seek_beishu:
			iProgressBeishu = iProgress;
			mTextBeishu.setText("" + iProgressBeishu);
			changeTextSumMoney();
			break;
		default:
			break;
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	/** 获取分析的数据 */
	public void getInfo(final int index) {
		showDialog(DIALOG1_KEY);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String error_code = "00";
				while (iretrytimes < 3 && iretrytimes > 0) {

					re = jrtLot.getZCInfo(str[2], str[0],
							teamInfos.get(index).num);

					try {
						obj = new JSONObject(re);
						error_code = obj.getString("error_code");
						iretrytimes = 3;
					} catch (Exception e) {
						iretrytimes--;

					}

					if (iretrytimes == 0 && iHttp.whetherChange == false) {
						iHttp.whetherChange = true;
						if (iHttp.conMethord == iHttp.CMWAP) {
							iHttp.conMethord = iHttp.CMNET;
						} else {
							iHttp.conMethord = iHttp.CMWAP;
						}
						iretrytimes = 2;
						while (iretrytimes < 3 && iretrytimes > 0) {
							re = jrtLot.getZCInfo(str[2], str[0], teamInfos
									.get(index).num);
							try {
								obj = new JSONObject(re);
								error_code = obj.getString("error_code");
								iretrytimes = 3;
							} catch (Exception e) {

								iretrytimes--;
								error_code = "000";

							}
						}
					}
				}
				iretrytimes = 2;
				if (error_code.equals("00")) {
					Message msg = new Message();
					msg.what = 0;
					handler.sendMessage(msg);

				} else if (error_code.equals("000000")) {
					JinQC.this.position = index + 1;
					Message msg = new Message();
					msg.what = 22;
					handler.sendMessage(msg);
				} else if (error_code.equals("100000")) {
					Message msg = new Message();
					msg.what = 2;
					handler.sendMessage(msg);
				} else if (error_code.equals("200001")) {
					Message msg = new Message();
					msg.what = 3;
					handler.sendMessage(msg);
				} else if (error_code.equals("200002")) {
					Message msg = new Message();
					msg.what = 4;
					handler.sendMessage(msg);
				} else if (error_code.equals("200003")) {
					Message msg = new Message();
					msg.what = 5;
					handler.sendMessage(msg);
				} else if (error_code.equals("200005")) {
					Message msg = new Message();
					msg.what = 6;
					handler.sendMessage(msg);
				} else if (error_code.equals("200008")) {
					Message msg = new Message();
					msg.what = 7;
					handler.sendMessage(msg);
				} else if (error_code.equals("200004")) {
					Message msg = new Message();
					msg.what = 20;
					handler.sendMessage(msg);
				} else if (error_code.equals("200006")) {
					Message msg = new Message();
					msg.what = 21;
					handler.sendMessage(msg);
				}

			}

		}).start();
	}

	private class TeamInfo {
		String hTeam;
		String vTeam;
		String hRankNum;
		String vRankNum;
		String num;
	}

}
