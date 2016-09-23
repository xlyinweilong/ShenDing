'use strict';
/* Controllers */
// goods controller
app.controller('GoodsInfoController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.goods = null;
        $scope.goodsId = $stateParams.id;
        $scope.list = null;
        $scope.order = null;
        $scope.totalItems = null;
        $scope.getoods = function () {
            $http.get("/webservice/admin/goods_info?id=" + $scope.goodsId).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.goods = responseData.data;
                    $scope.list = responseData.list;
                    $scope.order = responseData.order;
                    $scope.totalItems = responseData.total;
                }
            });
        };
        if ($scope.goodsId != null && $scope.goodsId > 0) {
            $scope.getoods();
        } else {
            $.scojs_message("URL错误", $.scojs_message.TYPE_ERROR);
        }
    }]);