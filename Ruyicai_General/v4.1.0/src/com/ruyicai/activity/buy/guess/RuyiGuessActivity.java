package com.ruyicai.activity.buy.guess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.palmdream.RuyicaiAndroid.R;
import com.ruyicai.activity.buy.guess.bean.ItemInfoBean;
import com.ruyicai.activity.common.PullRefreshListView;
import com.ruyicai.activity.common.UserLogin;
import com.ruyicai.activity.common.PullRefreshListView.OnRefreshListener;
import com.ruyicai.constant.ShellRWConstants;
import com.ruyicai.controller.Controller;
import com.ruyicai.util.PublicMethod;
import com.ruyicai.util.RWSharedPreferences;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/***
 * @author yejc
 *
 */
public class RuyiGuessActivity extends Activity   implements OnRefreshListener{

	public final static String ITEM_ID = "itemId";
	public final static String USER_NO = "userNo";
	public final static String TITLE = "title";
	public final static String ISEND = "isend";
	public final static String JUMP_FLAG = "jump_flag";
	public final static String MYSELECTED = "my_selected";
	/**用户名*/
	private String mUserNo = "";
	/**当前列表显示了多少页数据*/
	private int mPageIndex = 0;
	/**服务器端总共有多少页数据*/
	private int mTotalPage = 0;
	/**选择的竞猜Id*/
	private int mSelectedId = 0;
	/**是否登陆*/
	private boolean mIsLogin = false;
	/**true我竞猜过的问题*/
	private boolean mIsMySelected = false;
	private boolean mIsFirst = true;
//	/**是否点击了更多按钮*/
//	private boolean mIsClickMoreBtn = false; 
	private LayoutInflater mInflater = null;
	private ProgressDialog mProgressdialog = null;
	private RWSharedPreferences mSharedPreferences = null;
	/**自定义listview 用于下拉刷新*/
	private PullRefreshListView mPullListView = null;
	private View mFooterView = null;
	private List<ItemInfoBean> mQuestionsList = new ArrayList<ItemInfoBean>();
	private MessageHandler mHandler = new MessageHandler();
	private ListViewAdapter mAdapter = new ListViewAdapter();
	private String[] mStateArray = { "竞猜中", "参与中", "已参与" };
	private int[] mIconArray = { R.drawable.buy_ruyiguess_item_yellow,
			R.drawable.buy_ruyiguess_item_orange,
			R.drawable.buy_ruyiguess_item_pink };
	/**把参与题目的状态保存到map中，不用再去请求后台来改变当前页面的显示状态状态*/
	private Map<Integer, Boolean> mLocalDataMap = new HashMap<Integer, Boolean>();
	/** 每次请求的数量 */
	private String mItemCount = "10";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.buy_ruyiguess);
		mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		readUserInfo();
		initListView();
		mProgressdialog = PublicMethod.creageProgressDialog(this);
		if (JUMP_FLAG.equals(getIntent().getStringExtra(JUMP_FLAG))) {
			mIsMySelected = true;
			Controller.getInstance(this).getRuyiGuessList(mHandler, mUserNo, 1, "1", mPageIndex, mItemCount);
		} else {
			Controller.getInstance(this).getRuyiGuessList(mHandler, mUserNo, 1, "0", mPageIndex, mItemCount);
		}
		Controller.getInstance(this).addActivity(this); //添加到Activity list用于管理
	}

	private void readUserInfo() {
		mSharedPreferences = new RWSharedPreferences(RuyiGuessActivity.this,
				"addInfo");
		String sessionId = mSharedPreferences.getStringValue(ShellRWConstants.SESSIONID);
		if (!"".equals(sessionId)) {
			mIsLogin = true;
			mUserNo = mSharedPreferences.getStringValue(ShellRWConstants.USERNO);
		}
	}
	
	private void initListView(){
//		mListView = (ListView)findViewById(R.id.ruyi_guess_listview);
		mPullListView = (PullRefreshListView)findViewById(R.id.ruyi_guess_listview);
		mFooterView = mInflater.inflate(R.layout.lookmorebtn, null);
		mFooterView.setBackgroundColor(this.getResources().getColor(R.color.jczq_listview_item_bg));
//		mListView.addFooterView(mFooterView);
		mPullListView.addFooterView(mFooterView);
		mFooterView.setVisibility(View.GONE);
		mFooterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addmore();
			}
		});
		mPullListView.setAdapter(mAdapter);
		mPullListView.setonRefreshListener(this);
		mPullListView.setShowState(false);
		mPullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (!mIsLogin) {
					startActivityForResult(new Intent(RuyiGuessActivity.this,
							UserLogin.class), 1000);
				} else {
					Intent intent = new Intent(RuyiGuessActivity.this,
							RuyiGuessDetailActivity.class);
					mSelectedId = arg2-1;
					intent.putExtra(ITEM_ID, mQuestionsList.get(mSelectedId).getId());
					intent.putExtra(USER_NO, mUserNo);
					intent.putExtra(TITLE, mQuestionsList.get(mSelectedId).getTitle());
					intent.putExtra(MYSELECTED, mIsMySelected);
					if ("1".equals(mQuestionsList.get(mSelectedId).getAllDraw())) {
						intent.putExtra(ISEND, true);
					} else {
						intent.putExtra(ISEND, false);
					}
					startActivityForResult(intent, 1001);
				}
			}
		});
	}
	
	private void setMoreViewState() {
		if (mTotalPage > 1 && mIsFirst) {
			mIsFirst = false;
			mFooterView.setVisibility(View.VISIBLE);
		}
	}
	
	private void dismissDialog() {
		if (mProgressdialog != null && mProgressdialog.isShowing()) {
			mProgressdialog.dismiss();
		}
	}
	
	/**
	 * 获取更多数据
	 */
	private void addmore() {
		mPageIndex++;
		if (mPageIndex > mTotalPage - 1) {
			mPageIndex = mTotalPage - 1;
			mFooterView.setVisibility(View.GONE);
//			mListView.removeFooterView(mFooterView);
			mIsFirst = true;
			Toast.makeText(this,
					R.string.usercenter_hasgonelast, Toast.LENGTH_SHORT).show();
		} else {
			mProgressdialog = PublicMethod.creageProgressDialog(this);
			if (mIsMySelected) {
				Controller.getInstance(this).getRuyiGuessList(mHandler, mUserNo, 1, "1", mPageIndex, mItemCount);
			} else {
				Controller.getInstance(this).getRuyiGuessList(mHandler, mUserNo, 1, "0", mPageIndex, mItemCount);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			mIsFirst = true;
			if (requestCode == 1000) {//登陆成功后重新加载当前账户的参与状态
				mPageIndex = 0;
				mIsLogin = true;
				mUserNo = mSharedPreferences.getStringValue(ShellRWConstants.USERNO);
				mProgressdialog = PublicMethod.creageProgressDialog(this);
				String count = String.valueOf(mQuestionsList.size());
				Controller.getInstance(this).getRuyiGuessList(mHandler, mUserNo, 2, "0", 0, count);
			} else if (requestCode == 1001){ //如果参与成功后把参与题目的状态保存到map对象中并重新加载
				mLocalDataMap.put(mSelectedId, true);
				mAdapter.notifyDataSetChanged();
			}
			
		}
	}

	private String formatString(int resId, String args) {
		return String.format(getResources().getString(resId), args);
	}

	private class ListViewAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			if (mQuestionsList != null) {
				return mQuestionsList.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return mQuestionsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemInfoBean info = mQuestionsList.get(position);
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.buy_ruyiguess_listview_item, null);
				holder = new ViewHolder();
				holder.integral = (TextView) convertView
						.findViewById(R.id.ruyi_guess_item_integral);
				holder.title = (TextView) convertView
						.findViewById(R.id.ruyi_guess_item_title);
				holder.time = (TextView) convertView
						.findViewById(R.id.ruyi_guess_item_time);
				holder.participate = (TextView) convertView
						.findViewById(R.id.ruyi_guess_item_participate);
				holder.guessStop = (ImageView) convertView
						.findViewById(R.id.ruyi_guess_item_stop);
				holder.itemLayout = (LinearLayout) convertView
						.findViewById(R.id.ruyi_guess_item_layout);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.integral.setText(formatString(
					R.string.buy_ruyi_guess_item_integral_two, info.getAward()));
			holder.title.setText(info.getTitle());
			StringBuffer strBuffer = new StringBuffer();
			strBuffer.append(getResources().getString(R.string.buy_ruyi_guess_item_time));
			strBuffer.append(info.getStartTime());
			strBuffer.append(" ~ ");
			strBuffer.append(info.getEndTime());
			holder.time.setText(strBuffer.toString());
			if ("0".equals(info.getAllDraw())) { //竞猜是否结束 0:未结束;1:已结束
				holder.guessStop.setVisibility(View.GONE);
				holder.integral.setBackgroundResource(mIconArray[position%3]);
				holder.participate.setBackgroundResource(R.drawable.buy_ruyiguess_item_participateing);
				holder.participate.setVisibility(View.VISIBLE);
				if (mIsLogin) {
					if (mLocalDataMap.containsKey(position) 
							&& mLocalDataMap.get(position)) {
						holder.participate.setText(mStateArray[1]);//根据状态判断显示
					} else {
						if ("0".equals(info.getParticipate())) { //是否参与竞猜 0:未参与;1:已参与
							holder.participate.setText(mStateArray[0]);//根据状态判断显示
						} else {
							holder.participate.setText(mStateArray[1]);//根据状态判断显示
						}
					}
				} else {
					holder.participate.setText(mStateArray[0]);//根据状态判断显示
				}
				holder.itemLayout.setBackgroundResource(R.drawable.buy_ruyiguess_item_bg);
			} else {
				holder.guessStop.setVisibility(View.VISIBLE);
				holder.integral.setBackgroundResource(R.drawable.buy_ruyiguess_item_gray);
				holder.participate.setBackgroundResource(R.drawable.buy_ruyiguess_item_participated);
				if (mIsLogin) {
					if ("0".equals(info.getParticipate())) { //是否参与竞猜 0:未参与;1:已参与
						holder.participate.setVisibility(View.GONE);
					} else {
						holder.participate.setVisibility(View.VISIBLE);
						holder.participate.setText(mStateArray[2]);//根据状态判断显示
					}
				} else {
					holder.participate.setVisibility(View.GONE);
				}
				holder.itemLayout.setBackgroundResource(R.drawable.buy_ruyiguess_item_bg_yellow);
			}
			int padValue = PublicMethod.getPxInt(2, RuyiGuessActivity.this);
			holder.participate.setPadding(3*padValue, padValue, padValue, padValue);
			int layoutPad = 2*padValue;
			holder.itemLayout.setPadding(layoutPad, layoutPad, layoutPad, layoutPad);
			
			return convertView;
		}
		
		class ViewHolder {
			TextView integral; //积分
			TextView title; //竞猜题目
			TextView time; //竞猜时间
			TextView participate; //参与状态
			ImageView guessStop; //截止
			LinearLayout itemLayout;
		}
	}
	
	private class MessageHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String str = (String)msg.obj;
			if (str == null || "".equals(null)) {
				Toast.makeText(RuyiGuessActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
				mPullListView.onRefreshComplete();
				dismissDialog();
			} else {
				try {
					JSONObject jsonObj = new JSONObject(str);
					String errorCode = jsonObj.getString("error_code");
					if ("0000".equals(errorCode)) {
						if (msg.what == 2) {
							mPageIndex = 0;
							mQuestionsList.clear();
							mLocalDataMap.clear();
						}
						
						JSONArray jsonArray = jsonObj.getJSONArray("result");
						mTotalPage = Integer.valueOf(jsonObj.getString("totalPage").trim());
						for (int i = 0; i < jsonArray.length(); i++) {
							ItemInfoBean info = new ItemInfoBean();
							JSONObject itemObj = jsonArray.getJSONObject(i);
							info.setId(itemObj.getString("id"));
							info.setTitle(itemObj.getString("title"));
							info.setAward(itemObj.getString("award"));
							info.setStartTime(itemObj.getString("startTime"));
							info.setEndTime(itemObj.getString("endTime"));
							info.setParticipate(itemObj.getString("participate"));
							info.setEndState(itemObj.getString("isEnd"));
							info.setAllDraw(itemObj.getString("allDraw"));
							mQuestionsList.add(info);
						}
						mAdapter.notifyDataSetChanged();
						setMoreViewState();
					} else if ("0047".equals(errorCode)) {
						TextView tv = (TextView) findViewById(R.id.ruyi_guest_no_record);
						tv.setVisibility(View.VISIBLE);
						mPullListView.setVisibility(View.GONE);
					} else {
						String message = jsonObj.getString("message");
						Toast.makeText(RuyiGuessActivity.this, message, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					if (msg.what == 2) {
						mPullListView.onRefreshComplete();
					}
					
					dismissDialog();
				}
			}
			
		}
		
	}

	@Override
	public void onRefresh() {
		mProgressdialog = PublicMethod.creageProgressDialog(this);
		int maxResult = 0;
		if (mQuestionsList.size() > 10) {
			maxResult = mQuestionsList.size();
		} else {
			maxResult = 10;
		}
		mItemCount = String.valueOf(maxResult);
		Controller.getInstance(this).getRuyiGuessList(mHandler, mUserNo, 2, "0", 0, mItemCount);
	}
}
