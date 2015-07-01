package com.zzn.aenote.http.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzn.aenote.http.vo.BaseRep;

/**
 * Servlet处理接口
 * @author Shawn
 */
public interface CmHandler {

	/**
	 * 业务逻辑处理接口,该接口不抛出任何异常<br>
	 * 当遇到运行期异常由外界进行捕获返回
	 */
	public void doHandler(HttpServletRequest req, HttpServletResponse resp, BaseRep rs) throws Exception;
}
