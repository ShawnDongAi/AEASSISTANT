package com.zzn.aenote.http.file;

import com.oreilly.servlet.multipart.FileRenamePolicy;

public interface FilePolicy extends FileRenamePolicy {

	/**
	 * 文件保存路径策略
	 */
	public String getSavePath();

	/**
	 * 文件名前缀策略
	 */
	public String getFilePrex();

	/**
	 * 设置文件保存路径策略
	 */
	public void setSavePath(String savePath);

	/**
	 * 设置文件前缀策略
	 */
	public void setFilePrex(String filePrex);

}
