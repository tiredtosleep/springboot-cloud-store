package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.nio.channels.Pipe;
import java.util.Date;
import java.util.List;

/**
 * SPU商品集
 */
@Table(name = "tb_spu")
@Data
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String title;// 标题
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    //返回字段的时候忽略lastUpdateTime
    @JsonIgnore
    private Boolean valid;// 是否有效，逻辑删除用

    private Date createTime;// 创建时间
    //返回字段的时候忽略lastUpdateTime
    @JsonIgnore
    private Date lastUpdateTime;// 最后修改时间

	@Transient//不是数据库字段的
    private String bname;
    @Transient//不是数据库字段的
    private String cname;

    @Transient
    private List<Sku> skus;

    @Transient
    private SpuDetail spuDetail;// 商品详情
    // 省略getter和setter


}