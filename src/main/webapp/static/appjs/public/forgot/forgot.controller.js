'use strict';

appForgot.controller('ForgotController', ['$scope', '$location', '$controller', 'ForgotService', function ($scope, $location, $controller, ForgotService) {

    $scope.forgotPasswordError = false;
    $scope.sentRestoreEmail = function () {
        $scope.forgotPasswordError = false;
        ForgotService.sentRestoreEmail({data: $scope.email}).$promise.then(
            function onsuccess(response){
                $controller('ModalController', {$scope: $scope})
                $scope.open("success", "Email was sent successful", "/signin");
            },
            function onerror(response){
                $scope.forgotPasswordError = true;
                $scope.forgotPasswordErrorMessage = response.data.message;
            }
        );
    }
}]);