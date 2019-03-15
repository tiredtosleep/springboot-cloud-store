package com.leyou.search.client;


import com.leyou.item.pojo.Category;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Autowired
    CategoryClient categoryClient;

    @Test
    public void queryCategoryByIds() {
        List<Category> list = categoryClient.queryCategoryByIds(Arrays.asList(1L, 2L, 3L));
        Assert.assertEquals(3, list.size());
        for (Category category : list) {
            System.out.println("category = "+category);
        }

    }
}
