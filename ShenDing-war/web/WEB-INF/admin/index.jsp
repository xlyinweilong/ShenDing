<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en" data-ng-app="app">
    <head>
        <meta charset="utf-8" />
        <title>啊啦叮</title>
        <meta name="description" content="" />
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
        <link rel="stylesheet" href="/back/css/bootstrap.css" type="text/css" />
        <link rel="stylesheet" href="/back/css/animate.css" type="text/css" />
        <link rel="stylesheet" href="/back/css/font-awesome.min.css" type="text/css" />
        <link rel="stylesheet" href="/back/css/simple-line-icons.css" type="text/css" />
        <link rel="stylesheet" href="/back/css/font.css" type="text/css" />
        <link rel="stylesheet" href="/back/css/app.css" type="text/css" />
        <link rel="stylesheet" href="/back/vendor/sco/scojs.css" type="text/css" />
        <link rel="stylesheet" href="/back/vendor/sco/sco.message.css" type="text/css" />
        <link rel="stylesheet" href="/back/vendor/audioplayer/css/main.css" type="text/css" />
    </head>
    <body ng-controller="AppCtrl">
        <div class="app" id="app" ng-class="{'app-header-fixed':app.settings.headerFixed, 'app-aside-fixed':app.settings.asideFixed, 'app-aside-folded':app.settings.asideFolded, 'app-aside-dock':app.settings.asideDock, 'container':app.settings.container}" ui-view></div>
        <!-- jQuery -->
        <script src="/back/vendor/jquery/jquery.min.js"></script>
        <!-- Angular -->
        <script src="/back/vendor/angular/angular.js"></script>
        <script src="/back/vendor/angular/angular-animate/angular-animate.js"></script>
        <script src="/back/vendor/angular/angular-cookies/angular-cookies.js"></script>
        <script src="/back/vendor/angular/angular-resource/angular-resource.js"></script>
        <script src="/back/vendor/angular/angular-sanitize/angular-sanitize.js"></script>
        <script src="/back/vendor/angular/angular-touch/angular-touch.js"></script>
        <!-- Vendor -->
        <script src="/back/vendor/angular/angular-ui-router/angular-ui-router.js"></script>
        <script src="/back/vendor/angular/ngstorage/ngStorage.js"></script>
        <!-- bootstrap -->
        <script src="/back/vendor/angular/angular-bootstrap/ui-bootstrap-tpls.js"></script>
        <!-- lazyload -->
        <script src="/back/vendor/angular/oclazyload/ocLazyLoad.js"></script>
        <!-- translate -->
        <script src="/back/vendor/angular/angular-translate/angular-translate.js"></script>
        <script src="/back/vendor/angular/angular-translate/loader-static-files.js"></script>
        <script src="/back/vendor/angular/angular-translate/storage-cookie.js"></script>
        <script src="/back/vendor/angular/angular-translate/storage-local.js"></script>
        <!-- App -->
        <script src="/back/js/app.js"></script>
        <script src="/back/js/config.js"></script>
        <script src="/back/js/config.lazyload.js"></script>
        <script src="/back/js/config.router.js"></script>
        <script src="/back/js/main.js"></script>
        <script src="/back/js/services/ui-load.js"></script>
        <script src="/back/js/filters/fromNow.js"></script>
        <script src="/back/js/directives/setnganimate.js"></script>
        <script src="/back/js/directives/ui-butterbar.js"></script>
        <script src="/back/js/directives/ui-focus.js"></script>
        <script src="/back/js/directives/ui-fullscreen.js"></script>
        <script src="/back/js/directives/ui-jq.js"></script>
        <script src="/back/js/directives/ui-module.js"></script>
        <script src="/back/js/directives/ui-nav.js"></script>
        <script src="/back/js/directives/ui-scroll.js"></script>
        <script src="/back/js/directives/ui-shift.js"></script>
        <script src="/back/js/directives/ui-toggleclass.js"></script>
        <script src="/back/js/directives/ui-validate.js"></script>
        <script src="/back/js/controllers/bootstrap.js"></script>
        <script src="/back/js/app/base/base-info-json.js"></script>
        <script src="/back/vendor/sco/spin.min.js"></script>
        <script src="/back/vendor/sco/sco.message.js"></script>
        <script src="/back/vendor/sco/sco.modal.js"></script>
        <!-- Lazy loading -->
    </body>
</html>
