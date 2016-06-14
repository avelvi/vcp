'use strict';

appProfile.controller('ProfileEditController', ['$scope', '$routeParams', '$controller', '$location', 'UsersService', 'CompanyService',
    function($scope, $routeParams, $controller, $location, UsersService, CompanyService){

        $scope.user = UsersService.get({id: $routeParams.id});
        CompanyService.query({page: 0, size: Number.MAX_VALUE}).$promise.then(function(data){
            $scope.companies = data.content;
        });

        $controller('ModalController', {$scope: $scope});

        $scope.saveUser = function(){
            UsersService.update({id: $routeParams.id}, $scope.user).$promise.then(
                function onsuccess(response){
                    $scope.open("success", "Profile was updated", "/profile/" + $routeParams.id);
                },
                function onerror(response){
                    $scope.open("error", response.data.message);
                }
            )
        }

        $scope.cancel = function(){
            $location.path("/profile/" + $routeParams.id)
        }

        $scope.changePassword = function(){
            $location.path("/profile/" + $routeParams.id + "/changePassword")
        }

}]);