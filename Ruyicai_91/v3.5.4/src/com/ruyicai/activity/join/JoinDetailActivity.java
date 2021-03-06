/**
 * 
 */
package com.ruyicai.activity.join;

import java.text.NumberFormat;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.palmdream.RuyicaiAndroid91.R;
import com.ruyicai.activity.common.UserLogin;
import com.ruyicai.constant.Constants;
import com.ruyicai.handler.HandlerMsg;
import com.ruyicai.handler.MyHandler;
import com.ruyicai.net.newtransaction.JoinCannelIdInterface;
import com.ruyicai.net.newtransaction.JoinCannelInterface;
import com.ruyicai.net.newtransaction.JoinInInterface;
import com.ruyicai.net.newtransaction.QueryJoinCanyuInterface;
import com.ruyicai.net.newtransaction.QueryJoinDetailInterface;
import com.ruyicai.util.PublicMethod;
import com.ruyicai.util.RWSharedPreferences;
import com.third.share.ShareActivity;
import com.third.share.Token;
import com.third.share.Weibo;
import com.third.share.WeiboDialogListener;
import com.third.sharetorenren.Renren;
import com.third.sharetorenren.StatusDemo;
import com.umeng.analytics.MobclickAgent;


/**
 * 合买详情
 * 
 * @author Administrator
 * 
 */
public class JoinDetailActivity extends Activity implements HandlerMsg {
	private TextView name, describe, atm, id, renAtm, baoAtm, progress, state,
			shengAtm, person, deduct, content, amountProgress, amountText,
			safeProgress, safeText, minText, minText1,lotnotext,beishutext,batchcodetext ,faqirengou;
	private LinearLayout starLayout;
	private LinearLayout faqixinxi, fanganxiangqing, fanganleirong,
			rengoushezhi,fenxianglayout;
	private Button faqi, xiangqing, leirong, rengou, canyu;
	private boolean isfaqi = false, isxiangqing = false, isleirong = false,
			isrengou = false, iscanyu = true;
	private EditText amountEdit, safeAmtEdit;
	private ImageButton joinInImg;
	private ProgressDialog progressdialog;
	private String caseId = "", issue = "";
	private String lotno = "F47104";
	private String phonenum, sessionid, userno, amount, safeAmt;
	JoinDetatil detatil = new JoinDetatil();
	private ListView canyulist;
	MyHandler handler = new MyHandler(this);// 自定义handler
	JSONObject json;
	boolean isJoinIn = false;
	boolean isVisable = false;
	String message;
	int bao = 0;
    int pageindex=0;
    int allpage=0;
	JoinCanyuadpter adapter;
    RWSharedPreferences shellRW ;
    private Renren renren;
    String token,expires_in;
    private boolean issharemove=false;
	ProgressBar progressbar;
	private ListView  canyurenyuan;
	Button chedan;
	Vector<CanyuInfo> canyudata = new Vector<CanyuInfo>();
	View view;
	ImageButton wangyi,xinlang;
	private boolean isSinaTiaoZhuan = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.join_detail);
		renren=new Renren(this);
		shellRW = new RWSharedPreferences(JoinDetailActivity.this, "addInfo");
		getInfo();
		init();
		joinDetailNet();
	}

	public void getInfo() {
		Intent intent = getIntent();
		if (intent != null) {
			caseId = intent.getStringExtra(JoinInfoActivity.ID);
			lotno = intent.getStringExtra(JoinHallActivity.LOTNO);
			issue = intent.getStringExtra(JoinHallActivity.ISSUE);
		}
	}
	private String isAmountZero = "";
	private String isSafeAmtZero = "";
	private boolean isClickable = true;
	/**
	 * 初始化组件
	 */
	public void init() {
		TextView title = (TextView) findViewById(R.id.join_detail_text_title);
		// title.append("-"+PublicMethod.toLotno(lotno));
		Button imgRetrun = (Button) findViewById(R.id.join_detail_img_return);
		imgRetrun.setBackgroundResource(R.drawable.returnselecter);
		// ImageView的返回事件
		imgRetrun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				JoinInfoActivity.isRefresh = false;
				finish();
			}
		});		
		wangyi=(ImageButton)findViewById(R.id.join_detail_img_buy2);
		xinlang=(ImageButton)findViewById(R.id.join_detail_img_buy3);
		wangyi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//		Toast.makeText(JoinDetailActivity.this, "网易分享", Toast.LENGTH_SHORT).show();	
	    StatusDemo st=new StatusDemo();
		st.publishStatusOneClick(JoinDetailActivity.this, renren);
			}
		});
		xinlang.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(JoinDetailActivity.this, "新浪分享", Toast.LENGTH_SHORT).show();		
				oauthOrShare();
	
			}
		});
		fenxianglayout=(LinearLayout)findViewById(R.id.LinearLayout10);	
		fenxianglayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(issharemove){
					TranslateAnimation anim=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.83f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
					anim.setDuration(500);
//					anim.setFillAfter(true);
					anim.setFillEnabled(true);
			        anim.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
						   LinearLayout.LayoutParams lp =(LinearLayout.LayoutParams)fenxianglayout.getLayoutParams();
						   lp.setMargins(PublicMethod.getPxInt(265, JoinDetailActivity.this), 0, 0, 0);
			               fenxianglayout.setLayoutParams(lp);
						}
					});
			        fenxianglayout.startAnimation(anim);
			        issharemove=false;
				}else{
					TranslateAnimation anim=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, -0.83f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
					anim.setDuration(500);
//					anim.setFillAfter(true);
					anim.setFillEnabled(true);
			        anim.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
						   LinearLayout.LayoutParams lp =(LinearLayout.LayoutParams)fenxianglayout.getLayoutParams();
						   lp.setMargins(0, 0, 0, 0);
			               fenxianglayout.setLayoutParams(lp);
						}
					});
			        fenxianglayout.startAnimation(anim);
			        issharemove=true;
					
				}
				
			}
		});
		
		lotnotext=(TextView)findViewById(R.id.join_detail_text_lotno);
		beishutext=(TextView)findViewById(R.id.join_detail_text_beishu);
		batchcodetext=(TextView)findViewById(R.id.join_detail_text_batchcode);
		faqirengou=(TextView)findViewById(R.id.join_detail_text_faqirengou);
		minText1=(TextView)findViewById(R.id.join_detail_text_zuidirengou);
		chedan=(Button)findViewById(R.id.join_chedan);
		name = (TextView) findViewById(R.id.join_detail_text_name);
		starLayout = (LinearLayout) findViewById(R.id.join_detail_linear_record);
		describe = (TextView) findViewById(R.id.join_detail_text_describe);
		atm = (TextView) findViewById(R.id.join_detail_text_atm);
		id = (TextView) findViewById(R.id.join_detail_text_num);
		baoAtm = (TextView) findViewById(R.id.join_detail_text_baodi_atm);
		renAtm = (TextView) findViewById(R.id.join_detail_text_rengou_atm);
		progress = (TextView) findViewById(R.id.join_detail_tex_progress);
		state = (TextView) findViewById(R.id.join_detail_text_state);
		shengAtm = (TextView) findViewById(R.id.join_detail_text_shengyu_atm);
		person = (TextView) findViewById(R.id.join_detail_text_person);
		deduct = (TextView) findViewById(R.id.join_detail_text_get);
		content = (TextView) findViewById(R.id.join_detail_text_context);
		amountProgress = (TextView) findViewById(R.id.join_detail_text_rengou_progress);
		amountText = (TextView) findViewById(R.id.join_detail_text_rengou_sheng);
		safeProgress = (TextView) findViewById(R.id.join_detail_text_baodi_progress);
		safeText = (TextView) findViewById(R.id.join_detail_text_baodi_sheng);
		minText = (TextView) findViewById(R.id.join_detail_text_rengou_min);
		amountEdit = (EditText) findViewById(R.id.join_detail_edit_rengou);
		safeAmtEdit = (EditText) findViewById(R.id.join_detail_edit_baodi);
		joinInImg = (ImageButton) findViewById(R.id.join_detail_img_buy);
		joinInImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					isLogin();
			
			}
		});
		amountEdit.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				String amount = amountEdit.getText().toString();
				isAmountZero =amount ;
				String renAmt = leavMount(detatil.getShengAtm(), amountEdit
						.getText().toString());
				amountEdit.setClickable(true);
				amountEdit.setEnabled(true);
				String amt = detatil.getAtm();
				amountProgress.setText("占总额" + progress(isNull(amount), amt)
						+ "%");// 总金额
				leavTextView(amountText, true);
				leavTextView(safeText, false);

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});
		safeAmtEdit.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				String amt = safeAmtEdit.getText().toString();
				isSafeAmtZero = amt;
				amt = isNull(amt);
				String renAmt = leavMount(detatil.getShengAtm(), amountEdit
						.getText().toString());
				String baoAmt = leavMount(renAmt, detatil.getBaoAtm());
				safeAmtEdit.setClickable(true);
				safeAmtEdit.setEnabled(true);
				safeProgress.setText("占总额"
						+ progress(isNull(amt), detatil.getAtm()) + "%");
				if (Integer.parseInt(baoAmt) > 0) {
					leavTextView(safeText, false);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 0) {

				} else {
					safeProgress.setText("占总额0%");
				}
			}

		});
		amountProgress.setText("占总额"
				+ progress(amountEdit.getText().toString(), detatil.getAtm())
				+ "%");
		safeProgress.setText("占总额"
				+ progress(safeAmtEdit.getText().toString(), detatil.getAtm())
				+ "%");
		leavTextView(amountText, true);
		leavTextView(safeText, false);
		// linshi
		PublicMethod.createStar(starLayout, detatil.getCrown(),
				detatil.getCup(), detatil.getDiamond(), detatil.getStar(),
				JoinDetailActivity.this,0);
		
		initButtonLayout();
	}
	
	
	
	private void oauthOrShare(){
		token = shellRW.getStringValue("token");
		expires_in = shellRW.getStringValue("expires_in");
		if(token.equals("")){
			oauth();
		}else{
			isSinaTiaoZhuan = true;
			initAccessToken(token, expires_in);
		}
	}
	
	private void oauth(){

		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		// Oauth2.0
		// 隐式授权认证方式
		weibo.setRedirectUrl(Constants.CONSUMER_URL);// 此处回调页内容应该替换为与appkey对应的应用回调页
		// 对应的应用回调页可在开发者登陆新浪微博开发平台之后，
		// 进入我的应用--应用详情--应用信息--高级信息--授权设置--应用回调页进行设置和查看，
		// 应用回调页不可为空
		weibo.authorize(JoinDetailActivity.this, new AuthDialogListener());
	}
	public void setListViewHeightBasedOnChildren(ListView listView) {  

	    ListAdapter listAdapter ; //取得listview绑定的适配器

	    if (listView.getAdapter()==null) {  

	        return;  

	    }  
	    listAdapter = listView.getAdapter();	 

        
	    ViewGroup.LayoutParams params = listView.getLayoutParams();  //取得listview所在布局的参数

	    params.height = PublicMethod.getPxInt(42, JoinDetailActivity.this) * (listAdapter.getCount());  

	    listView.setLayoutParams(params); //改变listview所在布局的参数
	}  
    public void initList(){
    	LayoutInflater	mInflater = LayoutInflater.from(this);
  	    canyulist=(ListView)findViewById(R.id.canyurenyuan);	
  	    if(view==null){
  	    	view = mInflater.inflate(R.layout.lookmorebtn, null);	
  	    	canyulist.addFooterView(view);
  	    }	
  	    progressbar=(ProgressBar)view.findViewById(R.id.getmore_progressbar);
       	// 数据源
  		adapter = new JoinCanyuadpter(this, canyudata);
  		canyulist.setAdapter(adapter);
  		view.setOnClickListener(new OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				
  				// TODO Auto-generated method stub
  				view.setEnabled(false);
  	            getMore();

  				
  			}
  		});
    	setListViewHeightBasedOnChildren(canyulist);

    }
	public void initButtonLayout() {
		faqi = (Button) findViewById(R.id.faqi);
		faqixinxi = (LinearLayout) findViewById(R.id.faqixinxi);
		xiangqing = (Button) findViewById(R.id.fangan);
		fanganxiangqing = (LinearLayout) findViewById(R.id.fanganxiangqing);
		leirong = (Button) findViewById(R.id.leirong);
		fanganleirong = (LinearLayout) findViewById(R.id.fanganleirong);
		rengou = (Button) findViewById(R.id.rengou);
		rengoushezhi = (LinearLayout) findViewById(R.id.rengoushezhi);
		canyu = (Button) findViewById(R.id.canyu);
		canyurenyuan = (ListView) findViewById(R.id.canyurenyuan);
		faqi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isfaqi) {
					faqixinxi.setVisibility(View.VISIBLE);
					faqi.setBackgroundResource(R.drawable.joininfobuttonup);
					isfaqi = false;
				} else {
					faqixinxi.setVisibility(View.GONE);
					faqi.setBackgroundResource(R.drawable.joninfobuttonoff);
					isfaqi = true;
				}
			}
		});
		xiangqing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isxiangqing) {
					fanganxiangqing.setVisibility(View.VISIBLE);
					xiangqing.setBackgroundResource(R.drawable.joininfobuttonup);
					isxiangqing = false;
				} else {
					fanganxiangqing.setVisibility(View.GONE);
					xiangqing.setBackgroundResource(R.drawable.joninfobuttonoff);
					isxiangqing = true;
				}
			}
		});
		leirong.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isleirong) {
					fanganleirong.setVisibility(View.VISIBLE);
					leirong.setBackgroundResource(R.drawable.joininfobuttonup);
					isleirong = false;
				} else {
					fanganleirong.setVisibility(View.GONE);
					leirong.setBackgroundResource(R.drawable.joninfobuttonoff);
					isleirong = true;
				}
			}
		});
		rengou.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isrengou) {
					rengoushezhi.setVisibility(View.VISIBLE);
					rengou.setBackgroundResource(R.drawable.joininfobuttonup);
					isrengou = false;
				} else {
					rengoushezhi.setVisibility(View.GONE);
					rengou.setBackgroundResource(R.drawable.joninfobuttonoff);
					isrengou = true;
				}
			}
		});
		canyu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (iscanyu) {
					canyurenyuan.setVisibility(View.VISIBLE);
					canyu.setBackgroundResource(R.drawable.joininfobuttonup);
					iscanyu = false;
					if(canyudata.size()==0){
					joinCanyuNet();
					}
				} else {
					canyurenyuan.setVisibility(View.GONE);
					canyu.setBackgroundResource(R.drawable.joninfobuttonoff);
					iscanyu = true;
				}
			}
		});

	}



	public String progress(String amt, String allAmt) {
		if (allAmt.equals("0")) {
			return "0";
		} else {
			float amount = Integer.parseInt(amt);
			float allAmount = Integer.parseInt(allAmt);
			float progress = (amount / allAmount) * 100;
			NumberFormat formatter = NumberFormat.getNumberInstance();
			formatter.setMaximumFractionDigits(1);
			formatter.setMinimumFractionDigits(1);
			return formatter.format(progress);
		}
	}

	public String leavMount(String allAmt, String amt) {
		String amtStr = "";
		amtStr = Integer.toString(Integer.parseInt(isNull(allAmt))
				- Integer.parseInt(isNull(amt)));
		return amtStr;
	}

	public void leavTextView(TextView text, boolean isRen) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		String renAmt = leavMount(detatil.getShengAtm(), amountEdit.getText()
				.toString());
		String amt = leavMount(renAmt, detatil.getBaoAtm());
		String baoAmt = leavMount(amt, safeAmtEdit.getText().toString());
		String textStr = "";
		int ren = Integer.parseInt(renAmt);
		if (isRen) {
			if (ren < 0) {
				amountEdit.setText(detatil.getShengAtm());
				textStr = "剩余￥0可认购";
			} else {
				textStr = "剩余￥" + renAmt + "可认购";
			}

		} else {
			safeAmtEdit.setEnabled(true);
			isClickable = true;
			int bao = Integer.parseInt(baoAmt);
			if (bao < 0) {
				if (Integer.parseInt(amt) < 0) {
					safeAmtEdit.setText("0");
				} else {
					safeAmtEdit.setText(amt);
				}
				safeAmtEdit.setEnabled(false);
				isClickable = false;
				this.bao = 0;
				textStr = "剩余￥0可保底";
			} else if (bao == 0) {
				safeAmtEdit.setEnabled(false);
				isClickable = false;
				this.bao = Integer.parseInt(baoAmt);
				textStr = "剩余￥" + baoAmt + "可保底";
			} else {
				this.bao = Integer.parseInt(baoAmt);
				textStr = "剩余￥" + baoAmt + "可保底";
				isClickable = true;
			}
		}
		builder.append(textStr);
		builder.setSpan(new ForegroundColorSpan(Color.RED), 2,
				textStr.length() - 3, Spanned.SPAN_COMPOSING);
		text.setText(builder, BufferType.EDITABLE);

	}

	/**
	 * 从后台获取值
	 */
	public JoinDetatil getValue() {

		try {
			detatil.setLotno(json.getString("lotName"));
			detatil.setBatchcode(json.getString("batchCode"));
			detatil.setBuyamtbystarter(json.getString("buyAmtByStarter"));
			detatil.setName(json.getString("starter"));
			detatil.setStar("");
			detatil.setDescribe(json.getString("description"));
			detatil.setAtm(json.getString("totalAmt"));
			detatil.setId(json.getString("caseLotId"));
			detatil.setRenAtm(json.getString("buyAmt"));
			detatil.setBaoAtm(json.getString("safeAmt"));
			detatil.setProgress(json.getString("progress"));
			detatil.setState(json.getString("displayStateMemo"));
			detatil.setShengAtm(json.getString("remainderAmt"));
			detatil.setPerson(json.getString("participantCount"));
			detatil.setDeduct(json.getString("commisionRatio"));
			detatil.setContent(json.getString("content"));
			detatil.setMinAmt(json.getString("minAmt"));
			detatil.setCancelCaselot(json.getString("cancelCaselot"));
			detatil.setUrl(json.getString("url"));
			detatil.setSafeRate(json.getString("safeRate"));
			detatil.setLotMulti(json.getString("lotMulti"));
			Constants.shareContent="Hi，我刚使用了如意彩手机客户端买彩票，很方便呢！" +
					"你也试试吧，彩票随身投，大奖时时有！中奖了记的要请客啊！地址:"+detatil.getUrl();
			Constants.source="4";
			JSONObject displayIcon = json.getJSONObject("displayIcon");
			try {
				detatil.setCup(displayIcon.getString("cup"));
			} catch (Exception e) {
			}
			try {
				detatil.setDiamond(displayIcon.getString("diamond"));
			} catch (Exception e) {
			}
			try {
				detatil.setStarNum(displayIcon.getString("goldStar"));
			} catch (Exception e) {
			}
			try {
				detatil.setCrown(displayIcon.getString("crown"));
			} catch (Exception e) {
			}
		} catch (Exception e) {

		}
		return detatil;
	}

	
	/**
	 * 初始化数据
	 */
	public void setValuecanyulist() {
		try {
			JSONArray array = json.getJSONArray("result");
			allpage = Integer.parseInt(json.getString("totalPage"));
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				CanyuInfo info = new CanyuInfo();			
				try{
				info.setName(obj.getString("nickName"));
				info.setCancelCaselotbuy(obj.getString("cancelCaselotbuy"));
				info.setMoney(obj.getString("buyAmt"));
				info.setTime(obj.getString("buyTime"));					
				}catch(Exception e){					
				}
				canyudata.add(info);
			}
		} catch (Exception e) {

		}
	}
	
	
	/**
	 * 用户中心的适配器
	 */
	public class JoinCanyuadpter extends BaseAdapter {

		private LayoutInflater mInflater; // 扩充主列表布局
		private Vector<CanyuInfo> mList;

		public JoinCanyuadpter(Context context, Vector<CanyuInfo> list) {
			mInflater = LayoutInflater.from(context);
			mList = list;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		int index;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			index = position;
			ViewHolder holder = null;
			CanyuInfo info = (CanyuInfo) mList.get(position);
			String name = info.getName();
			String time = info.getTime();
			String cancelCaselotbuy = info.getCancelCaselotbuy();
			String money = info.getMoney();
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.join_canyu_item, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.canyumingcheng);
				holder.time = (TextView) convertView.findViewById(R.id.canyuriqi);
				holder.money = (TextView) convertView.findViewById(R.id.canyujine);	
				holder.chezi=(TextView)convertView.findViewById(R.id.canyurchezi);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if(cancelCaselotbuy.equals("true")){
				holder.chezi.setVisibility(TextView.VISIBLE);
				holder.chezi.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						JoinCannelIdDialog();
					}
				});
				
			}
			holder.name.setText(name);
			holder.time.setText(time);
			holder.money.setText("￥"+PublicMethod.toIntYuan(money));
			return convertView;
		}

		class ViewHolder {
			TextView name;
			TextView time;
			TextView chezi;
			TextView money;
		}
	}
	/**
	 * 赋值值
	 */
	public void setValue(JoinDetatil detatil) {
        if(detatil.getCancelCaselot().equals("true")){
        	chedan.setVisibility(View.VISIBLE);
        	chedan.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					JoinCannelDialog();
				}
			});
        }
        lotnotext.append(detatil.getLotno());
        beishutext.append(detatil.getLotMulti());
        if(detatil.getBatchcode().equals("null")||detatil.getBatchcode().equals("")){
        batchcodetext.setVisibility(View.GONE);
        }else{
        batchcodetext.append(detatil.getBatchcode());
        }
        faqirengou.append("￥" + detatil.getBuyamtbystarter());
        minText1.append(detatil.getMinAmt());
		name.append(detatil.getName());
		describe.append(detatil.getDescribe());
		atm.append("￥" + detatil.getAtm());
		id.append(detatil.getId());
		baoAtm.append("￥" + detatil.getBaoAtm());
		renAtm.append("￥" + detatil.getRenAtm());
		progress.append(detatil.getProgress()+"+"+detatil.getSafeRate());
		state.append(detatil.getState());
		shengAtm.append("￥" + detatil.getShengAtm());
		person.append(detatil.getPerson());
		deduct.append(detatil.getDeduct());
		content.append(detatil.getContent());
		amountProgress.setText("占总额"
				+ progress(amountEdit.getText().toString(), detatil.getAtm())
				+ "%");
		safeProgress.setText("占总额"
				+ progress(safeAmtEdit.getText().toString(), detatil.getAtm())
				+ "%");
		showMinText(detatil.getMinAmt());
		leavTextView(amountText, true);
		leavTextView(safeText, false);
		PublicMethod.createStar(starLayout, detatil.getCrown(),
				detatil.getCup(), detatil.getDiamond(), detatil.getStarNum(),
				JoinDetailActivity.this,0);

	}

	/**
	 * 显示最低认购金额
	 */
	public void showMinText(String minText) {
		String renAmt = leavMount(detatil.getShengAtm(), amountEdit.getText()
				.toString());
		if (Integer.parseInt(minText) > Integer.parseInt(renAmt)
				|| Integer.parseInt(minText) == 0) {
			isVisable = false;
			this.minText.setVisibility(TextView.GONE);
		} else {
			isVisable = true;
			SpannableStringBuilder builder = new SpannableStringBuilder();
			String textStr = "至少认购￥";
			textStr += minText;
			builder.append(textStr);
			builder.setSpan(new ForegroundColorSpan(Color.RED), 4,
					textStr.length(), Spanned.SPAN_COMPOSING);
			this.minText.setText(builder, BufferType.EDITABLE);
		}
	}

	/**
	 * 判断是否登录
	 */
	public void isLogin() {
		getLoginInfo();
		if (sessionid == null || sessionid.equals("")) {
			Intent intentSession = new Intent(JoinDetailActivity.this,
					UserLogin.class);
			startActivityForResult(intentSession, 0);
		} else {
			isJoinInNet();
		}
	}

	/**
	 * 获取登录信息
	 */
	public void getLoginInfo() {
		RWSharedPreferences shellRW = new RWSharedPreferences(
				JoinDetailActivity.this, "addInfo");
		phonenum = shellRW.getStringValue("phonenum");
		sessionid = shellRW.getStringValue("sessionid");
		userno = shellRW.getStringValue("userno");
	}

	/**
	 * 判断是否联网
	 */
	public void isJoinInNet() {
		amount = amountEdit.getText().toString();
		safeAmt = safeAmtEdit.getText().toString();
		String renAmt = leavMount(detatil.getShengAtm(), amountEdit.getText()
				.toString());
		int amountInt = Integer.parseInt(isNull(amount));
		int safeAmtInt = Integer.parseInt(isNull(safeAmt));
		if (amount.equals("") && safeAmt.equals("")) {
			Toast.makeText(JoinDetailActivity.this, "认购金额或保底金额不能为空",
					Toast.LENGTH_SHORT).show();
		} else if (amountInt == 0 && safeAmtInt == 0) {
			Toast.makeText(JoinDetailActivity.this, "认购金额和保底金额不能都为零",
					Toast.LENGTH_SHORT).show();
		} else if (!isVisable) {
			joinInNet();
		} else if (safeAmtInt != 0 && amountInt == 0) {
			joinInNet();
		} else if (amountInt < Integer.parseInt(detatil.getMinAmt())) {
			Toast.makeText(JoinDetailActivity.this,
					"请您至少认购" + detatil.getMinAmt() + "元", Toast.LENGTH_SHORT)
					.show();
		}
		// else if(safeAmtInt>bao){
		// Toast.makeText(JoinDetailActivity.this, "保底金额不正确",
		// Toast.LENGTH_SHORT).show();
		// }
		else {
			Log.e("dddddddd===",""+amount);
			joinInNet();
		}

	}

	/**
	 * 参与合买
	 */
	public void joinInNet() {
		isJoinIn = true;
		showDialog(0); // 显示网络提示框 2010/7/4
		// 加入是否改变切入点判断 陈晨 8.11
		Thread t = new Thread(new Runnable() {
			String str = "00";

			@Override
			public void run() {
				str = JoinInInterface.betLotJoin(userno, phonenum, caseId,
						PublicMethod.toFen(isNull(amount)),
						PublicMethod.toFen(isNull(safeAmt)));
				try {
					json = new JSONObject(str);
					message = json.getString("message");
					String error = json.getString("error_code");
					handler.handleMsg(error, message);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				progressdialog.dismiss();
			}

		});
		t.start();
	}

	/**
	 * 联网查询
	 */
	public void joinDetailNet() {
		getLoginInfo();
		showDialog(0); // 显示网络提示框 2010/7/4
		// 加入是否改变切入点判断 陈晨 8.11
		Thread t = new Thread(new Runnable() {
			String str = "00";

			@Override
			public void run() {
				str = QueryJoinDetailInterface.queryLotJoinDetail(caseId,
						userno, phonenum);
				try {
					json = new JSONObject(str);
					String msg = json.getString("message");
					String error = json.getString("error_code");
					handler.handleMsg(error, msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				progressdialog.dismiss();
			}

		});
		t.start();
	}

	
	/**
	 * 参与人员联网查询
	 */
	public void joinCanyuNet() {
		getLoginInfo();
		showDialog(0); // 显示网络提示框 2010/7/4
		// 加入是否改变切入点判断 陈晨 8.11
		if(progressbar!=null){
			progressbar.setVisibility(ProgressBar.VISIBLE);
		}
		final Handler tHandler = new Handler();
		new Thread(new Runnable() {
			@Override
		public void run() {
		String str = "00";
		str = QueryJoinCanyuInterface.queryLotJoincanyu(caseId,userno, phonenum,String.valueOf(pageindex),"10");
		try {
			json = new JSONObject(str);
			String msg = json.getString("message");
			String error = json.getString("error_code");
			tHandler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(progressbar!=null){
						progressbar.setVisibility(ProgressBar.INVISIBLE);
						view.setEnabled(true);
					}
					if(progressdialog!=null){
						progressdialog.dismiss();
					}
					setValuecanyulist();
				    if(pageindex==0){
				    	initList();
				    }else{
				    	setListViewHeightBasedOnChildren(canyulist);
				    	adapter.notifyDataSetChanged();		
				    }
				}
			 });
		} catch (JSONException e) {
			e.printStackTrace();
		}
		}
	  }).start();
	}
	
	
	
	//撤单
		protected void JoinCannelDialog() {
			final Dialog dialog =new Dialog(this,R.style.dialog);
			LayoutInflater inflater = LayoutInflater.from(this);
			View successView = inflater.inflate(R.layout.base_dialog_default_view, null);
			TextView title = (TextView) successView.findViewById(R.id.zfb_text_title);
			TextView alertText = (TextView) successView.findViewById(R.id.zfb_text_alert);
			title.setText("撤单");
			alertText.setText("是否撤单");
	        Button submit = (Button) successView.findViewById(R.id.ok);
	        submit.setText("确定");
	        submit.setBackgroundResource(R.drawable.join_info_btn_selecter);
	        Button cancel = (Button) successView.findViewById(R.id.canel);
	        cancel.setBackgroundResource(R.drawable.join_info_btn_selecter);
	        submit.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					joinCannelNet();
					dialog.cancel();
				}
			});
	        cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.cancel();
				}
			});
	        dialog.setContentView(successView);
	        dialog.show();
		}
		
		//撤资
		protected void JoinCannelIdDialog() {
			final Dialog dialog =new Dialog(this,R.style.dialog);
			LayoutInflater inflater = LayoutInflater.from(this);
			View successView = inflater.inflate(R.layout.base_dialog_default_view, null);
			TextView title = (TextView) successView.findViewById(R.id.zfb_text_title);
			TextView alertText = (TextView) successView.findViewById(R.id.zfb_text_alert);
			title.setText("撤资");
			alertText.setText("是否撤资");
	        Button submit = (Button) successView.findViewById(R.id.ok);
	        submit.setText("确定");
	        submit.setBackgroundResource(R.drawable.join_info_btn_selecter);
	        Button cancel = (Button) successView.findViewById(R.id.canel);
	        cancel.setBackgroundResource(R.drawable.join_info_btn_selecter);
	        submit.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					joinCannelIdNet();
					dialog.cancel();
				}
			});
	        cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.cancel();
				}
			});
	        dialog.setContentView(successView);
	        dialog.show();
		}
    private void getMore(){
    	pageindex++;
		if (pageindex < allpage) {
			joinCanyuNet();
		} else {
			progressbar.setVisibility(view.INVISIBLE);
			pageindex = allpage - 1;
			Toast.makeText(JoinDetailActivity.this, "已至尾页",
					Toast.LENGTH_SHORT).show();
		}
    }
	/**
	 * 撤单联网
	 */
	public void joinCannelNet() {
		getLoginInfo();
		showDialog(0); // 显示网络提示框 2010/7/4
		// 加入是否改变切入点判断 陈晨 8.11
		Thread t = new Thread(new Runnable() {
			String str = "00";

			@Override
			public void run() {
				str = JoinCannelInterface.joincanel(caseId, userno, phonenum);
				try {
					json = new JSONObject(str);
					final String msg = json.getString("message");
					String error = json.getString("error_code");
					if(error.equals("0000")){
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
							chedan.setVisibility(View.GONE);	
							progressdialog.dismiss();
							JoinInfoActivity.isRefresh = true;
							JoinDetailActivity.this.finish();
							Toast.makeText(JoinDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
							}
						});
					}else{
                          handler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stu
							progressdialog.dismiss();
							Toast.makeText(JoinDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
							}
						});
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});
		t.start();
	}
	
	/**
	 * 撤资联网
	 */
	public void joinCannelIdNet() {
		getLoginInfo();
		showDialog(0); // 显示网络提示框 2010/7/4
		// 加入是否改变切入点判断 陈晨 8.11
		Thread t = new Thread(new Runnable() {
			String str = "00";

			@Override
			public void run() {
				str = JoinCannelIdInterface.joincanelid(caseId,
						userno, phonenum);
				try {
					json = new JSONObject(str);
					final String msg = json.getString("message");
					String error = json.getString("error_code");
                    if(error.equals("0000")){
                    	handler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
							progressdialog.dismiss();
							Toast.makeText(JoinDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
							canyudata.clear();
							pageindex=0;
							joinCanyuNet();
							}
						});
                    }else{
                           handler.post(new Runnable() {			
							@Override
							public void run() {
								// TODO Auto-generated method stub
							progressdialog.dismiss();
							Toast.makeText(JoinDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
							}
						});
                    }
 				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});
		t.start();
	}
	/**
	 * 从上一个activity返回当前activity执行的方法
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case RESULT_OK:
			isLogin();
			break;
		}
	}

	/**
	 * 参与合买成功
	 * 
	 * @param title
	 * @param string
	 */
	public void succeedDialog(String title, String string) {

		Dialog dialog = new AlertDialog.Builder(this).setMessage(string)
				.setTitle(title)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						JoinInfoActivity.isRefresh = true;
						finish();
					}
				}).create();
		dialog.setCancelable(false);
		dialog.show();

	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	class JoinDetatil {
		private String name = "", star = "", describe = "", atm = "0", id = "",
				renAtm = "0", baoAtm = "0", progress = "", state = "",
				shengAtm = "0", person = "", deduct = "", content = "";
		String minAmt = "0";// 最低认购金额
		String crown = "0";// 皇冠
		String cup = "0";// 奖杯
		String diamond = "0";// 钻石
		String starNum = "0";// 星
        String cancelCaselot="";
        String lotno="";
        String batchcode="";
        String buyamtbystarter="";
        String url="";
        String safeRate="";
        String lotMulti = "";
		public String getLotMulti() {
			return lotMulti;
		}

		public void setLotMulti(String lotMulti) {
			this.lotMulti = lotMulti;
		}

		public String getSafeRate() {
			return safeRate + "%";
		}

		public void setSafeRate(String safeRate) {
			this.safeRate = safeRate;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getBuyamtbystarter() {
			return buyamtbystarter;
		}

		public void setBuyamtbystarter(String buyamtbystarter) {
			this.buyamtbystarter = Integer.toString(Integer.parseInt(buyamtbystarter) / 100);	
			
		}

		public String getLotno() {
			return lotno;
		}

		public void setLotno(String lotno) {
			this.lotno = lotno;
		}

		public String getBatchcode() {
			return batchcode;
		}

		public void setBatchcode(String batchcode) {
			this.batchcode = batchcode;
		}

		public String getCancelCaselot() {
			return cancelCaselot;
		}

		public void setCancelCaselot(String cancelCaselot) {
			this.cancelCaselot = cancelCaselot;
		}

		public String getMinAmt() {
			return PublicMethod.toIntYuan(minAmt);
		}

		public void setMinAmt(String minAmt) {
			this.minAmt = minAmt;
		}

		public String getStarNum() {
			return starNum;
		}

		public void setStarNum(String starNum) {
			this.starNum = starNum;
		}

		public String getCrown() {
			return crown;
		}

		public void setCrown(String crown) {
			this.crown = crown;
		}

		public String getCup() {
			return cup;
		}

		public void setCup(String cup) {
			this.cup = cup;
		}

		public String getDiamond() {
			return diamond;
		}

		public void setDiamond(String diamond) {
			this.diamond = diamond;
		}

		public JoinDetatil() {

		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStar() {
			return star;
		}

		public void setStar(String star) {
			this.star = star;
		}

		public String getDescribe() {
			if (describe == null) {
				return "";
			} else {
				return describe;
			}
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

		public String getAtm() {
			return atm;
		}

		public void setAtm(String atm) {
			this.atm = Integer.toString(Integer.parseInt(atm) / 100);
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getRenAtm() {
			return renAtm;
		}

		public void setRenAtm(String renAtm) {
			this.renAtm = Integer.toString(Integer.parseInt(renAtm) / 100);
		}

		public String getBaoAtm() {
			return baoAtm;
		}

		public void setBaoAtm(String baoAtm) {
			this.baoAtm = Integer.toString(Integer.parseInt(baoAtm) / 100);
		}

		public String getProgress() {
			return progress + "%";
		}

		public void setProgress(String progress) {
			this.progress = progress;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getShengAtm() {
			return shengAtm;
		}

		public void setShengAtm(String shengAtm) {
			this.shengAtm = Integer.toString(Integer.parseInt(shengAtm) / 100);
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person + "人";
		}

		public String getDeduct() {
			return deduct;
		}

		public void setDeduct(String deduct) {
			this.deduct = deduct + "%";
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}
	
	class CanyuInfo{
		String name="";
		String time="";
		String cancelCaselotbuy="";
		String money="";
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getCancelCaselotbuy() {
			return cancelCaselotbuy;
		}
		public void setCancelCaselotbuy(String cancelCaselotbuy) {
			this.cancelCaselotbuy = cancelCaselotbuy;
		}
		public String getMoney() {
			return money;
		}
		public void setMoney(String money) {
			this.money = money;
		}
				
	}

	/**
	 * 网络连接框
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0: {
			progressdialog = new ProgressDialog(this);
			progressdialog.setMessage("网络连接中...");
			progressdialog.setIndeterminate(true);
			return progressdialog;
		}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ruyicai.handler.HandlerMsg#errorCode_0000()
	 */
	@Override
	public void errorCode_0000() {
		if (isJoinIn) {
			succeedDialog("提示", message);
		} else {
			setValue(getValue());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ruyicai.handler.HandlerMsg#errorCode_000000()
	 */
	@Override
	public void errorCode_000000() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ruyicai.handler.HandlerMsg#getContext()
	 */
	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	/**
	 * 重写放回建
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case 4:
			JoinInfoActivity.isRefresh = false;
			finish();
			break;
		}
		return false;
	}
   
	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			PublicMethod.myOutLog("token111", "zhiqiande"+shellRW.getStringValue("token"));
			PublicMethod.myOutLog("onComplete", "12131321321321");
			String token = values.getString("access_token");
			PublicMethod.myOutLog("token", token);
			String expires_in = values.getString("expires_in");
			shellRW.putStringValue("token", token);
			shellRW.putStringValue("expires_in", expires_in);
//			is_sharetosinaweibo.setBackgroundResource(R.drawable.on);
			initAccessToken(token,expires_in);
		}


		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}
	}
	
	
	
	private void initAccessToken(String token,String expires_in){
		Token accessToken = new Token(token, Weibo.getAppSecret());
		accessToken.setExpiresIn(expires_in);
		Weibo.getInstance().setAccessToken(accessToken);
		share2weibo(Constants.shareContent);
		if(isSinaTiaoZhuan){
			Intent intent = new Intent();
			intent.setClass(JoinDetailActivity.this, ShareActivity.class);
			startActivity(intent);
		}
	}
	 private void share2weibo(String content)  {
	        Weibo weibo = Weibo.getInstance();
	        weibo.share2weibo(this, weibo.getAccessToken().getToken(), weibo.getAccessToken().getSecret(), content, "");
	    }
	/**
	 * 判断字符串是否是空值
	 * 
	 */
	public String isNull(String str) {
		String string;
		if (str == null || str.equals("")) {
			return "0";
		} else {
			return str;
		}

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
