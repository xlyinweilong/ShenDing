var userInfo = null;//user info
/**
 *get nav
 */
app.controller('NavCtrl', ['$scope', '$http', '$modal', '$state', function ($scope, $http, $modal, $state) {
        $http.get('/webservice/admin/nav').success(function (data) {
            $scope.navs = data.data;
            if (userInfo != null && userInfo.roleId == -1 && (userInfo.idCard == null || userInfo.bankCardCode == null)) {
                var modalInstance = $modal.open({
                    templateUrl: '/back/tpl/go_my_info.html',
                    controller: 'ConfirmCtrl', resolve: {
                        modal: function () {
                            return {title: "删除确认", content: "确定要删除已经选定的用户吗？", ok: "确定", cancel: "取消"};
                        }
                    }
                });
                modalInstance.result.then(function (confirm) {
                    if (confirm) {
                        userInfo.idCard = '';
                        userInfo.bankCardCode = '';
                        $state.go('app.setting.myinfo');
                    }
                });
            }
        });
    }]);
/**
 *get user info
 */
app.controller('UserCtrl', ['$scope', '$http', function ($scope, $http) {
        if (userInfo == null) {
            userInfo = {};
            $http.get('/webservice/admin/user_info').success(function (data) {
                $scope.user = data.user;
                userInfo = data.user;
            });
        } else {
            $scope.user = userInfo;
        }
    }]);
app.controller('ConfirmCtrl', ['$scope', '$modalInstance', 'modal', function ($scope, $modalInstance, modal) {
        $scope.modal = modal;
        $scope.okFunction = function () {
            $modalInstance.close(true);
        };
        $scope.cancelFunction = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);

app.controller('WageInfoCtrl', ['$scope', '$modalInstance', '$http', 'modal', function ($scope, $modalInstance, $http, modal) {
        $scope.modal = modal;
        $scope.okFunction = function () {
            $modalInstance.close(true);
        };
        $scope.cancelFunction = function () {
            $modalInstance.dismiss('cancel');
        };
        $scope.list = null;
        $scope.listLoading = false;
        $scope.listLoadingData = false;
        $scope.wageList = null;
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
            if ($scope.modal.start instanceof Date) {
                start = $scope.modal.start.getFullYear() + "-" + ($scope.modal.start.getMonth() + 1) + "-" + $scope.modal.start.getDate();
            }
            if ($scope.modal.end instanceof Date) {
                end = $scope.modal.end.getFullYear() + "-" + ($scope.modal.end.getMonth() + 1) + "-" + $scope.modal.end.getDate();
            }
            if (start == "" || end == "") {
                $.scojs_message("请输入时间", $.scojs_message.TYPE_ERROR);
                return;
            }
            $http.get("/webservice/admin/user_wage_info?pageIndex=" + $scope.currentPage + "&uid=" + $scope.modal.uid + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    if ($scope.currentPage == 1) {
                        $scope.wageList = responseData.wageList;
                    }
                    $scope.list = responseData.data;
                    $scope.totalItems = responseData.totalCount;
                }
                $scope.listLoadingData = false;
                $scope.listLoading = false;
            });
        };
        $scope.getList(1, true);

        $scope.adList = null;
        $scope.adListLoading = false;
        $scope.adlListLoadingData = false;
        /**
         * pagination
         */
        $scope.adTotalItems = 0;
        $scope.adCurrentPage = 1;
        $scope.pageAdChanged = function () {
            $scope.getAdList($scope.adCurrentPage, false);
        };
        $scope.getAdList = function (page, isInit) {
            $scope.adCurrentPage = page;
            if (!isInit) {
                $scope.adListLoadingData = true;
            } else {
                $scope.adListLoading = true;
            }
            var start = "";
            var end = "";
            if ($scope.modal.start instanceof Date) {
                start = $scope.modal.start.getFullYear() + "-" + ($scope.modal.start.getMonth() + 1) + "-" + $scope.modal.start.getDate();
            }
            if ($scope.modal.end instanceof Date) {
                end = $scope.modal.end.getFullYear() + "-" + ($scope.modal.end.getMonth() + 1) + "-" + $scope.modal.end.getDate();
            }
            if (start == "" || end == "") {
                $.scojs_message("请输入时间", $.scojs_message.TYPE_ERROR);
                return;
            }
            $http.get("/webservice/admin/user_wage_ad_info?pageIndex=" + $scope.adCurrentPage + "&uid=" + $scope.modal.uid + "&start=" + start + "&end=" + end).success(function (responseData) {
                if (responseData.success !== "1") {
                    $.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
                } else {
                    $scope.adList = responseData.data;
                    $scope.adTotalItems = responseData.totalCount;
                }
                $scope.adListLoadingData = false;
                $scope.adListLoading = false;
            });
        };
        $scope.getAdList(1, true);

    }]);