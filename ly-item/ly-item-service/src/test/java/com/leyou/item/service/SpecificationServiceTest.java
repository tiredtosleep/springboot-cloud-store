package com.leyou.item.service;


import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
public class SpecificationServiceTest {

    @Autowired
    private  SpecificationService specificationService;
    @Test
    public void queryListByCid() {
        List<SpecGroup> specGroups = specificationService.queryGroupByCid(76L);
        for (SpecGroup specGroup : specGroups) {
            System.out.println("------------"+specGroup+"------------");
        }

    }
}