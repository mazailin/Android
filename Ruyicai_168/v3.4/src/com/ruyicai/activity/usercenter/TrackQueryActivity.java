package com.ruyicai.activity.usercenter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.palmdream.RuyicaiAndroid168.R;
import com.ruyicai.constant.Constants;
import com.ruyicai.handler.HandlerMsg;
import com.ruyicai.handler.MyHandler;
import com.ruyicai.net.newtransaction.BetAndGiftInterface;
import com.ruyicai.net.newtransaction.CancelTrackInterface;
import com.ruyicai.net.newtransaction.GetLotNohighFrequency;
import com.ruyicai.net.newtransaction.GiftQueryInterface;
import com.ruyicai.net.newtransaction.SoftwareUpdateInterface;
import com.ruyicai.net.newtransaction.TrackDailInterface;
import com.ruyicai.net.newtransaction.pojo.BetAndGiftPojo;
import com.ruyicai.net.newtransaction.pojo.BetAndWinAndTrackAndGiftQueryPojo;
import com.ruyicai.util.PublicMethod;
import com.ruyicai.util.RWSharedPreferences;
import com.umeng.analytics.MobclickAgent;
/**
 * 追号查
 * @author miao
 */
public class TrackQueryActivity extends Activity implements HandlerMsg{

	private LinearLayout usecenerLinear;
	private Button returnButton;
	private TextView	titleTextView;
	
	String jsonString;
	String jsontrack;
	final	String BETCODE="betCode",BATCHNUM="batchNum",ORDERTIME="orderTime",ID = "id",
						LOTNO="lotNo",LOTNAME = "lotName",AMOUNT="amount",LASTNUM = "lastNum",BEGINBATCH = "beginBatch"
							,STATE = "state",ERROR_CODE = "error_code",MESSAGE = "message",PRIZEEND="prizeEnd",ISBUY="isRepeatBuy",
						 BET_CODE="bet_code",LOTMULTI="lotMulti";
	private final int DIALOG1_KEY = 0;
	AlertDialog remindCancleDialog;
	private String phonenum,sessionid,userno;
	List<TrackQueryInfo> windatalist = new ArrayList<TrackQueryInfo>(); 
	List<TrackQueryInfo2> tracklist = new ArrayList<TrackQueryInfo2>(); 

	Context context = this ;
	ProgressDialog dialog;
	String jsonobject;
	int  allPage;
	int pageindex;
	ListView queryinfolist;
	boolean isfirst = false,isCancleTrackNet = false;
	WinPrizeAdapter adapter;
	View view;
	ProgressBar progressbar;
	private MyHandler touzhuhandler = new MyHandler(this);
	private Dialog continueDialog = null;
	 Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(dialog !=null){
					dialog.dismiss();
				}
				Toast.makeText(TrackQueryActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
				break;
			case 1:
				if(dialog !=null){
					dialog.dismiss();
				}
				encodejson((String) msg.obj);
				adapter.notifyDataSetChanged();
				break;
			case 2:
				if(dialog !=null){
					dialog.dismiss();
				}
				Toast.makeText(TrackQueryActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
				cancleTrackError000000();
				break;
			case 3:
				if(dialog !=null){
					dialog.dismiss();
				}
				Toast.makeText(TrackQueryActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                tracklist=encodejsontrack(jsontrack);
                lookDetailDialogtow(tracklist);
				break;
			}
		}
	 };
	public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.usercenter_mainlayoutold);
			returnButton = (Button)findViewById(R.id.layout_usercenter_img_return);
			initReturn();
			titleTextView = (TextView)findViewById(R.id.usercenter_mainlayou_text_title);
			returnButton.setBackgroundResource(R.drawable.returnselecter);
			titleTextView.setText(R.string.usercenter_trackNumberInquiry);
			returnButton.setText(R.string.returnlastpage);
			jsonobject = this.getIntent().getStringExtra("trackjson");
			encodejson(jsonobject);
			isfirst = true;
			initLinear();
	}
	protected  void initReturn(){
		returnButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					finish();
			}
		});
	}
	 private void initPojo(){
			RWSharedPreferences shellRW = new RWSharedPreferences(this, "addInfo");
			phonenum = shellRW.getStringValue("phonenum");
			sessionid = shellRW.getStringValue("sessionid");
			userno = shellRW.getStringValue("userno");
	}
	 
	final Handler tHandler = new Handler();
	private void netting(final int pageindex){
		tHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressbar.setVisibility(ProgressBar.VISIBLE);
			}
		 });
	
		new Thread(new Runnable() {
			@Override
		public void run() {
		initPojo();
		BetAndWinAndTrackAndGiftQueryPojo winQueryPojo = new BetAndWinAndTrackAndGiftQueryPojo();
		winQueryPojo.setUserno(userno);
		winQueryPojo.setSessionid(sessionid);
		winQueryPojo.setPhonenum(phonenum);
		winQueryPojo.setPageindex(String.valueOf(pageindex));
		winQueryPojo.setMaxresult("10");
		winQueryPojo.setType("track");
		isCancleTrackNet = false;
		Message msg = new Message();
		jsonString = GiftQueryInterface.getInstance().giftQuery(winQueryPojo);
		tHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressbar.setVisibility(ProgressBar.INVISIBLE);
				view.setEnabled(true);
			}
		 });
		try {
			JSONObject aa = new JSONObject(jsonString);
			String errcode = aa.getString(ERROR_CODE);
			String message = aa.getString(MESSAGE);
			if(errcode.equals("0000")){
				msg.what = 1;
				msg.obj = jsonString;
				handler.sendMessage(msg);
			}else{
				msg.what = 0;
				msg.obj = message;
				handler.sendMessage(msg);					
			}
		} catch (Exception e) {
		}
		}
	 }).start();
	}
	private void nettingContinue(final int pageindex){
		tHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressbar.setVisibility(ProgressBar.VISIBLE);
			}
		 });
	
		new Thread(new Runnable() {
			@Override
		public void run() {
		initPojo();
		BetAndWinAndTrackAndGiftQueryPojo winQueryPojo = new BetAndWinAndTrackAndGiftQueryPojo();
		winQueryPojo.setUserno(userno);
		winQueryPojo.setSessionid(sessionid);
		winQueryPojo.setPhonenum(phonenum);
		winQueryPojo.setPageindex(String.valueOf(pageindex));
		winQueryPojo.setMaxresult("10");
		winQueryPojo.setType("track");
		isCancleTrackNet = false;
		Message msg = new Message();
		jsonString = GiftQueryInterface.getInstance().giftQuery(winQueryPojo);
		tHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressbar.setVisibility(ProgressBar.INVISIBLE);
				view.setEnabled(true);
			}
		 });
		try {
			JSONObject aa = new JSONObject(jsonString);
			String errcode = aa.getString(ERROR_CODE);
			String message = aa.getString(MESSAGE);
			if(errcode.equals("0000")){
				windatalist.clear();
				msg.what = 1;
				msg.obj = jsonString;
				handler.sendMessage(msg);
			}else{
				msg.what = 0;
				msg.obj = message;
				handler.sendMessage(msg);					
			}
		} catch (Exception e) {
		}
		}
	 }).start();
	}
	private void trackQueryNet(final int pageindex){
		showDialog(DIALOG1_KEY); 
		new Thread(new Runnable() {
			public void run() {
			nettingContinue(pageindex);
			}
		}).start();
	}
	private void initLinear(){
		usecenerLinear = (LinearLayout)findViewById(R.id.usercenterContent);
		usecenerLinear.addView(initLinearView());
	}
	private  View	initLinearView(){
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = (LinearLayout) inflate.inflate(R.layout.usercenter_listview_layout, null);
		queryinfolist = (ListView) view.findViewById(R.id.usercenter_listview_queryinfo);
		initListView(queryinfolist,windatalist);
		
		return view;
	}
	/**
	 * 初始化列表
	 */
	private void addmore(){
		 isfirst = false;
         pageindex++;
         if(pageindex<allPage){
	        
	       netting(pageindex);
		   }else{
			   pageindex=allPage-1;
			   progressbar.setVisibility(view.INVISIBLE);
			   queryinfolist.removeFooterView(view);
			Toast.makeText(TrackQueryActivity.this, R.string.usercenter_hasgonelast, Toast.LENGTH_SHORT).show();   
		   }
	}
	public void initList(){
		initListView(queryinfolist,windatalist);
	}

	private void	initListView(ListView listview,List list){
		adapter = new WinPrizeAdapter(this, windatalist);
		LayoutInflater mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    view = mInflater.inflate(R.layout.lookmorebtn,null);
	    progressbar=(ProgressBar)view.findViewById(R.id.getmore_progressbar);
		listview.addFooterView(view);
	 	// 数据源
		listview.setAdapter(adapter);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				view.setEnabled(false);
	            addmore();

				
			}
		});
	}
	private AlertDialog lookDetailDialog(String detail,final String ID){
		LayoutInflater factory = LayoutInflater.from(this);
		/*中奖查询的查看详情使用余额查询的布局*/
		View	view = factory.inflate(R.layout.usercenter_zhuihaodingdan, null);
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		TextView  title = (TextView)view.findViewById(R.id.usercenter_balancequery_title);
		TextView  remind = (TextView)view.findViewById(R.id.usercenter_remind_text);
		Button lookDail=(Button)view.findViewById(R.id.usercenter_balancequery_ok);
		remind.setVisibility(TextView.GONE);
		title.setText(R.string.usercenter_detailsTitle);
		TextView	detailTextView = (TextView)view.findViewById(R.id.balanceinfo);
		detailTextView.setText(detail);
		Button cancleLook = (Button)view.findViewById(R.id.usercenter_balancequery_back);
		cancleLook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		lookDail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			 lookTrackNet(ID)	;
			}
		});
		dialog.show();
		dialog.getWindow().setContentView(view);
		return dialog;
			
	}
	
	
	private AlertDialog cancleTrackDialog(final String id){
		LayoutInflater factory = LayoutInflater.from(this);
		/*中奖查询的查看详情使用余额查询的布局*/
		View	view = factory.inflate(R.layout.usercenter_balancequery, null);
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		TextView  title = (TextView)view.findViewById(R.id.usercenter_balancequery_title);
		TextView  remind = (TextView)view.findViewById(R.id.usercenter_remind_text);
		remind.setVisibility(TextView.GONE);
		title.setText(R.string.cancel_add_num);
		TextView	detailTextView = (TextView)view.findViewById(R.id.balanceinfo);
		detailTextView.setTextSize(18);
		detailTextView.setText(R.string.usercenter_cancleTrackRemind);
		Button cancleLook = (Button)view.findViewById(R.id.usercenter_balancequery_back);
		cancleLook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		Button okBtn = (Button)view.findViewById(R.id.usercenter_balancequery_ok);
		okBtn.setVisibility(Button.VISIBLE);
		okBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
				cancleTrackNet(id);
			}
		});
		dialog.show();
		dialog.getWindow().setContentView(view);
		return dialog;
	}
	
	
	private AlertDialog lookDetailDialogtow(List listdata){
		LayoutInflater factory = LayoutInflater.from(this);
		/*中奖查询的查看详情使用余额查询的布局*/
		View	view = factory.inflate(R.layout.usercenter_trak_two, null);
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		TextView  title = (TextView)view.findViewById(R.id.usercenter_balancequery_title);
		ListView  list=(ListView)view.findViewById(R.id.shouyizhuihaolist);
		Tracklookadapter adapter=new Tracklookadapter(TrackQueryActivity.this, listdata);
		list.setAdapter(adapter);
		title.setText("追号详情");
  		Button cancleLook = (Button)view.findViewById(R.id.usercenter_balancequery_back);
		cancleLook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		dialog.show();
		dialog.getWindow().setContentView(view);
		return dialog;
	}
	/**
	 * 取消追号联网
	 * @param tsubscribeid 追号记录id
	 */
	private void cancleTrackNet(final String tsubscribeNo){
		showDialog(0);
		new Thread(new Runnable() {
			public void run() {
				initPojo();
				Message msg = new Message();
				String cancleTrackBack = CancelTrackInterface.getInstance().canceltrack(userno, sessionid, tsubscribeNo, phonenum);
				try {
					JSONObject cancleTrackReturn = new JSONObject(cancleTrackBack);
					String errorCode = cancleTrackReturn.getString(ERROR_CODE);
					String message = cancleTrackReturn.getString(MESSAGE);
					if(errorCode.equals("0000")){
						msg.what = 2;
						msg.obj = message;
						handler.sendMessage(msg);
					}else{
						msg.what = 0;
						msg.obj = message;
						handler.sendMessage(msg);
					};
				} catch (JSONException e) {
				}
			}
		}).start();
	}
	
	
	/**
	 * 取消追号联网
	 * @param tsubscribeid 追号记录id
	 */
	private void lookTrackNet(final String tsubscribeNo){
		showDialog(0);
		new Thread(new Runnable() {
			public void run() {
				initPojo();
				Message msg = new Message();
				jsontrack = TrackDailInterface.getInstance().looktrack(tsubscribeNo);
				try {
					JSONObject lookTrackBackreturn = new JSONObject(jsontrack);
					String errorCode = lookTrackBackreturn.getString(ERROR_CODE);
					String message = lookTrackBackreturn.getString(MESSAGE);
					if(errorCode.equals("0000")){
						msg.what = 3;
						msg.obj = message;
						handler.sendMessage(msg);
					}else{
						msg.what = 0;
						msg.obj = message;
						handler.sendMessage(msg);
					};
				} catch (JSONException e) {
				}
			}
		}).start();
	}
	
	  public void encodejson(String json) {
		  try {
			  JSONObject  winprizejsonobj = new JSONObject(json);
			  allPage = Integer.parseInt(winprizejsonobj.getString("totalPage"));
			  String winprizejsonstring = winprizejsonobj.getString("result");
			  JSONArray winprizejson = new JSONArray(winprizejsonstring);
			  for(int i=0;i<winprizejson.length();i++){
				try{
					TrackQueryInfo winPrizeQueryinfo = new TrackQueryInfo();
					winPrizeQueryinfo.setBetCode(winprizejson.getJSONObject(i).getString(BETCODE));
					winPrizeQueryinfo.setAmount(winprizejson.getJSONObject(i).getString(AMOUNT));
					winPrizeQueryinfo.setState(winprizejson.getJSONObject(i).getString(STATE));
					winPrizeQueryinfo.setBatchNum(winprizejson.getJSONObject(i).getString(BATCHNUM));
					winPrizeQueryinfo.setLotName(winprizejson.getJSONObject(i).getString(LOTNAME));
					winPrizeQueryinfo.setOrderTime(winprizejson.getJSONObject(i).getString(ORDERTIME));
					winPrizeQueryinfo.setBeginBatch(winprizejson.getJSONObject(i).getString(BEGINBATCH));
					winPrizeQueryinfo.setLastNums(winprizejson.getJSONObject(i).getString(LASTNUM));
					winPrizeQueryinfo.setId(winprizejson.getJSONObject(i).getString(ID));
					winPrizeQueryinfo.setPrizeEnd(winprizejson.getJSONObject(i).getString(PRIZEEND));
					winPrizeQueryinfo.setLotno(winprizejson.getJSONObject(i).getString(LOTNO));
					winPrizeQueryinfo.setIsRepeatBuy(winprizejson.getJSONObject(i).getString(ISBUY));
					winPrizeQueryinfo.setBetTouCode(winprizejson.getJSONObject(i).getString(BET_CODE));
					winPrizeQueryinfo.setLotMulti(winprizejson.getJSONObject(i).getString(LOTMULTI));
					windatalist.add(winPrizeQueryinfo);
				}catch (Exception e) {
				}
			 }
		  	 } catch (JSONException e) {
		  		try {
						JSONObject winprizejson = new JSONObject(json);
		  		} catch (JSONException e1) {
		  		}
	    	}
	  }
	  
	  public List encodejsontrack(String json) {
		  ArrayList<TrackQueryInfo2> dail=new ArrayList<TrackQueryInfo2>();
		  try {
			  JSONObject  winprizejsonobj = new JSONObject(json);
			  String winprizejsonstring = winprizejsonobj.getString("result");
			  JSONArray winprizejson = new JSONArray(winprizejsonstring);
			  for(int i=0;i<winprizejson.length();i++){
				try{
					TrackQueryInfo2 winPrizeQueryinfo = new TrackQueryInfo2();
					winPrizeQueryinfo.setBatchcode(winprizejson.getJSONObject(i).getString("batchCode"));
					winPrizeQueryinfo.setAmount(winprizejson.getJSONObject(i).getString(AMOUNT));
					winPrizeQueryinfo.setState(winprizejson.getJSONObject(i).getString(STATE));
					winPrizeQueryinfo.setLotMulti(winprizejson.getJSONObject(i).getString("lotMulti"));
					winPrizeQueryinfo.setWinCode(winprizejson.getJSONObject(i).getString("winCode"));
					winPrizeQueryinfo.setStateMemo(winprizejson.getJSONObject(i).getString("stateMemo"));
					winPrizeQueryinfo.setPrizeAmt(winprizejson.getJSONObject(i).getString("prizeAmt"));
					winPrizeQueryinfo.setDesc(winprizejson.getJSONObject(i).getString("desc"));
					dail.add(winPrizeQueryinfo);
				}catch (Exception e) {
				}
			 }
		  	 } catch (JSONException e) {
		  		try {
						JSONObject winprizejson = new JSONObject(json);
		  		} catch (JSONException e1) {
		  		}
	    	}
		return dail;
	  }
	class TrackQueryInfo{
	
		private String lotName;
		private String betCode;
		private String batchNum;
		private String  state;
		private String orderTime;
		private String beginBatch;
		private String amount;
		private String lotMulti;
		private String lastNums;
		private String id;
		private String prizeEnd;
		private String lotno;
		private String isRepeatBuy;//true可以继续追号
		private String betTouCode;
		public String getBetTouCode() {
			return betTouCode;
		}
		public void setBetTouCode(String betTouCode) {
			this.betTouCode = betTouCode;
		}
		public String getIsRepeatBuy() {
			return isRepeatBuy;
		}
		public void setIsRepeatBuy(String isRepeatBuy) {
			this.isRepeatBuy = isRepeatBuy;
		}
		public String getLotno() {
			return lotno;
		}
		public void setLotno(String lotno) {
			this.lotno = lotno;
		}
		public String getPrizeEnd() {
			return prizeEnd;
		}
		public void setPrizeEnd(String prizeEnd) {
			this.prizeEnd = prizeEnd;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getLastNums() {
			return lastNums;
		}
		public void setLastNums(String lastNums) {
			this.lastNums = lastNums;
		}
		public String getAmount() {
			return amount;
		}
		public String getLotMulti() {
			return lotMulti;
		}
		public void setLotMulti(String lotMulti) {
			this.lotMulti = lotMulti;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}
		public String getBetCode() {
			return betCode;
		}
		public void setBetCode(String betCode) {
			this.betCode = betCode;
		}
		public String getLotName() {
			return lotName;
		}
		public void setLotName(String lotName) {
			this.lotName = lotName;
		}
		public String getBatchNum() {
			return batchNum;
		}
		public void setBatchNum(String batchNum) {
			this.batchNum = batchNum;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getOrderTime() {
			return orderTime;
		}
		public void setOrderTime(String orderTime) {
			this.orderTime = orderTime;
		}
		public String getBeginBatch() {
			return beginBatch;
		}
		public void setBeginBatch(String beginBatch) {
			this.beginBatch = beginBatch;
		}
	}
	
	class TrackQueryInfo2{
		String batchcode="";
		String lotMulti="";
		String amount="";
		String winCode="";
		String state="";
		String stateMemo="";
		String prizeAmt="";
		public String getBatchcode() {
			return batchcode;
		}
		public void setBatchcode(String batchcode) {
			this.batchcode = batchcode;
		}
		public String getLotMulti() {
			return lotMulti;
		}
		public void setLotMulti(String lotMulti) {
			this.lotMulti = lotMulti;
		}
		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}
		public String getWinCode() {
			return winCode;
		}
		public void setWinCode(String winCode) {
			this.winCode = winCode;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getStateMemo() {
			return stateMemo;
		}
		public void setStateMemo(String stateMemo) {
			this.stateMemo = stateMemo;
		}
		public String getPrizeAmt() {
			return prizeAmt;
		}
		public void setPrizeAmt(String prizeAmt) {
			this.prizeAmt = prizeAmt;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		String desc="";
	}
	/**
	 * 中奖查询的适配器
	 */
	public class WinPrizeAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater; // 扩充主列表布局
			private List<TrackQueryInfo> mList;
			public WinPrizeAdapter(Context context, List<TrackQueryInfo> list) {
				mInflater = LayoutInflater.from(context);
				mList = list;
			}
			public int getCount() {
				return mList.size();
			}
			public Object getItem(int position) {
				return mList.get(position);
			}
			public long getItemId(int position) {
				return position;
			}
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder = null;
				final String lotName = (String) mList.get(position).getLotName();
				final String betAmount = (String) mList.get(position).getAmount();
				final String trackState = (String) mList.get(position).getState();
				final String batchNums = (String) mList.get(position).getBatchNum();
				final String lastNums = (String) mList.get(position).getLastNums();
				final String ordertime = (String) mList.get(position).getOrderTime();
				final String betcode = (String) mList.get(position).getBetCode();
				final String beginBatch = (String) mList.get(position).getBeginBatch();
				final String trackId = (String) mList.get(position).getId();
				String prizeEnd = (String)mList.get(position).getPrizeEnd();
				String lotno = (String)mList.get(position).getLotno();
				String isBuy = (String)mList.get(position).getIsRepeatBuy();
				final String isPrizeEnd ;
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.usercenter_trackquery_listitem,null);
					holder = new ViewHolder();
					holder.lotName = (TextView) convertView.findViewById(R.id.usercenter_trackquery_lotteryname);
					holder.trackState = (TextView)convertView.findViewById(R.id.usercenter_trackquery_trackstate);
					holder.batchNums = (TextView) convertView.findViewById(R.id.usercenter_trackquery_tracknum);
					holder.batchNumed = (TextView) convertView.findViewById(R.id.usercenter_trackquery_tracknumed);
					holder.trackAmount = (TextView)convertView.findViewById(R.id.usercenter_trackquery_money);
					holder.lookdetail = (Button)convertView.findViewById(R.id.usercenter_trackquery_lookdetail);
					holder.cancleTrack = (Button)convertView.findViewById(R.id.usercenter_trackquery_cancle);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				if(prizeEnd.equals("0")){
					isPrizeEnd = "中奖后停止：否";
				}else{
					isPrizeEnd = "中奖后停止：是";
				}
				holder.lotName.setText(lotName);
				final String lastBatch = ""+(Integer.valueOf(batchNums)-Integer.valueOf(lastNums));
				trackState(holder.trackState, trackState,lastBatch);
				cancleTrackVisible(holder.cancleTrack,trackState,lastBatch,trackId,mList.get(position));
				holder.trackAmount .setText(PublicMethod.formatMoney(betAmount));
				holder.batchNums .setText(batchNums);
				holder.batchNumed .setText(lastNums);
				holder.lookdetail.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						StringBuffer detailinfo = new StringBuffer();
						detailinfo.append(getString(R.string.usercenter_lotterytypename)).append(lotName).append("\n")
//							.append(getString(R.string.usercenter_winningCheck_lotteryMultiple)).append(lotMulti).append("\n\n")//倍数先不显示
							.append(getString(R.string.usercenter_trackbatchnums)).append(batchNums).append("\n")
							.append(getString(R.string.usercenter_trackedbatchnums)).append(lastNums).append("\n")
							.append(getString(R.string.usercenter_startbatchnums)).append(beginBatch).append("\n")
							.append(getString(R.string.usercenter_alltrackmoney)).append(PublicMethod.formatMoney(betAmount)).append("\n")
							.append(getString(R.string.usercenter_lottery_time)).append(ordertime).append("\n")
							.append(isPrizeEnd).append("\n")
							.append(getString(R.string.usercenter_trackquery_nowstate)).append(formatTrackState(trackState,lastBatch)).append("\n")
							.append(getString(R.string.usercenter_betcode)).append("\n")
							.append(betcode);
						lookDetailDialog(""+detailinfo,trackId).show();
					}
				});
				return convertView;
			}
			class ViewHolder {
				TextView lotName;
				TextView trackState;
				TextView batchNums;
				TextView batchNumed;
				TextView trackAmount;
				Button   cancleTrack;
				Button   lookdetail;
			}
	}
	
	/**
	 * 中奖查询的适配器
	 */
	public class Tracklookadapter extends BaseAdapter {
		
		private LayoutInflater mInflater;  // 扩充主列表布局
			private List<TrackQueryInfo2> mList;
			public Tracklookadapter(Context context, List<TrackQueryInfo2> list) {
				mInflater = LayoutInflater.from(context);
				mList = list;
			}
			public int getCount() {
				return mList.size();
			}
			public Object getItem(int position) {
				return mList.get(position);
			}
			public long getItemId(int position) {
				return position;
			}
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder = null;
			    String batchcode = mList.get(position).getBatchcode();
			    String lotMulti = mList.get(position).getLotMulti();
			    String amount = mList.get(position).getAmount();
			    String winCode = mList.get(position).getWinCode();
			    String state = mList.get(position).getState();
			    String stateMemo = mList.get(position).getStateMemo();
			    String prizeAmt = mList.get(position).getPrizeAmt();		
			    String desc = mList.get(position).getDesc();		    
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.high_frequencyrevenue_recovery_itme,null);
					holder = new ViewHolder();
				    holder.text=(TextView)convertView.findViewById(R.id.shouyiitem);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				StringBuffer str =new StringBuffer();
				str.append("期号:").append(batchcode).append("\n").append("倍数:").append(lotMulti).append("\n").append("当前投入:").append(PublicMethod.toIntYuan(amount)).append("元").append("\n")
				.append("开奖号码:").append(winCode).append("\n").append("状态:").append(stateMemo).append("\n").append("奖金:").append(PublicMethod.toIntYuan(prizeAmt)).append("元").append("\n");
				
				if(!desc.equals("")&&!desc.equals("null")){
				String dstr="";
				String descstr[]=desc.split("_");	
				str.append("计划投入:").append(descstr[0]).append("元").append("\n").append("计划收益:").append(descstr[1]).append("元").append("\n").append("收益率:").append(descstr[2]);
				}
				holder.text.setText(str);
				return convertView;
			}
			class ViewHolder {
				TextView text;
			
			}
	}
	
	protected Dialog onCreateDialog(int id) {
	   dialog = new ProgressDialog(this);
       switch (id) {
           case DIALOG1_KEY: {
        	   dialog.setTitle(R.string.usercenter_netDialogTitle);
               dialog.setMessage(getString(R.string.usercenter_netDialogRemind));
               dialog.setIndeterminate(true);
               dialog.setCancelable(true);
               return dialog;
           }
       }
       return dialog;
   }
	/**
	 * 格式化state串，将state串设置颜色付值给TextView
	 * @param text   TextView
	 * @param state 
	 */
	private void trackState(TextView text,String state,String lastnum){
		int stateInt = 0;
		stateInt = Integer.parseInt(state);
		int StringId = 0;
		switch (stateInt) {
		case 0:
			if(lastnum.equals("0")){
				StringId =  R.string.usercenter_str_hasClosed;
				text.setTextColor(0xffd4d4d4);
			}else{
				StringId = R.string.usercenter_str_running;
				text.setTextColor(0xff006600);
			}
			break;
		case 2:
			
			StringId = R.string.usercenter_str_hasCancled;
			text.setTextColor(0xffcc0000);
			break;
		case 3:
			StringId =  R.string.usercenter_str_hasClosed;
			text.setTextColor(0xffd4d4d4);
			break;
		}
		text.setText(StringId);
	}
	private void cancleTrackVisible(Button btn,String state,String lastnum,final String trackId,TrackQueryInfo info){
		int stateInt = 0;
		stateInt = Integer.parseInt(state);
		switch (stateInt) {
		case 0:
			if(lastnum.equals("0")){
                setContinueBtn(btn,info);
			}else{
				btn.setBackgroundResource(R.drawable.usercenter_cancletrack_selector);
				btn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						cancleTrackDialog(trackId);
					}
				});
			}
			break;
		case 2:
			setContinueBtn(btn,info);
			break;
		case 3:
			setContinueBtn(btn,info);
			break;
		}
	}
	private void setContinueBtn(Button btn,final TrackQueryInfo info) {
		if(info.getIsRepeatBuy().equals("true")){
			btn.setBackgroundResource(R.drawable.user_continue_issue_selector);
			btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					createContinueDialog(info);
				}
			});
		}else{
			btn.setBackgroundResource(R.drawable.btn_qxzq_stop);
		}
	}
	/**
	 * 继续追号输入期数对话框
	 */
	public void createContinueDialog(final TrackQueryInfo info){
		    String issue = "10";
			continueDialog = new Dialog(this,R.style.MyDialog);
			LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
			View viewDialog = inflater.inflate(R.layout.usercenter_bindphone, null);
			TextView title = (TextView) viewDialog.findViewById(R.id.usercenter_bindphonetitle);
			TextView contentTitle = (TextView) viewDialog.findViewById(R.id.usercenter_bindphonelabel);
			final EditText editIssue = (EditText) viewDialog.findViewById(R.id.usercenter_edittext_bindphoneContext);
			Button cancel = (Button) viewDialog.findViewById(R.id.usercenter_bindphone_ok);
			Button ok = (Button) viewDialog.findViewById(R.id.usercenter_bindphone_back);
			editIssue.setText(issue);
			title.setText(getString(R.string.continue_title));
			contentTitle.setText(getString(R.string.continue_content_title));
//			title.setTextSize(PublicMethod.getPxInt(15, context));
//			contentTitle.setTextSize(PublicMethod.getPxInt(14, context));
			editIssue.setKeyListener(new DigitsKeyListener());
			cancel.setText(getString(R.string.cancel));
			ok.setText(getString(R.string.ok));
			cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				   continueDialog.cancel();	
				}
			});
			ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				   String issueNum = editIssue.getText().toString();
				   if(issueNum.equals("")){
					   Toast.makeText(context, "您输入的期数不能为空！", Toast.LENGTH_SHORT).show();
				   }else if(Integer.parseInt(issueNum)==0){
					   Toast.makeText(context, "您输入的期数不能为零！", Toast.LENGTH_SHORT).show();
				   }else{
					   getIssue(info,issueNum);
					   continueDialog.cancel();	
				   }
				}
			});
			continueDialog.setContentView(viewDialog);
			continueDialog.show();
	}
	/**
	 * 获取期号
	 */
	public void getIssue(TrackQueryInfo info,String issueNum){
		// 获取期号和截止时间
		JSONObject ssqLotnoInfo = PublicMethod.getCurrentLotnoBatchCode(info.getLotno());
		if (ssqLotnoInfo != null&&ssqLotnoInfo.has(info.getLotno())) {
			// 成功获取到了期号信息
			try {
				String issueStr = ssqLotnoInfo.getString("batchCode");
				createContinueInfo(info,issueNum,issueStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			// 没有获取到期号信息,重新联网获取期号
		    getIssue(info,issueNum,new Handler());
		}
	}
	/**
	 * 联网获取期号
	 * @param lotno
	 * @param term
	 * @param time
	 * @param updateIssueHandler
	 */
	public void getIssue(final TrackQueryInfo info,final String issueNum,final Handler updateIssueHandler){
		final ProgressDialog  progressDialog = UserCenterDialog.onCreateDialog(context);
		progressDialog.show();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject softupdateObj = new JSONObject(SoftwareUpdateInterface.getInstance().softwareupdate(null));
//					Constants.currentLotnoInfo = softupdateObj.getJSONObject("currentBatchCode");// 获取网络的期号信息
//					JSONObject temp_obj = Constants.currentLotnoInfo.getJSONObject(info.getLotno());
					String temp_obj = GetLotNohighFrequency.getInstance().getInfo(info.getLotno());
					JSONObject json = new JSONObject(temp_obj);
					final String issueStr = json.getString("batchcode");
					progressDialog.cancel();
					updateIssueHandler.post(new Runnable() {
						public void run() {
							createContinueInfo(info,issueNum,issueStr);
						}
					});// 获取成功
				} catch (Exception e) {
					e.printStackTrace();
					updateIssueHandler.post(new Runnable() {
						public void run() {
							progressDialog.cancel();
							Toast.makeText(context, "起始期号获取失败", Toast.LENGTH_SHORT).show();
						}
					});// 获取成功
				}
			}
		});
		t.start();
	}
	private BetAndGiftPojo betPojo = new BetAndGiftPojo();
	private void initBetPojo(String zhuma,String issue,String lotMulti,String lotno,String amount) {
		initPojo();
		betPojo.setPhonenum(phonenum);
		betPojo.setSessionid(sessionid);
		betPojo.setUserno(userno);
		betPojo.setBet_code(zhuma);
		betPojo.setLotno(lotno);
		betPojo.setBatchnum(issue);
		betPojo.setLotmulti(lotMulti);
		betPojo.setBettype("bet");
		betPojo.setAmount(amount);
		betPojo.setAmt(0);
		betPojo.setIsSellWays("1");
	}
	/**
	 * 继续追号对话框
	 */
	public void createContinueInfo(TrackQueryInfo info,String issueNum,String issue){
		int oneAmt = PublicMethod.toInt(info.getAmount())/PublicMethod.toInt(info.getBatchNum())/100;
		int amount = oneAmt*PublicMethod.toInt(issueNum);
		StringBuffer continueStr = new StringBuffer();
		continueStr.append(getString(R.string.usercenter_winningCheck_lotteryCategory)).append(info.getLotName()+"\n\n");
		continueStr.append(getString(R.string.usercenter_trackbatchnums)).append(issueNum+"期\n\n");
		continueStr.append(getString(R.string.continue_start_issue)).append(issue+"期\n\n");
		continueStr.append(getString(R.string.continue_amt)).append(amount+"元\n\n");
		initBetPojo(info.getBetTouCode(),issueNum,info.getLotMulti(),info.getLotno(),oneAmt+"00");//初始化投注信息
		continueInfoDialog("\n"+continueStr);
	}
	AlertDialog continueDialogInfo;
	private AlertDialog continueInfoDialog(String detail){
		LayoutInflater factory = LayoutInflater.from(this);
		/*中奖查询的查看详情使用余额查询的布局*/
		View view = factory.inflate(R.layout.usercenter_zhuihaodingdan, null);
		continueDialogInfo = new AlertDialog.Builder(this).create();
		TextView  title = (TextView)view.findViewById(R.id.usercenter_balancequery_title);
		TextView  remind = (TextView)view.findViewById(R.id.usercenter_remind_text);
		Button lookDail=(Button)view.findViewById(R.id.usercenter_balancequery_ok);
		Button cancleLook = (Button)view.findViewById(R.id.usercenter_balancequery_back);
		TextView	detailTextView = (TextView)view.findViewById(R.id.balanceinfo);
		
		remind.setVisibility(TextView.GONE);
		title.setText(R.string.continue_title);
		detailTextView.setText(detail);
		cancleLook.setText(getString(R.string.cancel));
		lookDail.setText(getString(R.string.ok));
		cancleLook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				continueDialogInfo.cancel();
			}
		});
		lookDail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				continueDialogInfo.cancel();
				touZhuNet();
			}
		});
		continueDialogInfo.show();
		continueDialogInfo.getWindow().setContentView(view);
		return continueDialogInfo;
			
	}

	/**
	 * 投注联网
	 */
	public void touZhuNet(){
		showDialog(DIALOG1_KEY); 
		Thread t = new Thread(new Runnable() {
			String str = "00";
			public void run() {
				str = BetAndGiftInterface.getInstance().betOrGift(betPojo);
				try {
					JSONObject obj = new JSONObject(str);		
					String message = obj.getString("message");
					String error = obj.getString("error_code");
					Message msg = new Message();
					if(error.equals("0000")){
						msg.what = 2;
						msg.obj = message;
						handler.sendMessage(msg);
					}else{
						msg.what = 0;
						msg.obj = message;
						handler.sendMessage(msg);
					};
			
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});
		t.start();
	}
	/**
	 * 格式化state串，将state串设置颜色付值给TextView
	 * @param text   TextView
	 * @param state 
	 */
	private String formatTrackState(String state,String lastnum){
		String formatState = "";
		int stateInt = 0;
		stateInt = Integer.parseInt(state);
		switch (stateInt) {
		case 0:
			if(lastnum.equals("0")){
				formatState =  getString(R.string.usercenter_str_hasClosedNoParentheses);
			}else{
				formatState =  this.getString(R.string.usercenter_str_runningNoParentheses);
			}
			break;
		case 2:
			formatState = getString(R.string.usercenter_str_hasCancledNoParentheses);
			break;
		case 3:
			formatState =  getString(R.string.usercenter_str_hasClosedNoParentheses);
			break;
		}
		return formatState;
	}
	/**
	 * 如果取消追号返回的是“000000”时的处理方法
	 */
	private void cancleTrackError000000(){
		isCancleTrackNet = true;
		pageindex = 0;
		trackQueryNet(0);
	}
	@Override
	public void errorCode_0000() {
		// TODO Auto-generated method stub
		Toast.makeText(this, R.string.betSuccess, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void errorCode_000000() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return this;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);//BY贺思明 2012-7-24
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);//BY贺思明 2012-7-24
	}
}
