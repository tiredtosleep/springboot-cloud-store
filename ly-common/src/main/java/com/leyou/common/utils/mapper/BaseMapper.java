package com.leyou.common.utils.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;


/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:定义一个批量处理的mapper
 * @author:cxg
 * @Date:${time}
 */
@RegisterMapper
public interface BaseMapper<T> extends Mapper<T>,IdListMapper<T,Long>,InsertListMapper<T> {
}
