package com.leyou.common.utils.exception;

import com.leyou.common.utils.enums.ExceptionEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LyException extends RuntimeException {
    private ExceptionEnums exceptionEnums;

}
