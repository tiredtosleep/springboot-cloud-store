package com.leyou.item.service;

import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class SpecificationService {
    @Autowired
   private   SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;
    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupByCid(Long cid) {
        //查询条件
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        //查询
        List<SpecGroup> list = specGroupMapper.select(group);
        if (CollectionUtils.isEmpty(list)){
          //没查到
            throw  new LyException(ExceptionEnums.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    /**
     * 新增规格组
     * @param
     * @return
     */
    public void addGroup(SpecGroup group) {

        int count = specGroupMapper.insert(group);
        if (count!=1){
            throw new LyException(ExceptionEnums.SPEC_GROUP_SAVE_ERROR);
        }
    }

    /**
     * 根据id删除规格组
     * @param id
     */
    public void deleteGroup(Long id) {
        SpecGroup group = new SpecGroup();
        group.setId(id);
        int delete = specGroupMapper.delete(group);
        if (delete!=1){
            throw new LyException(ExceptionEnums.SPEC_GROUP_DELETE_ERROR);
        }
    }

    /**
     * 修改规格组
     * @param specGroup
     */
    public void editGroup(SpecGroup specGroup) {

        int update = specGroupMapper.updateByPrimaryKeySelective(specGroup);
        if (update!=1){
            throw new LyException(ExceptionEnums.SPEC_GROUP_UPLOAD_ERROR);
        }
    }
    /**
     * 通过规格组的gid查询规格参数
     * 通过商品分类cid查询规格参数
     * required=false：表示不传值的时候给null
     * @param gid 规格组id
     * @param cid 商品分类id
     * @param searching 是否用于搜索关键字
     * @param generic   是否是sku通用属性
     * @return
     */
    public List<SpecParam> queryParamByList(Long gid,Long cid,Boolean searching,Boolean generic) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);
        param.setGeneric(generic);
        List<SpecParam> list = specParamMapper.select(param);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnums.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

    /**
     * 添加规格参数
     * @param specParam
     * @return
     */
    public void addParam(SpecParam specParam) {
        int insert = specParamMapper.insert(specParam);
        if (insert!=1){
            throw  new LyException(ExceptionEnums.SPEC_PARAM_ADD_ERROR);
        }
    }

    /**
     * 规格参数修改
     * @param specParam
     * @return
     */
    public void editParam(SpecParam specParam) {
        int update = specParamMapper.updateByPrimaryKeySelective(specParam);
        if (update!=1){
            throw  new LyException(ExceptionEnums.SPEC_PARAM_UPLOAD_ERROR);
        }
    }


    /**
     * 根据id删除规格参数
     * @param id
     * @return
     */
    public void deleteParam(Long id) {
        SpecParam param = new SpecParam();
        param.setId(id);
        int delete = specParamMapper.delete(param);
        if (delete!=1){
            throw  new LyException(ExceptionEnums.SPEC_PARAM_DELETE_ERROR);
        }
    }


    public List<SpecGroup> queryListByCid(Long cid) {
        //查询规格组
        List<SpecGroup> specGroups = queryGroupByCid(cid);
        //查询当前分类下的规格参数
        List<SpecParam> specParams = queryParamByList(null, cid, null, null);
        //先把规格参数变成map，map的key是规格参数组id，map的值是组下的所有参数
        Map<Long,List<SpecParam>> map=new HashMap<>();
        for (SpecParam param : specParams) {
            if (!map.containsKey(param.getGroupId())){
                //这个组id在map中不存在，新增一个list
                map.put(param.getGroupId(),new ArrayList<>());
            }
            map.get(param.getGroupId()).add(param);
        }

        //填充param到group
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }
        return specGroups;
    }
}
