'use strict';

/* Controllers */
// super controller
app.controller('SuperUserController', ['$scope', '$http', '$modal', function ($scope, $http, $modal) {
        $scope.userList = null;
        $scope.userListLoading = false;
        $scope.adminList = null;
        $scope.adminListLoading = false;
        $scope.userListLoadingData = false;
        $scope.adminListLoadingData = false;
        $scope.userListSearch = "";
        $scope.adminListSearch = "";
        /**
         * pagination
         */
        $scope.userTotalItems = 0;
        $scope.userCurrentPage = 1;
        $scope.adminTotalItems = 0;
        $scope.adminCurrentPage = 1;
        $scope.maxSize = 5;
        /**
         * user pagination page changed
         * @returns {undefined}
         */
        $scope.userPageChanged = function () {
            $scope.getUserList($scope.userCurrentPage, false);
        };
        /**
         * 
         * admin pagination page changed
         * @returns {undefined}
         */
        $scope.adminPageChanged = function () {
            $scope.getAdminList($scope.adminCurrentPage, false);
        };

        /**
         * get user list
         * @param {type} page
         * @param {type} isInit
         * @returns {undefined}
         */
        $scope.getUserList = function (page, isInit) {
            $scope.userCurrentPage = page;
            if (!isInit) {
                $scope.userListLoadingData = true;
            } else {
                $scope.userListLoading = true;
                $scope.userListSearch = "";
            }
            $http.get("/webservice/admin/user_list?pageIndex=" + page + "&search=" + $scope.userListSearch).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.userList = responseData.data;
                    $scope.userTotalItems = responseData.totalCount;
                }
                $scope.userListLoadingData = false;
                $scope.userListLoading = false;
            });
        };
        /**
         * get admin list
         * @param {type} page
         * @param {type} isInit
         * @returns {undefined}
         */
        $scope.getAdminList = function (page, isInit) {
            $scope.adminCurrentPage = page;
            if (!isInit) {
                $scope.adminListLoadingData = true;
            } else {
                $scope.adminListLoading = true;
                $scope.adminListSearch = "";
            }
            $http.get("/webservice/admin/admin_list?pageIndex=" + page + "&search=" + $scope.adminListSearch).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.adminList = responseData.data;
                    $scope.adminTotalItems = responseData.totalCount;
                }
                $scope.adminListLoadingData = false;
                $scope.adminListLoading = false;
            });
        };

        // init list
        $scope.getUserList(1, true);
        $scope.getAdminList(1, true);

        /**
         * change admin to user
         * @returns {undefined}
         */
        $scope.changeToUser = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.adminList.length; i++) {
                if ($scope.adminList[i].lableId == true) {
                    checkedList.push($scope.adminList[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个管理员", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl',
                    resolve: {
                        modal: function () {
                            return {title: "转化确认", content: "确定要将已经选定的管理员转化为普通用户？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.userListLoadingData = true;
                        $scope.adminListLoadingData = true;
                        var data = new Array();
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/cast_sysuser_to_user", {uids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.userPageChanged();
                                $scope.adminPageChanged();
                            }
                            $scope.userListLoadingData = false;
                            $scope.adminListLoadingData = false;
                        });
                    }
                });
            }
        };

        /**
         * change user to admin
         * @returns {undefined}
         */
        $scope.changeToAdmin = function () {
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
                    controller: 'ConfirmCtrl',
                    resolve: {
                        modal: function () {
                            return {title: "转化确认", content: "确定要将已经选定的普通用户转化为管理员？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.userListLoadingData = true;
                        $scope.adminListLoadingData = true;
                        var data = new Array();
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/cast_user_to_sysuser", {uids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.userPageChanged();
                                $scope.adminPageChanged();
                            }
                            $scope.userListLoadingData = false;
                            $scope.adminListLoadingData = false;
                        });
                    }
                });
            }
        };
    }]);

app.controller('SuperRoleController', ['$scope', '$http', '$modal', '$location', function ($scope, $http, $modal, $location) {
        $scope.roleList = null;
        $scope.roleListLoading = false;
        $scope.roleListLoadingData = false;
        $scope.block = {show: false};
        $scope.power = {show: false};
        $scope.formMenu = {};
        $scope.powerForm = {};
        $scope.powerLoading = false;
        $scope.menuList = null;
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
            $scope.getRoleList($scope.currentPage, false);
        };

        /**
         * get role list
         * @param {type} page
         * @param {type} isInit
         * @returns {undefined}
         */
        $scope.getRoleList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.roleListLoadingData = true;
            } else {
                $scope.roleListLoading = true;
            }
            $http.get("/webservice/admin/role_list?pageIndex=" + page).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.roleList = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.roleListLoadingData = false;
                $scope.roleListLoading = false;
            });
        };

        // init list
        $scope.getRoleList(1, true);

        $scope.newRole = function () {
            $scope.block = {show: true, title: "新增角色", type: "new", submitting: false};
            $scope.formMenu.id = "";
            $scope.formMenu = {};
            $location.url("#role");
        };

        $scope.editRole = function () {
            var checkedFlag = 0
            var index = -1;
            for (var i = 0; i < $scope.roleList.length; i++) {
                if ($scope.roleList[i].lableId == true) {
                    index = i;
                    checkedFlag++;
                }
            }
            if (checkedFlag == 0) {
                $.scojs_message("请选择一个角色", $.scojs_message.TYPE_ERROR);
            } else if (checkedFlag > 1) {
                $.scojs_message("只能选择一个角色", $.scojs_message.TYPE_ERROR);
            } else {
                $scope.block = {show: true, title: "编辑角色", type: "edit", submitting: false};
                $scope.formMenu.id = $scope.roleList[index].id;
                $scope.formMenu.name = $scope.roleList[index].name;
                $scope.formMenu.sortIndex = $scope.roleList[index].sortIndex;
                $location.url("#role");
            }
        };

        $scope.powerRole = function () {
            var checkedFlag = 0
            var index = -1;
            for (var i = 0; i < $scope.roleList.length; i++) {
                if ($scope.roleList[i].lableId == true) {
                    index = i;
                    checkedFlag++;
                }
            }
            if (checkedFlag == 0) {
                $.scojs_message("请选择一个角色", $.scojs_message.TYPE_ERROR);
            } else if (checkedFlag > 1) {
                $.scojs_message("只能选择一个角色", $.scojs_message.TYPE_ERROR);
            } else {
                $scope.powerLoading = true;
                $scope.power = {show: true, title: "角色权限", submitting: false};
                $scope.powerForm.rid = $scope.roleList[index].id;
                $location.url("#power");
                $http.get("/webservice/admin/role_power?rid=" + $scope.powerForm.rid).success(function (responseData) {
                    if (responseData.success !== "1") {
                        $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    } else {
                        $scope.menuList = responseData.data;
                        $scope.powerLoading = false;
                    }
                });
            }
        };

        $scope.savePower = function () {
            if ($scope.power.submitting) {
                return;
            }
            $scope.power.submitting = true;
            $scope.powerForm.mids = [];
            for (var i = 0; i < $scope.menuList.length; i++) {
                if ($scope.menuList[i].lableId == true) {
                    $scope.powerForm.mids.push($scope.menuList[i].id);
                }
            }
            $http.post("/webservice/admin/save_power", $scope.powerForm).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.menuList = responseData.data;
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.power.submitting = false;
            });
        };

        $scope.submit = function () {
            if ($scope.block.submitting) {
                return;
            }
            $scope.block.submitting = true;
            $scope.roleListLoadingData = true;
            $http.post("/webservice/admin/create_update_role", $scope.formMenu).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.pageChanged();
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.block.submitting = false;
                $scope.roleListLoadingData = false;
            });
        };

        $scope.deleteRole = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.roleList.length; i++) {
                if ($scope.roleList[i].lableId == true) {
                    checkedList.push($scope.roleList[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要删除的角色", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl',
                    resolve: {
                        modal: function () {
                            return {title: "删除确认", content: "确定要删除已经选定的角色？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.block.show = false;
                        $scope.roleListLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/delete_role", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                            }
                            $scope.roleListLoadingData = false;
                        });
                    }
                });
            }
        };

    }]);

app.controller('SuperAdminController', ['$scope', '$http', '$modal', function ($scope, $http, $modal) {
        $scope.adminList = null;
        $scope.adminListLoading = false;
        $scope.adminListLoadingData = false;
        $scope.adminSearch = "";
        $scope.roles = null;
        $scope.adminFormSubmitting = false;
        $scope.rolesLoading = true;
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
            $scope.getAdminList($scope.currentPage, false);
        };

        $http.get("/webservice/admin/role_list?maxPerPage=65535").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
            } else {
                $scope.roles = responseData.data;
                $scope.rolesLoading = false;
            }
        });

        $scope.getAdminList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.adminListLoadingData = true;
            } else {
                $scope.adminListLoading = true;
                $scope.adminSearch = "";
            }
            $http.get("/webservice/admin/admin_list?pageIndex=" + page + "&search=" + $scope.adminSearch).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.adminList = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.adminListLoadingData = false;
                $scope.adminListLoading = false;
            });
        };

        // init list
        $scope.getAdminList(1, true);

        $scope.saveAdmin = function () {
            if ($scope.adminFormSubmitting) {
                return;
            }
            $scope.adminFormSubmitting = true;
            var dataList = new Array();
            for (var i = 0; i < $scope.adminList.length; i++) {
                var data = new Object();
                data.id = $scope.adminList[i].id;
                if ($scope.adminList[i].sysRole != null) {
                    data.roleId = $scope.adminList[i].sysRole.id;
                }
                dataList.push(data);
            }
            var transformRequest = function (obj) {
                return JSON.stringify(obj);
            };
            $http.post("/webservice/admin/save_admin", dataList, {
                headers: {'Content-Type': 'application/json; charset=UTF-8'},
                transformRequest: transformRequest
            }).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.pageChanged();
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.adminFormSubmitting = false;
            });
        };

        $scope.disableAdmin = function () {
            $.scojs_message("此功能程序猿在努力研发中。。。", $.scojs_message.TYPE_ERROR);
        };

    }]);
