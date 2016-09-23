'use strict';
/* Controllers */
// ad controller
app.controller('AdInfoController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.ad = null;
        $scope.adId = $stateParams.id;
        $scope.list = null;
        $scope.totalItems = null;
        $scope.getAd = function () {
            $http.get("/webservice/admin/ad_info?id=" + $scope.adId).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.ad = responseData.data;
                    $scope.list = responseData.list;
                    $scope.totalItems = responseData.total;
                }
            });
        };
        if ($scope.adId != null && $scope.adId > 0) {
            $scope.getAd();
        } else {
            $.scojs_message("URL错误", $.scojs_message.TYPE_ERROR);
        }
    }]);