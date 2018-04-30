'use strict';
/* Controllers */
// statistics controller
app.controller('SummaryController', ['$scope', '$http', '$modal', '$location', function ($scope, $http, $modal, $location) {
        $scope.listToday = null;
        $scope.listYesterday = false;
        $scope.pieColors = ['#23b7e5', '#7266ba', '#f05050', '#3a3f51', '#e8eff0', '#27c24c', '#fad733', '#999', '#1c2b36', '#cfdadd'];
        $scope.pieColorsCss = ['bg-info', 'bg-primary', 'bg-danger', 'bg-dark', 'bg-light', 'bg-success', 'bg-warning', 'bg-muted', 'bg-black', 'bg-gd-dk'];
        $scope.startCount = null;
        $scope.pieStartCount = pieStartSet;
        $scope.loadType = null;
        $scope.ticks = [[1, '1'], [2, '2'], [3, '3'], [4, '4'], [5, '5'], [6, '6'], [7, '7'], [8, '8'], [9, '9'], [10, '10'], [11, '11'], [12, '12'], [13, '13'], [14, '14'], [15, '15'], [16, '16'], [17, '17'], [18, '18'], [19, '19'], [20, '20'], [21, '21'], [22, '22'], [23, '23'], [24, '24']];
        $scope.day = {loading: false, todayActive: false, yesdayActive: false, day7dayActive: false, day30dayActive: false, day60Active: false};
        $scope.plotDate = plotDateStartSet;
        $scope.getList = function () {
            $http.get("/webservice/admin/summary_list").success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.listToday = responseData.listToday; //今日用户列表
                    $scope.listYesterday = responseData.listYesterday; //昨日用户列表
                    $scope.totalstartCount = responseData.totalstartCount; //启动用户总数
                    $scope.totalCount = responseData.totalCount; //用户总计
                    $scope.weekStartCount = responseData.weekStartCount; //上周启动用户
                    $scope.weekNewStartCount = responseData.weekNewStartCount; //上个月的新用户在过去7天的有多少启动过
                    $scope.monthNewUserCount = responseData.monthNewUserCount; //上个月的新用户
                    $scope.regions = responseData.regions;
                    $scope.pieStartCount = responseData.pieStartCount
                    pieStartSet = $scope.pieStartCount;
                }
            });
        };
        $scope.loadDay = function (type) {
            $scope.loadType = type;
            $scope.day.loading = true;
            $http.get("/webservice/admin/plot_date_list?type=" + type).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.plotDate = responseData.plotDate;
                    $scope.day = {loading: false, todayActive: false, yesdayActive: false, day7dayActive: false, day30dayActive: false, day60Active: false};
                    if (type == 0) {
                        if (plotDateStartSet == null) {
                            plotDateStartSet = $scope.plotDate;
                        }
                        $scope.day.todayActive = true;
                    } else if (type == 1) {
                        $scope.day.yesdayActive = true;
                    } else if (type == 2) {
                        $scope.day.day7Active = true;
                    } else if (type == 3) {
                        $scope.day.day30Active = true;
                    } else {
                        $scope.day.day60Active = true;
                    }
                }
            });
        };
        $scope.getList();
        $scope.loadDay(0);

    }]);


app.controller('RecommendController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.recommendList = null;
        $scope.recommendListLoading = false;
        $scope.recommendListLoadingData = false;
        $scope.search = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.pageChanged = function () {
            $scope.getRecommendList($scope.currentPage, false);
        };
        $scope.getRecommendList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.recommendListLoadingData = true;
            } else {
                $scope.recommendListLoading = true;
                $scope.search = "";
            }
            $http.get("/webservice/admin/recommend_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.search).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.recommendList = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.recommendListLoadingData = false;
                $scope.recommendListLoading = false;
            });
        };
        $scope.getRecommendList(1, true);
    }]);

app.controller('OrderRemindController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
                $scope.search = "";
            }
            $http.get("/webservice/admin/order_list?task=true&status=EARNEST&pageIndex=" + $scope.currentPage + "&search=" + $scope.search).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                    if (responseData.anCount != null && responseData.iosCount != null) {
                        $scope.anCount = responseData.anCount;
                        $scope.iosCount = responseData.iosCount;
                    }
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList(1, true);
    }]);

app.controller('MyOrderController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
                $scope.search = "";
            }
            $http.get("/webservice/admin/order_list?user=true&pageIndex=" + $scope.currentPage + "&search=" + $scope.search).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                    if (responseData.anCount != null && responseData.iosCount != null) {
                        $scope.anCount = responseData.anCount;
                        $scope.iosCount = responseData.iosCount;
                    }
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList(1, true);
    }]);

app.controller('TaskMyNewOrderController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.startDateOpened = false;
        $scope.endDateOpened = false;
        $scope.totalAmount = null;
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startDateOpened = true;
        };
        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endDateOpened = true;
        };
        $scope.dateOptions = {
            class: 'datepicker'
        };
        
        $http.get("/webservice/config/excitation").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                if (responseData.success == "-1") {
                    $state.go('access.signin');
                }
            } else if (responseData.data != null && responseData.data != '') {
                $.scojs_message(responseData.data, $.scojs_message.TYPE_OK);
            }
        });
        $scope.goAmount = function(){
            $state.go('app.task.my_amount');
        }
        
        $scope.isFindSelfYearAmount = false;
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
                $scope.search = "";
            }
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            $http.get("/webservice/admin/my_new_order_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.search + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.isFindSelfYearAmount = responseData.isFindSelfYearAmount;
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                    $scope.totalAmount = responseData.totalAmount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList(1, true);
    }]);

app.controller('MyNewAdController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.startDateOpened = false;
        $scope.endDateOpened = false;
        $scope.totalAmount = null;
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startDateOpened = true;
        };
        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endDateOpened = true;
        };
        $scope.dateOptions = {
            class: 'datepicker'
        };

        $http.get("/webservice/config/excitation").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                if (responseData.success == "-1") {
                    $state.go('access.signin');
                }
            } else if (responseData.data != null && responseData.data != '') {
                $.scojs_message(responseData.data, $.scojs_message.TYPE_OK);
            }
        });

        $scope.pageChanged = function () {
            //提示激励语
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
                $scope.search = "";
            }
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            $http.get("/webservice/admin/my_new_ad_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.search + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                    $scope.totalAmount = responseData.totalAmount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList(1, true);
    }]);


app.controller('MyProductController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.startDateOpened = false;
        $scope.endDateOpened = false;
        $scope.totalAmount = null;
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startDateOpened = true;
        };
        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endDateOpened = true;
        };
        $scope.dateOptions = {
            class: 'datepicker'
        };
        
        $http.get("/webservice/config/excitation").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                if (responseData.success == "-1") {
                    $state.go('access.signin');
                }
            } else if (responseData.data != null && responseData.data != '') {
                $.scojs_message(responseData.data, $.scojs_message.TYPE_OK);
            }
        });
        
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
            }
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            $http.get("/webservice/product/my_product_list?pageIndex=" + $scope.currentPage + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                    $scope.totalAmount = responseData.totalAmount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList(1, true);
    }]);

app.controller('MyProductProductGrandSlamController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.startDateOpened = false;
        $scope.endDateOpened = false;
        $scope.totalAmount = null;
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startDateOpened = true;
        };
        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endDateOpened = true;
        };
        $scope.dateOptions = {
            class: 'datepicker'
        };
        
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
            }
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            $http.get("/webservice/product/my_grand_slam_list?pageIndex=" + $scope.currentPage + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                    $scope.totalAmount = responseData.totalAmount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList(1, true);
    }]);


app.controller('MyProductMinShengBankController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.startDateOpened = false;
        $scope.endDateOpened = false;
        $scope.totalAmount = null;
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startDateOpened = true;
        };
        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endDateOpened = true;
        };
        $scope.dateOptions = {
            class: 'datepicker'
        };
        
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
            }
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            $http.get("/webservice/product/my_min_sheng_bank_list?pageIndex=" + $scope.currentPage + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                    $scope.totalAmount = responseData.totalAmount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList(1, true);
    }]);


app.controller('MyProductVipController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.startDateOpened = false;
        $scope.endDateOpened = false;
        $scope.totalAmount = null;
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startDateOpened = true;
        };
        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endDateOpened = true;
        };
        $scope.dateOptions = {
            class: 'datepicker'
        };
        
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
            }
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            $http.get("/webservice/product/my_vip_list?pageIndex=" + $scope.currentPage + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                    $scope.totalAmount = responseData.totalAmount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList(1, true);
    }]);


app.controller('ModifyOrderDateController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {

        $scope.opened = false;

        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };

        $scope.searchOrder = function () {
            $http.get("/webservice/admin/find_order?serialId=" + $scope.order.serialId).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.order = responseData.data;
                }
            });
        }
        $scope.submitForm = function () {
            $scope.submitting = true;
            var payDateTime = "";
            if ($scope.order.payDate instanceof Date) {
                payDateTime = $scope.order.payDate.getFullYear() + "-" + ($scope.order.payDate.getMonth() + 1) + "-" + $scope.order.payDate.getDate();
            }
            $http.post("/webservice/admin/modify_order_date", {id: $scope.order.id, payDate: payDateTime}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.submitting = false;
            });
        };
    }]);


app.controller('ContractRemindController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.listSearch = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.$watch(function () {
            return $location.search();
        }, function (params) {
            var loadPage = false;
            if (params.search != null & params.search != $scope.listSearch) {
                $scope.listSearch = params.search;
                loadPage = true;
            }
            if (params.page != null & params.page != $scope.currentPage) {
                $scope.currentPage = params.page;
                loadPage = true;
            }
            if (loadPage) {
                $scope.pageChanged();
            }
        });

        if ($location.search().search != null) {
            $scope.listSearch = $location.search().search;
        }

        if ($location.search().page != null) {
            $scope.currentPage = $location.search().page;
        }
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listSearch = "";
                $scope.listLoading = true;
            }
            $location.search("search", $scope.listSearch);
            if ($scope.currentPage != null) {
                $location.search("page", $scope.currentPage);
            }
            $http.get("/webservice/admin/order_list?limitEnd=true&pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList($scope.currentPage, false);
    }]);

app.controller('LogController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.listSearch = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.$watch(function () {
            return $location.search();
        }, function (params) {
            var loadPage = false;
            if (params.search != null & params.search != $scope.listSearch) {
                $scope.listSearch = params.search;
                loadPage = true;
            }
            if (params.page != null & params.page != $scope.currentPage) {
                $scope.currentPage = params.page;
                loadPage = true;
            }
            if (loadPage) {
                $scope.pageChanged();
            }
        });

        if ($location.search().search != null) {
            $scope.listSearch = $location.search().search;
        }

        if ($location.search().page != null) {
            $scope.currentPage = $location.search().page;
        }
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listSearch = "";
                $scope.listLoading = true;
            }
            $location.search("search", $scope.listSearch);
            if ($scope.currentPage != null) {
                $location.search("page", $scope.currentPage);
            }
            $http.get("/webservice/admin/log_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList($scope.currentPage, false);
    }]);


app.controller('TaskMyAmountController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        
        $scope.isFindSelfYearAmount = false;
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
                $scope.search = "";
            }
            $http.get("/webservice/admin/my_amount_list?pageIndex=" + $scope.currentPage).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.data.length;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList(1, true);
    }]);