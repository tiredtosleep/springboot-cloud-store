package com.leyou.common.utils.vo;

import com.leyou.common.utils.enums.ExceptionEnums;
import lombok.Data;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:异常结果对象
 * @author:cxg
 * @Date:${time}
 */
@Data
public class ExceptionResult {
    private int status;
    private String message;
    private Long timestamp;
    public ExceptionResult(ExceptionEnums em){
        this.status=em.getCode();
        this.message=em.getMsg();
        this.timestamp=System.currentTimeMillis();
    }
}
