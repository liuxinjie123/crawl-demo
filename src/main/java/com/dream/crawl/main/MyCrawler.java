package com.dream.crawl.main;

import com.dream.crawl.link.LinkFilter;
import com.dream.crawl.link.Links;
import com.dream.crawl.page.Page;
import com.dream.crawl.page.PageParserTool;
import com.dream.crawl.page.RequestAndResponseTool;
import com.dream.crawl.util.FileTool;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MyCrawler {

    private static List<String> seedList = Arrays.asList("http://www.baidu.com", "http://www.yimei180.com", "http://www.haowu.com");

    /**
     * 使用种子初始化 URL 队列
     *
     * @param seeds 种子 URL
     * @return
     */
    private void initCrawlerWithSeeds(List<String> seeds) {
        for (String seed : seeds){
            Links.addUnvisitedUrlQueue(seed);
        }
    }


    /**
     * 抓取过程
     *
     * @param seeds
     * @return
     */
    public void crawling(List<String> seeds) {

        //初始化 URL 队列
        initCrawlerWithSeeds(seeds);

        //定义过滤器，提取以 http://www.baidu.com 开头的链接
        LinkFilter filter = new LinkFilter() {
            public boolean accept(String url) {
                for (String seed : seedList) {
                    if (url.startsWith(seed)) {
                        return true;
                    }
                }
                return false;
            }
        };

        //循环条件：待抓取的链接不空且抓取的网页不多于 1000
        while (!Links.unVisitedUrlQueueIsEmpty()  && Links.getVisitedUrlNum() <= 10000) {

            //先从待访问的序列中取出第一个；
            String visitUrl = (String) Links.removeHeadOfUnVisitedUrlQueue();
            if (visitUrl == null){
                continue;
            }

            //根据URL得到page;
            Page page = RequestAndResponseTool.sendRequstAndGetResponse(visitUrl);

            //对page进行处理： 访问DOM的某个标签
            Elements es = PageParserTool.select(page,"a");
            if(!es.isEmpty()){
                System.out.println("下面将打印所有a标签： ");
                System.out.println(es);
            }

            //将保存文件
            FileTool.saveToLocal(page);

            //将已经访问过的链接放入已访问的链接中；
            Links.addVisitedUrlSet(visitUrl);

            //得到超链接
            Set<String> links = PageParserTool.getLinks(page,"img");
            for (String link : links) {
                Links.addUnvisitedUrlQueue(link);
                System.out.println("新增爬取路径: " + link);
            }
        }
    }


    //main 方法入口
    public static void main(String[] args) {
        MyCrawler crawler = new MyCrawler();
        crawler.crawling(seedList);
    }
}
