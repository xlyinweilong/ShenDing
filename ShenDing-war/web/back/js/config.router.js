'use strict';
/**
 * Config for the router
 */
angular.module('app').run(['$rootScope', '$state', '$stateParams',
    function ($rootScope, $state, $stateParams) {
        $rootScope.$state = $state;
        $rootScope.$stateParams = $stateParams;
    }
]).config(['$stateProvider', '$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.when("", "/access/signin");
        $urlRouterProvider.otherwise('/access/signin');
        $stateProvider
                .state('app', {
                    abstract: true,
                    url: '/app',
                    templateUrl: '/back/tpl/app.html'
                })
                .state('app.index', {
                    url: '/index',
                    templateUrl: '/back/tpl/index.html'
                })
                .state('app.setting', {
                    url: '/setting',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/setting.js']);
                            }
                        ]
                    }
                })
                .state('app.setting.repassword', {
                    url: '/repassword',
                    templateUrl: '/back/tpl/repassword.html'
                })
                .state('app.setting.myinfo', {
                    url: '/myinfo',
                    templateUrl: '/back/tpl/my_info.html'
                })
                .state('app.root', {
                    url: '/root',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/root.js']);
                            }
                        ]
                    }
                })
                .state('app.root.menu', {
                    url: '/menu',
                    templateUrl: '/back/tpl/menu_management.html'
                })
                .state('app.super', {
                    url: '/super',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/super.js']);
                            }
                        ]
                    }
                })
                .state('app.super.role', {
                    url: '/role',
                    templateUrl: '/back/tpl/super_role_management.html'
                })
                .state('app.ad', {
                    url: '/ad',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/ad.js']);
                            }
                        ]
                    }
                })
                .state('app.ad.ad_info', {
                    url: '/ad_info/:id',
                    templateUrl: '/back/tpl/ad_info.html'
                })
                .state('app.order', {
                    url: '/order',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/order.js']);
                            }
                        ]
                    }
                })
                .state('app.order.order_info', {
                    url: '/order_info/:id',
                    templateUrl: '/back/tpl/order_info.html'
                })
                .state('app.config', {
                    url: '/config',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/config.js']);
                            }
                        ]
                    }
                })
                .state('app.config.commission', {
                    url: '/commission',
                    templateUrl: '/back/tpl/config_commission.html'
                })
                .state('app.config.excitation', {
                    url: '/excitation',
                    templateUrl: '/back/tpl/config_excitation.html'
                })
                .state('app.goods', {
                    url: '/goods',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/goods.js']);
                            }
                        ]
                    }
                })
                .state('app.goods.goods_info', {
                    url: '/goods_info/:id',
                    templateUrl: '/back/tpl/goods_info.html'
                })
                .state('app.user', {
                    url: '/user',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/user.js']);
                            }
                        ]
                    }
                })
                .state('app.user.user', {
                    url: '/user',
                    templateUrl: '/back/tpl/user_management.html'
                })
                .state('app.user.approve', {
                    url: '/approve',
                    templateUrl: '/back/tpl/user_approve.html'
                })
                .state('app.user.user_info', {
                    url: '/user_info/:id',
                    templateUrl: '/back/tpl/user_info.html'
                })
                .state('app.business', {
                    url: '/business',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/business.js']);
                            }
                        ]
                    }
                })
                .state('app.business.business_modify_order_user_price_by_type', {
                    url: '/business_modify_order_user_price_by_type',
                    templateUrl: '/back/tpl/business_modify_order_user_price_by_type.html'
                })
                .state('app.business.new_ad_list', {
                    url: '/new_ad_list',
                    templateUrl: '/back/tpl/business_new_ad_list.html'
                })
                .state('app.business.business_custom_list', {
                    url: '/custom_list',
                    templateUrl: '/back/tpl/business_custom_list.html'
                })
                .state('app.business.contract_list', {
                    url: '/contract_list',
                    templateUrl: '/back/tpl/business_contract_list.html'
                })
                .state('app.business.goods', {
                    url: '/goods',
                    templateUrl: '/back/tpl/business_goods.html'
                })
                .state('app.business.ad', {
                    url: '/ad',
                    templateUrl: '/back/tpl/business_ad.html'
                })
                .state('app.business.create_ad', {
                    url: '/create_ad/:id',
                    templateUrl: '/back/tpl/business_create_ad.html'
                })
                .state('app.business.update_ad', {
                    url: '/update_ad/:id',
                    templateUrl: '/back/tpl/business_update_ad.html'
                })
                .state('app.business.order', {
                    url: '/order',
                    templateUrl: '/back/tpl/business_order.html'
                })
                .state('app.business.order_back', {
                    url: '/order_back',
                    templateUrl: '/back/tpl/business_order_back.html'
                })
                .state('app.business.create_order', {
                    url: '/create_order',
                    templateUrl: '/back/tpl/business_create_order.html'
                })
                .state('app.business.update_order', {
                    url: '/update_order/:id',
                    templateUrl: '/back/tpl/business_update_order.html'
                })
                .state('app.business.update_success_order', {
                    url: '/update_success_order/:id',
                    templateUrl: '/back/tpl/business_update_success_order.html'
                })
                .state('app.business.delete_earnest_order', {
                    url: '/delete_earnest_order/:id',
                    templateUrl: '/back/tpl/business_delete_earnest_order.html'
                })
                .state('app.business.user_wage_log_list', {
                    url: '/user_wage_log_list',
                    templateUrl: '/back/tpl/business_user_wage_log_list.html'
                })
                .state('app.business.modify_place_user', {
                    url: '/modify_place_user',
                    templateUrl: '/back/tpl/business_modify_place_user.html'
                })
                .state('app.business.modify_order_divide_amount', {
                    url: '/modify_order_divide_amount',
                    templateUrl: '/back/tpl/business_modify_order_divide_amount.html'
                })
                .state('app.business.modify_order_user', {
                    url: '/modify_order_user',
                    templateUrl: '/back/tpl/business_modify_order_user.html'
                })
                .state('app.business.business_modify_goods_price_all_by_type', {
                    url: '/business_modify_goods_price_all_by_type',
                    templateUrl: '/back/tpl/business_modify_goods_price_all_by_type.html'
                })
                .state('app.business.wage', {
                    url: '/wage',
                    templateUrl: '/back/tpl/business_wage.html'
                })
                .state('app.business.order_wage', {
                    url: '/order_wage',
                    templateUrl: '/back/tpl/business_order_wage.html'
                })
                .state('app.make_friends', {
                    url: '/make_friends',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/make_friends.js']);
                            }
                        ]
                    }
                })
                .state('app.make_friends.new_ad_list', {
                    url: '/new_ad_list',
                    templateUrl: '/back/tpl/make_friends_new_ad_list.html'
                })
                .state('app.make_friends.make_friends_modify_order_user_price_by_type', {
                    url: '/make_friends_modify_order_user_price_by_type',
                    templateUrl: '/back/tpl/make_friends_modify_order_user_price_by_type.html'
                })
                .state('app.make_friends.business_custom_list', {
                    url: '/custom_list',
                    templateUrl: '/back/tpl/make_friends_custom_list.html'
                })
                .state('app.make_friends.contract_list', {
                    url: '/contract_list',
                    templateUrl: '/back/tpl/make_friends_contract_list.html'
                })
                .state('app.make_friends.goods', {
                    url: '/goods',
                    templateUrl: '/back/tpl/make_friends_goods.html'
                })
                .state('app.make_friends.ad', {
                    url: '/ad',
                    templateUrl: '/back/tpl/make_friends_ad.html'
                })
                .state('app.make_friends.create_ad', {
                    url: '/create_ad/:id',
                    templateUrl: '/back/tpl/make_friends_create_ad.html'
                })
                .state('app.make_friends.update_ad', {
                    url: '/update_ad/:id',
                    templateUrl: '/back/tpl/make_friends_update_ad.html'
                })
                .state('app.make_friends.order', {
                    url: '/order',
                    templateUrl: '/back/tpl/make_friends_order.html'
                })
                .state('app.make_friends.order_back', {
                    url: '/order_back',
                    templateUrl: '/back/tpl/make_friends_order_back.html'
                })
                .state('app.make_friends.create_order', {
                    url: '/create_order',
                    templateUrl: '/back/tpl/make_friends_create_order.html'
                })
                .state('app.make_friends.update_order', {
                    url: '/update_order/:id',
                    templateUrl: '/back/tpl/make_friends_update_order.html'
                })
                .state('app.make_friends.user_wage_log_list', {
                    url: '/user_wage_log_list',
                    templateUrl: '/back/tpl/make_friends_user_wage_log_list.html'
                })
                .state('app.make_friends.modify_place_user', {
                    url: '/modify_place_user',
                    templateUrl: '/back/tpl/make_friends_modify_place_user.html'
                })
                .state('app.make_friends.wage', {
                    url: '/wage',
                    templateUrl: '/back/tpl/make_friends_wage.html'
                })
                .state('app.make_friends.order_wage', {
                    url: '/order_wage',
                    templateUrl: '/back/tpl/make_friends_order_wage.html'
                })
                .state('app.statistics', {
                    url: '/statistics',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/statistics.js']);
                            }
                        ]
                    }
                })
                .state('app.statistics.user_wage', {
                    url: '/user_wage',
                    templateUrl: '/back/tpl/statistics_user_wage.html'
                })
                .state('app.statistics.place', {
                    url: '/place',
                    templateUrl: '/back/tpl/statistics_place.html'
                })
                .state('app.statistics.region', {
                    url: '/region',
                    templateUrl: '/back/tpl/statistics_region.html'
                })
                .state('app.product', {
                    url: '/product',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/product.js']);
                            }
                        ]
                    }
                })
                .state('app.product.grand_slam_list', {
                    url: '/grand_slam_list',
                    templateUrl: '/back/tpl/product_grand_slam_list.html'
                })
                .state('app.product.grand_slam_create_or_update', {
                    url: '/grand_slam_create_or_update/:id',
                    templateUrl: '/back/tpl/product_grand_slam_create_update.html'
                })
                .state('app.product.product_list', {
                    url: '/product_list',
                    templateUrl: '/back/tpl/product_list.html'
                })
                .state('app.product.product_create_or_update', {
                    url: '/product_create_or_update/:id',
                    templateUrl: '/back/tpl/product_create_update.html'
                })
                .state('app.product.cosmetics_list', {
                    url: '/cosmetics_list',
                    templateUrl: '/back/tpl/cosmetics_list.html'
                })
                .state('app.product.cosmetics_create_or_update', {
                    url: '/cosmetics_create_or_update/:id',
                    templateUrl: '/back/tpl/cosmetics_create_update.html'
                })
                .state('app.task', {
                    url: '/task',
                    template: '<div ui-view></div>',
                    resolve: {
                        deps: [
                            'uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/task.js']);
                            }
                        ]
                    }
                })
                .state('app.task.order_remind', {
                    url: '/order_remind',
                    templateUrl: '/back/tpl/task_order_remind.html'
                })
                .state('app.task.my_order', {
                    url: '/my_order',
                    templateUrl: '/back/tpl/task_my_order.html'
                })
                .state('app.task.my_amount', {
                    url: '/my_amount',
                    templateUrl: '/back/tpl/task_my_amount.html'
                })
                .state('app.task.my_product', {
                    url: '/my_product',
                    templateUrl: '/back/tpl/task_my_product.html'
                })
                .state('app.task.my_grand_slam', {
                    url: '/my_grand_slam',
                    templateUrl: '/back/tpl/task_my_grand_slam.html'
                })
                .state('app.task.my_new_order', {
                    url: '/my_new_order',
                    templateUrl: '/back/tpl/task_my_new_order.html'
                })
                .state('app.task.my_new_ad', {
                    url: '/my_new_ad',
                    templateUrl: '/back/tpl/task_my_new_ad.html'
                })
                .state('app.task.modify_order_date', {
                    url: '/modify_order_date',
                    templateUrl: '/back/tpl/task_modify_order_date.html'
                })
                .state('app.task.contract_remind', {
                    url: '/contract_remind',
                    templateUrl: '/back/tpl/task_contract_remind.html'
                })
                .state('app.task.log', {
                    url: '/log',
                    templateUrl: '/back/tpl/task_log.html'
                })
                .state('access', {
                    url: '/access',
                    template: '<div ui-view class="fade-in-right-big smooth"></div>'
                })
                .state('access.signin', {
                    url: '/signin',
                    templateUrl: '/back/tpl/page_signin.html',
                    resolve: {
                        deps: ['uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/signin.js']);
                            }]
                    }
                })
                .state('access.logout', {
                    url: '/logout',
                    templateUrl: '/back/tpl/page_logout.html',
                    resolve: {
                        deps: ['uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/logout.js']);
                            }]
                    }
                })
                .state('access.signup', {
                    url: '/signup',
                    templateUrl: '/back/tpl/page_signup.html',
                    resolve: {
                        deps: ['uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/signup.js']);
                            }]
                    }
                })
                .state('access.signup_ok', {
                    url: '/signup_ok',
                    templateUrl: '/back/tpl/page_signup_ok.html'
                })
                .state('access.404', {
                    url: '/404',
                    templateUrl: '/back/tpl/page_404.html'
                })
                .state('layout', {
                    abstract: true,
                    url: '/layout',
                    templateUrl: '/back/tpl/layout.html'
                })
                .state('layout.fullwidth', {
                    url: '/fullwidth',
                    views: {
                        '': {
                            templateUrl: '/back/tpl/layout_fullwidth.html'
                        },
                        'footer': {
                            templateUrl: '/back/tpl/layout_footer_fullwidth.html'
                        }
                    },
                    resolve: {
                        deps: ['uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/vectormap.js']);
                            }]
                    }
                })
                .state('layout.mobile', {
                    url: '/mobile',
                    views: {
                        '': {
                            templateUrl: '/back/tpl/layout_mobile.html'
                        },
                        'footer': {
                            templateUrl: '/back/tpl/layout_footer_mobile.html'
                        }
                    }
                })
                .state('layout.app', {
                    url: '/app',
                    views: {
                        '': {
                            templateUrl: '/back/tpl/layout_app.html'
                        },
                        'footer': {
                            templateUrl: '/back/tpl/layout_footer_fullwidth.html'
                        }
                    },
                    resolve: {
                        deps: ['uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/controllers/tab.js']);
                            }]
                    }
                })
                .state('apps', {
                    abstract: true,
                    url: '/apps',
                    templateUrl: '/back/tpl/layout.html'
                })
                .state('apps.note', {
                    url: '/note',
                    templateUrl: '/back/tpl/apps_note.html',
                    resolve: {
                        deps: ['uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/app/note/note.js',
                                    '/back/vendor/libs/moment.min.js']);
                            }]
                    }
                })
                .state('apps.contact', {
                    url: '/contact',
                    templateUrl: '/back/tpl/apps_contact.html',
                    resolve: {
                        deps: ['uiLoad',
                            function (uiLoad) {
                                return uiLoad.load(['/back/js/app/contact/contact.js']);
                            }]
                    }
                })
    }
]
        );