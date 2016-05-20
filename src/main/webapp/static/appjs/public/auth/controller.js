'use strict';

appAuth.controller('AuthController', ['$scope', '$rootScope', '$location', 'AuthSharedService', function ($scope, $rootScope, $location, AuthSharedService) {
    $scope.rememberMe = false;
    $scope.doLogin = function () {
        $rootScope.authenticationError = false;
        AuthSharedService.login(
            $scope.login,
            $scope.password,
            $scope.rememberMe
        );

    }
    $scope.doRegistration = function () {
        if($scope.password !== $scope.repeat_password){
            $rootScope.registrationError = true;
            $rootScope.registrationErrorMessage = 'Password don\'t match';
        } else {
            $rootScope.registrationError = false;
            AuthSharedService.register(
                    $scope.name,
                    $scope.surname,
                    $scope.login,
                    $scope.email,
                    $scope.password
            );

        }

    }
}]);

appAuth.controller('ActivationController', ['$scope', '$routeParams', 'AuthSharedService', function ($scope, $routeParams, AuthSharedService) {
    var code = $routeParams.code;
    AuthSharedService.activate(code);
}])