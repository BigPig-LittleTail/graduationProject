package hit.zhou.spyder;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Deprecated
public class LoyalSpyderProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
//            .addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//            .addHeader("Accept-Encoding","gzip, deflate")
//            .addHeader("Accept-Language","zh-CN,zh;q=0.9")
//            .addHeader("Cache-Control","max-age=0")
//            .addHeader("Connection","keep-alive")
//            .addHeader("Host","www.moj.gov.cn")
//            .addHeader("Referer","http://www.moj.gov.cn/Department/content/2019-01/17/592_227082.html")
//            .addHeader("Upgrade-Insecure-Requests","1")
//            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
    @Override
    public void process(Page page) {

//        page.putField("content",page.getHtml().xpath("//div[@class='con']/span//p/span/text() | //div[@class='con']/span//p/strong/text() | //div[@class='con']/span//p/text()").all());
        page.putField("content",page.getHtml().xpath("//div[@class='con']/span//text()"));


        System.out.println(page.getResultItems().get("content").toString());

//        List<String> list = (List<String>) page.getResultItems().get("content");
//
//
//        for(String s:list){
//            System.out.println(s);
//        }
    }

    @Override
    public Site getSite() {
        return site;
    }


}
