'use strict';
/* Controllers */
// business controller
app.controller('CommissionController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.commission = {submitting: false};
        $http.get("/webservice/config/commission").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                if (responseData.success == "-1") {
                    $state.go('access.signin');
                }
            } else {
                $scope.commission = responseData.data;
            }
        });

        $scope.submitForm = function () {
            $scope.commission.submitting = true;
            $http.post("/webservice/config/commission", {capital: $scope.commission.PROVINCIAL_CAPITAL, prefecture: $scope.commission.PREFECTURE, others: $scope.commission.OTHERS}).success(function (responseData) {
                $scope.commission.submitting = false;
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
            });
        }
    }]);


app.controller('ExcitationController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.excitation = {submitting: false};
        $http.get("/webservice/config/excitation").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                if (responseData.success == "-1") {
                    $state.go('access.signin');
                }
            } else {
                $scope.excitation.data = responseData.data;
            }
        });

        $scope.submitForm = function () {
            $scope.excitation.submitting = true;
            $http.post("/webservice/config/excitation", {excitation: $scope.excitation.data}).success(function (responseData) {
                $scope.excitation.submitting = false;
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
            });
        }
    }]);


app.controller('ConfigProductController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.order = {show: false, submitting: false};
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
            $http.get("/webservice/product/product_type_config_list?type=1&pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch).success(function (responseData) {
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
        $scope.edit = function (ele) {
            $scope.order.show = true;
            $scope.order.name = ele.name;
            $scope.order.key = ele.key;
        };

        $scope.add = function () {
            $scope.order.name = null;
            $scope.order.key = null;
            $scope.order.show = true;
        }

        $scope.submitForm = function () {
            $scope.order.submitting = true;
            $scope.order.type = 1;
            $http.post("/webservice/product/save_product_type_config", $scope.order).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                    $scope.order.show = false;
                    $scope.pageChanged();
                }
                $scope.order.submitting = false;
            });
        };
    }]);

app.controller('ConfigCosmeticsController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.order = {show: false, submitting: false};
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
            $http.get("/webservice/product/product_type_config_list?type=2&pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch).success(function (responseData) {
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
        $scope.edit = function (ele) {
           $scope.order.show = true;
            $scope.order.name = ele.name;
            $scope.order.key = ele.key;
        };

        $scope.add = function () {
            $scope.order.name = null;
            $scope.order.key = null;
            $scope.order.show = true;
        }

        $scope.submitForm = function () {
            $scope.order.submitting = true;
            $scope.order.type = 2;
            $http.post("/webservice/product/save_product_type_config", $scope.order).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                    $scope.order.show = false;
                    $scope.pageChanged();
                }
                $scope.order.submitting = false;
            });
        };
    }]);