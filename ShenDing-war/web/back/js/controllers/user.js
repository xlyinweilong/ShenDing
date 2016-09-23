'use strict';

/* Controllers */
// user controller
app.controller('UserController', ['$scope', '$http', '$modal', '$location', function ($scope, $http, $modal, $location) {
        $scope.userList = null;
        $scope.userListLoading = false;
        $scope.userListLoadingData = false;
        $scope.userListSearch = "";
        $scope.block = {show: false};
        $scope.blockAmount = {show: false};
        $scope.user = {role: -1};
        $scope.province = null;
        $scope.city = null;
        $scope.area = null;
        $scope.areaList = null;
        $scope.provinceList = null;
        $scope.cityList = null;
        $scope.approve = 0;
        $scope.roleList = null;
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
            if (params.search != null & params.search != $scope.userListSearch) {
                $scope.userListSearch = params.search;
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
            $scope.userListSearch = $location.search().search;
        }

        if ($location.search().page != null) {
            $scope.currentPage = $location.search().page;
        }
        /**
         * pagination page changed
         * @returns {undefined}
         */
        $scope.pageChanged = function () {
            $scope.getUserList($scope.currentPage, false);
        };

        $scope.getUserList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.userListLoadingData = true;
            } else {
                $scope.userListLoading = true;
                $scope.userListSearch = "";
            }
            $location.search("search", $scope.userListSearch);
            if ($scope.currentPage != null) {
                $location.search("page", $scope.currentPage);
            }
            $http.get("/webservice/admin/user_list?pageIndex=" + page + "&search=" + $scope.userListSearch + "&approve=" + $scope.approve).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.userList = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.userListLoadingData = false;
                $scope.userListLoading = false;
            });
        };
        $scope.getUserList($scope.currentPage, true);

        $http.get("/webservice/admin/role_list?pageIndex=1").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
            } else {
                $scope.roleList = responseData.data;
            }
        });

        $scope.editUser = function () {
            var checkedFlag = 0;
            var index = -1;
            for (var i = 0; i < $scope.userList.length; i++) {
                if ($scope.userList[i].lableId == true) {
                    index = i;
                    checkedFlag++;
                }
            }
            if (checkedFlag == 0) {
                $.scojs_message("请选择一个用户", $.scojs_message.TYPE_ERROR);
            } else if (checkedFlag > 1) {
                $.scojs_message("只能选择一个用户", $.scojs_message.TYPE_ERROR);
            } else {
                $scope.block.type = "edit";
                $scope.block.title = "修改用户信息";
                $scope.block.show = true;
                $scope.block.submitting = false;
                $scope.block.i = index;
                $scope.user.id = $scope.userList[index].id;
                $scope.user.name = $scope.userList[index].name;
                $scope.user.account = $scope.userList[index].account;
                $scope.city = $scope.userList[index].city;
                $scope.province = $scope.userList[index].province;
                $scope.area = $scope.userList[index].area;
                $scope.user.email = $scope.userList[index].email;
                $scope.user.passwd = "";
                $location.url("#block");
                $scope.areaList = $scope.getArea($scope.city);
                $scope.cityList = $scope.getCity($scope.province);
                $scope.user.address = $scope.userList[index].address;
                $scope.user.qq = $scope.userList[index].qq;
                $scope.user.weChatCode = $scope.userList[index].weChatCode;
                $scope.user.mobile = $scope.userList[index].mobile;
                $scope.user.birthday = $scope.userList[index].birthdayStr;
                $scope.user.idCard = $scope.userList[index].idCard;
                $scope.user.sex = $scope.userList[index].sex;
                $scope.user.role = $scope.userList[index].roleId;
                $scope.user.bankCardCode = $scope.userList[index].bankCardCode;
                $scope.user.bankType = $scope.userList[index].bankType;
//                $("#province").val($scope.province).trigger("change");

            }
        };


        $scope.editUserAmount = function () {
            var checkedFlag = 0;
            var index = -1;
            for (var i = 0; i < $scope.userList.length; i++) {
                if ($scope.userList[i].lableId == true) {
                    index = i;
                    checkedFlag++;
                }
            }
            if (checkedFlag == 0) {
                $.scojs_message("请选择一个用户", $.scojs_message.TYPE_ERROR);
            } else if (checkedFlag > 1) {
                $.scojs_message("只能选择一个用户", $.scojs_message.TYPE_ERROR);
            } else {
                $scope.blockAmount.title = "修改用户余额";
                $scope.blockAmount.show = true;
                $scope.blockAmount.submitting = false;
                $scope.blockAmount.i = index;
                $scope.user.id = $scope.userList[index].id;
                $scope.user.name = $scope.userList[index].name;
                $scope.user.account = $scope.userList[index].account;
                $scope.user.balance = $scope.userList[index].balance;
                $location.url("#blockAmount");
            }
        };

        $scope.addUser = function () {
            $scope.block.type = "add";
            $scope.block.title = "创建用户";
            $scope.block.show = true;
            $scope.block.submitting = false;
            $scope.block.i = null;
            $scope.user.id = null;
            $scope.user.name = null;
            $scope.user.account = null;
            $scope.province = null;
            $scope.city = null;
            $scope.area = null;
            $scope.areaList = null;
            $scope.cityList = null;
            $scope.user.passwd = "";
            $scope.user.address = null;
            $scope.user.email = null;
            $scope.user.qq = null;
            $scope.user.weChatCode = null;
            $scope.user.mobile = null;
            $scope.user.birthday = null;
            $scope.user.idCard = null;
            $scope.user.bankCardCode = null;
            $scope.user.bankType = null;
            $scope.user.sex = 1;
            $scope.user.role = -1;
            $location.url("#block");
        };

        $scope.deleteUser = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.userList.length; i++) {
                if ($scope.userList[i].lableId == true) {
                    checkedList.push($scope.userList[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要删除的用户", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "删除确认", content: "确定要删除已经选定的用户吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.userListLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/delete_user", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.userListLoadingData = false;
                        });
                    }
                });
            }
        };

        $scope.disableUser = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.userList.length; i++) {
                if ($scope.userList[i].lableId == true) {
                    checkedList.push($scope.userList[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要禁用的用户", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "禁用确认", content: "确定要禁用已经选定的用户吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.userListLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/user_disable", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.userListLoadingData = false;
                        });
                    }
                });
            }
        };

        $scope.enableUser = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.userList.length; i++) {
                if ($scope.userList[i].lableId == true) {
                    checkedList.push($scope.userList[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要启用的用户", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "启用确认", content: "确定要启用已经选定的用户吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.userListLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/user_normal", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.userListLoadingData = false;
                        });
                    }
                });
            }
        };

        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
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
//                    $("#city").select2();
//                    $("#city").val(code).trigger("change");
                }
            });
        };

        $scope.getArea = function (code) {
            $http.get("/webservice/admin/data_area?code=" + code).success(function (responseData) {
                if (responseData.success !== "1") {
                    $scope.authError = responseData.msg;
                } else {
                    $scope.areaList = responseData.data;
//                    $("#area").select2();
//                    $("#area").val(code).trigger("change");
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

        $scope.submit = function () {
            if ($scope.block.submitting) {
                return;
            }
            $scope.block.submitting = true;
            var birthday = "";
            if ($scope.user.birthday instanceof Date) {
                birthday = $scope.user.birthday.getFullYear() + "-" + ($scope.user.birthday.getMonth() + 1) + "-" + $scope.user.birthday.getDate();
            }
            $http.post("/webservice/admin/create_update_user", {bankType: $scope.user.bankType, bankCardCode: $scope.user.bankCardCode, city: $scope.city, area: $scope.area, province: $scope.province, name: $scope.user.name, email: $scope.user.email, passwd: $scope.user.passwd, account: $scope.user.account, birthday: birthday, code: $scope.user.code, idCard: $scope.user.idCard, mobile: $scope.user.mobile, weChatCode: $scope.user.weChatCode, qq: $scope.user.qq, address: $scope.user.address, sex: $scope.user.sex, id: $scope.user.id, roleId: $scope.user.role}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
//                    $scope.user = responseData.data;
                    $scope.user.passwd = "";
                    if ($scope.block.type != "add") {
                        $scope.userList[$scope.block.i] = responseData.data;
                        $scope.userList[$scope.block.i].lableId = true;
                    }
                    $scope.pageChanged();
                    $scope.block.show = false;
                }
                $scope.block.submitting = false;

            });
        }

        $scope.submitAmount = function () {
            if ($scope.blockAmount.submitting) {
                return;
            }
            $scope.blockAmount.submitting = true;
            $http.post("/webservice/admin/create_update_user_amount", {amount: $scope.user.balance, id: $scope.user.id}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                    $scope.pageChanged();
                    $scope.blockAmount.show = false;
                }
                $scope.blockAmount.submitting = false;

            });
        }
    }]);

app.controller('UserApproveController', ['$scope', '$http', '$modal', '$location', function ($scope, $http, $modal, $location) {
        $scope.userList = null;
        $scope.userListLoading = false;
        $scope.userListLoadingData = false;
        $scope.userListSearch = "";
        $scope.block = {show: false};
        $scope.user = {};
        $scope.approve = 1;
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;

        /**
         * pagination page changed
         * @returns {undefined}
         */
        $scope.pageChanged = function () {
            $scope.getUserList($scope.currentPage, false);
        };

        $scope.getUserList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.userListLoadingData = true;
            } else {
                $scope.userListLoading = true;
                $scope.userListSearch = "";
            }
            $http.get("/webservice/admin/user_list?pageIndex=" + page + "&search=" + $scope.userListSearch + "&approve=" + $scope.approve).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.userList = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.userListLoadingData = false;
                $scope.userListLoading = false;
            });
        };
        $scope.getUserList(1, true);

        $scope.approveUser = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.userList.length; i++) {
                if ($scope.userList[i].lableId == true) {
                    checkedList.push($scope.userList[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个用户", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "审批确认", content: "确定要审批已经选定的用户吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.userListLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/approve_user", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.userListLoadingData = false;
                        });
                    }
                });
            }
        };
    }]);

app.controller('UserInfoController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.user = null;
        $scope.userId = $stateParams.id;
        $scope.getUser = function () {
            $http.get("/webservice/admin/target_user_info?id=" + $scope.userId).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.user = responseData.data;
                }
            });
        };

        $scope.list = null;
        $scope.totalItems = 0;
        $scope.getList = function () {
            $http.get("/webservice/admin/order_list?uid=" + $scope.userId).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
            });
        };

        if ($scope.userId != null && $scope.userId > 0) {
            $scope.getUser();
            $scope.getList();
        } else {
            $.scojs_message("ERROR", $.scojs_message.TYPE_ERROR);
        }
    }]);
