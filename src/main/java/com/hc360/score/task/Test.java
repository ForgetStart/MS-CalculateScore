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
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手摇警报器无需电源，可以在无电源支持的都场所提供一种有效的报警。只需通过人工用手顺时针方向摇动手柄，速度达到初级（50-80转/分）就可达到理想的报警效果，发出的声音尖锐，震撼力大、穿透力强。警报声音大小取决于手柄摇动的速度，新款手摇警报在音窗口设置档板快门机制，可以迅速关闭音窗口，阻断声音，产生不同的信号。\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手摇警报器体积小、重量轻、便于携带、安装方便。不需要任何工具。只需0.15平方的面种和人站立的地方就可以正常操作了，流动性作业非常强。民防、部队、看守所、监狱、矿山、机场、草场、油库、仓库、宾馆、酒店等企业单位在发生危急单位在发生危急情况下，使用的一种理想报警设备。\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手摇警报器的用途：\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;多用于突发事件应急预警,其产品携带方便.主要用于防汛、抗灾、森林消防、部队军需物资、公共及工业场合应对突发事件报警使用.\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手摇警报器的原理\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手摇警报器是通过人工转动摇柄，经齿轮箱的传动逐级加速，再经过传动块带动鸣轮高速运转。鸣轮在进行高速运转过程中，对吸收的空气产生高速高压向定轮的固定窗口同时挤出而产生的共鸣，从而起到报警的作用。\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;因为是手摇警报器，手摇的初级转速决定了警报器的声响效果。转速和声响效果是成正比的，相对而言，转速越高则声音越大。人与人之间来摇动警报器是有区别的。人们的高矮程度、力气的大小、摇动的快慢等都是影响警报器声响效果的直接因素，所以我们在这里作了宏观规定，摇动的初级转速不能小于90r/min，否则就达不到应有的声响效果。\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;安全使用与维护保养\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1、各型号手摇警报器地操作只需要一个人就可以正常工作了。警报器在工作时，里面的鸣轮在高速运转，一般都在2000r/min以上，所以千万不要将手指或其它异物伸进正在旋转的鸣轮里面，否则会造成人身伤害或对警报器造成损坏。\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、禁止未成年人使用手摇警报器。\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3、警报器地使用应遵守《防空法》和国家有关警报器地使用规定，定期对警报器进行维护保养，使警报器长期处于正常工作状态。如警报器发生故障不能正常运转，要安排有经验的师傅进行维修或直接与该公司联系解决。\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用途\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;多用于突发事件应急预警,其产品携带方便.主要用于防汛、抗灾、森林消防、部队军需物资、公共及工业场合应对突发事件报警使用.\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t</p>\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                "\t<p>\n" +
                "\t\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告别古老的敲打铁盆、高声嘶叫告之险情的历史。森林防火、防灾救灾、户外野营时长出现通讯中断的时候，手摇警报器让你们第一时间找到对方！大楼发生险情时，智能系统一旦出了故障，有了手摇警报器同样可以解决问题，它能发出110分贝以上的危险警报，及时的通知楼内人员危险的来临，迅速撤离（楼内配备建议每500平方米一台，因楼内会阻断部分音频）\n" +
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
