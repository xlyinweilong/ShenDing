'use strict';
/* Controllers */
// product controller
app.controller('ProductListController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.listSearch = "";
        $scope.startDate = null;
        $scope.endDate = null;
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
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            } else {
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            $http.get("/webservice/product/product_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch + "&start=" + start + "&end=" + end).success(function (responseData) {
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
        $scope.add = function () {
            $state.go('app.product.product_create_or_update');
        };

        $scope.downLoad = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            } else {
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            if (start == "" || end == "") {
                $.scojs_message("请输入完整的时间段", $.scojs_message.TYPE_ERROR);
                return;
            }
            window.open("/admin/PRODUCT_LOG_LIST?startDate=" + start + "&endDate=" + end);
        };

        $scope.downLoadWage = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            } else {
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            if (start == "" || end == "") {
                $.scojs_message("请输入完整的时间段", $.scojs_message.TYPE_ERROR);
                return;
            }
            window.open("/admin/PRODUCT_LOG_WAGE_LIST?startDate=" + start + "&endDate=" + end);
        };

        $scope.deleteItems = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].lableId == true) {
                    checkedList.push($scope.list[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要作废的产品", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "作废确认", content: "确定要作废已经选定的产品吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.listLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/product/delete_product", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.listLoadingData = false;
                        });
                    }
                });
            }
        };
    }]);

app.controller('CreateUpdateProductController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.product = {submitting: false, soldCount: 3, incomeAmount: 540, commissionAmount: 130, product: 'MA_KA'};
        $scope.orderList = null;
        $scope.orderListLoadingData = null;
        $scope.orderListTotalItems = null;
        $scope.checkedOrder = null;
        $scope.searchOrderMsg = null;
        $scope.payDate = null;
        $scope.tempDate = null;
        $scope.product.id = $stateParams.id;
        if ($scope.product.id != null && $scope.product.id != "") {
            $http.get("/webservice/product/product_info?id=" + $scope.product.id).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.product = responseData.data;
                    $scope.payDate = $scope.product.payDate;
                    $scope.tempDate = new Date($scope.product.payDate);
                    $scope.checkedOrder = $scope.product.goodsOrder;
                }
            });
        }
        $scope.searchOrderList = function () {
            $scope.orderListLoadingData = true;
            $http.get("/webservice/product/order_list?status=SUCCESS&search=" + $scope.searchOrderMsg).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.orderList = responseData.data;
                    $scope.orderListTotalItems = responseData.totalCount;
                }
                $scope.orderListLoadingData = false;
            });
        };
        $scope.checkOrder = function (ele, index) {
            $scope.orderList = null;
            $scope.checkedOrder = ele;
            $scope.searchOrderMsg = ele.goods.name + " (" + ele.goods.provinceStr + ") " + ele.goods.categoryMean + " (代理:" + ele.agentUser.name + ")";
        };
        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };

        //根据产品和数量，修改总金额和提成
        $scope.changeAmount = function () {
            if (!$scope.product.soldCount > 0) {
                $scope.product.incomeAmount = null;
                $scope.product.commissionAmount = null;
            } else {
                if ($scope.product.product == 'MA_KA') {
                    if ($scope.product.soldCount == 1) {
                        $scope.product.incomeAmount = 288;
                        $scope.product.commissionAmount = 100;
                    } else if ($scope.product.soldCount == 2) {
                        $scope.product.incomeAmount = 576;
                        $scope.product.commissionAmount = 200;
                    } else if ($scope.product.soldCount == 3) {
                        $scope.product.incomeAmount = 540;
                        $scope.product.commissionAmount = 130;
                    } else if ($scope.product.soldCount == 4) {
                        $scope.product.incomeAmount = 720;
                        $scope.product.commissionAmount = 130;
                    } else if ($scope.product.soldCount == 15) {
                        $scope.product.incomeAmount = 2250;
                        $scope.product.commissionAmount = 400;
                    } else {
                        $scope.product.incomeAmount = null;
                        $scope.product.commissionAmount = null;
                    }
                } else if ($scope.product.product == 'MA_KA_WEN_HE') {
                    if ($scope.product.soldCount == 1) {
                        $scope.product.incomeAmount = 258;
                        $scope.product.commissionAmount = 100;
                    } else if ($scope.product.soldCount == 2) {
                        $scope.product.incomeAmount = 516;
                        $scope.product.commissionAmount = 200;
                    } else if ($scope.product.soldCount == 3) {
                        $scope.product.incomeAmount = 480;
                        $scope.product.commissionAmount = 130;
                    } else if ($scope.product.soldCount == 4) {
                        $scope.product.incomeAmount = 640;
                        $scope.product.commissionAmount = 130;
                    } else if ($scope.product.soldCount == 15) {
                        $scope.product.incomeAmount = 1950;
                        $scope.product.commissionAmount = 400;
                    } else {
                        $scope.product.incomeAmount = null;
                        $scope.product.commissionAmount = null;
                    }
                } else if ($scope.product.product == 'LAN_MEI_HUA_QING_SU') {
                    if ($scope.product.soldCount == 1) {
                        $scope.product.incomeAmount = 258;
                        $scope.product.commissionAmount = 100;
                    } else if ($scope.product.soldCount == 2) {
                        $scope.product.incomeAmount = 516;
                        $scope.product.commissionAmount = 200;
                    } else if ($scope.product.soldCount == 3) {
                        $scope.product.incomeAmount = 564;
                        $scope.product.commissionAmount = 130;
                    } else if ($scope.product.soldCount == 4) {
                        $scope.product.incomeAmount = 752;
                        $scope.product.commissionAmount = 130;
                    } else if ($scope.product.soldCount == 15) {
                        $scope.product.incomeAmount = 2250;
                        $scope.product.commissionAmount = 400;
                    } else {
                        $scope.product.incomeAmount = null;
                        $scope.product.commissionAmount = null;
                    }
                }
            }
        }

        $scope.submitForm = function () {
            $scope.product.submitting = true;
            if ($scope.payDate instanceof Date) {
                $scope.product.payDate = $scope.payDate.getFullYear() + "-" + ($scope.payDate.getMonth() + 1) + "-" + $scope.payDate.getDate();
            } else if ($scope.tempDate instanceof Date) {
                $scope.product.payDate = $scope.tempDate.getFullYear() + "-" + ($scope.tempDate.getMonth() + 1) + "-" + $scope.tempDate.getDate();
            }
            if ($scope.checkedOrder != null) {
                $scope.product.goodsOrderId = $scope.checkedOrder.id;
            }
            $http.post("/webservice/product/create_or_update_product", $scope.product).success(function (responseData) {
                $scope.product.submitting = false;
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                    $state.go('app.product.product_list');
                }
            });
        };
    }]);