package com.zzn.aenote.http.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

@SuppressWarnings("unchecked")
public final class StringUtil {
	public static String ELLIPSES = "&#133";

	public static final String EMPTY_STRING = "";

	
	public static boolean isEmpty(Object data) {
		return data == null || data.toString().trim().equals("")
				|| data.toString().trim().equals("null");
	}
   
    public static boolean toBoolean(String theString) {
		if (theString == null) {
			return false;
		}

		theString = theString.trim();
		if (theString.equalsIgnoreCase("y") || theString.equalsIgnoreCase("yes") || theString.equalsIgnoreCase("true")
				|| theString.equalsIgnoreCase("1")) {
			return true;
		}

		return false;
	}

	
	public static String replace(String s, String sub, String with) {
		int c = 0;
		int i = s.indexOf(sub, c);

		if (i == -1) {
			return s;
		}

		StringBuffer buf = new StringBuffer(s.length() + with.length());
		do {
			buf.append(s.substring(c, i));
			buf.append(with);
			c = i + sub.length();
		} while ((i = s.indexOf(sub, c)) != -1);

		if (c < s.length()) {
			buf.append(s.substring(c, s.length()));
		}

		return buf.toString();
	}

	
	public static String xmlEscape(String s) {
		int length = s.length();
		StringBuffer fsb = new StringBuffer(length);

		for (int i = 0; i < length; i++) {
			fsb = printEscaped(s.charAt(i), fsb);
		}

		return fsb.toString();
	}

	
	public static String truncate(String str, int len) {
		String result = str;
		if (str.length() > len) {
			result = str.substring(0, len) + ELLIPSES;
		}
		return result;
	}

	public static int compareNumber(String num1, String num2) throws ParseException {
		BigDecimal dec1 = new BigDecimal(num1);
		BigDecimal dec2 = new BigDecimal(num2);
		return dec1.compareTo(dec2);
	}

	
	public static boolean isAlphaNumeric(String s) {
		return isAlphaNumeric(s, "");
	}

	
	public static boolean isAlphaNumeric(String str, String otherChars) {
		String alphaNum = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + otherChars;
		for (int i = 0; i < str.length(); i++) {
			if (alphaNum.indexOf(str.charAt(i)) == -1) {
				return false;
			}
		}
		return true;
	}

	public static boolean isDecimal(String s) {
		try {
			new BigDecimal(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isInteger(String str) {
		try {
			new BigInteger(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	public static boolean isNumber(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static int isEmail(String email) {
		StringTokenizer st = new StringTokenizer(email, "@");

		if (st.countTokens() != 2) {
			return 1;
		}

		st.nextToken();
		if (st.nextToken().indexOf(".") == -1) {
			return 2;
		}

		return 0;
	}
	
	
	public static List string2List(String s) {
		return string2List(s, ",");
	}

	
	public static List string2List(String s, String sep) {
		return string2List(s, sep, s != null ? s.length() : Integer.MAX_VALUE);
	}

	public static List string2List(String s, String sep, int maxSize) {
		List l = null;
		if (s != null) {
			l = new Vector();
			for (int i = 0; i < maxSize;) {
				int index = s.indexOf(sep, i);
				String token;
				if (index != -1) {
					token = s.substring(i, index);
				} else {
					token = s.substring(i);
				}
				if (token.length() > 0 && !token.equals(sep)) {
					l.add(token.trim());
				}
				i += token.length() + sep.length();
			}
		}
		return l;
	}

	/**
	 * ����ĸСд
	 * 
	 * @param str
	 *            ҪСд����ĸ���ַ�
	 * @return String
	 */
	public static String unUpperFirstChar(String str) {
		StringBuffer fsb = new StringBuffer();

		fsb.append(Character.toLowerCase(str.charAt(0)));
		fsb.append(str.substring(1));
		return fsb.toString();
	}

	/**
	 * ����ĸ��д
	 * 
	 * @param str
	 *            Ҫ��д����ĸ���ַ�
	 * @return String
	 */
	public static String upperFirstChar(String str) {
		StringBuffer fsb = new StringBuffer();

		fsb.append(Character.toTitleCase(str.charAt(0)));
		fsb.append(str.substring(1));
		return fsb.toString();
	}

	/**
	 * ��һ���ַ���n�����һ���´�
	 * 
	 * @param str
	 *            Ҫ���������ַ�
	 * @param n
	 *            ��������
	 * @return String
	 */
	static public String repeatString(String str, int n) {
		StringBuffer buffer = new StringBuffer();

		int val = n * str.length();
		if (val > buffer.capacity()) {
			buffer.ensureCapacity(val);
		}

		for (int i = 0; i < n; i++) {
			buffer.append(str);
		}
		return buffer.toString();

	}

	/**
	 * ���ַ�ǰ��ԳƼӿո�,ʹ�ﵽ��Ҫ�ĳ���,��"abc"���" abc "
	 * 
	 * @param str
	 *            String str string to center padding
	 * @param n
	 *            ��Ҫ���ַ�ĳ���
	 * @return String Result
	 */
	static public String centerPad(String str, int n) {
		return centerPad(str, n, " ");
	}

	/**
	 * ���ַ�ǰ��ԳƼ�ָ�����ַ�,ʹ�ﵽ��Ҫ�ĳ���,��"abc"���"***abc***"
	 * 
	 * @param str
	 *            String str string to pad with
	 * @param n
	 *            ��Ҫ���ַ�ĳ���
	 * @param delim
	 *            Ҫ��ӵ��ַ�
	 * @return String result of the center padding
	 */
	static public String centerPad(String str, int n, String delim) {
		int sz = str.length();
		int p = n - sz;
		if (p < 1) {
			return str;
		}
		str = leftPad(str, sz + p / 2, delim);
		str = rightPad(str, n, delim);
		return str;
	}

	/**
	 * ���ַ�����ָ�����ַ�,ʹ�ﵽ��Ҫ�ĳ���,��"abc"���"abc***"
	 * 
	 * @param str
	 *            String
	 * @param n
	 *            ��Ҫ���ַ�ĳ���
	 * @param delim
	 *            Ҫ��ӵ��ַ�
	 * @return String padding string
	 */
	static public String rightPad(String str, int n, String delim) {
		int sz = str.length();
		n = (n - sz) / delim.length();
		if (n > 0) {
			str += repeatString(delim, n);
		}
		return str;
	}

	/**
	 * ���ַ����ӿո�,ʹ�ﵽ��Ҫ�ĳ���,��"abc"���"abc "
	 * 
	 * @param str
	 *            String
	 * @param n
	 *            ��Ҫ���ַ�ĳ���
	 * @return String
	 */
	static public String rightPad(String str, int n) {
		return rightPad(str, n, " ");
	}

	/**
	 * ���ַ�ǰ��ӿո�,ʹ�ﵽ��Ҫ�ĳ���,��"abc"���" abc"
	 * 
	 * @param str
	 *            String
	 * @param n
	 *            ��Ҫ���ַ�ĳ���
	 * @return String
	 */
	static public String leftPad(String str, int n) {
		return leftPad(str, n, " ");
	}

	/**
	 * ���ַ�ǰ���ָ�����ַ�,ʹ�ﵽ��Ҫ�ĳ���,��"abc"���" abc"
	 * 
	 * @param str
	 *            String
	 * @param n
	 *            ��Ҫ���ַ�ĳ���
	 * @param delim
	 *            Ҫ��ӵ��ַ�
	 * @return String
	 */
	static public String leftPad(String str, int n, String delim) {
		int sz = str.length();
		n = (n - sz) / delim.length();
		if (n > 0) {
			str = repeatString(delim, n) + str;
		}
		return str;
	}

	/**
	 * ����ַ�Ϊ��,��""��ʾ
	 */
	public static String nullToEmpty(String s) {
		if (s == null || s.equalsIgnoreCase("null")) {
			return "";
		} else {
			return s;
		}
	}

	/**
	 * ����ַ�Ϊ��,��"0"��ʾ
	 */
	public static String nullToZero(Object s) {
		if (s == null || (String.valueOf(s)).equalsIgnoreCase("null"))
			return "0";
		else
			return String.valueOf(s);
	}

	/**
	 * ��ͷ��ȡһ�����ȵ��Ӵ�,���Ϊ��,����Ҫ�ĳ��Ȳ���,����null
	 * 
	 * @param str
	 *            ԭʼ�ַ�
	 * @param lg
	 *            ��Ҫ���Ӵ�����
	 * @return �Ӵ�
	 */
	public static String substring(String str, int lg) {
		return substring(str, 0, lg);
	}

	/**
	 * ��ȡ�Ӵ�,���Ϊ��,����Ҫ�ĳ��Ȳ���,����null
	 * 
	 * @param str
	 *            ԭʼ�ַ�
	 * @param start
	 *            �Ӵ��Ŀ�ʼλ��
	 * @param end
	 *            �Ӵ��Ľ���λ��
	 * @return �Ӵ�
	 */
	public static String substring(String str, int start, int end) {
		if (str == null || str.length() <= start) {
			return null;
		} else if (str.length() >= end) {
			return str.substring(start, end);
		} else {
			return str.substring(start);
		}
	}

	/**
	 * ��ת�ַ�, ��"abc"���"cba"
	 * 
	 * @param str
	 *            Ҫ��ת���ַ�
	 * @return ��ת����ַ�
	 */
	public static String reverseString(String str) {
		StringBuffer fsb = new StringBuffer();
		try {
			fsb.append(str);
			return fsb.reverse().toString();
		} finally {

		}
	}

	/**
	 * ��ת�ַ�
	 */
	public static String swapCase(String str) {
		int sz = str.length();
		StringBuffer buffer = new StringBuffer();

		try {
			if (sz > buffer.capacity()) {
				buffer.ensureCapacity(sz);
			}
			boolean whiteSpace = false;
			char ch = 0;
			char tmp = 0;
			for (int i = 0; i < sz; i++) {
				ch = str.charAt(i);
				if (Character.isUpperCase(ch)) {
					tmp = Character.toLowerCase(ch);
				} else if (Character.isTitleCase(ch)) {
					tmp = Character.toLowerCase(ch);
				} else if (Character.isLowerCase(ch)) {
					if (whiteSpace) {
						tmp = Character.toTitleCase(ch);
					} else {
						tmp = Character.toUpperCase(ch);
					}
				}
				buffer.append(tmp);
				whiteSpace = Character.isWhitespace(ch);
			}
			return buffer.toString();
		} finally {
		}
	}

	/**
	 * ����һ������ַ�
	 * 
	 * @param count
	 *            ��Ҫ���ַ�ĳ���
	 * @return ����ַ�
	 */
	public static String random(int count) {
		return random(count, false, false);
	}

	/**
	 * ����һ�����ASCII���ַ�,��1c6x^9X7\G
	 * 
	 * @param count
	 *            ��Ҫ���ַ�ĳ���
	 * @return ����ַ�
	 */
	public static String randomAscii(int count) {
		return random(count, 32, 127, false, false);
	}

	/**
	 * ����һ������ַ�, ֻ����ĸ���, ��lKQpeyoACJ
	 * 
	 * @param count
	 *            ��Ҫ���ַ�ĳ���
	 * @return ����ַ�
	 */
	public static String randomAlphabetic(int count) {
		return random(count, true, false);
	}

	/**
	 * ����һ������ַ�, ֻ����ֵ����ĸ���, ��yFBPfo9oF9
	 * 
	 * @param count
	 *            ��Ҫ���ַ�ĳ���
	 * @return ����ַ�
	 */
	public static String randomAlphanumeric(int count) {
		return random(count, true, true);
	}

	/**
	 * ����һ������ַ�, ֻ����ֵ���, ��8879114164
	 * 
	 * @param count
	 *            ��Ҫ���ַ�ĳ���
	 * @return ����ַ�
	 */
	public static String randomNumeric(int count) {
		return random(count, false, true);
	}

	/**
	 * ����һ������ַ�
	 * 
	 * @param count
	 *            ��Ҫ���ַ�ĳ���
	 * @param set
	 *            ��ɵ��ַ�
	 * @return ����ַ�
	 */
	public static String random(int count, String set) {
		return random(count, set.toCharArray());
	}

	/**
	 * �滻����html��tag, ����ܳ����Ƿ񳬹���ϵͳ�����,�Է�ֹ�ڿͳ��ַ�Ĺ���
	 * 
	 * @param strSrc
	 *            Ҫ�滻���ַ�
	 * @param lngStrLen
	 *            ϵͳ�������󳤶�
	 */
	public static String getFilterStr(String strSrc, int lngStrLen) {
		String strRst = strSrc;
		// �滻���Ϸ��ַ�
		strRst = replace(strRst, "<", "&lt;");
		strRst = replace(strRst, ">", "&gt;");
		strRst = replace(strRst, " ", "&nbsp;");
		strRst = replace(strRst, "\n", "<br>");
		strRst = replace(strRst, "\r", "");

		// ��ȡ����Ҫ�ĳ���
		if (lngStrLen > 0) {
			if (lngStrLen > strRst.length()) {
				lngStrLen = strRst.length();
			}
			strRst = strRst.substring(0, lngStrLen);
		}
		return strRst;
	}

	/**
	 * ����һ������ַ�
	 * 
	 * @param count
	 *            ��Ҫ���ַ�ĳ���
	 * @param letters
	 *            �Ƿ���Ҫ��ĸ
	 * @param numbers
	 *            �Ƿ���Ҫ����
	 * @return ����ַ�
	 */
	private static String random(int count, boolean letters, boolean numbers) {
		return random(count, 0, 0, letters, numbers);
	}

	/**
	 * ����һ������ַ�
	 * 
	 * @param count
	 *            ��Ҫ���ַ�ĳ���
	 * @param start
	 *            int minimum 'value' of the character
	 * @param end
	 *            maximum 'value' of the character
	 * @param letters
	 *            �Ƿ���Ҫ��ĸ
	 * @param numbers
	 *            �Ƿ���Ҫ����
	 * @return ����ַ�
	 */
	private static String random(int count, int start, int end, boolean letters, boolean numbers) {
		return random(count, start, end, letters, numbers, null);
	}

	/**
	 * Create a random numeric string where you have control over size, and
	 * whether you want letters, numbers, as well as ANSI minimum and maximum
	 * values of the characters.
	 * 
	 * @param count
	 *            the size of the string
	 * @param start
	 *            int minimum 'value' of the character
	 * @param end
	 *            maximum 'value' of the character
	 * @param letters
	 *            �Ƿ���Ҫ��ĸ
	 * @param numbers
	 *            �Ƿ���Ҫ����
	 * @param set
	 *            the set of possible characters that you're willing to let the
	 *            string contain. may be null if all values are open.
	 * @return ����ַ�
	 */
	private static String random(int count, int start, int end, boolean letters, boolean numbers, char[] set) {
		if ((start == 0) && (end == 0)) {
			end = (int) 'z';
			start = (int) ' ';
			if (!letters && !numbers) {
				start = 0;
				end = Integer.MAX_VALUE;
			}
		}
		Random rnd = new Random();
		StringBuffer buffer = new StringBuffer();
		try {
			int gap = end - start;
			while (count-- != 0) {
				char ch;
				if (set == null) {
					ch = (char) (rnd.nextInt(gap) + start);
				} else {
					ch = set[rnd.nextInt(gap) + start];
				}
				if ((letters && numbers && Character.isLetterOrDigit(ch)) || (letters && Character.isLetter(ch))
						|| (numbers && Character.isDigit(ch)) || (!letters && !numbers)) {
					buffer.append(ch);
				} else {
					count++;
				}
			}
			return buffer.toString();
		} finally {
		}
	}

	/**
	 * ����һ������ַ�
	 * 
	 * @param count
	 *            ��Ҫ���ַ�ĳ���
	 * @param set
	 *            ��ɵ��ַ�
	 * @return ����ַ�
	 */
	private static String random(int count, char[] set) {
		return random(count, 0, set.length - 1, false, false, set);
	}

	/**
	 * Formats a particular character to something workable in xml Helper to
	 * xmlEscape()
	 * 
	 * @param ch
	 *            the character to print.
	 * @param fsb
	 *            The StringBuffer to add this to.
	 * @return a StringBuffer that is modified
	 */
	protected static StringBuffer printEscaped(char ch, StringBuffer fsb) {
		String charRef;

		// If there is a suitable entity reference for this
		// character, print it. The list of available entity
		// references is almost but not identical between
		// XML and HTML.
		charRef = getEntityRef(ch);

		if (charRef != null) {
			fsb.append('&');
			fsb.append(charRef);
			fsb.append(';');
		} else if ((ch >= ' ' && ch < 0xFF && ch != 0xF7) || ch == '\n' || ch == '\r' || ch == '\t') {
			// If the character is not printable, print as character reference.
			// Non printables are below ASCII space but not tab or line
			// terminator, ASCII delete, or above a certain Unicode threshold.
			if (ch < 0x10000) {
				fsb.append(ch);
			} else {
				fsb.append((char) ((((int) ch - 0x10000) >> 10) + 0xd800));
				fsb.append((char) ((((int) ch - 0x10000) & 0x3ff) + 0xdc00));
			}
		} else {
			fsb.append("&#x");
			fsb.append(Integer.toHexString(ch));
			fsb.append(';');
		}

		return fsb;
	}
	protected static String getEntityRef(int ch) {
		// Encode special XML characters into the equivalent character
		// references.
		// These five are defined by default for all XML documents.
		switch (ch) {
		case '<':
			return "lt";
		case '>':
			return "gt";
		case '"':
			return "quot";
		case '\'':
			return "apos";
		case '&':
			return "amp";
		}
		return null;
	}
	
  public static int toInt(String str) {
    int lpResult = 0;
    try {
      lpResult = Integer.parseInt(str);
    } catch(NumberFormatException notint) {}
    return lpResult;
  }
  public static float toFloat(String str) {
    float lpResult = 0;

    try {
      lpResult = Float.parseFloat(nullToZero(str));
    }catch(NumberFormatException nfe){}
    return lpResult;
  }

  public static short toShort(String str) {
    short lpResult = 0;

    try {
      lpResult = Short.parseShort(nullToZero(str));
    }catch(NumberFormatException nfe){}
    return lpResult;
  }
  public static long toLong(String str) {
    long lpResult = 0;

    try {
      lpResult = Long.parseLong(nullToZero(str));
    }catch(NumberFormatException nfe){}
    return lpResult;
  }

  public static double toDouble(String str) {
    double lpResult = 0;

    try {
      lpResult = Double.parseDouble(nullToZero(str));
    }catch(NumberFormatException nfe) {}
    return lpResult;
  }
  public static BigDecimal toBigDecimal(String str){
    java.math.BigDecimal lpReturnValue = new java.math.BigDecimal( (long) 0);
    try{
      lpReturnValue = new BigDecimal(nullToZero(str));
    }catch(NumberFormatException nfe) {
      lpReturnValue = new BigDecimal((long) 0);
    }catch(Exception nfe) {
      lpReturnValue = new BigDecimal((long) 0);
    }
    return lpReturnValue;
  }
  
  public static String intToStr(int lpInt, int lpMaxLength){
    int length, i;
    String returnValue = "";

    length = Integer.toString(lpInt).length();
    if (length < lpMaxLength){
      i = lpMaxLength - length;
      while( i > 0) {
        returnValue = returnValue + "0";
        i--;
      }
      returnValue = returnValue + Integer.toString(lpInt);
    }else{
      returnValue = Integer.toString(lpInt);
    }
   return returnValue;
  }
  
}