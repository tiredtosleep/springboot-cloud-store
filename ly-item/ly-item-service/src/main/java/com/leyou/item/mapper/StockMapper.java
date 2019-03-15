package com.leyou.item.mapper;

import com.leyou.common.utils.mapper.BaseMapper;
import com.leyou.item.pojo.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;


/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public interface StockMapper extends BaseMapper<Stock> {
    @Update("update tb_stock set stock = stock- #{num} where sku_id=#{id} and stock>=#{num}")
    int decreaseStock(@Param("id") Long id, @Param("num") Integer num);
}
