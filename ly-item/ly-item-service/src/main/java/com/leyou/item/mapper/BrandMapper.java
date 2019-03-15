package com.leyou.item.mapper;

import com.leyou.common.utils.mapper.BaseMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public interface BrandMapper extends BaseMapper<Brand> {
    @Insert("insert into tb_category_brand (category_id,brand_id) value (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Delete("delete from tb_category_brand where brand_id=#{bid}")
    void deleteByBid(@Param("bid") Long bid);

    @Select("SELECT * FROM tb_brand WHERE id IN (SELECT brand_id FROM tb_category_brand WHERE category_id = #{cid})")
    List<Brand> queryBrandByCid(@Param("cid") Long cid);

}
