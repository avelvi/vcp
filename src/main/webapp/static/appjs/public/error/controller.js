'use strict';

appError.controller('ErrorController', function ($scope, $rootScope, $routeParams) {
    $scope.code = $routeParams.code;
    $scope.message = $rootScope.message

    switch ($scope.code) {
        case "403" :
            $scope.message = "Sorry, but you don't have permissions to access this page"
            break;
        case "404" :
            $scope.message = "Page not found."
            break;
        case "500" :
            $scope.message = "Server internal error.";
        default:
            break;

    }

});