package com.ruyicai.activity.buy.jc;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.palmdream.RuyicaiAndroid.R;
import com.ruyicai.activity.buy.jc.touzhu.TouzhuDialog;
import com.ruyicai.activity.buy.jc.zq.view.BFView;
import com.ruyicai.activity.buy.jc.zq.view.BQCView;
import com.ruyicai.activity.buy.jc.zq.view.SPfView;
import com.ruyicai.activity.buy.jc.zq.view.ZJQView;
import com.ruyicai.activity.common.UserLogin;
import com.ruyicai.activity.usercenter.UserCenterDialog;
import com.ruyicai.custom.jc.button.MyButton;
import com.ruyicai.dialog.ExitDialogFactory;
import com.ruyicai.handler.HandlerMsg;
import com.ruyicai.handler.MyHandler;
import com.ruyicai.net.newtransaction.BetAndGiftInterface;
import com.ruyicai.net.newtransaction.pojo.BetAndGiftPojo;
import com.ruyicai.util.Constants;
import com.ruyicai.util.PublicMethod;
import com.ruyicai.util.RWSharedPreferences;



public class JcMainActivity extends Activity implements SeekBar.OnSeekBarChangeListener,HandlerMsg{
	protected static int TYPE = 0;
	protected final static int SF = 1;//胜负
	protected  final static int RF_SF = 2;//让分胜负
	protected  final static int SFC = 3;//胜分差
	protected  final static int DXF = 4;//大小分
	protected TextView textTitle;
	private TextView mTextBeishu;
	private SeekBar mSeekBarBeishu;
	public String phonenum;
	public String sessionId;
	public String userno;
	private int iProgressBeishu = 1;
	private MyHandler handler = new MyHandler(this);//自定义handler
	protected BetAndGiftPojo betAndGift=new BetAndGiftPojo();//投注信息类
	private String msg;
	private boolean isGift = false;//是否赠送
	private boolean isTouzhu = true;//是否投注
	private Dialog	dialogType = null;//玩法切换提示框
	private View viewType;
	protected JcMainView lqMainView;
	protected LinearLayout layoutView;
	protected String type = Constants.JCBASKET;
	private TouzhuDialog touzhuDialog;
	protected  TextView textTeamNum;
	protected  boolean isDanguan=false;
	protected List<RadioButton> radioBtns = new ArrayList<RadioButton>();
	private Context context;
	private Dialog dialogTeam;
	protected List<String> checkTeam = new ArrayList<String>();/*被选择后的赛事列表 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.buy_jc_main_new);
		context = this;
		initView();
		
	}
	public void isTeamBtn(boolean isVisable){
		Button teamBtn = (Button) findViewById(R.id.buy_lq_main_btn_team);
		if(isVisable){
			teamBtn.setVisibility(Button.VISIBLE);
    		teamBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(dialogTeam == null){
						createTeamDialog();
					}else{
						dialogTeam.show();
					}
				}
			});
		}else{
			teamBtn.setVisibility(Button.GONE);
		}
	}
	public int getTeamNum(){
		return lqMainView.getTeamNum();
	}
	public BetAndGiftPojo getBetAndGiftPojo(){
		return betAndGift;
	}
	public int getIprogressBeiShu(){
		return iProgressBeishu;
	}
	public  void setType(String type){
		this.type = type;
	}
	MyButton[] myBtns;
	/**
	 * 赛事选择窗口
	 */
	public void createTeamDialog(){
	dialogTeam = new AlertDialog.Builder(context).create();
	LayoutInflater factory = LayoutInflater.from(context);
	View view = factory.inflate(R.layout.jc_main_team_dialog,null);
	TextView  title = (TextView) view.findViewById(R.id.zfb_text_title);
	title.setText(context.getString(R.string.jc_main_team_check));
	LinearLayout layoutMain = (LinearLayout) view.findViewById(R.id.jc_linear_check_all);
	myBtns = new MyButton[JcMainView.listTeam.length];
	if(JcMainView.listTeam.length>0){
		addLayout(layoutMain,myBtns);
	}
	Button all = (Button) view.findViewById(R.id.all_check);
	Button clear = (Button) view.findViewById(R.id.clear_check);
	Button ok = (Button) view.findViewById(R.id.ok);
	all.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			for(MyButton btn:myBtns){
				btn.setOnClick(true);
				btn.switchBg();
			}
		}
	});
    clear.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			for(MyButton btn:myBtns){
				btn.setOnClick(false);
				btn.switchBg();
			}
		}
	});
    ok.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			checkTeam.clear();
			for(MyButton btn:myBtns){
				if(btn.isOnClick()){
					checkTeam.add(btn.getBtnText());
				}
			}
			lqMainView.updateList(checkTeam);
			if(checkTeam.size()!=0){
				dialogTeam.cancel();
			}else{
				Toast.makeText(context, "请至少选择一个赛事!", Toast.LENGTH_SHORT).show();
			}
			
		}
	});
    dialogTeam.show();
    dialogTeam.setCancelable(false);
    dialogTeam.getWindow().setContentView(view);
	}
	private void addLayout(LinearLayout layoutMain,MyButton[] myBtns) {
		int length = 0;
		if(JcMainView.listTeam!=null){
			length = JcMainView.listTeam.length;
		}
		int lineNum = 2;//每行个数
		int lastNum = length%lineNum;//最后一行个数
		int line = 1;//行数
		if(length>=lineNum){
			line = length/lineNum;
			for(int i=0;i<line;i++){
				LinearLayout layoutOne = addLine(lineNum,i,myBtns,lineNum);
				layoutMain.addView(layoutOne);
			}
			if(lastNum>0){
				LinearLayout layoutOne = addLine(lastNum, line,myBtns,lineNum);
				layoutMain.addView(layoutOne);
			}
		}else{
			LinearLayout layoutOne = addLine(length, 0,myBtns,lineNum);
			layoutMain.addView(layoutOne);
		}
	}
	/**
	 * 
	 * @param lastNum
	 * @param line 当前行数，从0开始
	 * @param myBtns
	 * @return
	 */
	private LinearLayout addLine(int lastNum, int line,MyButton[] myBtns,int lineNum) {
		LinearLayout layoutOne = new LinearLayout(context);
		for(int j=0;j<lastNum;j++){
			final MyButton btn = new MyButton(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(PublicMethod.getPxInt(135, context), 
					                                                          PublicMethod.getPxInt(35, context));
			params.setMargins(PublicMethod.getPxInt(10, context), PublicMethod.getPxInt(20, context), 0, 0);
			btn.setLayoutParams(params);
			myBtns[line*lineNum+j] = btn;
			btn.setBtnText(JcMainView.listTeam[line*lineNum+j]);
			btn.onAction();
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					btn.onAction();
				}
			});
			layoutOne.addView(btn);
		}
		return layoutOne;
	}
	/**
	 * 初始化倍数和期数进度条
	 * @param view
	 */
    public void initImageView(View view){
		mSeekBarBeishu = (SeekBar) view.findViewById(R.id.buy_jc_zixuan_seek_beishu);
		mSeekBarBeishu.setOnSeekBarChangeListener(this);
		mSeekBarBeishu.setProgress(iProgressBeishu);

		mTextBeishu = (TextView) view.findViewById(R.id.buy_zixuan_text_beishu);
		mTextBeishu.setText("" + iProgressBeishu);
		/*
		 * 点击加减图标，对seekbar进行设置：
		 * 
		 * @param int idFind, 图标的id View iV, 当前的view final int isAdd, 1表示加 -1表示减
		 * final SeekBar mSeekBar
		 * 
		 * @return void
		 */
		setSeekWhenAddOrSub(R.id.buy_zixuan_img_subtract_beishu, -1,mSeekBarBeishu, true,view);
		setSeekWhenAddOrSub(R.id.buy_zixuan_img_add_beishu, 1, mSeekBarBeishu,true,view);
    }
    /**
     * 是否隐藏客队在前文字
     * 
     */
    public void setTitle(boolean isVisable){
    TextView title = (TextView)findViewById(R.id.buy_jc_main_text_title);
    	if(isVisable){
    		title.setVisibility(TextView.VISIBLE);
    	}else{
    		title.setVisibility(TextView.GONE);
    	}
    }
    /**
     * 初始化组建
     */
	public void initView(){
        layoutView = (LinearLayout)findViewById(R.id.buy_lq_mian_layout);
        textTitle = (TextView) findViewById(R.id.layout_main_text_title);
        textTeamNum = (TextView) findViewById(R.id.buy_jc_main_text_team_num);
		Button btnType = (Button) findViewById(R.id.buy_lq_main_btn_type);
		btnType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createDialog();
			}
		});
		ImageButton zixuanTouzhu = (ImageButton) findViewById(R.id.buy_zixuan_img_touzhu);
		zixuanTouzhu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					beginTouZhu();
		
				
			}
		});
		ImageButton again = (ImageButton) findViewById(R.id.buy_zixuan_img_again);
		again.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lqMainView.clearChecked();
				lqMainView.initTeamNum(textTeamNum);
			}
		});
	}

	
	/**
	 * 玩法切换弹出框
	 */
	public void createDialog(){
		if(dialogType == null){
			LayoutInflater factory = LayoutInflater.from(this);
			viewType = factory.inflate(R.layout.buy_lq_main_type_dialog,null);
			TextView text1 = (TextView) viewType.findViewById(R.id.buy_lq_main_dialog_type1);
			TextView text2 = (TextView) viewType.findViewById(R.id.buy_lq_main_dialog_type2);
			TextView text3 = (TextView) viewType.findViewById(R.id.buy_lq_main_dialog_type3);
			TextView text4 = (TextView) viewType.findViewById(R.id.buy_lq_main_dialog_type4);
			if(type.equals(Constants.JCBASKET)){
				text1.setText(getString(R.string.jclq_dialog_sf_guoguan_title).toString());
				text2.setText(getString(R.string.jclq_dialog_rf_guoguan_title).toString());
				text3.setText(getString(R.string.jclq_dialog_sfc_guoguan_title).toString());
				text4.setText(getString(R.string.jclq_dialog_dxf_guoguan_title).toString());
			}else{
				text1.setText(getString(R.string.jczq_dialog_sf_guoguan_title).toString());
				text2.setText(getString(R.string.jczq_dialog_rf_guoguan_title).toString());
				text3.setText(getString(R.string.jczq_dialog_sfc_guoguan_title).toString());
				text4.setText(getString(R.string.jczq_dialog_dxf_guoguan_title).toString());
			}
			initRadioGroup(viewType);
			dialogType = new AlertDialog.Builder(this).create();
		}
	    dialogType.show();
	    dialogType.getWindow().setContentView(viewType);
	}
	private void initRadioGroup(View view) {
	    RadioButton radio0 =(RadioButton)view.findViewById(R.id.radio0);
	    RadioButton radio1 =(RadioButton)view.findViewById(R.id.radio1);
	    RadioButton radio2 =(RadioButton)view.findViewById(R.id.radio2);
	    RadioButton radio3 =(RadioButton)view.findViewById(R.id.radio3);
	    RadioButton radio4 =(RadioButton)view.findViewById(R.id.radio4);
	    RadioButton radio5 =(RadioButton)view.findViewById(R.id.radio5);
	    RadioButton radio6 =(RadioButton)view.findViewById(R.id.radio6);
	    RadioButton radio7 =(RadioButton)view.findViewById(R.id.radio7);
	    radioBtns.add(radio0); 
	    radioBtns.add(radio1); 
	    radioBtns.add(radio2); 
	    radioBtns.add(radio3); 
	    radioBtns.add(radio4); 
	    radioBtns.add(radio5); 
	    radioBtns.add(radio6); 
	    radioBtns.add(radio7); 
	  	for(RadioButton radio:radioBtns){
		    radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
					switch(buttonView.getId()){
					case R.id.radio0:
						isDanguan=false;
						createView(SF,isDanguan);		
						break;
					case R.id.radio1:
						isDanguan=false;
						createView(RF_SF,isDanguan);
						break;
					case R.id.radio2:
						isDanguan=false;
						createView(SFC,isDanguan);
						break;
					case R.id.radio3:
						isDanguan=false;
						createView(DXF,isDanguan);
						break;	
					case R.id.radio4:
						isDanguan=true;
						createView(SF,isDanguan);		
						break;
					case R.id.radio5:
						isDanguan=true;
						createView(RF_SF,isDanguan);
						break;
					case R.id.radio6:
						isDanguan=true;
						createView(SFC,isDanguan);
						break;
					case R.id.radio7:
						isDanguan=true;
						createView(DXF,isDanguan);
						break;		
					}
					clearRadio(buttonView);	
					dialogType.cancel();
				}
			  }
			});
	}
}
	
	
	private void clearRadio(CompoundButton buttonView) {
		for(RadioButton radio:radioBtns){
			if(radio.isChecked()&&radio.getId()!=buttonView.getId()){
				radio.setChecked(false);
			}
		}
	}
	/**
	 * 根据玩法按钮创建界面
	 */
	protected void createView(int type,boolean isdanguan){
		switch(type){
		case SF:
			lqMainView = new SPfView(this,betAndGift,new Handler(),layoutView,this.type,isdanguan,checkTeam);
			TYPE = SF;
			break;
		case RF_SF:
			lqMainView = new ZJQView(this,betAndGift,new Handler(),layoutView,this.type,isdanguan,checkTeam);
			TYPE = RF_SF;
			break;
		case SFC:
			lqMainView = new BFView(this,betAndGift,new Handler(),layoutView,this.type,isdanguan,checkTeam);
			TYPE = SFC;
			break;
		case DXF:
			lqMainView = new BQCView(this,betAndGift,new Handler(),layoutView,this.type,isdanguan,checkTeam);
			TYPE = DXF;
			break;
		}
		lqMainView.initTeamNum(textTeamNum);
		textTitle.setText(lqMainView.getTitle());
		
	}
	private void initSeekBar() {
		iProgressBeishu = 1;
		mSeekBarBeishu.setProgress(iProgressBeishu);
		mTextBeishu.setText("" + iProgressBeishu);
	}
	/**
	 * 投注方法
	 */
	public void beginTouZhu() {
			if(lqMainView.isTouZhuNet()){
				touzhuDialog = new TouzhuDialog(this,lqMainView);
				touzhuDialog.alert();
			};
	}
	/**
	 * fqc edit 添加一个参数 isBeiShu 来判断当前是倍数还是期数 。
	 * 
	 * @param idFind
	 * @param iV
	 * @param isAdd
	 * @param mSeekBar
	 * @param isBeiShu
	 */
	protected void setSeekWhenAddOrSub(int idFind, final int isAdd,final SeekBar mSeekBar, final boolean isBeiShu,View view) {
		ImageButton subtractBeishuBtn = (ImageButton) view.findViewById(idFind);
		subtractBeishuBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBeiShu) {
					if (isAdd == 1) {
						mSeekBar.setProgress(++iProgressBeishu);
					} else {
						mSeekBar.setProgress(--iProgressBeishu);
					}
					iProgressBeishu = mSeekBar.getProgress();
				}
			}
		});
	}

	
	


	/**
	 * 初始化投注信息
	 */
	public void initBet(){
		betAndGift.setSessionid(sessionId);
		betAndGift.setPhonenum(phonenum);
		betAndGift.setUserno(userno);
		betAndGift.setBettype("bet");// 投注为bet,赠彩为gift 
	}
	/**
	 * 投注联网
	 */
	public void touZhuNet(){
		final ProgressDialog progressDialog =  UserCenterDialog.onCreateDialog(this);// 显示网络提示框 2010/7/4
		progressDialog.show();
		// 加入是否改变切入点判断 陈晨 8.11
		Thread t = new Thread(new Runnable() {
			String str = "00";
			@Override
			public void run() {
				str = BetAndGiftInterface.getInstance().betOrGift(betAndGift);
				progressDialog.cancel();
				try {
					JSONObject obj = new JSONObject(str);		
					msg = obj.getString("message");
					String error = obj.getString("error_code");
					handler.handleMsg(error,msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				progressDialog.dismiss();
			}

		});
		t.start();
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			break;
		}
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (progress < 1)
			seekBar.setProgress(1);
		int iProgress = seekBar.getProgress();

		switch (seekBar.getId()) {
		case R.id.buy_jc_zixuan_seek_beishu:
			iProgressBeishu = iProgress;
			mTextBeishu.setText("" + iProgressBeishu);
			touzhuDialog.setAlertText();
			break;
		default:
			break;
		}

		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	protected void onStop(){
		super.onStop();
	}
	public void onDestroy(){
	super.onDestroy();  
	lqMainView.clearInfo();
	}
	public void errorCode_0000() {
		// TODO Auto-generated method stub
		createView(TYPE,isDanguan);
	    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}


	public void errorCode_000000() {
		// TODO Auto-generated method stub
		
	}

	public Context getContext() {
		// TODO Auto-generated method stub
		return this;
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 重写放回建
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case 4:
			lqMainView.clearInfo();
			finish();
			break;
		}
		return false;
	}
}
