package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:商品规格组
 * @author:cxg
 * @Date:${time}
 */
@Table(name = "tb_spec_group")
@Data
public class SpecGroup {
    @Id
    @KeySql(useGeneratedKeys=true)
    private Long id;
    private Long cid;
    private String name;
    @Transient
    private List<SpecParam> params; // 该组下的所有规格参数集合
}