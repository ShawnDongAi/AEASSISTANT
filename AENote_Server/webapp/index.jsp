<%@ page language="java" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">
		<title>简正劳务</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="简正劳务app下载">
		<script type="text/javascript">
            /*
             * 智能机浏览器版本信息:
             */
            var browser = {
                versions: function() {
                    var u = navigator.userAgent, app = navigator.appVersion;
                    return {//移动终端浏览器版本信息
                        trident: u.indexOf('Trident') > -1, //IE内核
                        presto: u.indexOf('Presto') > -1, //opera内核
                        webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
                        gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
                        mobile: !!u.match(/AppleWebKit.*Mobile.*/) || !!u.match(/AppleWebKit/), //是否为移动终端
                        ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
                        android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
                        iPhone: u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, //是否为iPhone或者QQHD浏览器
                        iPad: u.indexOf('iPad') > -1, //是否iPad
                        webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
                    };
                }(),
                language: (navigator.browserLanguage || navigator.language).toLowerCase()
            }
            if (browser.versions.ios || browser.versions.iPhone || browser.versions.iPad) {
                window.location="https://itunes.apple.com/cn/app/xxx";
            }
            else if (browser.versions.android) {
                window.location="http://zhj8.aliapp.com/xxx.apk";
            }
//			document.writeln("语言版本: " + browser.language);
//          document.writeln(" 是否为移动终端: " + browser.versions.mobile);
//          document.writeln(" ios终端: " + browser.versions.ios);
//          document.writeln(" android终端: " + browser.versions.android);
//          document.writeln(" 是否为iPhone: " + browser.versions.iPhone);
//          document.writeln(" 是否iPad: " + browser.versions.iPad);
//          document.writeln(navigator.userAgent); 
			if (browser.versions.android) {
				document.location='http://112.124.16.245:8080/AENote_Server/download?platform=0'
			}
			else if (browser.versions.ios) {
				document.location='http://www.baidu.com'
			}
			else if (browser.versions.iPhone) {
				document.location='http://www.baidu.com'
			}
			else if (browser.versions.iPad) {
				document.location='http://www.baidu.com'
			}
			else {
				document.location='http://112.124.16.245:8080/AENote_Server/download?platform=0'
			}
        </script>
	</head>

	<body>
		开始下载简正劳务app...
		<br>
	</body>
</html>
