package com.leyou.item.service;

import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 根据父节点查询商品分类
     * ResponseEntity<List<Category>>：是restful风格
     * @param pid
     * @return
     */
    public List<Category> queryCategoryListByPid(Long pid) {
        //查询条件，mapper会把对象中的非空属性作为查询条件
        Category category = new Category();
        category.setParentId(pid);
        List<Category> list = categoryMapper.select(category);
        if(CollectionUtils.isEmpty(list)){
            //自定义的错误返回
            throw new  LyException(ExceptionEnums.CATEGORY_NOT_FOND);
        }
        return list;
    }
    /**
     * 根据品牌id查询商品分类
     * @param bid
     * @return
     */
    public List<Category> queryCategoryListByBid(Long bid) {
        List<Category> list = categoryMapper.queryCategoryListByBid(bid);
        if (CollectionUtils.isEmpty(list)){
            throw  new  LyException(ExceptionEnums.CATEGORY_NOT_FOND);
        }
        return list;
    }

    /**
     * 根据ids集合查询商品分类
     * @param ids
     * @return
     */
    public List<Category> queryByIds(List<Long> ids){
        List<Category> list = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)){
            throw  new  LyException(ExceptionEnums.CATEGORY_NOT_FOND);
        }
        return list;
    }

    public List<Category> queryAllByCid3(Long id) {
        Category c3 = categoryMapper.selectByPrimaryKey(id);
        Category c2 = categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1,c2,c3);
    }
}
