<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en" class="no-js">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1"> 
        <title>市场调查问卷表</title>
        <link rel="stylesheet" type="text/css" href="/questionnaire/css/normalize.css" />
        <link rel="stylesheet" type="text/css" href="/questionnaire/css/demo.css" />
        <link rel="stylesheet" type="text/css" href="/questionnaire/css/component.css" />
        <link rel="stylesheet" type="text/css" href="/questionnaire/css/cs-select.css" />
        <link rel="stylesheet" type="text/css" href="/questionnaire/css/cs-skin-boxes.css" />
        <link rel="stylesheet" href="/questionnaire/css/jquery-labelauty.css">
        <script src="/questionnaire/js/modernizr.custom.js"></script>
        <style>
            .px100 li{
            }
            ul { list-style-type: none;}
            li { display: inline-block;}
            li { margin: 3px 0;margin-right: 3px;}
        </style>
    </head>
    <body>
        <div class="container">
            <div class="fs-form-wrap" id="fs-form-wrap">
                <div class="fs-title">
                    <h1>问卷调查表</h1>
                </div>
                <form id="myform" class="fs-form fs-form-full" autocomplete="off" method="post" action="/question/result">
                    <input type="hidden" name="a" value="SET_RESULT" />
                    <ol class="fs-fields">
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q1">您的姓名是?</label>
                            <input class="fs-anim-lower" id="q1" name="q1" type="text" placeholder="王小明" required/>
                        </li>
                        <li data-input-trigger>
                            <label class="fs-field-label fs-anim-upper" for="q2">您的性别是?</label>
                            <div class="fs-radio-group  clearfix fs-anim-lower">
                                <ul class="dowebok">
                                    <li style=""><input type="radio" name="q2" value="1" data-labelauty="男"></li>
                                    <li style=""><input type="radio" name="q2" value="0" data-labelauty="女"></li>
                                </ul>
                            </div>
                        </li>
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q3">您的年龄是?</label>
                            <input class="fs-anim-lower" id="q3" name="q3" type="number" placeholder="18" />
                        </li>
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q4">您的身高是多少厘米?</label>
                            <input class="fs-anim-lower" id="q4" name="q4" type="number" placeholder="180" />
                        </li>
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q5">您的体重是多少斤?</label>
                            <input class="fs-anim-lower" id="q5" name="q5" type="number" placeholder="45" />
                        </li>
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q6">您所知道的休闲服装品牌:(多选)</label>
                            <div class="fs-radio-group clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li style=""><input type="checkbox" name="q6" value="1" data-labelauty="以纯"></li>
                                    <li><input type="checkbox" name="q6" value="2" data-labelauty="美特斯邦威"></li>
                                    <li><input type="checkbox" name="q6" value="3" data-labelauty="森马"></li>
                                    <li ><input type="checkbox" name="q6" value="4" data-labelauty="班尼路"></li>
                                    <li ><input type="checkbox" name="q6" value="5" data-labelauty="佐丹奴"></li>
                                    <li ><input type="checkbox" name="q6" value="6" data-labelauty="wtaps"></li>
                                    <li ><input type="checkbox" name="q6" value="7" data-labelauty="neighborhood"></li>
                                    <li ><input type="checkbox" name="q6" value="8" data-labelauty="aape/bape"></li>
                                    <li ><input type="checkbox" name="q6" value="9" data-labelauty="supreme"></li>
                                    <li ><input type="checkbox" name="q6" value="10" data-labelauty="undefeated"></li>
                                    <li ><input type="checkbox" name="q6" value="11" data-labelauty="stussy"></li>
                                    <li ><input type="checkbox" name="q6" value="12" data-labelauty="evisu"></li>
                                    <li ><input type="checkbox" name="q6" value="13" data-labelauty="boy london"></li>
                                    <li ><input type="checkbox" name="q6" value="14" data-labelauty="fingercroxx"></li>
                                    <li ><input type="checkbox" name="q6" value="15" data-labelauty="clot"></li>
                                    <li ><input type="checkbox" name="q6" value="16" data-labelauty="izzue"></li>
                                    <li>
                                        <input type="checkbox" name="q6" value="17" data-labelauty="其他">
                                    </li>
                                </ul>
                                <input class="fs-anim-lower" id="q6" name="q6" type="text" placeholder="其他品牌" />
                            </div>
                        </li>
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q7">在您所熟知的休闲品牌中哪些品牌美誉比较高?(多选)</label>
                            <div class="fs-radio-group clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li style=""><input type="checkbox" name="q7" value="1" data-labelauty="以纯"></li>
                                    <li><input type="checkbox" name="q7" value="2" data-labelauty="美特斯邦威"></li>
                                    <li><input type="checkbox" name="q7" value="3" data-labelauty="森马"></li>
                                    <li ><input type="checkbox" name="q7" value="4" data-labelauty="班尼路"></li>
                                    <li ><input type="checkbox" name="q7" value="5" data-labelauty="佐丹奴"></li>
                                    <li ><input type="checkbox" name="q7" value="6" data-labelauty="wtaps"></li>
                                    <li ><input type="checkbox" name="q7" value="7" data-labelauty="neighborhood"></li>
                                    <li ><input type="checkbox" name="q7" value="8" data-labelauty="aape/bape"></li>
                                    <li ><input type="checkbox" name="q7" value="9" data-labelauty="supreme"></li>
                                    <li ><input type="checkbox" name="q7" value="10" data-labelauty="undefeated"></li>
                                    <li ><input type="checkbox" name="q7" value="11" data-labelauty="stussy"></li>
                                    <li ><input type="checkbox" name="q7" value="12" data-labelauty="evisu"></li>
                                    <li ><input type="checkbox" name="q7" value="13" data-labelauty="boy london"></li>
                                    <li ><input type="checkbox" name="q7" value="14" data-labelauty="fingercroxx"></li>
                                    <li ><input type="checkbox" name="q7" value="15" data-labelauty="clot"></li>
                                    <li ><input type="checkbox" name="q7" value="16" data-labelauty="izzue"></li>
                                    <li style="">
                                        <input type="checkbox" name="q7" value="17" data-labelauty="其他">
                                    </li>
                                </ul>
                                <input class="fs-anim-lower" id="q6" name="q7" type="text" placeholder="其他品牌" />
                            </div>
                        </li>
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q8">您对服装品牌的认知渠道主要通过:</label>
                            <div class="fs-radio-group clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li style=""><input type="checkbox" name="q8" value="1" data-labelauty="电视"></li>
                                    <li><input type="checkbox" name="q8" value="2" data-labelauty="报纸"></li>
                                    <li><input type="checkbox" name="q8" value="3" data-labelauty="杂志"></li>
                                    <li ><input type="checkbox" name="q8" value="4" data-labelauty="网络"></li>
                                    <li ><input type="checkbox" name="q8" value="5" data-labelauty="朋友介绍"></li>
                                    <li style="">
                                        <input type="checkbox" name="q8" value="6" data-labelauty="其他">
                                    </li>
                                </ul>
                            </div>
                            <input class="fs-anim-lower" id="q6" name="q6" type="text" placeholder="其他" />
                        </li>
                        <li data-input-trigger>
                            <label class="fs-field-label fs-anim-upper" for="q9">您每个月的生活费是多少?</label>
                            <div class="fs-radio-group  clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li style=""><input type="radio" name="q9" value="1" data-labelauty="300元以下"></li>
                                    <li style=""><input type="radio" name="q9" value="2" data-labelauty="300--400元"></li>
                                    <li style=""><input type="radio" name="q9" value="3" data-labelauty="400--500元"></li>
                                    <li><input type="radio" name="q9" value="4" data-labelauty="500--600元"></li>
                                    <li><input type="radio" name="q9" value="5" data-labelauty="600--800元"></li>
                                    <li><input type="radio" name="q9" value="6" data-labelauty="800--1000元"></li>
                                    <li><input type="radio" name="q9" value="7" data-labelauty="1000元以上"></li>
                                    <li style="width: 200px"><input type="radio" name="q9" value="8" data-labelauty="生活费中不含服装消费的请选这一项"></li>
                                </ul>
                            </div>
                        </li>
                        <li data-input-trigger>
                            <label class="fs-field-label fs-anim-upper" for="q10">您每个月在服装上通过淘宝购买消费是多少?</label>
                            <div class="fs-radio-group  clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li><input type="radio" name="q10" value="1" data-labelauty="100元以下"></li>
                                    <li><input type="radio" name="q10" value="2" data-labelauty="100--150元"></li>
                                    <li><input type="radio" name="q10" value="3" data-labelauty="150--200元"></li>
                                    <li><input type="radio" name="q10" value="4" data-labelauty="200--250元"></li>
                                    <li><input type="radio" name="q10" value="5" data-labelauty="250--300元"></li>
                                    <li><input type="radio" name="q10" value="6" data-labelauty="300--350元"></li>
                                    <li><input type="radio" name="q10" value="7" data-labelauty="350--400元"></li>
                                    <li><input type="radio" name="q10" value="8" data-labelauty="400--500元"></li>
                                    <li><input type="radio" name="q10" value="9" data-labelauty="500元以上"></li>
                                </ul>
                            </div>
                        </li>
                        <li data-input-trigger>
                            <label class="fs-field-label fs-anim-upper" for="q11">您每个月购买衣服的频率?</label>
                            <div class="fs-radio-group  clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li><input type="radio" name="q11" value="1" data-labelauty="一次"></li>
                                    <li><input type="radio" name="q11" value="2" data-labelauty="两次"></li>
                                    <li><input type="radio" name="q11" value="3" data-labelauty="三次"></li>
                                    <li><input type="radio" name="q11" value="4" data-labelauty="三次以上"></li>
                                </ul>
                            </div>
                        </li>
                        <li data-input-trigger>
                            <label class="fs-field-label fs-anim-upper" for="q12">您平时购买服装的情况</label>
                            <div class="fs-radio-group  clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li><input type="radio" name="q12" value="1" data-labelauty="有意识的"></li>
                                    <li><input type="radio" name="q12" value="2" data-labelauty="随机性的"></li>
                                </ul>
                            </div>
                        </li>
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q13">您对服装品牌的认知渠道主要通过:</label>
                            <div class="fs-radio-group clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li><input type="checkbox" name="q13" value="1" data-labelauty="天气变化"></li>
                                    <li><input type="checkbox" name="q13" value="2" data-labelauty="节庆促销"></li>
                                    <li><input type="checkbox" name="q13" value="3" data-labelauty="看到同学朋友购买新衣服"></li>
                                    <li><input type="checkbox" name="q13" value="4" data-labelauty="逛街时随意看中"></li>
                                </ul>
                            </div>
                        </li>
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q14">您购买服装的时间段:</label>
                            <div class="fs-radio-group clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li><input type="checkbox" name="q14" value="1" data-labelauty="新货上市"></li>
                                    <li><input type="checkbox" name="q14" value="2" data-labelauty="促销打折"></li>
                                    <li><input type="checkbox" name="q14" value="3" data-labelauty="换季打折"></li>
                                    <li><input type="checkbox" name="q14" value="4" data-labelauty="清仓甩卖"></li>
                                    <li><input type="checkbox" name="q14" value="5" data-labelauty="其他时间"></li>
                                </ul>
                            </div>
                        </li>
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q15">您常通过什么方式买衣服：</label>
                            <div class="fs-radio-group clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li><input type="checkbox" name="q15" value="1" data-labelauty="网上"></li>
                                    <li><input type="checkbox" name="q15" value="2" data-labelauty="实体店"></li>
                                    <li><input type="checkbox" name="q15" value="3" data-labelauty="朋友代购"></li>
                                </ul>
                            </div>
                        </li>
                        <li>
                            <label class="fs-field-label fs-anim-upper" for="q16">购买衣服的时候首先考虑的是什么?</label>
                            <div class="fs-radio-group clearfix fs-anim-lower">
                                <ul class="dowebok px100">
                                    <li><input type="checkbox" name="q16" value="1" data-labelauty="款式"></li>
                                    <li><input type="checkbox" name="q16" value="2" data-labelauty="舒适度"></li>
                                    <li><input type="checkbox" name="q16" value="3" data-labelauty="质量"></li>
                                    <li><input type="checkbox" name="q16" value="4" data-labelauty="品牌知名度"></li>
                                </ul>
                            </div>
                        </li>
                    </ol><!-- /fs-fields -->
                    <button class="fs-submit" type="submit">提交问卷</button>
                </form><!-- /fs-form -->
            </div><!-- /fs-form-wrap -->

        </div><!-- /container -->
        <script src="/questionnaire/js/classie.js"></script>
        <script src="/questionnaire/js/selectFx.js"></script>
        <script src="/questionnaire/js/fullscreenForm.js"></script>
        <script>
            (function () {
                var formWrap = document.getElementById('fs-form-wrap');

                [].slice.call(document.querySelectorAll('select.cs-select')).forEach(function (el) {
                    new SelectFx(el, {
                        stickyPlaceholder: false,
                        onChange: function (val) {
                            document.querySelector('span.cs-placeholder').style.backgroundColor = val;
                        }
                    });
                });

                new FForm(formWrap, {
                    onReview: function () {
                        classie.add(document.body, 'overview'); // for demo purposes only
                    }
                });
            })();
        </script>
        <script src="/questionnaire/js/jquery-1.8.3.min.js"></script>
        <script src="/questionnaire/js/jquery-labelauty.js"></script>
        <script>
            $(function () {
                $(':input').labelauty();
            });
        </script>
    </body>
</html>
