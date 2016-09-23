'use strict';

// logout controller
app.controller('LogoutController', ['$scope', '$http', '$state', function ($scope, $http, $state) {
        $http.post('/admin/logout', {});
        $state.go('access.signin');
    }]);