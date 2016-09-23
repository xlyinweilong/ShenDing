// lazyload config

angular.module('app')
    /**
     * jQuery plugin config use ui-jq directive , config the js and css files that required
     * key: function name of the jQuery plugin
     * value: array of the css js file located
     */
    .constant('JQ_CONFIG', {
//        easyPieChart: ['/back/vendor/jquery/charts/easypiechart/jquery.easy-pie-chart.js'],
//        sparkline: ['/back/vendor/jquery/charts/sparkline/jquery.sparkline.min.js'],
//        plot: ['/back/vendor/jquery/charts/flot/jquery.flot.min.js',
//            '/back/vendor/jquery/charts/flot/jquery.flot.resize.js',
//            '/back/vendor/jquery/charts/flot/jquery.flot.tooltip.min.js',
//            '/back/vendor/jquery/charts/flot/jquery.flot.spline.js',
//            '/back/vendor/jquery/charts/flot/jquery.flot.orderBars.js',
//            '/back/vendor/jquery/charts/flot/jquery.flot.pie.min.js'],
//        slimScroll: ['/back/vendor/jquery/slimscroll/jquery.slimscroll.min.js'],
//        sortable: ['/back/vendor/jquery/sortable/jquery.sortable.js'],
//        nestable: ['/back/vendor/jquery/nestable/jquery.nestable.js',
//            '/back/vendor/jquery/nestable/nestable.css'],
//        filestyle: ['/back/vendor/jquery/file/bootstrap-filestyle.min.js'],
//        slider: ['/back/vendor/jquery/slider/bootstrap-slider.js',
//            '/back/vendor/jquery/slider/slider.css'],
//        chosen: ['/back/vendor/jquery/chosen/chosen.jquery.min.js',
//            '/back/vendor/jquery/chosen/chosen.css'],
        select2: ['/back/vendor/jquery/select2/select2.min.js',
            '/back/vendor/jquery/select2/select2.css'],
//        TouchSpin: ['/back/vendor/jquery/spinner/jquery.bootstrap-touchspin.min.js',
//            '/back/vendor/jquery/spinner/jquery.bootstrap-touchspin.css'],
//        wysiwyg: ['/back/vendor/jquery/wysiwyg/bootstrap-wysiwyg.js',
//            '/back/vendor/jquery/wysiwyg/jquery.hotkeys.js'],
//        dataTable: ['/back/vendor/jquery/datatables/jquery.dataTables.min.js',
//            '/back/vendor/jquery/datatables/dataTables.bootstrap.js',
//            '/back/vendor/jquery/datatables/dataTables.bootstrap.css'],
//        vectorMap: ['/back/vendor/jquery/jvectormap/jquery-jvectormap.min.js',
//            '/back/vendor/jquery/jvectormap/jquery-jvectormap-world-mill-en.js',
//            '/back/vendor/jquery/jvectormap/jquery-jvectormap-us-aea-en.js',
//            '/back/vendor/jquery/jvectormap/jquery-jvectormap.css'],
        footable: ['/back/vendor/jquery/footable/footable.all.min.js',
            '/back/vendor/jquery/footable/footable.core.css']
    }
    )
    // oclazyload config
    .config(['$ocLazyLoadProvider', function ($ocLazyLoadProvider) {
            // We configure ocLazyLoad to use the lib script.js as the async loader
            $ocLazyLoadProvider.config({
                debug: false,
                events: true,
                modules: [
                    {
                        name: 'ngGrid',
                        files: [
                            '/back/vendor/modules/ng-grid/ng-grid.min.js',
                            '/back/vendor/modules/ng-grid/ng-grid.min.css',
                            '/back/vendor/modules/ng-grid/theme.css'
                        ]
                    },
//                    {
//                        name: 'ui.select',
//                        files: [
//                            '/back/vendor/modules/angular-ui-select/select.min.js',
//                            '/back/vendor/modules/angular-ui-select/select.min.css'
//                        ]
//                    },
                    {
                        name: 'angularFileUpload',
                        files: [
                            '/back/vendor/modules/angular-file-upload/angular-file-upload.min.js'
                        ]
                    }
//                    {
//                        name: 'ui.calendar',
//                        files: ['/back/vendor/modules/angular-ui-calendar/calendar.js']
//                    },
//                    {
//                        name: 'ngImgCrop',
//                        files: [
//                            '/back/vendor/modules/ngImgCrop/ng-img-crop.js',
//                            '/back/vendor/modules/ngImgCrop/ng-img-crop.css'
//                        ]
//                    },
//                    {
//                        name: 'angularBootstrapNavTree',
//                        files: [
//                            '/back/vendor/modules/angular-bootstrap-nav-tree/abn_tree_directive.js',
//                            '/back/vendor/modules/angular-bootstrap-nav-tree/abn_tree.css'
//                        ]
//                    },
//                    {
//                        name: 'toaster',
//                        files: [
//                            '/back/vendor/modules/angularjs-toaster/toaster.js',
//                            '/back/vendor/modules/angularjs-toaster/toaster.css'
//                        ]
//                    },
//                    {
//                        name: 'textAngular',
//                        files: [
//                            '/back/vendor/modules/textAngular/textAngular-sanitize.min.js',
//                            '/back/vendor/modules/textAngular/textAngular.min.js'
//                        ]
//                    },
//                    {
//                        name: 'vr.directives.slider',
//                        files: [
//                            '/back/vendor/modules/angular-slider/angular-slider.min.js',
//                            '/back/vendor/modules/angular-slider/angular-slider.css'
//                        ]
//                    },
//                    {
//                        name: 'com.2fdevs.videogular',
//                        files: [
//                            '/back/vendor/modules/videogular/videogular.min.js'
//                        ]
//                    },
//                    {
//                        name: 'com.2fdevs.videogular.plugins.controls',
//                        files: [
//                            '/back/vendor/modules/videogular/plugins/controls.min.js'
//                        ]
//                    },
//                    {
//                        name: 'com.2fdevs.videogular.plugins.buffering',
//                        files: [
//                            '/back/vendor/modules/videogular/plugins/buffering.min.js'
//                        ]
//                    },
//                    {
//                        name: 'com.2fdevs.videogular.plugins.overlayplay',
//                        files: [
//                            '/back/vendor/modules/videogular/plugins/overlay-play.min.js'
//                        ]
//                    },
//                    {
//                        name: 'com.2fdevs.videogular.plugins.poster',
//                        files: [
//                            '/back/vendor/modules/videogular/plugins/poster.min.js'
//                        ]
//                    },
//                    {
//                        name: 'com.2fdevs.videogular.plugins.imaads',
//                        files: [
//                            '/back/vendor/modules/videogular/plugins/ima-ads.min.js'
//                        ]
//                    }
                ]
            });
        }])
    ;