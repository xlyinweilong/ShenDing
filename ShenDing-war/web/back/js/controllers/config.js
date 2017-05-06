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