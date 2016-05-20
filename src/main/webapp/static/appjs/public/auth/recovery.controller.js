

'use strict';

appAuth.controller('RecoveryController', ['$scope', '$routeParams', '$location', 'AuthSharedService',
    function ($scope, $routeParams, $location, AuthSharedService) {

    $scope.changePassword = function(){
        if($scope.password !== $scope.repeat_password){
            $scope.changePasswordError = true;
            $scope.changePasswordErrorMessage = 'Password don\'t match';
        } else {
            $scope.registrationError = false;
            AuthSharedService.changePassword($routeParams.code, $scope.password)
                .then(function(){
                    $location.path('/signin')
                });

        }
    }

}]);