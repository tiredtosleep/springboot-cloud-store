package com.leyou.common.utils.advice;

import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.common.utils.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@ControllerAdvice
public class CommonExceptionHander {
    //拦截ly-item-service的ItemController的RuntimeException()方法
    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handleException(LyException e){

        return ResponseEntity.status(e.getExceptionEnums().getCode())
                .body(new ExceptionResult(e.getExceptionEnums()));
    }
}
