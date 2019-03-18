package hit.zhou.spyder;

import us.codecraft.webmagic.Spider;

@Deprecated
public class SpyderTest {

    public static void main(String[] args) {
        /*添加爬取的url链接，开启5个线程爬取*/
        Spider spider = Spider.create(new LoyalSpyderProcessor())
                .addUrl("http://www.moj.gov.cn/Department/content/2019-01/17/592_227081.html")
                .thread(5);
        /*爬虫启动*/
        spider.run();
    }
}
