package com.hc360.score.task;

import com.hc360.bcs.utils.BusinCompleCaluteNewUtils;
import com.hc360.rsf.imgup.FileStorageService2WH;
import com.hc360.score.db.rsf.RSFService;

/**
 * Created by Administrator on 2016/8/12.
 */
public class Test {

    public static void main(String[] args) throws Exception{
        System.out.println("Test start....");
        FileStorageService2WH fss= RSFService.getFilestorageService2WH();
        /*String imgSrc="http://img13.hc360.cn/13/product/250/137/b/13-25013713.jpg";
        Map<String, Object> fieldMap = fss.readImgFile(imgSrc,2);
        System.out.println("width1:"+fieldMap.get("width")+",height1:"+fieldMap.get("height"));
        Map<String, Object> fieldMap2 = fss.readImgFile(imgSrc);
        System.out.println("width2:"+fieldMap2.get("width")+",height2:"+fieldMap2.get("height"));*/
        /*String introduce="<img src=\"http://img005.hc360.cn/k3/M0D/32/1F/wKhQv1exUW-ECqrDAAAAAGarEec627.jpg\" /><img src=\"http://img004.hc360.cn/k1/M0C/30/40/wKhQwFexUGeEXKnjAAAAAIwY7Ic945.jpg\" /><img src=\"http://img001.hc360.cn/k1/M06/C1/53/wKhQw1fBibuEMLuKAAAAAG0jDaU944.jpg..100x100.jpg\" /><img src=\"http://img002.hc360.cn/k3/M02/B1/72/wKhQv1fBic6EQ7-6AAAAAO2Q2No689.jpg..100x100.jpg\" /><img src=\"http://img008.hc360.cn/k1/M06/C1/54/wKhQw1fBicWEaRsfAAAAAKcAWCU468.jpg..100x100.jpg\" />&nbsp;\n" +
                "<div>\n" +
                "\t&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��ҡ�����������Դ���������޵�Դ֧�ֵĶ������ṩһ����Ч�ı�����ֻ��ͨ���˹�����˳ʱ�뷽��ҡ���ֱ����ٶȴﵽ������50-80ת/�֣��Ϳɴﵽ����ı���Ч�����������������������󡢴�͸��ǿ������������Сȡ�����ֱ�ҡ�����ٶȣ��¿���ҡ���������������õ�����Ż��ƣ�����Ѹ�ٹر������ڣ����������������ͬ���źš�\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��ҡ���������С�������ᡢ����Я������װ���㡣����Ҫ�κι��ߡ�ֻ��0.15ƽ�������ֺ���վ���ĵط��Ϳ������������ˣ���������ҵ�ǳ�ǿ����������ӡ�����������������ɽ���������ݳ����Ϳ⡢�ֿ⡢���ݡ��Ƶ����ҵ��λ�ڷ���Σ����λ�ڷ���Σ������£�ʹ�õ�һ�����뱨���豸��\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��ҡ����������;��\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;������ͻ���¼�Ӧ��Ԥ��,���ƷЯ������.��Ҫ���ڷ�Ѵ�����֡�ɭ�����������Ӿ������ʡ���������ҵ����Ӧ��ͻ���¼�����ʹ��.\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��ҡ��������ԭ��\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��ҡ��������ͨ���˹�ת��ҡ������������Ĵ����𼶼��٣��پ���������������ָ�����ת�������ڽ��и�����ת�����У������յĿ����������ٸ�ѹ���ֵĹ̶�����ͬʱ�����������Ĺ������Ӷ��𵽱��������á�\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��Ϊ����ҡ����������ҡ�ĳ���ת�پ����˾�����������Ч����ת�ٺ�����Ч���ǳ����ȵģ���Զ��ԣ�ת��Խ��������Խ��������֮����ҡ����������������ġ����ǵĸ߰��̶ȡ������Ĵ�С��ҡ���Ŀ����ȶ���Ӱ�쾯��������Ч����ֱ�����أ������������������˺�۹涨��ҡ���ĳ���ת�ٲ���С��90r/min������ʹﲻ��Ӧ�е�����Ч����\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��ȫʹ����ά������\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1�����ͺ���ҡ�������ز���ֻ��Ҫһ���˾Ϳ������������ˡ��������ڹ���ʱ������������ڸ�����ת��һ�㶼��2000r/min���ϣ�����ǧ��Ҫ����ָ�������������������ת���������棬�������������˺���Ծ���������𻵡�\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2����ֹδ������ʹ����ҡ��������\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3����������ʹ��Ӧ���ء����շ����͹����йؾ�������ʹ�ù涨�����ڶԾ���������ά��������ʹ���������ڴ�����������״̬���羯�����������ϲ���������ת��Ҫ�����о����ʦ������ά�޻�ֱ����ù�˾��ϵ�����\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��;\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;������ͻ���¼�Ӧ��Ԥ��,���ƷЯ������.��Ҫ���ڷ�Ѵ�����֡�ɭ�����������Ӿ������ʡ���������ҵ����Ӧ��ͻ���¼�����ʹ��.\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;�����ϵ��ô����衢����˻�и�֮�������ʷ��ɭ�ַ��𡢷��־��֡�����ҰӪʱ������ͨѶ�жϵ�ʱ����ҡ�����������ǵ�һʱ���ҵ��Է�����¥��������ʱ������ϵͳһ�����˹��ϣ�������ҡ������ͬ�����Խ�����⣬���ܷ���110�ֱ����ϵ�Σ�վ�������ʱ��֪ͨ¥����ԱΣ�յ����٣�Ѹ�ٳ��루¥���䱸����ÿ500ƽ����һ̨����¥�ڻ���ϲ�����Ƶ��\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "<br />\n" +
                "</div>";*/

        String[] imgSrcArr= new String[]{
        "http://img005.hc360.cn/k3/M0D/32/1F/wKhQv1exUW-ECqrDAAAAAGarEec627.jpg",
        "http://img004.hc360.cn/k1/M0C/30/40/wKhQwFexUGeEXKnjAAAAAIwY7Ic945.jpg",
        "http://img001.hc360.cn/k1/M06/C1/53/wKhQw1fBibuEMLuKAAAAAG0jDaU944.jpg..100x100.jpg",
        "http://img002.hc360.cn/k3/M02/B1/72/wKhQv1fBic6EQ7-6AAAAAO2Q2No689.jpg..100x100.jpg",
        "http://img008.hc360.cn/k1/M06/C1/54/wKhQw1fBicWEaRsfAAAAAKcAWCU468.jpg..100x100.jpg"};

        /*String[] imgSrcArr= new String[]{
                "https://www.baidu.com/img/bd_logo1.png"};*/

        for(String imgSrc:imgSrcArr){
            float[] imgWH=BusinCompleCaluteNewUtils.obtainImageWH(imgSrc, fss);
            System.out.println("imgSrc="+imgSrc+",w:"+imgWH[0]+",h:"+imgWH[1]);
        }

        System.out.println("Test end....");
    }
}
