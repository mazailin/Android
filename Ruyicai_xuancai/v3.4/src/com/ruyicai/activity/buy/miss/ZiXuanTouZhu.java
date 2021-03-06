package com.ruyicai.activity.buy.miss;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.palmdream.RuyicaiAndroidxuancai.R;
import com.ruyicai.activity.buy.ApplicationAddview;
import com.ruyicai.activity.common.UserLogin;
import com.ruyicai.activity.usercenter.UserCenterDialog;
import com.ruyicai.handler.HandlerMsg;
import com.ruyicai.handler.MyHandler;
import com.ruyicai.net.newtransaction.BetAndGiftInterface;
import com.ruyicai.net.newtransaction.pojo.BetAndGiftPojo;
import com.ruyicai.util.PublicMethod;
import com.ruyicai.util.RWSharedPreferences;

public class ZiXuanTouZhu extends Activity implements HandlerMsg,OnSeekBarChangeListener{
	
	
	String phonenum,sessionId,userno;
	protected ProgressDialog progressdialog;
	private BetAndGiftPojo betAndGift;// 投注信息类
	String lotno;
	TextView alertText;
	TextView issueText;
	Button codeInfo;
	MyHandler handler = new MyHandler(this);//自定义handler
	TextView textAlert;
	TextView textZhuma;
    TextView textTitle;	
	public SeekBar mSeekBarBeishu, mSeekBarQishu;
	protected EditText mTextBeishu;
	public int iProgressBeishu = 1, iProgressQishu = 1;
	TextView zhushu;
	TextView jine;
	TextView caizhong;
	private boolean toLogin = false;
	public boolean isTouzhu = false;//是否投注
	public static String type="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_touzhu);
		ApplicationAddview app=(ApplicationAddview)getApplicationContext();
	    betAndGift=app.getPojo();
		init();
	}
	
	private void init(){
		zhushu = (TextView)findViewById(R.id.alert_dialog_touzhu_textview_zhushu);
		jine=(TextView) findViewById(R.id.alert_dialog_touzhu_textview_jine);
		caizhong=(TextView)findViewById(R.id.alert_dialog_touzhu_textview_caizhong);
		caizhong.setText(PublicMethod.toLotno(betAndGift.getLotno()));
		issueText =(TextView) findViewById(R.id.alert_dialog_touzhu_textview_qihao);
		textZhuma =(TextView) findViewById(R.id.alert_dialog_touzhu_text_zhuma);
		textTitle = (TextView) findViewById(R.id.alert_dialog_touzhu_text_zhuma_title);
		issueText.setText("第"+PublicMethod.toIssue(betAndGift.getLotno())+"期");
		if(type.equals("zc")){
			textTitle.setText("注码：共有1笔投注");
			textZhuma.setText(betAndGift.getBet_code());
		}else{
			initImageView();
			ZixuanActivity.addView.getCodeList().get(ZixuanActivity.addView.getSize()-1).setTextCodeColor(textZhuma);
			textTitle.setText("注码："+"共有"+ZixuanActivity.addView.getSize()+"笔投注");
			codeInfo = (Button) findViewById(R.id.alert_dialog_touzhu_btn_look_code);
			isCodeText(codeInfo);
			codeInfo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ZixuanActivity.addView.createCodeInfoDialog(ZiXuanTouZhu.this);
					ZixuanActivity.addView.showDialog();
				}
			});
		}
		if(betAndGift.isZhui()){
			initZhuiJia();
		}
		getTouzhuAlert();
		Button cancel = (Button) findViewById(R.id.alert_dialog_touzhu_button_cancel);
		Button ok = (Button) findViewById(R.id.alert_dialog_touzhu_button_ok);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ZiXuanTouZhu.this.finish();
			}
		});
		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				RWSharedPreferences pre = new RWSharedPreferences(ZiXuanTouZhu.this, "addInfo");
				sessionId = pre.getStringValue("sessionid");
				phonenum = pre.getStringValue("phonenum");
				userno = pre.getStringValue("userno");
				if (userno.equals("")) {
					toLogin = true;
					Intent intentSession = new Intent(ZiXuanTouZhu.this, UserLogin.class);
					startActivityForResult(intentSession, 0);
				} else {
					touZhu();
				}
			}
		});
		
	}
	
	
	private void isCodeText(Button codeInfo) {
	if(ZixuanActivity.addView.getSize()>1){
		codeInfo.setVisibility(Button.VISIBLE);
	}else{
		codeInfo.setVisibility(Button.GONE);
	}
}
	
	/**
	 * 显示追加投注
	 * @param view
	 */
	private void initZhuiJia() {
		LinearLayout toggleLinear = (LinearLayout)findViewById(R.id.buy_zixuan_linear_toggle);
		toggleLinear.setVisibility(LinearLayout.VISIBLE);
		ToggleButton zhuijiatouzhu = (ToggleButton)findViewById(R.id.dlt_zhuijia);
		zhuijiatouzhu.setOnCheckedChangeListener(new OnCheckedChangeListener() {			

			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if(isChecked){
					betAndGift.setAmt(3);
					betAndGift.setIssuper("0");
				}else{
					betAndGift.setIssuper("");
					betAndGift.setAmt(2);
				}
				ZixuanActivity.addView.setCodeAmt(betAndGift.getAmt());
				getTouzhuAlert();
			}
		});
	}
	
	/**
	 * 投注提示框中的信息
	 */
	public void getTouzhuAlert(){
		if(type.equals("zc")){
			zhushu.setText(betAndGift.getZhushu() + "注     " );	
			jine.setText(+iProgressQishu * (Integer.valueOf(betAndGift.getAmount())/100) * iProgressBeishu
					+ "元");
		}else{
		zhushu.setText(ZixuanActivity.addView.getAllZhu() + "注     " );
		jine.setText(iProgressQishu * ZixuanActivity.addView.getAllAmt() * iProgressBeishu
				+ "元");
		}
		
//		return "注数："
//				+ ZixuanActivity.addView.getAllZhu() + "注     " 
//				+ "金额：" + 
//				+ iProgressQishu * ZixuanActivity.addView.getAllAmt() * iProgressBeishu
//				+ "元"; 
	}
	
	/**
	 * 投注方法
	 */
	private void touZhu() {
		toLogin = false;
		initBet();
		// TODO Auto-generated method stub
	    touZhuNet();
//		clearProgress();
	}
	
	
	/**
	 * 清空倍数和期数的进度条
	 */
	public void clearProgress(){
		iProgressBeishu = 1;
		iProgressQishu = 1;
		mSeekBarBeishu.setProgress(iProgressBeishu);
//		mSeekBarQishu.setProgress(iProgressQishu);
		ZixuanActivity.addView.clearInfo();
		ZixuanActivity.addView.updateTextNum();
	}
	
	/**
	 * 投注联网
	 */
	public void touZhuNet(){
		progressdialog = UserCenterDialog.onCreateDialog(this);
		progressdialog.show();
		// 加入是否改变切入点判断 陈晨 8.11
		Thread t = new Thread(new Runnable() {
			String str = "00";
			@Override
			public void run() {
				str = BetAndGiftInterface.getInstance().betOrGift(betAndGift);
				try {
					JSONObject obj = new JSONObject(str);		
					String msg = obj.getString("message");
					String error = obj.getString("error_code");
					handler.handleMsg(error,msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				progressdialog.dismiss();
			}

		});
		t.start();
	}
	/**
	 * 初始化投注信息
	 */
	public void initBet(){
		betAndGift.setIsSellWays("1");
		betAndGift.setAmount(""+ZixuanActivity.addView.getAllAmt()*iProgressBeishu*100);
		betAndGift.setSessionid(sessionId);
		betAndGift.setPhonenum(phonenum);
		betAndGift.setUserno(userno);
		betAndGift.setBettype("bet");// 投注为bet,赠彩为gift 
		betAndGift.setLotmulti(""+iProgressBeishu);//lotmulti    倍数   投注的倍数
		betAndGift.setBatchnum(""+iProgressQishu);//batchnum    追号期数 默认为1（不追号）
		betAndGift.setBet_code(ZixuanActivity.addView.getTouzhuCode(iProgressBeishu,betAndGift.getAmt()*100));
		lotno = PublicMethod.toLotno(betAndGift.getLotno());
		betAndGift.setBatchcode(PublicMethod.toIssue(betAndGift.getLotno()));//期号
		
	}
	/**
	 * 从ShellRWSharesPreferences中获取phonenum 、sessionid 和userno
	 */
	/**
	 * 初始化倍数和期数进度条
	 * @param view
	 */
   public void initImageView(){
		mSeekBarBeishu = (SeekBar) findViewById(R.id.buy_zixuan_seek_beishu);
		mSeekBarBeishu.setOnSeekBarChangeListener(this);
		mSeekBarBeishu.setProgress(iProgressBeishu);
	

		mTextBeishu = (EditText) findViewById(R.id.buy_zixuan_text_beishu);
		mTextBeishu.setText("" + iProgressBeishu);
		
		
		PublicMethod.setEditOnclick(mTextBeishu,mSeekBarBeishu,new Handler());
	
		/*
		 * 点击加减图标，对seekbar进行设置：
		 * 
		 * @param int idFind, 图标的id View iV, 当前的view final int isAdd, 1表示加 -1表示减
		 * final SeekBar mSeekBar
		 * 
		 * @return void
		 */
		setSeekWhenAddOrSub(R.id.buy_zixuan_img_subtract_beishu, -1,mSeekBarBeishu, true);
		setSeekWhenAddOrSub(R.id.buy_zixuan_img_add_beishu, 1, mSeekBarBeishu,true);
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
   
	protected void setSeekWhenAddOrSub(int idFind, final int isAdd,final SeekBar mSeekBar, final boolean isBeiShu) {
		ImageButton subtractBeishuBtn = (ImageButton) findViewById(idFind);
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
				} else {
					if (isAdd == 1) {
						mSeekBar.setProgress(++iProgressQishu);
					} else {
						mSeekBar.setProgress(--iProgressQishu);
					}
					iProgressQishu = mSeekBar.getProgress();
				}
			}
		});
	}
	@Override
	public void errorCode_0000() {
		// TODO Auto-generated method stub
		clearProgress();
		showfenxdialog();
		}
	 public void showfenxdialog(){
		    LayoutInflater inflate = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View view = inflate.inflate(R.layout.touzhu_succe,null);
		    final AlertDialog dialog = new AlertDialog.Builder(this).create();
		    ImageView image = (ImageView) view.findViewById(R.id.touzhu_succe_img);
		    Button ok = (Button) view.findViewById(R.id.touzhu_succe_button_sure);
		    Button share = (Button) view.findViewById(R.id.touzhu_succe_button_share);
	        image.setImageResource(R.drawable.succee);
	        ok.setBackgroundResource(R.drawable.loginselecter);
	        share.setBackgroundResource(R.drawable.loginselecter);
	        ok.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.cancel();
					ZiXuanTouZhu.this.finish();
				}
			});
	        share.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.cancel();
					Intent intent=new Intent(Intent.ACTION_SEND);  
					intent.setType("text/plain");  
					intent.putExtra(Intent.EXTRA_SUBJECT, "分享给朋友");  
					intent.putExtra(Intent.EXTRA_TEXT, "我刚刚使用如意彩手机客户端购买了一张彩票:"+"快来参与吧！官网www.ruyicai.com");  
					startActivity(Intent.createChooser(intent,"请选择分享方式")); 
					ZiXuanTouZhu.this.finish();
				}
			});		   
	        dialog.show();
	        dialog.getWindow().setContentView(view);
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
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
//		 TODO Auto-generated method stub
		if (progress < 1)
		seekBar.setProgress(1);
		int iProgress = seekBar.getProgress();
		switch (seekBar.getId()) {
		case R.id.buy_zixuan_seek_beishu:
			iProgressBeishu = iProgress;
			mTextBeishu.setText("" + iProgressBeishu);
//			changeTextSumMoney();
			break;
		default:
			break;
		}
        getTouzhuAlert();
	
}
		

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}
