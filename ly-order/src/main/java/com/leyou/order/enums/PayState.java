package com.leyou.order.enums;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public enum PayState {
    NOT_PAY(0),SUCCESS(1),FAIL(2);
    PayState(int value){
        this.value=value;
    }
    int value;
    public int getValue(){
        return value;
    }
}
