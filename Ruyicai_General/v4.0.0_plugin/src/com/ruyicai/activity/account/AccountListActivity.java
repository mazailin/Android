package com.ruyicai.activity.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alipay.android.secure.AlipaySecurePayDialog;
import com.palmdream.RuyicaiAndroid.R;
import com.ruyicai.activity.common.UserLogin;
import com.ruyicai.constant.Constants;
import com.ruyicai.constant.ShellRWConstants;
import com.ruyicai.util.PublicConst;
import com.ruyicai.util.PublicMethod;
import com.ruyicai.util.RWSharedPreferences;
import com.ruyicai.util.RuyicaiActivityManager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 主页面 充值界面
 * 
 * @author Administrator
 * 
 */
public class AccountListActivity extends Activity {
	private String isonkey = "";
	private RelativeLayout top;
	private String textString;
	private Button returnButton;
	private String TITLE = "title";
	private String ISHANDINGFREE = "isHandingFree";
	private String PICTURE = "";
	private Context context;
	private RWSharedPreferences shellRW = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Constants.isDebug) PublicMethod.outLog(this.getClass().getSimpleName(), "onCreate()");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.account_list_main);
		context = this;
		shellRW = new RWSharedPreferences(
				context, ShellRWConstants.ACCOUNT_DISPAY_STATE);
		setTopText();
		setTopState();
		initListView();
		/**add by yejc 20130419 start*/
		if(Constants.isDebug) {
			PublicMethod.outLog(this.getClass().getName(), "onCreate");
		}
		/**add by yejc 20130419 end*/
	}

	protected void onResume() {
		super.onResume();
		Constants.MEMUTYPE = 0;
	}

	protected void onPause() {
		super.onPause();
	}

	/**
	 * 初始化主列表
	 */
	private void initListView() {
		ListView listView = (ListView) findViewById(R.id.account_rechange_listview);
		List list = setContentForMainList();
		AccountAdapter adapter = new AccountAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView AccountType = (TextView) view
						.findViewById(R.id.account_recharge_listview_text);
				textString = AccountType.getText().toString();
				onClickList();
			}
		});
	}

	/**
	 * 设置头部text信息
	 * 
	 * @author Administrator
	 * 
	 */
	public void setTopText() {
		TextView textTop = (TextView) findViewById(R.id.account_list_main_text);
		String text1 = getString(R.string.computer_rechargeinfo1);
		String text2 = getString(R.string.computer_address);
		String text3 = getString(R.string.computer_rechargeinfo2);
		Spanned spanned = Html.fromHtml("<a href=\"http://www.ruyicai.com\">"
				+ text2 + "</a>");
		textTop.append(text1);
		textTop.append(spanned);
		textTop.append(text3);
		textTop.setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * 设置标题显示状态
	 */
	private void setTopState() {
		String onkey = getIntent().getStringExtra("isonKey");
		if (onkey != null && !onkey.equals("")) {
			isonkey = onkey;
		}
		top = (RelativeLayout) findViewById(R.id.main_buy_title);
		if (isonkey.equals("fasle")) {
			top.setVisibility(View.VISIBLE);
			returnButton = (Button) findViewById(R.id.layout_main_img_return);
			returnButton.setVisibility(View.INVISIBLE);
			returnButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		}
	}

	/**
	 * 判断用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogin() {
		boolean isLogin = false;
		RWSharedPreferences pre = new RWSharedPreferences(this, "addInfo");
		String userno = pre.getStringValue(ShellRWConstants.USERNO);
		if (userno == null || userno.equals("")) {
			isLogin = false;
			Intent intentSession = new Intent(this, UserLogin.class);
			startActivityForResult(intentSession, 0);
		} else {
			isLogin = true;
		}
		return isLogin;
	}

	/**
	 * intent回调函数 用户登录过后直接弹出充值框
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			onClickList();
			break;
		}
	}

	/**
	 * 列表响应事件
	 */
	private void onClickList() {
		if ("支付宝充值(免手续费)".equals(textString)) {
			if (isLogin()) {
				Intent intent = new Intent(context, AlipaySecureActivity.class);
				startActivity(intent);
			}
		} else if ((getString(R.string.zhfb_cards_secure_recharge) + getString(R.string.freeHanding))
				.equals(textString)) {// 支付宝安全支付
			if (isLogin()) {
				Intent alipay_secure = new Intent(context,
						AlipaySecurePayDialog.class);
				startActivity(alipay_secure);
			}
		} else if (getString(R.string.atm_recharge).equals(textString)) {// 银行转账
			Intent intent = new Intent(context, Accoutmovecash.class);
			startActivity(intent);
		} 
	}

	/**
	 * 添加充值方式数据
	 * 
	 * @return
	 */
	private List<Map<String, Object>> setContentForMainList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(2);
		Map<String, Object> map;
		
		// 支付宝安全支付
		if (shellRW.getBooleanValue(Constants.ZHIFUBAO_SECURE_PAYMENT_DISPLAY_STATE, false)) {
			map = new HashMap<String, Object>();
			map.put(TITLE, getString(R.string.zhfb_cards_secure_recharge));
			map.put(PICTURE, R.drawable.recharge_alipay_safe);
			map.put(ISHANDINGFREE, getString(R.string.account_zfb_secure));
			list.add(map);
		}
		
		// 支付宝充值
		if (shellRW.getBooleanValue(Constants.ZHIFUBAO_RECHARGE_DISPLAY_STATE, false)) {
			map = new HashMap<String, Object>();
			map.put(TITLE, getString(R.string.zhfb_cards_recharge));
			map.put(PICTURE, R.drawable.recharge_alipay);
			map.put(ISHANDINGFREE, getString(R.string.account_zfb_alert));
			list.add(map);
		}
		
		// 银行转账
		map = new HashMap<String, Object>();
		map.put(TITLE, getString(R.string.atm_recharge));
		map.put(PICTURE, R.drawable.recharge_atm);
		map.put(ISHANDINGFREE, getString(R.string.account_zhuanzhang_alert));
		list.add(map);
		return list;
	}

	/**
	 * 主列表适配器
	 * 
	 * @author win
	 * 
	 */
	class AccountAdapter extends BaseAdapter {
		private LayoutInflater mInflater; // 扩充主列表布局
		private List<Map<String, Object>> mList;

		public AccountAdapter(Context context, List<Map<String, Object>> list) {
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
			String title = (String) mList.get(position).get(TITLE);
			Integer iconid = (Integer) mList.get(position).get(PICTURE);
			String alertStr = (String) mList.get(position).get(ISHANDINGFREE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.account_listviw_item,
						null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView
						.findViewById(R.id.account_recharge_listview_text);
				holder.isfreeHanding = (TextView) convertView
						.findViewById(R.id.ishandingfree);
				holder.lefticon = (ImageView) convertView
						.findViewById(R.id.account_recharge_type);
				holder.layout = (LinearLayout) convertView
						.findViewById(R.id.account_recharge_listview_linerlayout);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			SpannableStringBuilder builder1 = new SpannableStringBuilder();
			String str1 = title;
			builder1.append(str1);
			String alertStr1 = "";
			
			if (str1.equals(getString(R.string.zhfb_cards_recharge)) 
					|| str1.equals(getString(R.string.zhfb_cards_secure_recharge))) {
				alertStr1 = getString(R.string.freeHanding);
			} 
			if (!alertStr1.equals("")) {
				builder1.append(alertStr1);
				builder1.setSpan(new ForegroundColorSpan(Color.RED),
						builder1.length() - alertStr1.length(),
						builder1.length(), Spanned.SPAN_COMPOSING);
			}
			holder.title.setText(builder1);
			holder.lefticon.setBackgroundResource(iconid);
			SpannableStringBuilder builder = new SpannableStringBuilder();
			String str = alertStr;
			builder.append(str);
			holder.isfreeHanding.setHint(builder);
			return convertView;
		}

		class ViewHolder {
			TextView title;
			ImageView lefticon;
			TextView isfreeHanding;
			LinearLayout layout;
		}
	}

	public static class AlixOnCancelListener implements
			DialogInterface.OnCancelListener {
		Activity mcontext;

		public AlixOnCancelListener(Activity context) {
			mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	/**
	 * 重写放回建
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 4:
			if (isonkey.equals("fasle")) {
				this.finish();
			} else {
				RuyicaiActivityManager.getInstance().exit();
				PublicConst.islogin = false;
				PublicConst.isthirdlogin = false;
			}
			break;
		}
		return false;
	}
	
}
