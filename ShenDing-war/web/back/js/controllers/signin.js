'use strict';

/* Controllers */
// signin controller
app.controller('SigninFormController', ['$scope', '$http', '$state', '$location', function ($scope, $http, $state) {
        $scope.user = {};
        $scope.authError = null;
        loadimage();
        $scope.login = function () {
            $scope.authError = null;
            $http.post("/admin/login", {a: "login", code: $scope.user.code, account: $scope.user.account, passwd: $scope.user.passwd}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $scope.authError = responseData.msg;
                    //reload image
                    loadimage();
                    //set pwd and code null
                    $scope.user.code = null;
                    $scope.user.passwd = null;
                } else {
                    userInfo = responseData.user;
                    $state.go('app.index');
                }
            });
        };
    }]);