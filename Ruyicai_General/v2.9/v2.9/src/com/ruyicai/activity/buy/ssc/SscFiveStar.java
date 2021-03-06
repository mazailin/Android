package com.ruyicai.activity.buy.ssc;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import com.palmdream.RuyicaiAndroid.R;
import com.ruyicai.activity.buy.ZixuanAndJiXuan;
import com.ruyicai.code.ssc.FiveStarCode;
import com.ruyicai.interfaces.BuyImplement;
import com.ruyicai.jixuan.SscBalls;
import com.ruyicai.pojo.AreaNum;
import com.ruyicai.pojo.BallTable;
import com.ruyicai.util.AreaInfo;
import com.ruyicai.util.Constants;

public class SscFiveStar extends ZixuanAndJiXuan implements BuyImplement {
	public boolean isjixuan=false;
	public int FIVESTARTYPE=0;
    public static int SSC_TYPE=4;//五星
    public static SscFiveStar self;
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		lotnoStr = Constants.LOTNO_SSC;
		childtype = new String []{"直选","机选","五星通选"};
		setContentView(R.layout.sscbuyview);
		sscCode = new FiveStarCode();
		self =this;
		init();
		highttype = "SSC";
		
	}
	public void theMethodYouWantToCall(){
        // do what ever you want here
		init();
    }
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		super.onCheckedChanged(group, checkedId);
		switch(checkedId){		
		case 0:
			isjixuan=false;
			FIVESTARTYPE=Constants.SSC_FIVESTAR_ZHIXUAN;
			BallTable qiantable;
			BallTable baitable;
			BallTable shitable;
			BallTable getable;
			iProgressBeishu = 1;iProgressQishu = 1;
			String wantitle = "万位区";
			String qiantitle = "千位区";
			String baititle = "百位区";
			String shititle = "十位区" ;
			String getitle = "个位区";
			areaInfos= new AreaInfo[5];
			areaInfos[0] = new AreaInfo(10, 9, BallResId, 0, 0,Color.RED, wantitle);
			areaInfos[1] = new AreaInfo(10, 9, BallResId, 0, 0,Color.RED, qiantitle);
			areaInfos[2] = new AreaInfo(10, 9, BallResId, 0, 0,Color.RED, baititle);
			areaInfos[3] = new AreaInfo(10, 9, BallResId, 0, 0,Color.RED, shititle);
			areaInfos[4] = new AreaInfo(10, 9, BallResId, 0, 0,Color.RED, getitle);
			createView(areaInfos, sscCode,this,FIVE);
			BallTable = areaNums[0].table;
			qiantable = areaNums[1].table;
			baitable = areaNums[2].table;
			shitable = areaNums[3].table;
		    getable = areaNums[4].table;
		break;
		case 1:
			isjixuan=true;
			iProgressBeishu = 1;iProgressQishu = 1;
			 SscBalls sscb = new SscBalls(5);
			 createviewmechine(sscb,this);
		break;	
		case 2:
			isjixuan=false;
			FIVESTARTYPE=Constants.SSC_FIVESTAR_TONGXUAN;
			BallTable qiantablet;
			BallTable baitablet;
			BallTable shitablet;
			BallTable getablet;
			iProgressBeishu = 1;iProgressQishu = 1;
			String wantitlet = "万位区";
			String qiantitlet = "千位区";
			String baititlet = "百位区";
			String shititlet = "十位区" ;
			String getitlet = "个位区";
			areaInfos= new AreaInfo[5];
			areaInfos[0] = new AreaInfo(10, 9, BallResId, 0, 0,Color.RED, wantitlet);
			areaInfos[1] = new AreaInfo(10, 9, BallResId, 0, 0,Color.RED, qiantitlet);
			areaInfos[2] = new AreaInfo(10, 9, BallResId, 0, 0,Color.RED, baititlet);
			areaInfos[3] = new AreaInfo(10, 9, BallResId, 0, 0,Color.RED, shititlet);
			areaInfos[4] = new AreaInfo(10, 9, BallResId, 0, 0,Color.RED, getitlet);
			createView(areaInfos, sscCode,this,FIVE_TONGXUAN);
			BallTable = areaNums[0].table;
			qiantable = areaNums[1].table;
			baitable = areaNums[2].table;
			shitable = areaNums[3].table;
		    getable = areaNums[4].table;
	}

}
	 protected void onResume() {
		    // TODO Auto-generated method stub
		    super.onResume();
		   
	}

	 public String getZxAlertZhuma() {
		int iZhuShu = getZhuShu();
		int[] wan = areaNums[0].table.getHighlightBallNOs();
		int[] qian =areaNums[1].table.getHighlightBallNOs();
		int[] bai = areaNums[2].table.getHighlightBallNOs();
		int[] shi = areaNums[3].table.getHighlightBallNOs();
		int[] ge =  areaNums[4].table.getHighlightBallNOs();

		return  "注码：" + "\n" + 
				 "万位："
				+ getStrZhuMa(wan) + "\n" + "千位：" + getStrZhuMa(qian) + "\n"
				+ "百位：" + getStrZhuMa(bai) + "\n" + "十位：" + getStrZhuMa(shi)
				+ "\n" + "个位：" + getStrZhuMa(ge);

	}
	  public String getZhuma(){
	 	   String zhuma="";
	 	   if(isjixuan){
	 	    zhuma = SscBalls.getzhuma(balls, SSC_TYPE);
	 	   }else{
	 	   zhuma =   sscCode.zhuma(areaNums, iProgressBeishu, FIVESTARTYPE);
	 	   }
	 	   return zhuma;
	    }
	private int getZhuShu() {
		int iReturnValue = 0;
		if(isjixuan){
			int beishu = iProgressBeishu;
			iReturnValue=balls.size()*beishu;
		}else{
		int wan = areaNums[0].table.getHighlightBallNums();
	    int qian = areaNums[1].table.getHighlightBallNums();
		int bai = areaNums[2].table.getHighlightBallNums();
		int shi = areaNums[3].table.getHighlightBallNums();
		int ge = areaNums[4].table.getHighlightBallNums();
		int beishu = iProgressBeishu;
		iReturnValue = wan * qian * bai * shi * ge * beishu;}
		return iReturnValue;
	}

	@Override
	public void isTouzhu() {
		if(isjixuan){
			setZhuShu(balls.size());
			alertJX(); 
		}else{
			int iZhuShu = getZhuShu();
			int wan = areaNums[0].table.getHighlightBallNums();
			int qian = areaNums[1].table.getHighlightBallNums();
		    int bai = areaNums[2].table.getHighlightBallNums();
			int shi = areaNums[3].table.getHighlightBallNums();
		    int ge = areaNums[4].table.getHighlightBallNums();
			if (wan == 0 | qian == 0 | bai == 0 | shi == 0 | ge == 0) {
				alertInfo("请至少选择一注！");
			} else if (iZhuShu * 2 > 100000) {
				dialogExcessive();
			} else{
				setZhuShu(iZhuShu);
				alertZX(getZxAlertZhuma());
			}
		}
		
	}
	@Override
	public String textSumMoney(AreaNum[] areaNum, int iProgressBeishu) {
		// TODO Auto-generated method stub
		String iTempString;
		int iZhuShu = getZhuShu();
		if (iZhuShu != 0) {
		    iTempString = "共" + iZhuShu + "注，共" + (iZhuShu * 2) + "元";
		} else {
			iTempString = getResources().getString(R.string.please_choose_number);
		}
	
		return iTempString;
	}
	@Override
	public void touzhuNet() {
		// TODO Auto-generated method stub
		int zhuShu=getZhuShu();
		if(isjixuan){
		betAndGift.setSellway("1")	;
		}else{
		betAndGift.setSellway("0");}//1代表机选 0代表自选
		betAndGift.setLotno(Constants.LOTNO_SSC);
		betAndGift.setBet_code(getZhuma());
		betAndGift.setAmount(""+zhuShu*200);
	}
}
