app.controller('AladingwebSearchController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
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
                    url: "/webservice/aldingweb/upload_file_alading_web_search",
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
            $http.get("/webservice/aldingweb/alading_web_search_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch).success(function (responseData) {
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
            $scope.listLoading = true;
            $http.get("/webservice/aldingweb/createPic").success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                    if (responseData.success == "-1") {
                        $state.go('access.signin');
                    }
                } else {
                    $.scojs_message("生成成功", $.scojs_message.TYPE_OK);
                }
                $scope.getList(1, 10);
            });
        };

        $scope.deleteItems = function () {
            var checkedList = new Array();
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].lableId == true) {
                    checkedList.push($scope.list[i]);
                }
            }
            if (checkedList.length < 1) {
                $.scojs_message("请至少选择一个要删除的对象", $.scojs_message.TYPE_ERROR);
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'confirm.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "作废确认", content: "确定要删除已经选定的元素吗？", ok: "确定", cancel: "取消"};
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
                        $http.post("/webservice/aldingweb/delete_alading_web_search", {ids: data}).success(function (responseData) {
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


        $scope.deleteItemsAll = function () {
            var modalInstance = $modal.open({
                templateUrl: 'confirm.html',
                controller: 'ConfirmCtrl', resolve: {
                    modal: function () {
                        return {title: "作废确认", content: "确定要删除所有的元素吗？", ok: "确定", cancel: "取消"};
                    }
                }
            });
            modalInstance.result.then(function (confirm) {
                if (confirm) {
                    $scope.listLoadingData = true;
                    $http.post("/webservice/aldingweb/delete_alading_web_search_all", {}).success(function (responseData) {
                        if (responseData.success !== "1") {
                            $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                        } else {
                            $scope.pageChanged();
                        }
                        $scope.listLoadingData = false;
                    });
                }
            });
        };
    }]);




app.controller('AladingwebApplyController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.listSearch = "";

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
            $http.get("/webservice/aldingweb/alading_web_apply_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch).success(function (responseData) {
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

    }]);


app.controller('AladingwebSpreadController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.listSearch = "";

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
            $http.get("/webservice/aldingweb/alading_web_spread_list?pageIndex=" + $scope.currentPage + "&search=" + $scope.listSearch).success(function (responseData) {
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

    }]);


app.controller('AladingwebConfigController', ['$scope', '$http', '$modal', '$location', "$state", function ($scope, $http, $modal, $location, $state) {

        //上传文件
        $scope.myFile = null;
        $scope.activeMyFile = false;//正在上传
        //上传方法
        $scope.submitForm = function () {
            $scope.activeMyFile = true;
            var fd = new FormData();
            var file = document.querySelector('input[type=file][name=a1]').files[0];
            var file2 = document.querySelector('input[type=file][name=a2]').files[0];
            if (file == null || file.length < 1 || file2 == null || file2.length < 1) {
                $.scojs_message("请选择文件", $.scojs_message.TYPE_ERROR);
                $scope.activeMyFile = false;
            } else {
                fd.append('file1', file);
                fd.append('file2', file2);
                $http({
                    method: 'POST',
                    url: "/webservice/aldingweb/upload_file_alading_web_config",
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
                });
            }
        }

    }]);