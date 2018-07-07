<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<!-- saved from url=(0032)http://www.cbsscx.com/index.php/Home/index/addui -->
<html class=""><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
        <link href="/aladingImg/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <style type="text/css">
            *{margin:0px;padding:0px;}
            .app-content{position:relative;}
            form{width:96%;height:75%;position:absolute;z-index:100;left:2%;top:25%;text-align:center;}
            .bg{width:100%;height:100%;}
            .input1{width:180px;}
            form table td{width:85%;}
            form table td.left{width:40%;text-align:right;}
            .sqtj{width:95%;text-align:left;font-size:14px;line-height:20px;margin-top:10px;}
        </style>
        <style type="text/css">.fancybox-margin{margin-right:17px;}</style></head>
    <body class="app">
        <div class="app-content">
            <img src="${ALADING_FILE_BG_URL}" class="bg">
            <form action="/admin/submit_result" method="post" name="Frm" id="Frm" onsubmit="return Search();">
                <input type="hidden" name="a" value="APPLY" />
                <h1 style="color:#E0E0E0">代理商申请</h1>
                <center>
                    <table>
                        <tbody><tr>
                                <td class="left" style="color:#E0E0E0">您的姓名：</td>
                                <td><input type="text" name="name" id="UserName" class="input1"></td>
                            </tr>
                            <tr>
                                <td class="left" style="color:#E0E0E0">您的微信：</td>
                                <td><input type="text" name="wecatCode" id="UserNum" class="input1"></td>
                            </tr>
                            <tr>
                                <td class="left" style="color:#E0E0E0">您的手机：</td>
                                <td><input type="text" name="mobile" id="UserTest" class="input1"></td>
                            </tr>
                            <tr>
                                <td class='left' style="color:#E0E0E0">产品名称：</td>
                                <td>
                                    <input type="text" name="product" class='input1'>
                                </td>
                            </tr>
                            <tr>
                                <td class='left' style="color:#E0E0E0">微信所在平台名：</td>
                                <td><input type="text" name="platform"  id="XcDepID" class='input1'></td>
                            </tr>
                            <tr>
                                <td colspan="2" style="height:50px;"><button class="btn btn-primary" type="submit" style="padding-left: 18px;padding-right: 18px;width:100%;height:50px;">申请提交</button></td>
                            </tr>
                        </tbody></table>
                </center>
                <div class="sqtj">
                </div>
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