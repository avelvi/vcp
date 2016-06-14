'use strict';

appProfile.controller('ProfileChangePasswordController', ['$scope', '$routeParams', '$controller', '$location', 'UsersService',
    function($scope, $routeParams, $controller, $location, UsersService){

        $controller('ModalController', {$scope: $scope});

        $scope.updatePassword = function(){
            if($scope.newPassword !== $scope.confirmPassword){
                $scope.open("error", "Password mismatch");
            } else {
                var data = {
                    newPassword: $scope.newPassword
                }
                UsersService.changePassword({id: $routeParams.id}, data).$promise.then(
                    function onsuccess(response){
                        $scope.open("success", "Password was updated", "/profile/" + $routeParams.id);
                    },
                    function onerror(response){
                        $scope.open("error", response.data.message);
                    }
                )
            }
        }

        $scope.cancel = function(){
            $location.path("/profile/" + $routeParams.id)
        }

    }]);