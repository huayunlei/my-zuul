package com.ihomefnt.sky.common.http; 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**

import com.wordnik.swagger.annotations.ApiModel;
 * Created by shulong on 15-1-9.
 */

/**
 * Http base response, the protocol between server and app client
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpBaseResponse<T> implements Serializable {
	private static final long serialVersionUID = -3385062289989979893L;
	
	private Long code; //ret code, 0x00: business success; others: error code;
	
    private Object ext;//Java bean(must conform Java bean standard), extra data; will be converted to json string when return to client
	
    private T obj;//Java bean, extra data

}
