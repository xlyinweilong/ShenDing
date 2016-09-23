'use strict';
/* Controllers */
// statistics controller

app.controller('MyNewOrderController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
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
            $http.get("/webservice/admin/region_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.search + "&start=" + start + "&end=" + end).success(function (responseData) {
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


app.controller('UserWageStaController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        $scope.startDateOpened = false;
        $scope.endDateOpened = false;
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
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            if (start == "" || end == "") {
                $.scojs_message("请输入时间", $.scojs_message.TYPE_ERROR);
                return;
            }
            $http.get("/webservice/admin/order_wage_list?pageIndex=" + $scope.currentPage + "&start=" + start + "&end=" + end).success(function (responseData) {
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

        $scope.wageList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            if (start == "" || end == "") {
                $.scojs_message("请输入完整的时间", $.scojs_message.TYPE_ERROR);
                return;
            }
            window.open("/admin/ALL_WAGE_LIST?startDate=" + start + "&endDate=" + end);
        };

    }]);