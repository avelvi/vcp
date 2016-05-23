

'use strict';

appForgot.controller('RecoveryController', ['$scope', '$routeParams', '$controller', 'ForgotService',
    function ($scope, $routeParams, $controller, ForgotService) {

    $scope.changePassword = function(){
        if($scope.password !== $scope.repeat_password){
            $scope.changePasswordError = true;
            $scope.changePasswordErrorMessage = 'Password don\'t match';
        } else {
            $scope.registrationError = false;
            var data = {
                code: $routeParams.code,
                password: $scope.password
            }
            ForgotService.changePassword(data).$promise.then(
                function onsuccess(response){
                    $controller('ModalController', {$scope: $scope})
                    $scope.open("success", "Password was changed successful", "/signin");
                },
                function onerror(response){
                    $scope.changePasswordError = true;
                    $scope.changePasswordErrorMessage = response.data.message;
                }
            )

        }
    }

}]);