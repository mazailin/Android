package com.ruyicai.activity.account;

import org.json.JSONObject;
import com.palmdream.RuyicaiAndroid.R;
import com.ruyicai.activity.common.UserLogin;
import com.ruyicai.constant.ShellRWConstants;
import com.ruyicai.net.newtransaction.BalanceQueryInterface;
import com.ruyicai.util.RWSharedPreferences;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 免费获取彩金
 * 
 * @author win
 * 
 */
public class GetFreeGoldActivity extends Activity {
	private Button secureOk;
	private TextView currentGold;
	public static  String adUnitID = "9c697272e78036382b35056bdf53904b";//这里是广告墙的广告位id
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_get_free_glod_for_limei);
		currentGold = (TextView)findViewById(R.id.current_gold);
		secureOk = (Button) findViewById(R.id.alipay_secure_ok);
		secureOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getFreeGold();
			}
		});
		setCurrentGold();
	}

	private void getFreeGold() {
		RWSharedPreferences pre = new RWSharedPreferences(this, "addInfo");
		String sessionIdStr = pre.getStringValue("sessionid");
		if (sessionIdStr.equals("")) {
			Intent intentSession = new Intent(this, UserLogin.class);
			startActivity(intentSession);
		} else {
			Intent intent = new Intent(this, AdWallActivity.class);
			startActivity(intent);
		}
	}
	
	private void setCurrentGold() {
		currentGold.setText("查询中....");
		final RWSharedPreferences shellRW = new RWSharedPreferences(this, "addInfo");
		final String  phonenum = shellRW.getStringValue(ShellRWConstants.PHONENUM);
		final String sessionid = shellRW.getStringValue(ShellRWConstants.SESSIONID);
		final String userno = shellRW.getStringValue(ShellRWConstants.USERNO);
		final Handler handler = new Handler();
		new Thread(new Runnable() {
			public void run() {
				try {
					String str = BalanceQueryInterface.balanceQuery(userno,
							sessionid, phonenum);
					JSONObject json = new JSONObject(str);
					String error_code = json.getString("error_code");
					if (error_code.equals("0000")) {
						final String money = json.getString("bet_balance");
						handler.post(new Runnable() {
							public void run() {
								currentGold.setText(money);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(new Runnable() {
						public void run() {
							currentGold.setText("查询失败");
						}
					});
				}
			}
		}).start();
	}
}