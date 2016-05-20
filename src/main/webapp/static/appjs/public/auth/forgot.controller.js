'use strict';

appAuth.controller('ForgotController', ['$scope', '$rootScope', '$location', 'AuthSharedService', function ($scope, $rootScope, $location, AuthSharedService) {

    $scope.sentRestoreEmail = function () {
        AuthSharedService.sendRestoreEmail(
            $scope.email
        ).then(
        function success(a,b,c,d){
            $location.path('/signin');
        },
        function error(a,b,c,d){
            $scope.forgotPasswordError = true;
            $scope.forgotPasswordErrorMessage = "User not found"
        });

    }
}]);