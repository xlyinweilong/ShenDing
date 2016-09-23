'use strict';
/* Controllers */
// order controller
app.controller('OrderInfoController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.order = null;
        $scope.orderId = $stateParams.id;
        $scope.list = null;
        $scope.totalItems = null;
        $scope.getOrder = function () {
            $http.get("/webservice/admin/order_info?id=" + $scope.orderId).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.order = responseData.data;
                    $scope.list = responseData.list;
                    $scope.totalItems = responseData.total;
                }
            });
        };
        if ($scope.orderId != null && $scope.orderId > 0) {
            $scope.getOrder();
        } else {
            $.scojs_message("URL错误", $.scojs_message.TYPE_ERROR);
        }
    }]);