<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
        <link href="/aladingImg/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <style type="text/css">
            *{margin:0px;padding:0px;}
            .app-content{position:relative;}
            form{width:80%;height:90%;position:absolute;z-index:100;left:10%;top:10%;text-align:center;}
            .bg{width:100%;height:100%;}
        </style>
    </head>
    <body class="app" >
        <div class="app-content">
            <img src="${ALADING_FILE_BG_URL}" class='bg'>
            <form action="/admin/search" method="post" name="FrmSearch" target="_self" id="FrmSearch" onSubmit="return Search()">
                <img src="${ALADING_FILE_LOGO}" style='height:100px;'>
                <input type="text" name="condition" id="UserName" class='key'   placeholder="请在此输入微信号,手机号或授权号" style='width:240px;height:40px;'>

                <button class="btn btn-primary" type="submit" style='width:240px;height:45px;margin-top:20px;'>立 即 查 询</button>	
                <a href="/alading/zuodaili" class="btn btn-large btn-primary disabled" style='width:200px;height:20px;margin-top:20px;background-color:#FFCC00;color:#000;opacity:1;'>申请做代理</a>
                <a href="/alading/quanguotuiguang" class="btn btn-large btn-primary disabled" style='width:200px;height:20px;margin-top:20px;background-color:#FFCC00;color:#000;opacity:1;'>申请全国推广</a>
            </form>
        </div>
        <script type="text/javascript">
            window.onload = function () {
                $(".bg").css("height", $(window).height());
                $(".bg").css("width", $(window).width());
            }
        </script>
    </body>
</html>