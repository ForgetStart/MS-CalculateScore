/**
 *
 */
package com.hc360.score.common;

/**
 * ������
 * @author ������
 *
 */
public class AppContent {
	//���̴߳�����Ϣ���̸߳���
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
		READLOGCOUNT,			//���������
		READNOTCOUNT,			//������
		READUSERSTATES,			//�ɳ�״̬
		READBUSINCOUNT,			//�̻�����
		READUSERAVERAGESCORE,	//�̻�ƽ������
		READAVERAGESCORE,		//
		READCOMANYCOMPASS,		//��˾������
		READMATCHINFO,          //��������
		READMATCHREAD,          //���������Ķ�״��
		READUSERLOGIN,          //�û���Ծ��

		READSUPPLYVISIT,		//��Ӧ������Ϊ
		READBUYVISIT,			//��������Ϊ
		READCOMPANYVISIT,		//��˾������Ϊ
		READINFOVISIT,			//��Ѷ������Ϊ

		READSUPPLYFINAL,		//��Ӧ�ռ�ҳ
		READBUYFINAL,			//���ռ�ҳ
		READPRUCDUCTNAL,		//���̲�Ʒ�ռ�ҳ

		READDETAIL,				//����ҳ��
		READDETAILCOMPANY,		//����ҳ��-��˾��ҳ
		READDETAILINTRO,		//����ҳ��-��˾����
		READDETAILSUPPLY,		//����ҳ��-��Ӧ��Ʒ
		READDETAILALBUM,		//����ҳ��-��˾���
		READDETAILNEWS,			//����ҳ��-��˾��̬
		READDETAILBUY,			//����ҳ��-��˾�ɹ�
		READDETAILCONTACT,		//����ҳ��-��ϵ����

		READMYBUY,				//�����������
		READMYEDITBUY,			//�����������-�����ɹ���Ϣ
		READMYMANAGEBUY,		//�����������-����ɹ���Ϣ
		READMYBUYOTHER,			//�����������-����

		READMYSUPPLY,			//������������
		READMYEDITSUPPLY,		//������������-������Ӧ��Ϣ
		READMYEDITMANAGESUPPLY,	//������������-����Ӧ��Ϣ
		READMYSUPPLYOTHER,		//������������-����

		READOTHERHOME,			//����-�۴���ҳ
		READOTHERTRADE,			//����-��ҵ��վ

		READSUPERMARKET,		//���з���
		READINDUSTRY			//��ҵ����
	}

	public static enum SAVETYPE{
		ADDSUPPLYVISIT,			//��Ӧ������Ϊ
		ADDBUYVISIT,			//��������Ϊ
		ADDCOMPANYVISIT,		//��˾������Ϊ
		ADDINFOVISIT,			//��Ѷ������Ϊ
		ADDSUPPLYFINAL,			//��Ӧ�ռ�ҳ
		ADDBUYFINAL,			//���ռ�ҳ
		ADDPRUCDUCTNAL,			//���̲�Ʒ�ռ�ҳ
		ADDDETAILCOMPANY,		//����ҳ��-��˾��ҳ
		ADDDETAILINTRO,			//����ҳ��-��˾����
		ADDDETAILSUPPLY,		//����ҳ��-��Ӧ��Ʒ
		ADDDETAILALBUM,			//����ҳ��-��˾���
		ADDDETAILNEWS,			//����ҳ��-��˾��̬
		ADDDETAILBUY,			//����ҳ��-��˾�ɹ�
		ADDDETAILCONTACT,		//����ҳ��-��ϵ����
		ADDMYEDITBUY,			//�����������-�����ɹ���Ϣ
		ADDMYMANAGEBUY,			//�����������-����ɹ���Ϣ
		ADDMYBUYOTHER,			//�����������-����
		ADDMYEDITSUPPLY,		//������������-������Ӧ��Ϣ
		ADDMYEDITMANAGESUPPLY,	//������������-����Ӧ��Ϣ
		ADDMYSUPPLYOTHER,		//������������-����
		ADDOTHERHOME,			//����-�۴���ҳ
		ADDOTHERTRADE,			//����-��ҵ��վ
		ADDVISITPREW,			//�ÿ���Դ
		ADDVISITKEYWORD,		//�ؼ���
		ADDVISITSUPERCAT,		//�ÿͷ���
		ADDVISITAREA,			//�ÿ���ҵ
		ADDVISITBUSINID			//�ÿ��̻�
	}

	public static int USER_STATUS_INTRO = 0;  //�����ڣ�Ĭ��
	public static int USER_STATUS_DEVE = 1;   //��չ��
	public static int USER_STATUS_RECOMM = 2; //�Ƽ���

	public static int USER_STATUS_SATISFY = 1; //������������
	public static int USER_STATUS_UNSATISFY = 2;//��������������

	//�·�δ��
	public static int BUSINESS_STATUS_NEW = 0;
	//��ɾ��
	public static int BUSINESS_STATUS_DEL = 1;
	//����
	public static int BUSINESS_STATUS_REFUSE = 2;
	//���ͨ��
	public static int BUSINESS_STATUS_PENDED = 3;
	//����
	public static int BUSINESS_STATUS_OVER = 4;


	//�̻���Ϣ��
	public static String HRTC_IRSL_BUSININFOLOG = "hrtc_irsl_busininfolog";
	//�û��̻���
	public static String HRTC_RDSL_AVERAGEQUALITYLOG = "hrtc_rdsl_averagequalitylogReverse";

	//��Ŀ�����ʶ
	public static String CALCULATE_SUPCAT="calculate_supcat";
	//���������ʶ
	public static String CALCULATE_ATT="calculate_att";
	//��������ʶ
	public static String CALCULATE_TITLE="calculate_title";
	//�۸�����ʶ
	public static String CALCULATE_PRICE="calculate_price";
	//ͼƬ�����ʶ
	public static String CALCULATE_MULTIMEDIA="calculate_multimedia";
	//��������ʶ
	public static String CALCULATE_INTRODUCE="calculate_introduce";

}
