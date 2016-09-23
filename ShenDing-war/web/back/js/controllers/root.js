'use strict';

/* Controllers */
// root controller
app.controller('RootMenuController', ['$scope', '$http', '$state', '$location', '$modal', '$log', function ($scope, $http, $state, $location, $modal, $log) {
        $scope.lv1IndexMenuList = null;
        $scope.lv1Loading = false;
        $scope.lv2Loading = false;
        $scope.lv2IndexMenuList = null;
        $scope.showLvBlock = false;
        $scope.lv = {};
        $scope.isNewLvBlock = false;
        $scope.isEditLvBlock = false;
        $scope.showLv2MenuList = false;
        $scope.switchPid = null;
        $scope.formMenu = {id: null, pid: null, name: null, type: 'COMMON', index: 0, glyphicon: null, sref: null, i: null, submitting: false};
        /**
         * get page data
         */
        $http.get("/webservice/admin/super_menu").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
            } else {
                $scope.lv1IndexMenuList = responseData.data;
            }
        });
        /**
         * click switch
         * @param {type} menu
         * @returns {undefined}
         */
        $scope.clickSwitch = function (menu) {
            if (menu.switch == true) {
                $scope.showLv2MenuList = true;
                $scope.colseAllSwitch();
                menu.switch = true;
                $scope.lv2Loading = true;
                $scope.switchPid = menu.id;
                $http.get("/webservice/admin/super_menu?pid=" + menu.id).success(function (responseData) {
                    if (responseData.success !== "1") {
                        $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    } else {
                        $scope.lv2IndexMenuList = responseData.data;
                    }
                    $scope.lv2Loading = false;
                });
            } else {
                $scope.switchPid = null;
                $scope.showLv2MenuList = false;
            }
        };
        /**
         *  show add new menu block
         * @param {type} lv
         * @param {type} pid
         * @returns {undefined}
         */
        $scope.newMenu = function (lv, pid) {
            $scope.showLvBlock = true;
            $scope.isNewLvBlock = true;
            $scope.isEditLvBlock = false;
            $scope.lv.type = "n" + lv;
            $scope.formMenu = {id: null, pid: null, name: null, type: 'COMMON', index: 0, glyphicon: null, sref: null, i: null, submitting: false};
            $location.url("#block");
            if (lv == 1) {
                $scope.lv.title = "新增一级菜单";
            } else if (lv == 2) {
                $scope.lv.title = "新增二级菜单";
                $scope.formMenu.pid = pid;
            }
        };
        /**
         * hide block
         * @returns {undefined}
         */
        $scope.closeLvBlock = function () {
            $scope.showLvBlock = false;
        };
        /**
         * show edit menu bolck
         * @param {type} lv
         * @param {type} pid
         * @returns {undefined}
         */
        $scope.editMenu = function (lv, pid) {
            var checkedFlag = 0;
            var index = -1;
            $scope.lv.type = "e" + lv;
            if (lv == 1) {
                $scope.lv.title = "编辑一级菜单";
                for (var i = 0; i < $scope.lv1IndexMenuList.length; i++) {
                    if ($scope.lv1IndexMenuList[i].lableId == true) {
                        index = i;
                        checkedFlag++;
                    }
                }
                if (checkedFlag == 0) {
                    $.scojs_message("请选择一个一级菜单", $.scojs_message.TYPE_ERROR);
                } else if (checkedFlag > 1) {
                    $.scojs_message("只能选择一个一级菜单", $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.showLvBlock = true;
                    $scope.isNewLvBlock = false;
                    $scope.isEditLvBlock = true;
                    $scope.formMenu.id = $scope.lv1IndexMenuList[index].id;
                    $scope.formMenu.pid = null;
                    $scope.formMenu.name = $scope.lv1IndexMenuList[index].name;
                    $scope.formMenu.type = $scope.lv1IndexMenuList[index].popedom;
                    $scope.formMenu.index = $scope.lv1IndexMenuList[index].sortIndex;
                    $scope.formMenu.glyphicon = $scope.lv1IndexMenuList[index].glyphicon;
                    $scope.formMenu.sref = $scope.lv1IndexMenuList[index].sref;
                    $scope.formMenu.i = index;
                    $location.url("#block");
                }
            } else if (lv == 2) {
                $scope.lv.title = "编辑二级菜单";
                for (var i = 0; i < $scope.lv2IndexMenuList.length; i++) {
                    if ($scope.lv2IndexMenuList[i].lableId == true) {
                        index = i;
                        checkedFlag++;
                    }
                }
                if (checkedFlag == 0) {
                    $.scojs_message("请选择一个二级菜单", $.scojs_message.TYPE_ERROR);
                } else if (checkedFlag > 1) {
                    $.scojs_message("只能选择一个二级菜单", $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.showLvBlock = true;
                    $scope.isNewLvBlock = false;
                    $scope.isEditLvBlock = true;
                    $scope.formMenu.id = $scope.lv2IndexMenuList[index].id;
                    $scope.formMenu.pid = pid;
                    $scope.formMenu.name = $scope.lv2IndexMenuList[index].name;
                    $scope.formMenu.type = $scope.lv2IndexMenuList[index].popedom;
                    $scope.formMenu.index = $scope.lv2IndexMenuList[index].sortIndex;
                    $scope.formMenu.glyphicon = $scope.lv2IndexMenuList[index].glyphicon;
                    $scope.formMenu.sref = $scope.lv2IndexMenuList[index].sref;
                    $scope.formMenu.i = index;
                    $location.url("#block");
                }
            }
        };
        /**
         * delete menu
         * @param {type} lv
         * @returns {undefined}
         */
        $scope.deleteMenu = function (lv) {
            var checkedList = new Array();
            if (lv == 1) {
                for (var i = 0; i < $scope.lv1IndexMenuList.length; i++) {
                    if ($scope.lv1IndexMenuList[i].lableId == true) {
                        checkedList.push($scope.lv1IndexMenuList[i]);
                    }
                }
                if (checkedList.length < 1) {
                    $.scojs_message("请至少选择一个要删除的一级菜单", $.scojs_message.TYPE_ERROR);
                } else {
                    var modalInstance = $modal.open({
                        templateUrl: 'confirm.html',
                        controller: 'ConfirmCtrl',
                        resolve: {
                            modal: function () {
                                return {title: "删除确认", content: "删除一级菜单后，里面的二级菜单也会被删除！确定要删除已经选定的一级菜单？", ok: "确定", cancel: "取消", lv: lv};
                            }
                        }
                    });
                    modalInstance.result.then(function (confirm) {
                        if (confirm) {
                            $scope.lv1Loading = true;
                            $scope.lv2Loading = true;
                            $scope.deleteMenus = [];
                            for (var i = 0; i < checkedList.length; i++) {
                                $scope.deleteMenus.push(checkedList[i].id);
                            }
                            $http.post("/webservice/admin/delete_menu", {menu_ids: $scope.deleteMenus}).success(function (responseData) {
                                if (responseData.success !== "1") {
                                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                                } else {
                                    //delete ok
                                    for (var i = 0; i < checkedList.length; i++) {
                                        $scope.lv1IndexMenuList.splice($scope.lv1IndexMenuList.indexOf(checkedList[i]), 1);
                                    }
                                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                                    $scope.showLvBlock = false;
                                    $scope.showLv2MenuList = false;
                                    $scope.colseAllSwitch();
                                }
                                $scope.lv1Loading = false;
                                $scope.lv2Loading = false;
                            });
                        }
                    });
                }
            } else if (lv == 2) {
                for (var i = 0; i < $scope.lv2IndexMenuList.length; i++) {
                    if ($scope.lv2IndexMenuList[i].lableId == true) {
                        checkedList.push($scope.lv2IndexMenuList[i]);
                    }
                }
                if (checkedList.length < 1) {
                    $.scojs_message("请至少选择一个要删除的二级菜单", $.scojs_message.TYPE_ERROR);
                } else {
                    var modalInstance = $modal.open({
                        templateUrl: 'confirm.html',
                        controller: 'ConfirmCtrl',
                        resolve: {
                            modal: function () {
                                return {title: "删除确认", content: "确定要删除已经选定的二级菜单？", ok: "确定", cancel: "取消", lv: lv};
                            }
                        }
                    });
                    modalInstance.result.then(function (confirm) {
                        if (confirm) {
                            $scope.lv2Loading = true;
                            $scope.deleteMenus = [];
                            for (var i = 0; i < checkedList.length; i++) {
                                $scope.deleteMenus.push(checkedList[i].id);
                            }
                            $http.post("/webservice/admin/delete_menu", {menu_ids: $scope.deleteMenus}).success(function (responseData) {
                                if (responseData.success !== "1") {
                                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                                } else {
                                    for (var i = 0; i < checkedList.length; i++) {
                                        $scope.lv2IndexMenuList.splice($scope.lv2IndexMenuList.indexOf(checkedList[i]), 1);
                                    }
                                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                                    $scope.showLvBlock = false;
                                }
                                $scope.lv2Loading = false;
                            });
                        }
                    });
                }
            }
        };
        /**
         * colse all switch
         * @returns {undefined}
         */
        $scope.colseAllSwitch = function () {
            for (var i = 0; i < $scope.lv1IndexMenuList.length; i++) {
                $scope.lv1IndexMenuList[i].switch = false;
            }
        };

        /**
         * submit form
         * @returns {undefined}
         */
        $scope.submit = function () {
            if ($scope.formMenu.submitting) {
                return;
            }
            $scope.formMenu.submitting = true;
            if ($scope.lv.type == "n1" || $scope.lv.type == "e1") {
                $scope.lv1Loading = true;
            } else {
                $scope.lv2Loading = true;
            }
            $http.post("/webservice/admin/create_update_menu", $scope.formMenu).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    if ($scope.lv.type == "n1") {
                        $scope.lv1IndexMenuList.push(responseData.data);
                    } else if ($scope.lv.type == "e1") {
                        $scope.lv1IndexMenuList[$scope.formMenu.i] = responseData.data;
                    } else if ($scope.lv.type == "n2") {
                        $scope.lv2IndexMenuList.push(responseData.data);
                    } else {
                        $scope.lv2IndexMenuList[$scope.formMenu.i] = responseData.data;
                    }
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                    $scope.showLvBlock = false;
                }
                $scope.lv1Loading = false;
                $scope.lv2Loading = false;
                $scope.formMenu.submitting = false;
            });

        }
    }]);
