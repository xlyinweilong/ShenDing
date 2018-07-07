<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" class="no-js">
    <head>
        <meta charset="utf-8">
        <title>代理查询系统</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">
        <link rel="stylesheet" href="/aladingImg/css/reset.css">
        <link rel="stylesheet" href="/aladingImg/css/supersized.css">
        <link rel="stylesheet" href="/aladingImg/css/style.css">
        <script language="JavaScript" src="/aladingImg/js/tab.js"></script> 
        <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
            <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
    </head>
    <body>

        <c:choose>
            <c:when test="${noRs}">
                <div class="page-container">
                    <h1><img src="/aladingImg/img/gfmpr18tj1431262136.png"/></h1>
                    <div class="rs_show">
                        <table width="100%" border="0" cellpadding="0" cellspacing="0" style="" valign=center>
                            <tr>
                                <td height="45" colspan="5" align="center" style="height:45px; line-height:45px;"><strong>查询结果如下：</strong></td>
                            </tr>            
                            <tr>
                                <td colspan="5" align="center"><h1>
                                        没有您所查询的代理商ID或微信号<br/>请您重新输入</h1>
                                    <p>
                                        &nbsp;<a href="javascript:window.history.go(-1);">返回代理查询页</a>
                                    </p>      
                                </td></tr>      <p></p>
                        </table>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="page-container">
                    <h1><img src="/aladingImg/img/gfmpr18tj1431262136.png"/></h1>
                    <div class="rs_show">
                        <table width="100%" border="0" cellpadding="0" cellspacing="0" style="" valign=center>
                            <tr>
                                <td height="45" colspan="5" align="center" style="height:45px; line-height:45px;"><strong>查询结果如下：</strong></td>
                            </tr>            
                            <tr>
                                <td colspan="5" align="center" style="">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5" align="center" style="">
                                    恭喜，您所查询的经销商是【啊啦叮集团】授权正版经销商，<br />您可以放心购买！！
                                    <p><input id="download" type="button" value="点击下载证书（手机端请点击图片另存为）" onClick="javascript:window.location.href = '${aladingwebSearch.picUrl}'"></p>
                                    <p id="p_cert_img" style="margin:15px auto; width:100%; text-align:center;"><img src="${aladingwebSearch.picUrl}" width="80%" /></p>
                                    <p>
                                        &nbsp;<a href="javascript:window.history.go(-1);">返回代理查询页</a>
                                        <br/>
                                    </p>  
                                </td></tr>
                            <p></p>
                        </table>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
        <!--<div class="foot">Powered by ALIKE微营销推广平台 Copyright © 2007 - 2017 </div>-->
        <script src="/aladingImg/js/jquery-1.8.2.min.js"></script>
        <script src="/aladingImg/js/supersized.3.2.7.min.js"></script>
        <script src="/aladingImg/js/scripts.js"></script>
        <script type="text/javascript">
                                jQuery(function ($) {
                                    $.supersized({
                                        // Functionality
                                        slide_interval: 4000, // Length between transitions
                                        transition: 1, // 0-None, 1-Fade, 2-Slide Top, 3-Slide Right, 4-Slide Bottom, 5-Slide Left, 6-Carousel Right, 7-Carousel Left
                                        transition_speed: 1000, // Speed of transition
                                        performance: 1, // 0-Normal, 1-Hybrid speed/quality, 2-Optimizes image quality, 3-Optimizes transition speed // (Only works for Firefox/IE, not Webkit)

                                        // Size & Position
                                        min_width: 0, // Min width allowed (in pixels)
                                        min_height: 0, // Min height allowed (in pixels)
                                        vertical_center: 1, // Vertically center background
                                        horizontal_center: 1, // Horizontally center background
                                        fit_always: 0, // Image will never exceed browser width or height (Ignores min. dimensions)
                                        fit_portrait: 1, // Portrait images will not exceed browser height
                                        fit_landscape: 0, // Landscape images will not exceed browser width

                                        // Components
                                        slide_links: 'blank', // Individual links for each slide (Options: false, 'num', 'name', 'blank')
                                        slides: [// Slideshow Images
                                            {image: '/aladingImg/img/1.jpg'},
                                            {image: '/aladingImg/img/2.jpg'},
                                            {image: '/aladingImg/img/3.jpg'}
                                        ]
                                    });
                                })
        </script>
    </body>
</html>