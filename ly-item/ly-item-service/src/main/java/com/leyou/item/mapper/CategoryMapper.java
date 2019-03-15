package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:Category的通用mapper
 * @author:cxg
 * @Date:${time}
 */
public interface CategoryMapper extends Mapper<Category>,IdListMapper<Category,Long>{//<T,PK> T:要查询的pojo，PK:是什么数据类型
    @Select("SELECT * FROM tb_category WHERE id IN (SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> queryCategoryListByBid(@Param("bid") Long bid);
}
