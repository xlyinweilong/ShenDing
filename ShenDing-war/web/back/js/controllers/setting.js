'use strict';
/* Controllers */
// setting controller
app.controller('RepasswordController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.oldPassword = null;
        $scope.newPassword = null;
        $scope.newRepassword = null;
        $scope.loading = false;
        $scope.submit = function () {
            if ($scope.newPassword.length < 6) {
                $.scojs_message("密码至少6位", $.scojs_message.TYPE_ERROR);
                return;
            }
            if ($scope.newPassword != $scope.newRepassword) {
                $.scojs_message("两次密码不一致", $.scojs_message.TYPE_ERROR);
                return;
            }
            $scope.loading = true;
            $http.post("/webservice/admin/reset_password", {oldPassword: $scope.oldPassword, newPassword: $scope.newPassword}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                    $scope.oldPassword = null;
                    $scope.newPassword = null;
                    $scope.newRepassword = null;
                }
                $scope.loading = false;
            });
        };
    }]);

app.controller('MyInfoController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.user = null;
        $scope.loading = false;
        $scope.submit = function () {
            $scope.loading = true;
            $http.post("/webservice/admin/my_info", $scope.user).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.loading = false;
            });
        };

        $http.get("/webservice/admin/my_info").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                if (responseData.success == "-1") {
                    $state.go('access.signin');
                }
            } else {
                $scope.user = responseData.data;
            }
            $scope.loading = false;
        });
        
    }]);