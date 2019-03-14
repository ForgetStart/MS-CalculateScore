/**
 *
 */
package com.hc360.score.common;

/**
 * 公共类
 * @author 张云鹏
 *
 */
public class AppContent {
	//多线程处理消息，线程个数
	public static final int nThreads = 5;
	public static final int retry = 3;

	public static final String calculatescorelog = "calculatescore";
	public static final String resendcalculatescorelog = "resendcalculatescore";
	public static final String cstatisticslog = "cstatistics";
	public static final String resendcstatisticslog = "resendcstatistics";
	public static final String businesslog = "business";
	public static final String resendbusinesslog = "resendbusiness";
	public static final String userlog = "user";

	public static final String businhistory60w = "businhistory60w";

	public static enum QUERYTYPE{
		READLOGCOUNT,			//商铺浏览量
		READNOTCOUNT,			//留言量
		READUSERSTATES,			//成长状态
		READBUSINCOUNT,			//商机数量
		READUSERAVERAGESCORE,	//商机平均质量
		READAVERAGESCORE,		//
		READCOMANYCOMPASS,		//公司完整度
		READMATCHINFO,          //买卖速配
		READMATCHREAD,          //买卖速配阅读状况
		READUSERLOGIN,          //用户活跃度

		READSUPPLYVISIT,		//供应搜索行为
		READBUYVISIT,			//求购搜索行为
		READCOMPANYVISIT,		//公司搜索行为
		READINFOVISIT,			//资讯搜索行为

		READSUPPLYFINAL,		//供应终极页
		READBUYFINAL,			//求购终极页
		READPRUCDUCTNAL,		//商铺产品终极页

		READDETAIL,				//商铺页面
		READDETAILCOMPANY,		//商铺页面-公司首页
		READDETAILINTRO,		//商铺页面-公司介绍
		READDETAILSUPPLY,		//商铺页面-供应产品
		READDETAILALBUM,		//商铺页面-公司相册
		READDETAILNEWS,			//商铺页面-公司动态
		READDETAILBUY,			//商铺页面-公司采购
		READDETAILCONTACT,		//商铺页面-联系我们

		READMYBUY,				//买家商务中心
		READMYEDITBUY,			//买家商务中心-发布采购信息
		READMYMANAGEBUY,		//买家商务中心-管理采购信息
		READMYBUYOTHER,			//买家商务中心-其它

		READMYSUPPLY,			//卖家商务中心
		READMYEDITSUPPLY,		//卖家商务中心-发布供应信息
		READMYEDITMANAGESUPPLY,	//卖家商务中心-管理供应信息
		READMYSUPPLYOTHER,		//卖家商务中心-其它

		READOTHERHOME,			//其它-慧聪首页
		READOTHERTRADE,			//其它-行业网站

		READSUPERMARKET,		//超市分类
		READINDUSTRY			//行业分类
	}

	public static enum SAVETYPE{
		ADDSUPPLYVISIT,			//供应搜索行为
		ADDBUYVISIT,			//求购搜索行为
		ADDCOMPANYVISIT,		//公司搜索行为
		ADDINFOVISIT,			//资讯搜索行为
		ADDSUPPLYFINAL,			//供应终极页
		ADDBUYFINAL,			//求购终极页
		ADDPRUCDUCTNAL,			//商铺产品终极页
		ADDDETAILCOMPANY,		//商铺页面-公司首页
		ADDDETAILINTRO,			//商铺页面-公司介绍
		ADDDETAILSUPPLY,		//商铺页面-供应产品
		ADDDETAILALBUM,			//商铺页面-公司相册
		ADDDETAILNEWS,			//商铺页面-公司动态
		ADDDETAILBUY,			//商铺页面-公司采购
		ADDDETAILCONTACT,		//商铺页面-联系我们
		ADDMYEDITBUY,			//买家商务中心-发布采购信息
		ADDMYMANAGEBUY,			//买家商务中心-管理采购信息
		ADDMYBUYOTHER,			//买家商务中心-其它
		ADDMYEDITSUPPLY,		//卖家商务中心-发布供应信息
		ADDMYEDITMANAGESUPPLY,	//卖家商务中心-管理供应信息
		ADDMYSUPPLYOTHER,		//卖家商务中心-其它
		ADDOTHERHOME,			//其它-慧聪首页
		ADDOTHERTRADE,			//其它-行业网站
		ADDVISITPREW,			//访客来源
		ADDVISITKEYWORD,		//关键词
		ADDVISITSUPERCAT,		//访客分类
		ADDVISITAREA,			//访客行业
		ADDVISITBUSINID			//访客商机
	}

	public static int USER_STATUS_INTRO = 0;  //引入期，默认
	public static int USER_STATUS_DEVE = 1;   //发展期
	public static int USER_STATUS_RECOMM = 2; //推荐期

	public static int USER_STATUS_SATISFY = 1; //满足升级条件
	public static int USER_STATUS_UNSATISFY = 2;//不满足升级条件

	//新发未审
	public static int BUSINESS_STATUS_NEW = 0;
	//已删除
	public static int BUSINESS_STATUS_DEL = 1;
	//拒审
	public static int BUSINESS_STATUS_REFUSE = 2;
	//审核通过
	public static int BUSINESS_STATUS_PENDED = 3;
	//过期
	public static int BUSINESS_STATUS_OVER = 4;


	//商机信息表
	public static String HRTC_IRSL_BUSININFOLOG = "hrtc_irsl_busininfolog";
	//用户商机表
	public static String HRTC_RDSL_AVERAGEQUALITYLOG = "hrtc_rdsl_averagequalitylogReverse";

	//类目计算标识
	public static String CALCULATE_SUPCAT="calculate_supcat";
	//参数计算标识
	public static String CALCULATE_ATT="calculate_att";
	//标题计算标识
	public static String CALCULATE_TITLE="calculate_title";
	//价格计算标识
	public static String CALCULATE_PRICE="calculate_price";
	//图片计算标识
	public static String CALCULATE_MULTIMEDIA="calculate_multimedia";
	//详情计算标识
	public static String CALCULATE_INTRODUCE="calculate_introduce";

}
