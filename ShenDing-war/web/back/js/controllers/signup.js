'use strict';

// signup controller
app.controller('SignupFormController', ['$scope', '$http', '$state', function ($scope, $http, $state) {
        $scope.user = {};
        $scope.authError = null;
        $scope.province = null;
        $scope.city = null;
        $scope.area = null;
        $scope.areaList = null;
        $scope.provinceList = null;
        $scope.cityList = null;
        loadimage();
        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };
        $scope.signup = function () {
            $scope.authError = null;
            // Try to create
            var birthday = "";
            if ($scope.user.birthday instanceof Date) {
                birthday = $scope.user.birthday.getFullYear() + "-" + ($scope.user.birthday.getMonth() + 1) + "-" + $scope.user.birthday.getDate();
            }
            $http.post("/admin/signup", {bankType: $scope.user.bankType, bankCardCode: $scope.user.bankCardCode, city: $scope.city, area: $scope.area, province: $scope.province, name: $scope.user.name, passwd: $scope.user.passwd, account: $scope.user.account, birthday: birthday, code: $scope.user.code, idCard: $scope.user.idCard, mobile: $scope.user.mobile, weChatCode: $scope.user.weChatCode, qq: $scope.user.qq, address: $scope.user.address, email: $scope.user.email, sex: $scope.user.sex}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $scope.authError = responseData.msg;
                    loadimage();
                    $scope.user.code = null;
                } else {
                    $state.go('access.signup_ok');
                }
            });
        };

        $scope.getProvince = function () {
            $http.get("/webservice/admin/data_province").success(function (responseData) {
                if (responseData.success !== "1") {
                    $scope.authError = responseData.msg;
                } else {
                    $scope.provinceList = responseData.data;
                }
            });
        };

        $scope.getProvince();
        $scope.getCity = function (code) {
            $http.get("/webservice/admin/data_city?code=" + code).success(function (responseData) {
                if (responseData.success !== "1") {
                    $scope.authError = responseData.msg;
                } else {
                    $scope.cityList = responseData.data;
                }
            });
        };

        $scope.getArea = function (code) {
            $http.get("/webservice/admin/data_area?code=" + code).success(function (responseData) {
                if (responseData.success !== "1") {
                    $scope.authError = responseData.msg;
                } else {
                    $scope.areaList = responseData.data;
                }
            });
        };

        $scope.changeProvince = function () {
            if ($scope.province != null && $scope.province != "") {
                $scope.areaList = null;
                $scope.cityList = null;
                $scope.area = null;
                $scope.city = null;
                $scope.getCity($scope.province);
            }
        };
        $scope.changeCity = function () {
            if ($scope.city != null && $scope.city != "") {
                $scope.areaList = null;
                $scope.area = null;
                $scope.getArea($scope.city);
            }
        };

    }]);