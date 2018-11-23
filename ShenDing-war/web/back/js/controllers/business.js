'use strict';
/* Controllers */
// business controller
app.controller('SeekInfoController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.seek = null;
        $scope.seekId = $stateParams.id;
        $scope.getSeek = function () {
            $http.get("/webservice/admin/seek_info?id=" + $scope.seekId).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.seek = responseData.data;
                }
            });
        };
        if ($scope.seekId != null && $scope.seekId > 0) {
            $scope.getSeek();
        } else {
            $.scojs_message("ERROR", $.scojs_message.TYPE_ERROR);
        }
    }]);
app.controller('GoodsController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.listSearch = "";
        $scope.provinceList = null;
        $scope.province = null;
        $scope.goodsTypeList = null;
        $scope.goodsStatusList = null;
        $scope.goods = {show: false, submitting: false};
        $scope.searchUserMsg = "";
        $scope.orderBy = null;
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
            if (params.search != null & params.search != $scope.listSearch) {
                $scope.listSearch = params.search;
                loadPage = true;
            }
            if (params.page != null & params.page != $scope.currentPage) {
                $scope.currentPage = params.page;
                loadPage = true;
            }
            if (params.orderBy != null & params.orderBy != $scope.orderBy) {
                $scope.orderBy = params.orderBy;
                loadPage = true;
            }
            if (loadPage) {
                $scope.pageChanged();
            }
        });
        $scope.orderTable = function (orderBy) {
            if ($scope.orderBy == orderBy) {
                $scope.orderBy = null;
            } else {
                $scope.orderBy = orderBy;
            }
            $scope.pageChanged();
        }
        if ($location.search().orderBy != null) {
            $scope.orderBy = $location.search().orderBy;
        }
        if ($location.search().search != null) {
            $scope.listSearch = $location.search().search;
        }

        if ($location.search().page != null) {
            $scope.currentPage = $location.search().page;
        }

        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listSearch = "";
                $scope.listLoading = true;
            }
            $location.search("search", $scope.listSearch);
            $location.search("orderBy", $scope.orderBy);
            if ($scope.currentPage != null) {
                $location.search("page", $scope.currentPage);
            }
            $http.get("/webservice/admin/goods_list?pageIndex=" + $scope.currentPage + "&orderBy=" + $scope.orderBy + "&category=SERVICE_PEOPLE&search=" + $scope.listSearch).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList($scope.currentPage, false);
        $scope.edit = function () {
            var checkedFlag = 0;
            var index = -1;
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].lableId == true) {
                    index = i;
                    checkedFlag++;
                }
            }
            if (checkedFlag == 0) {
                $.scojs_message("请选择一个商品", $.scojs_message.TYPE_ERROR);
            } else if (checkedFlag > 1) {
                $.scojs_message("只能选择一个商品", $.scojs_message.TYPE_ERROR);
            } else {
                $scope.goods.setType = "edit";
                $scope.goods.title = "修改用户信息";
                $scope.goods.show = true;
                $scope.goods.submitting = false;
                $scope.goods.i = index;
                $scope.goods.id = $scope.list[index].id;
                $scope.goods.name = $scope.list[index].name;
                $scope.goods.type = $scope.list[index].type;
                $scope.goods.price = $scope.list[index].price;
                $scope.goods.weChatCode = $scope.list[index].weChatCode;
                $scope.goods.qqCode = $scope.list[index].qqCode;
                $scope.province = $scope.list[index].province;
                $scope.goods.peopleCount = $scope.list[index].peopleCount;
                $scope.checkedEle = $scope.list[index].user;
                if ($scope.list[index].user.idCard == null) {
                    $scope.searchUserMsg = $scope.list[index].user.name + " (" + $scope.list[index].user.roleString + ")";
                } else {
                    $scope.searchUserMsg = $scope.list[index].user.name + " (" + $scope.list[index].user.roleString + ") " + $scope.list[index].user.idCard;
                }
                $location.url("#a_goods");
            }
        };
        $scope.add = function () {
            $scope.checkedEle != null;
            $scope.goods.setType = "add";
            $scope.goods.title = "创建商品";
            $scope.goods.show = true;
            $scope.goods.submitting = false;
            $scope.goods.i = null;
            $scope.goods.weChatCode = null;
            $scope.goods.qqCode = null;
            $scope.goods.id = null;
            $scope.goods.name = null;
            $scope.goods.type = $scope.goodsTypeList[0].key;
            $scope.goods.price = $scope.goodsTypeList[0].price;
            $scope.goods.peopleCount = 0;
            $scope.province = null;
            $scope.searchUserMsg = null;
            $location.url("#a_goods");
        };
        $scope.deleteItems = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].lableId == true) {
                    checkedList.push($scope.list[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要删除的商品", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "删除确认", content: "确定要删除已经选定的商品吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.listLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/delete_goods", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.listLoadingData = false;
                        });
                    }
                });
            }
        };
        $scope.changeGoodsType = function () {
            for (var i = 0; i < $scope.goodsTypeList.length; i++) {
                if ($scope.goods.type == $scope.goodsTypeList[i].key) {
                    $scope.goods.price = $scope.goodsTypeList[i].price;
                }
            }
        };
        $http.get("/webservice/admin/data_province").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
            } else {
                $scope.provinceList = responseData.data;
            }
        });
        $http.get("/webservice/admin/goods_status_and_type_list").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
            } else {
                $scope.goodsTypeList = responseData.typeList;
                $scope.goodsStatusList = responseData.statusList;
            }
        });
        $scope.searchListLoadingData = false;
        $scope.searchTotalItems = 0;
        $scope.searchList = null;
        $scope.searchUser = function () {
            if ($scope.searchUserMsg == "") {
                $.scojs_message("请输入关键字", $.scojs_message.TYPE_ERROR);
                return;
            }
            $scope.searchListLoadingData = true;
            $http.get("/webservice/admin/user_list?pageIndex=" + $scope.currentPage + "&approve=0&search=" + $scope.searchUserMsg).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.searchList = responseData.data;
                    $scope.searchTotalItems = responseData.totalCount;
                }
                $scope.searchListLoadingData = false;
            });
        };
        $scope.checkedEle = null;
        $scope.checkEle = function (ele) {
            $scope.searchList = null;
            $scope.checkedEle = ele;
            if (ele.idCard == null) {
                $scope.searchUserMsg = ele.name + " (" + ele.roleString + ")";
            } else {
                $scope.searchUserMsg = ele.name + " (" + ele.roleString + ") " + ele.idCard;
            }
        };
        $scope.submitForm = function () {
            $scope.goods.submitting = true;
            $http.post("/webservice/admin/create_update_goods", {category: "SERVICE_PEOPLE", id: $scope.goods.id, peopleCount: $scope.goods.peopleCount, name: $scope.goods.name, type: $scope.goods.type, price: $scope.goods.price, uid: $scope.checkedEle.id, province: $scope.province, weChatCode: $scope.goods.weChatCode, qqCode: $scope.goods.qqCode}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                    $scope.pageChanged();
                    if ($scope.goods.type != "add") {
                        $scope.list[$scope.goods.i] = responseData.data;
                        $scope.list[$scope.goods.i].lableId = true;
                    }
                    $scope.goods.show = false;
                }
                $scope.goods.submitting = false;
            });
        };
    }]);
app.controller('OrderController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.listSearch = "";
        $scope.orderBy = null;
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
            if (params.search != null & params.search != $scope.listSearch) {
                $scope.listSearch = params.search;
                loadPage = true;
            }
            if (params.page != null & params.page != $scope.currentPage) {
                $scope.currentPage = params.page;
                loadPage = true;
            }
            if (params.orderBy != null & params.orderBy != $scope.orderBy) {
                $scope.orderBy = params.orderBy;
                loadPage = true;
            }
            if (loadPage) {
                $scope.pageChanged();
            }
        });

        $scope.orderTable = function (orderBy) {
            if ($scope.orderBy == orderBy) {
                $scope.orderBy = null;
            } else {
                $scope.orderBy = orderBy;
            }
            $scope.pageChanged();
        }
        if ($location.search().search != null) {
            $scope.listSearch = $location.search().search;
        }

        if ($location.search().page != null) {
            $scope.currentPage = $location.search().page;
        }
        if ($location.search().orderBy != null) {
            $scope.orderBy = $location.search().orderBy;
        }
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listSearch = "";
                $scope.listLoading = true;
            }
            $location.search("search", $scope.listSearch);
            $location.search("orderBy", $scope.orderBy);
            if ($scope.currentPage != null) {
                $location.search("page", $scope.currentPage);
            }
            $http.get("/webservice/admin/order_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch + "&orderBy=" + $scope.orderBy + "&category=SERVICE_PEOPLE").success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList($scope.currentPage, false);
        $scope.add = function () {
            $state.go('app.business.create_order');
        };
        $scope.deleteItems = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].lableId == true) {
                    checkedList.push($scope.list[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要作废的订单", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "作废确认", content: "确定要作废已经选定的订单吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.listLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/delete_order", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.listLoadingData = false;
                        });
                    }
                });
            }
        };

        $scope.wagePlaceList = function () {
            window.open("/admin/DOWN_GOODS_LIST");
        };

        //上传文件
        $scope.myFile = null;
        $scope.activeMyFile = false;//正在上传
        //上传方法
        $scope.uploadFile = function () {
            $scope.activeMyFile = true;
            var fd = new FormData();
            var file = document.querySelector('input[type=file]').files[0];
            if (file == null || file.length < 1) {
                $.scojs_message("请选择文件", $.scojs_message.TYPE_ERROR);
                $scope.activeMyFile = false;
            } else {
                fd.append('file1', file);
                $http({
                    method: 'POST',
                    url: "/webservice/admin/upload_file_order",
                    data: fd,
                    headers: {'Content-Type': undefined},
                    transformRequest: angular.identity
                }).success(function (response) {
                    if (response.success == 1) {
                        //上传成功的操作
                        $.scojs_message(response.msg, $.scojs_message.TYPE_OK);
                    } else {
                        $.scojs_message(response.msg, $.scojs_message.TYPE_ERROR);
                    }
                    $scope.activeMyFile = false;
                    $scope.currentPage = 1;
                    $scope.pageChanged();
                });
            }


        }

        $scope.backOrder = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].lableId == true) {
                    checkedList.push($scope.list[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要回收的订单", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "回收确认", content: "确定要回收已经选定的订单吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.listLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/back_order", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.listLoadingData = false;
                        });
                    }
                });
            }
        };

        $scope.backOrderReturn = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].lableId == true) {
                    checkedList.push($scope.list[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要撤销回收的订单", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "撤销回收确认", content: "确定要撤销回收已经选定的订单吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.listLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/back_order_return", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.listLoadingData = false;
                        });
                    }
                });
            }
        };
    }]);
app.controller('ContractListController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.listSearch = "";
        $scope.order = {show: false, submitting: false};
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
            if (params.search != null & params.search != $scope.listSearch) {
                $scope.listSearch = params.search;
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
            $scope.listSearch = $location.search().search;
        }

        if ($location.search().page != null) {
            $scope.currentPage = $location.search().page;
        }
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listSearch = "";
                $scope.listLoading = true;
            }
            $location.search("search", $scope.listSearch);
            if ($scope.currentPage != null) {
                $location.search("page", $scope.currentPage);
            }
            $http.get("/webservice/admin/order_list?contract=true&category=SERVICE_PEOPLE&pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList($scope.currentPage, false);
        $scope.userList = null;
        $scope.userListLoadingData = false;
        $scope.userListTotalItems = 0;
        $scope.checkedUser = null;
        $scope.searchUserMsg = null;
        $scope.searchUser = function () {
            $scope.userListLoadingData = true;
            $http.get("/webservice/admin/user_list?search=" + $scope.searchUserMsg).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.userList = responseData.data;
                    $scope.userListTotalItems = responseData.totalCount;
                }
                $scope.userListLoadingData = false;
            });
        };
        $scope.checkUser = function (ele) {
            $scope.userList = null;
            $scope.checkedUser = ele;
            if (ele.idCard == null) {
                $scope.searchUserMsg = ele.name + " (" + ele.roleString + ")";
            } else {
                $scope.searchUserMsg = ele.name + " (" + ele.roleString + ") " + ele.idCard;
            }
            if (ele.goodsMsg != null) {
                $scope.searchUserMsg = $scope.searchUserMsg + " " + ele.goodsMsg;
            }
        };
        $scope.edit = function (ele) {
            $scope.order.show = true;
            $scope.order.id = ele.id;
            $scope.order.goods = {name: ele.goods.name};
            $scope.order.contractSerialId = ele.contractSerialId;
            $scope.order.lastRenewDate = ele.lastRenewDateStr;
            if (ele.agentUser != null) {
                $scope.checkUser(ele.agentUser);
            } else {
                $scope.checkedUser = null;
                $scope.searchUserMsg = null;
            }
        };
        $scope.deleteItems = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].lableId == true) {
                    checkedList.push($scope.list[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要删除的订单", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "删除确认", content: "删除不会删除订单本身，只会解除签约人和加盟订单的关系，确定要这样吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.listLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/delete_contract_order", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.listLoadingData = false;
                        });
                    }
                });
            }
        };
        $scope.opened = false;

        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };

        $scope.downList = function () {
            window.open("/admin/contract_list?category=SERVICE_PEOPLE");
        };

        $scope.submitForm = function () {
            $scope.order.submitting = true;
            var lastRenewDate = "";
            if ($scope.order.lastRenewDate instanceof Date) {
                lastRenewDate = $scope.order.lastRenewDate.getFullYear() + "-" + ($scope.order.lastRenewDate.getMonth() + 1) + "-" + $scope.order.lastRenewDate.getDate();
            } else if ($scope.order.lastRenewDate != null) {
                lastRenewDate = $scope.order.lastRenewDate;
            }
            $http.post("/webservice/admin/sign_contract", {id: $scope.order.id, uid: $scope.checkedUser.id, contractSerialId: $scope.order.contractSerialId, lastRenew: lastRenewDate}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                    $scope.order.show = false;
                    $scope.pageChanged();
                }
                $scope.order.submitting = false;
            });
        };
    }]);

app.controller('CreateOrderController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.order = {submitting: false, amountType: 'EARNEST', payType: 'BANK_TRANSFER', setType: "add", franchiseDepartmentCommission: 0};
        $scope.saleGoodsList = null;
        $scope.saleGoodsListLoadingData = false;
        $scope.saleGoodsListTotalItems = 0;
        $scope.checkedAgent = null;
        $scope.searchAgentMsg = null;
        $scope.searchSaleGoods = function () {
            $scope.saleGoodsListLoadingData = true;
            $http.get("/webservice/admin/goods_list?search=" + $scope.searchAgentMsg + "&status=SALE&category=SERVICE_PEOPLE").success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.saleGoodsList = responseData.data;
                    $scope.saleGoodsListTotalItems = responseData.totalCount;
                }
                $scope.saleGoodsListLoadingData = false;
            });
        };
        $scope.commission = {};
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

        $scope.checkAgent = function (ele) {
            $scope.saleGoodsList = null;
            $scope.checkedAgent = ele;
            if ($scope.order.price == null) {
                $scope.order.price = ele.price;
            }
            $scope.searchAgentMsg = ele.name + " (" + ele.provinceStr + ") " + ele.user.roleString + "：" + ele.user.name;
            if (ele.type == 'GOVERNMENT_DIRECTLY' || ele.type == 'PROVINCIAL_CAPITAL') {
                $scope.order.divideAmount = $scope.commission.PROVINCIAL_CAPITAL;
            } else if (ele.type == 'HOT' || ele.type == 'PREFECTURE') {
                $scope.order.divideAmount = $scope.commission.PREFECTURE;
            } else {
                $scope.order.divideAmount = $scope.commission.OTHERS;
            }
        };
        $scope.divideUserList = null;
        $scope.divideUserListLoadingData = false;
        $scope.divideUserListTotalItems = 0;
        $scope.checkedDivideUser = null;
        $scope.searchDivideUserMsg = null;
        $scope.searchDivideUser = function () {
            if ($scope.searchDivideUserMsg == "" || $scope.searchDivideUserMsg == null) {
                $.scojs_message("请输入关键字", $.scojs_message.TYPE_ERROR);
                return;
            }
            $scope.divideUserListLoadingData = true;
            $http.get("/webservice/admin/user_list?approve=0&search=" + $scope.searchDivideUserMsg).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.divideUserList = responseData.data;
                    $scope.divideUserListTotalItems = responseData.totalCount;
                }
                $scope.divideUserListLoadingData = false;
            });
        };

        $scope.dicUserAmountList = [];

        $http.get("/webservice/admin/dic_def_user_amount_list?category=SERVICE_PEOPLE").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                if (responseData.success == "-1") {
                    $state.go('access.signin');
                }
            } else {
                $scope.dicUserAmountList = responseData.data;
            }
        });

        $scope.checkDivideUser = function (ele) {
            $scope.checkedDivideUser = ele;
            $scope.searchDivideUserMsg = ele.name + " (" + ele.roleString + ") ";
            $scope.divideUserList = null;
            if ($scope.checkedAgent != null) {
                if ($scope.checkedAgent.type == 'GOVERNMENT_DIRECTLY' || $scope.checkedAgent.type == 'PROVINCIAL_CAPITAL' || $scope.checkedAgent.type == 'HOT') {
                    $scope.order.userAmount = 200;
                } else if ($scope.checkedAgent.type == 'PREFECTURE') {
                    $scope.order.userAmount = 100;
                } else {
                    $scope.order.userAmount = 50;
                }
                for (var i = 0; i < $scope.dicUserAmountList.length; i++) {
                    if ($scope.checkedAgent.type == $scope.dicUserAmountList[i].type) {
                        $scope.order.userAmount = $scope.dicUserAmountList[i].amount;
                        break;
                    }
                }
            }
        };
        $scope.eles = [0];
        $scope.addEles = function () {
            if ($scope.eles.length > 4) {
                $.scojs_message("最多只能添加5个", $.scojs_message.TYPE_ERROR);
                return;
            }
            $scope.eles.push($scope.eles.length);
        };
        $scope.soldOutGoodsList = new Array();
        $scope.soldOutGoodsListLoadingData = new Array();
        $scope.soldOutGoodsListTotalItems = new Array();
        $scope.checkedRecommend = new Array();
        $scope.rate = new Array();
        $scope.searchRecommendMsg = new Array();
        $scope.searchSoldOutGoods = function (index) {
            $scope.soldOutGoodsListLoadingData[index] = true;
            $http.get("/webservice/admin/user_order_list?search=" + $scope.searchRecommendMsg[index]).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.soldOutGoodsList[index] = responseData.data;
                    $scope.soldOutGoodsListTotalItems[index] = responseData.totalCount;
                }
                $scope.soldOutGoodsListLoadingData[index] = false;
            });
        };
        $scope.checkRecommend = function (ele, index) {
            $scope.soldOutGoodsList[index] = null;
            $scope.checkedRecommend[index] = ele;
            if (ele.idCard != null) {
                $scope.searchRecommendMsg[index] = ele.name + " (" + ele.roleString + ") " + ele.idCard;
            } else {
                $scope.searchRecommendMsg[index] = ele.name + " (" + ele.roleString + ") ";
            }
            if (ele.goodsMsg != null) {
                $scope.searchRecommendMsg[index] = $scope.searchRecommendMsg[index] + " " + ele.goodsMsg + " " + ele.goodsCategory;
            }
        };
        $scope.userList = null;
        $scope.userListLoadingData = false;
        $scope.userListTotalItems = 0;
        $scope.checkedUser = null;
        $scope.searchUserMsg = null;
        $scope.searchUser = function () {
            $scope.userListLoadingData = true;
            $http.get("/webservice/admin/user_list?search=" + $scope.searchUserMsg).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.userList = responseData.data;
                    $scope.userListTotalItems = responseData.totalCount;
                }
                $scope.userListLoadingData = false;
            });
        };
        $scope.checkUser = function (ele) {
            $scope.userList = null;
            $scope.checkedUser = ele;
            if (ele.idCard == null) {
                $scope.searchUserMsg = ele.name + " (" + ele.roleString + ")";
            } else {
                $scope.searchUserMsg = ele.name + " (" + ele.roleString + ") " + ele.idCard;
            }
        };
        $scope.startOpen = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startOpened = true;
        };
        $scope.endOpen = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endOpened = true;
        };
        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };
        $scope.submitForm = function () {
            $scope.order.submitting = true;
            var not100 = false;
            if ($scope.checkedRecommend.length > 1) {
                var total = 0;
                for (var i = 0; i < $scope.checkedRecommend.length; i++) {
                    if ($scope.rate[i] == null) {
                        not100 = true;
                    } else {
                        total = total + parseInt($scope.rate[i]);
                    }
                }
                if (total != 100) {
                    not100 = true;
                }
            }
            var payDateTime = "";
            if ($scope.order.payDate instanceof Date) {
                payDateTime = $scope.order.payDate.getFullYear() + "-" + ($scope.order.payDate.getMonth() + 1) + "-" + $scope.order.payDate.getDate();
            }
            if (payDateTime != "" && ($scope.order.amount == null || $scope.order.amount == "")) {
                $.scojs_message("请输入汇款金额", $.scojs_message.TYPE_ERROR);
                $scope.order.submitting = false;
            } else if (payDateTime == "" && ($scope.order.amount != null && $scope.order.amount != "")) {
                $.scojs_message("请输入回款时间", $.scojs_message.TYPE_ERROR);
                $scope.order.submitting = false;
            } else if (not100) {
                $.scojs_message("分配比例总和不是100", $.scojs_message.TYPE_ERROR);
                $scope.order.submitting = false;
            } else {
                var recommendIds = new Array();
                var recommendOrderIds = new Array();
                for (var i = 0; i < $scope.checkedRecommend.length; i++) {
                    recommendIds.push($scope.checkedRecommend[i].id);
                    if ($scope.checkedRecommend[i].orderId == null) {
                        recommendOrderIds.push("");
                    } else {
                        recommendOrderIds.push($scope.checkedRecommend[i].orderId);
                    }
                }
                if ($scope.checkedDivideUser == null) {
                    $scope.checkedDivideUser = {id: null};
                }
                $http.post("/webservice/admin/create_update_order", {franchiseDepartmentCommission:$scope.order.franchiseDepartmentCommission,category: "SERVICE_PEOPLE", peopleCountFee: $scope.order.peopleCountFee, divideUserId: $scope.checkedDivideUser.id, userAmount: $scope.order.userAmount, remark: $scope.order.remark, id: $scope.order.id, divideAmount: $scope.order.divideAmount, backAmount: $scope.order.backAmount, price: $scope.order.price, amount: $scope.order.amount, orderRecordType: $scope.order.amountType, gatewayType: $scope.order.payType, payDate: payDateTime, goodsId: $scope.checkedAgent.id, recommendIds: recommendIds, recommendOrderIds: recommendOrderIds, rates: $scope.rate}).success(function (responseData) {
                    if (responseData.success !== "1") {
                        $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                        if (responseData.success == "-1") {
                            $state.go('access.signin');
                        }
                    } else {
                        $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                        $state.go('app.business.order');
                    }
                    $scope.order.submitting = false;
                });
            }
        };
        $scope.backPage = function () {
            $state.go('app.business.order');
        };

        $scope.myKeyup = function (e) {
            var keycode = window.event ? e.keyCode : e.which;
            if (keycode == 13) {
                if ($scope.order.payDate == null || $scope.order.payDate == '' || $scope.order.submitting || $scope.order.price == null || $scope.order.price < 0 || $scope.order.amount == null || $scope.order.amount < 0 || $scope.checkedAgent == null || $scope.checkedRecommend == null) {
                    return;
                }
                $scope.submitForm();
            }
        };

    }]);


app.controller('UpdateOrderController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.order = null;
        $scope.orderId = $stateParams.id;
        $scope.list = null;
        $scope.totalItems = null;
        $scope.peopleCountFee = "";
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
        $scope.orderRecord = {submitting: false, amountType: 'EARNEST', payType: 'BANK_TRANSFER'};
        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };
        $scope.submitForm = function () {
            $scope.orderRecord.submitting = true;
            var payDateTime = "";
            if ($scope.orderRecord.payDate instanceof Date) {
                payDateTime = $scope.orderRecord.payDate.getFullYear() + "-" + ($scope.orderRecord.payDate.getMonth() + 1) + "-" + $scope.orderRecord.payDate.getDate();
            }
            if (payDateTime != "" && ($scope.orderRecord.amount == null || $scope.orderRecord.amount < 0)) {
                $scope.order.submitting = false;
                $.scojs_message("请输入汇款金额", $.scojs_message.TYPE_ERROR);
            } else if (payDateTime == "" && ($scope.orderRecord.amount != null && $scope.orderRecord.amount >= 0)) {
                $scope.order.submitting = false;
                $.scojs_message("请输入回款时间", $.scojs_message.TYPE_ERROR);
            } else {
                $http.post("/webservice/admin/create_order_record", {category: "SERVICE_PEOPLE", peopleCountFee: $scope.peopleCountFee, orderId: $scope.orderId, amount: $scope.orderRecord.amount, orderRecordType: $scope.orderRecord.amountType, gatewayType: $scope.orderRecord.payType, payDate: payDateTime}).success(function (responseData) {
                    if (responseData.success !== "1") {
                        $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                        if (responseData.success == "-1") {
                            $state.go('access.signin');
                        }
                    } else {
                        $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                        $state.go('app.business.order');
                    }
                    $scope.orderRecord.submitting = false;
                });
            }
        };
        $scope.backPage = function () {
            $state.go('app.business.order');
        };

        $scope.myKeyup = function (e) {
            var keycode = window.event ? e.keyCode : e.which;
            if (keycode == 13) {
                if ($scope.orderRecord.submitting || $scope.orderRecord.amount == null || $scope.orderRecord.amount < 0 || $scope.orderRecord.payDate == null || $scope.orderRecord.payDate == '') {
                    return;
                }
                $scope.submitForm();
            }
        };
    }]);
app.controller('AdController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.listSearch = "";

        //上传文件
        $scope.myFile = null;
        $scope.activeMyFile = false;//正在上传
        //上传方法
        $scope.uploadFile = function () {
            $scope.activeMyFile = true;
            var fd = new FormData();
            var file = document.querySelector('input[type=file]').files[0];
            if (file == null || file.length < 1) {
                $.scojs_message("请选择文件", $.scojs_message.TYPE_ERROR);
                $scope.activeMyFile = false;
            } else {
                fd.append('file1', file);
                $http({
                    method: 'POST',
                    url: "/webservice/admin/upload_file_ad",
                    data: fd,
                    headers: {'Content-Type': undefined},
                    transformRequest: angular.identity
                }).success(function (response) {
                    if (response.success == 1) {
                        //上传成功的操作
                        $.scojs_message(response.msg, $.scojs_message.TYPE_OK);
                    } else {
                        $.scojs_message(response.msg, $.scojs_message.TYPE_ERROR);
                    }
                    $scope.activeMyFile = false;
                    $scope.currentPage = 1;
                    $scope.getList($scope.currentPage, false);

                });
            }


        }

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
            if (params.search != null & params.search != $scope.listSearch) {
                $scope.listSearch = params.search;
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
            $scope.listSearch = $location.search().search;
        }

        if ($location.search().page != null) {
            $scope.currentPage = $location.search().page;
        }
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listSearch = "";
                $scope.listLoading = true;
            }
            $location.search("search", $scope.listSearch);
            if ($scope.currentPage != null) {
                $location.search("page", $scope.currentPage);
            }
            $http.get("/webservice/admin/ad_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch + "&category=SERVICE_PEOPLE").success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList($scope.currentPage, false);
        $scope.add = function () {
            $state.go('app.business.create_ad');
        };
        $scope.deleteItems = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].lableId == true) {
                    checkedList.push($scope.list[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要作废的广告", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "作废确认", content: "确定要作废已经选定的广告吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.listLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/delete_ad", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.listLoadingData = false;
                        });
                    }
                });
            }
        };
        $scope.backOrder = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].lableId == true) {
                    checkedList.push($scope.list[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要回收的订单", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "回收确认", content: "确定要回收已经选定的订单吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        $scope.listLoadingData = true;
                        var data = [];
                        for (var i = 0; i < checkedList.length; i++) {
                            data.push(checkedList[i].id);
                        }
                        $http.post("/webservice/admin/back_order", {ids: data}).success(function (responseData) {
                            if (responseData.success !== "1") {
                                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                            } else {
                                $scope.pageChanged();
                            }
                            $scope.listLoadingData = false;
                        });
                    }
                });
            }
        };
    }]);
app.controller('CreateAdController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.ad = {submitting: false, payType: 'BANK_TRANSFER', amount: 0, userBalanceAmount: 0, userAmount: 0, categoryPlus: 'NORMAL'};
        $scope.goodsList = null;
        $scope.adLimitList = null;
        $http.get("/webservice/product/ad_limit_config_list").success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.adLimitList = responseData.data;
                }
            });
        $scope.goodsListLoadingData = null;
        $scope.goodsListTotalItems = null;
        $scope.checkedGoods = null;
        $scope.searchGoodsMsg = null;
        $scope.payDate = null;
        $scope.tempDate = null;
        $scope.ad.id = $stateParams.id;
        if ($scope.ad.id != null && $scope.ad.id != "") {
            $scope.submitting = true;
            $http.get("/webservice/admin/ad_info?id=" + $scope.ad.id).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.ad = responseData.data;
                    $scope.payDate = $scope.ad.payDate;
                    $scope.tempDate = new Date($scope.ad.payDate);
                    $scope.checkedGoods = $scope.ad.goods;
                }
                $scope.submitting = false;
            });
        }
        $scope.searchGoodsList = function () {
            $scope.goodsListLoadingData = true;
            $http.get("/webservice/admin/goods_list?status=SOLD_OUT&category=SERVICE_PEOPLE&search=" + $scope.searchGoodsMsg).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.goodsList = responseData.data;
                    $scope.goodsListTotalItems = responseData.totalCount;
                }
                $scope.goodsListLoadingData = false;
            });
        };
        $scope.checkGoods = function (ele, index) {
            $scope.goodsList = null;
            $scope.checkedGoods = ele;
            $scope.searchGoodsMsg = ele.name + " (" + ele.provinceStr + ") " + ele.typeString + " (" + ele.peopleCount + "人)";
        };
        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };
        $scope.submitForm = function () {
            $scope.ad.submitting = true;
            $scope.ad.category = "SERVICE_PEOPLE";
            if ($scope.payDate instanceof Date) {
                $scope.ad.payDate = $scope.payDate.getFullYear() + "-" + ($scope.payDate.getMonth() + 1) + "-" + $scope.payDate.getDate();
            } else if ($scope.tempDate instanceof Date) {
                $scope.ad.payDate = $scope.tempDate.getFullYear() + "-" + ($scope.tempDate.getMonth() + 1) + "-" + $scope.tempDate.getDate();
            }
            if ($scope.checkedGoods != null) {
                $scope.ad.goodsId = $scope.checkedGoods.id;
            }
            $http.post("/webservice/admin/create_update_new_ad", $scope.ad).success(function (responseData) {
                $scope.ad.submitting = false;
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                    $state.go('app.business.new_ad_list');
                }
            });
        };
        $scope.backPage = function () {
            $state.go('app.business.new_ad_list');
        };

        $scope.myKeyup = function (e) {
            var keycode = window.event ? e.keyCode : e.which;
            if (keycode == 13) {
                if ($scope.ad.submitting || $scope.ad.userAmount == null || $scope.ad.userAmount < 0 || $scope.ad.userBalanceAmount == null || $scope.ad.userBalanceAmount < 0 || $scope.ad.amount == null || $scope.ad.amount < 0 || $scope.ad.name == null || $scope.ad.name == '' || $scope.payDate == null) {
                    return;
                }
                $scope.submitForm();
            }
        };
    }]);
app.controller('UpdateAdController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
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
                    $scope.ad.newPayType = 'BANK_TRANSFER';
                    $scope.ad.submitting = false;
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
        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };
        $scope.totalAddAmount = 0;
        $scope.startOpened = new Array();
        $scope.adPush = new Array();
        $scope.amount = new Array();
        $scope.setAmount = function (index) {
            if ($scope.adPush[index].picCount < $scope.list[index].picCount) {
                $.scojs_message("图片不能数量不能减少", $.scojs_message.TYPE_ERROR);
                return;
            }
            if ($scope.adPush[index].pushType == 'SUPER' && $scope.adPush[index].limitType == 'DAY_15') {
                $scope.adPush[index].limitType == 'MONTH_1';
                $.scojs_message("半个月不能发送高级", $.scojs_message.TYPE_ERROR);
            }
            var j = 0;
            if ($scope.adPush[index].picCount == null || $scope.adPush[index].picCount < 4) {
                j = 0;
            } else if ($scope.adPush[index].picCount < 7) {
                if ($scope.adPush[index].pushType != 'SUPER') {
                    j = 50;
                }
            } else {
                j = 100;
            }
            var i = 300;
            if ($scope.adPush[index].pushType == 'SUPER') {
                if ($scope.list[index].goodsType == 'LV_400') {
                    i = 600;
                } else {
                    i = 500;
                }
            } else {
                if ($scope.list[index].goodsType == 'LV_400') {
                    i = 400;
                }
            }
            if ($scope.adPush[index].limitType == 'MONTH_1') {
                $scope.amount[index] = i + j;
            } else if ($scope.adPush[index].limitType == 'MONTH_2') {
                $scope.amount[index] = i * 2 + j * 2;
            } else if ($scope.adPush[index].limitType == 'MONTH_3') {
                if ($scope.adPush[index].pushType == 'SUPER') {
                    if ($scope.list[index].goodsType == 'LV_400') {
                        $scope.amount[index] = 1800 + j * 3;
                    } else {
                        $scope.amount[index] = 1500 + j * 3;
                    }
                } else {
                    if ($scope.list[index].peopleCount > 3500) {
                        $scope.amount[index] = 1000 + j * 3;
                    } else {
                        $scope.amount[index] = 800 + j * 3;
                    }
                }
            } else {
                if ($scope.list[index].goodsType == 'LV_400') {
                    $scope.amount[index] = 280;
                } else {
                    $scope.amount[index] = 200;
                }
            }
            if ($scope.list[index].isGiving) {
                $scope.amount[index] = 0;
            } else {
                $scope.amount[index] = $scope.amount[index] - $scope.list[index].amount;
            }
            $scope.totalAddAmount = 0;
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.amount[i] != null && !isNaN($scope.amount[i])) {
                    $scope.totalAddAmount = $scope.totalAddAmount + $scope.amount[i];
                }
            }
        }

        $scope.startOpen = function ($event, index) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startOpened[index] = true;
        };
        $scope.submitForm = function () {
            $scope.ad.submitting = true;
            var payDateTime = "";
            if ($scope.ad.newPayDate instanceof Date) {
                payDateTime = $scope.ad.newPayDate.getFullYear() + "-" + ($scope.ad.newPayDate.getMonth() + 1) + "-" + $scope.ad.newPayDate.getDate();
            }
            if (payDateTime == "") {
                $scope.ad.submitting = false;
                $.scojs_message("请输入回款时间", $.scojs_message.TYPE_ERROR);
            } else {
                var idList = new Array();
                var pushTypeList = new Array();
                var picCountList = new Array();
                var startDateList = new Array();
                var limitTypeList = new Array();
                for (var i = 0; i < $scope.list.length; i++) {
                    if ($scope.adPush[i].start == null) {
                        $scope.ad.submitting = false;
                        $.scojs_message("平台有未选择的起始时间", $.scojs_message.TYPE_ERROR);
                        return;
                    }
                    idList[i] = $scope.list[i].id;
                    if ($scope.adPush[i].picCount == null) {
                        picCountList[i] = 0;
                    } else {
                        picCountList[i] = $scope.adPush[i].picCount;
                    }
                    pushTypeList[i] = $scope.adPush[i].pushType;
                    if ($scope.adPush[i].start instanceof Date) {
                        startDateList[i] = $scope.adPush[i].start.getFullYear() + "-" + ($scope.adPush[i].start.getMonth() + 1) + "-" + $scope.adPush[i].start.getDate();
                    } else {
                        startDateList[i] = $scope.adPush[i].start;
                    }
                    limitTypeList[i] = $scope.adPush[i].limitType;
                }
                $http.post("/webservice/admin/update_ad", {id: $scope.ad.id, idList: idList, pushTypeList: pushTypeList, picCountList: picCountList, limitTypeList: limitTypeList, fee: $scope.ad.addFee, gatewayType: $scope.ad.newPayType, payDate: payDateTime}).success(function (responseData) {
                    $scope.ad.submitting = false;
                    if (responseData.success !== "1") {
                        $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                        if (responseData.success == "-1") {
                            $state.go('access.signin');
                        }
                    } else {
                        $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                        $state.go('app.business.ad');
                    }
                });
            }
        };
        $scope.backPage = function () {
            $state.go('app.business.ad');
        };
    }]);

app.controller('UserWageLogListController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        $scope.startDateOpened = false;
        $scope.endDateOpened = false;
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startDateOpened = true;
        };
        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endDateOpened = true;
        };
        $scope.dateOptions = {
            class: 'datepicker'
        };
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;

        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
                $scope.search = "";
            }
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            } else {
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            if (start == "" || end == "") {
                $.scojs_message("请输入时间", $.scojs_message.TYPE_ERROR);
                return;
            }
            $http.get("/webservice/admin/user_wage_log_list?pageIndex=" + $scope.currentPage + "&category=SERVICE_PEOPLE&search=" + $scope.search + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };

        $scope.wageLogList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            window.open("/admin/user_wage_log_list?startDate=" + start + "&category=SERVICE_PEOPLE&category=SERVICE_PEOPLE&endDate=" + end);
        };


        $scope.wageTotalList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            window.open("/admin/USER_WAGE_LOG_TOTAL_LIST?startDate=" + start + "&category=SERVICE_PEOPLE&endDate=" + end);
        };

        $scope.orderRecordList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            window.open("/admin/order_record_list?startDate=" + start + "&category=SERVICE_PEOPLE&endDate=" + end);
        };

    }]);


app.controller('PlaceUserController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.provinceList = null;
        $scope.province = null;
        $scope.goods = {submitting: false};
        $scope.searchUserMsg = "";
        $http.get("/webservice/admin/data_province").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
            } else {
                $scope.provinceList = responseData.data;
            }
        });
        $scope.searchListLoadingData = false;
        $scope.searchTotalItems = 0;
        $scope.searchList = null;
        $scope.searchUser = function () {
            if ($scope.searchUserMsg == "") {
                $.scojs_message("请输入关键字", $.scojs_message.TYPE_ERROR);
                return;
            }
            $scope.searchListLoadingData = true;
            $http.get("/webservice/admin/user_list?approve=0&search=" + $scope.searchUserMsg).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.searchList = responseData.data;
                    $scope.searchTotalItems = responseData.totalCount;
                }
                $scope.searchListLoadingData = false;
            });
        };
        $scope.checkedEle = null;
        $scope.checkEle = function (ele) {
            $scope.searchList = null;
            $scope.checkedEle = ele;
            if (ele.idCard == null) {
                $scope.searchUserMsg = ele.name + " (" + ele.roleString + ")";
            } else {
                $scope.searchUserMsg = ele.name + " (" + ele.roleString + ") " + ele.idCard;
            }
        };
        $scope.submitForm = function () {
            $scope.goods.submitting = true;
            $http.post("/webservice/admin/modify_place_user", {uid: $scope.checkedEle.id, province: $scope.province, category: "SERVICE_PEOPLE"}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.goods.submitting = false;
            });
        };
    }]);


app.controller('OrderUserPriceController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.goodsTypeList = null
        $scope.dic = {submitting: false};
        $http.get("/webservice/admin/goods_status_and_type_list").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
            } else {
                $scope.goodsTypeList = responseData.typeList;
            }
        });
        $scope.loadDic = function () {
            $scope.dic.submitting = true;
            $scope.dic.amount = null;
            $http.get("/webservice/admin/dic_def_user_amount?category=SERVICE_PEOPLE&type=" + $scope.dic.type).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else if (responseData.data != null) {
                    $scope.dic = responseData.data;
                }
                $scope.dic.submitting = false;
            });
        };
        $scope.submitForm = function () {
            $scope.dic.submitting = true;
            $http.post("/webservice/admin/dic_def_user_amount", {type: $scope.dic.type, amount: $scope.dic.amount, category: "SERVICE_PEOPLE"}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.dic.submitting = false;
            });
        };
    }]);

app.controller('GoodsPriceByTypeController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.goodsTypeList = null
        $scope.goods = {submitting: false};
        $http.get("/webservice/admin/goods_status_and_type_list").success(function (responseData) {
            if (responseData.success !== "1") {
                $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
            } else {
                $scope.goodsTypeList = responseData.typeList;
            }
        });
        $scope.submitForm = function () {
            $scope.goods.submitting = true;
            $http.post("/webservice/admin/modify_goods_price_all_by_type", {type: $scope.goods.type, price: $scope.goods.price, category: "SERVICE_PEOPLE"}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.goods.submitting = false;
            });
        };
    }]);

app.controller('OrderWageController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        $scope.startDateOpened = false;
        $scope.endDateOpened = false;
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startDateOpened = true;
        };
        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endDateOpened = true;
        };
        $scope.dateOptions = {
            class: 'datepicker'
        };
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
                $scope.search = "";
            }
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            if (start == "" || end == "") {
                $.scojs_message("请输入时间", $.scojs_message.TYPE_ERROR);
                return;
            }
            $http.get("/webservice/admin/order_wage_list?pageIndex=" + $scope.currentPage + "&category=SERVICE_PEOPLE&search=" + $scope.search + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                    if (responseData.anCount != null && responseData.iosCount != null) {
                        $scope.anCount = responseData.anCount;
                        $scope.iosCount = responseData.iosCount;
                    }
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };

        $scope.adList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            window.open("/admin/wage_log_list?startDate=" + start + "&category=SERVICE_PEOPLE&endDate=" + end);
        };

        $scope.orderRecordList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            window.open("/admin/order_record_list?startDate=" + start + "&category=SERVICE_PEOPLE&endDate=" + end);
        };
        
        
        $scope.franchiseDepartmentCommissionList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            window.open("/admin/franchise_department_commission?startDate=" + start + "&category=SERVICE_PEOPLE&endDate=" + end);
        };

        $scope.wageList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            window.open("/admin/order_wage_list?startDate=" + start + "&category=SERVICE_PEOPLE&endDate=" + end);
        };

    }]);

app.controller('WageController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.search = "";
        $scope.startDateOpened = false;
        $scope.endDateOpened = false;
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.startDateOpened = true;
        };
        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endDateOpened = true;
        };
        $scope.dateOptions = {
            class: 'datepicker'
        };
        /**
         * pagination
         */
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.maxSize = 5;
        $scope.pageChanged = function () {
            $scope.getList($scope.currentPage, false);
        };
        $scope.getList = function (page, isInit) {
            $scope.currentPage = page;
            if (!isInit) {
                $scope.listLoadingData = true;
            } else {
                $scope.listLoading = true;
                $scope.search = "";
            }
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            if (start == "" || end == "") {
                $.scojs_message("请输入时间", $.scojs_message.TYPE_ERROR);
                return;
            }
            $http.get("/webservice/admin/wage_list?pageIndex=" + $scope.currentPage + "&category=SERVICE_PEOPLE&search=" + $scope.search + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };

        $scope.adList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            window.open("/admin/ad_list?startDate=" + start + "&category=SERVICE_PEOPLE&endDate=" + end);
        };

        $scope.wageList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            window.open("/admin/wage_list?startDate=" + start + "&category=SERVICE_PEOPLE&endDate=" + end);
        };

        $scope.wageTotalList = function () {
            var start = "";
            var end = "";
            if ($scope.startDate instanceof Date) {
                start = $scope.startDate.getFullYear() + "-" + ($scope.startDate.getMonth() + 1) + "-" + $scope.startDate.getDate();
            }
            if ($scope.endDate instanceof Date) {
                end = $scope.endDate.getFullYear() + "-" + ($scope.endDate.getMonth() + 1) + "-" + $scope.endDate.getDate();
            }
            window.open("/admin/wage_total_list?startDate=" + start + "&category=SERVICE_PEOPLE&endDate=" + end);
        };

    }]);


app.controller('ModifyOrderUserController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.submitting = false;
        $scope.order = {};
        $scope.searchUserMsg = "";
        $scope.searchListLoadingData = false;
        $scope.searchTotalItems = 0;
        $scope.searchList = null;
        $scope.searchUser = function () {
            if ($scope.searchUserMsg == "") {
                $.scojs_message("请输入关键字", $.scojs_message.TYPE_ERROR);
                return;
            }
            $scope.searchListLoadingData = true;
            $http.get("/webservice/admin/user_list?approve=0&search=" + $scope.searchUserMsg).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.searchList = responseData.data;
                    $scope.searchTotalItems = responseData.totalCount;
                }
                $scope.searchListLoadingData = false;
            });
        };
        $scope.checkedEle = null;
        $scope.checkEle = function (ele) {
            $scope.searchList = null;
            $scope.checkedEle = ele;
            if (ele.idCard == null) {
                $scope.searchUserMsg = ele.name + " (" + ele.roleString + ")";
            } else {
                $scope.searchUserMsg = ele.name + " (" + ele.roleString + ") " + ele.idCard;
            }
        };

        $scope.searchOrder = function () {
            $http.get("/webservice/admin/find_order?serialId=" + $scope.order.serialId).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.order = responseData.data;
                    if ($scope.order.divideUser != null) {
                        $scope.checkedEle = $scope.order.divideUser;
                    }
                }
            });
        }
        $scope.submitForm = function () {
            $scope.submitting = true;
            $http.post("/webservice/admin/modify_order_user", {id: $scope.order.id, uid: $scope.checkedEle.id, userAmount: $scope.order.userAmount}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.submitting = false;
            });
        };
    }]);


app.controller('ModifyOrderDivideAmountController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.submitting = false;
        $scope.order = {id:null};

        $scope.searchOrder = function () {
            $http.get("/webservice/admin/find_order?serialId=" + $scope.order.serialId).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.order = responseData.data;
                }
            });
        }
        $scope.submitForm = function () {
            $scope.submitting = true;
            $http.post("/webservice/admin/modify_order_divide_amount", {id: $scope.order.id, divideAmount: $scope.order.divideAmount}).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.submitting = false;
            });
        };
    }]);

app.controller('UpdateSuccessOrderController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.order = null;
        $scope.submitting = false;
        $scope.getOrder = function () {
            $scope.submitting = true;
            $http.get("/webservice/admin/order_info?id=" + $stateParams.id).success(function (responseData) {
                $scope.submitting = false;
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.order = responseData.data;
                }
            });
        };

        if ($stateParams.id != null && $stateParams.id > 0) {
            $scope.getOrder();
        } else {
            $.scojs_message("ERROR", $.scojs_message.TYPE_ERROR);
        }

        $scope.submitForm = function () {
            $scope.submitting = true;
            $http.post("/webservice/admin/update_success_order", $scope.order).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                }
                $scope.submitting = false;
            });
        };
    }]);

app.controller('DeleteEarnestOrderController', ['$scope', '$http', '$modal', '$location', "$state", "$stateParams", function ($scope, $http, $modal, $location, $state, $stateParams) {
        $scope.order = null;
        $scope.submitting = false;
        $scope.getOrder = function () {
            $scope.submitting = true;
            $http.get("/webservice/admin/order_info?id=" + $stateParams.id).success(function (responseData) {
                $scope.submitting = false;
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $scope.order = responseData.data;
                    $scope.order.userAmount = 20;
                }
            });
        };

        if ($stateParams.id != null && $stateParams.id > 0) {
            $scope.getOrder();
        } else {
            $.scojs_message("ERROR", $.scojs_message.TYPE_ERROR);
        }

        $scope.submitForm = function () {
            $scope.submitting = true;
            if ($scope.order.date instanceof Date) {
                $scope.order.date = $scope.order.date.getFullYear() + "-" + ($scope.order.date.getMonth() + 1) + "-" + $scope.order.date.getDate();
            }
            $http.post("/webservice/admin/delete_earnest_order", $scope.order).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
                    $state.go('app.business.order');
                }
                $scope.submitting = false;
            });
        };

        $scope.opened = false;

        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };
    }]);