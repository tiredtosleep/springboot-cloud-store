package com.leyou.page.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PageServiceTest {

    @Autowired
    private PageService pageService;

   @Test
    public void createHtml() {
        pageService.createHtml(141L);
    }
}