package com.zzn.aenote.http.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;

import com.zzn.aenote.http.AppException;

public class XMLParseUtils {

	private Document doc;

	private Element root;

	public final List getChildren(final Element es, final String qpath) {
		List list = es.getChildren(qpath);
		return list;
	}

	public final List getListByXPath(final Element es, final String xpath) {
		List list;
		try {
			list = XPath.selectNodes(es, xpath);
		} catch (JDOMException e) {
			throw new AppException("XML解析错误", e);
		}
		return list;
	}

	// TODO JDOM 0.9的版本不支持QPATH 要1.0以上版本才支持QPATH BY AXIN

	public final Element getRootElement(String xmlStr) {
		StringReader read = new StringReader(xmlStr);
		InputSource source = new InputSource(read);
		SAXBuilder sb = new SAXBuilder();
		try {
			doc = sb.build(source);
		} catch (JDOMException e) {
			throw new AppException("XML解析错误.", e);
		} catch (IOException e) {
			throw new AppException("XML解析错误", e);
		}
		root = doc.getRootElement();
		return root;
	}

	public final Element getRootElement(final File file) {
		SAXBuilder sb = new SAXBuilder();
		try {
			doc = sb.build(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new AppException("XML解析错误", e);
		} catch (JDOMException e) {
			throw new AppException("XML解析错误", e);
		} catch (IOException e) {
			throw new AppException("XML解析错误", e);
		}
		root = doc.getRootElement();
		return root;
	}

	public final Element getRootElement(FileInputStream fin) {
		SAXBuilder sb = new SAXBuilder();
		try {
			doc = sb.build(fin);
		} catch (FileNotFoundException e) {
			throw new AppException("XML解析错误", e);
		} catch (JDOMException e) {
			throw new AppException("XML解析错误", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException("XML解析错误", e);
		}
		root = doc.getRootElement();
		return root;
	}

}
