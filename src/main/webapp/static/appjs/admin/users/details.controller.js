'use strict';

appUsers.controller('UserDetailsController', ['$scope', '$routeParams', '$location', '$controller', 'CompanyService', 'AuthorityService', 'UsersService',
    function($scope, $routeParams, $location, $controller, CompanyService, AuthorityService, UsersService){

        $scope.user = {
            active: false
        }

        $scope.isNew = true;

        $controller('ModalController', {$scope: $scope});

        AuthorityService.getAuthorities().then(function(data){
            $scope.authorities = data;
        });

        CompanyService.query({page: 0, size: Number.MAX_VALUE}).$promise.then(function(data){
            $scope.companies = data.content;
        });


        if($routeParams.id !== 'new'){
            $scope.isNew = false;
            UsersService.get({id: $routeParams.id}).$promise.then(function(data){
                $scope.user = data;
                if($scope.user.authorities.length == 2){
                    $scope.selectedAuthority =
                        $scope.user.authorities[$scope.user.authorities.map(
                            function(e) {
                                return e.name;
                            }
                        ).indexOf('admin')];
                } else {
                    $scope.selectedAuthority = $scope.user.authorities[0]
                }
            });

        }

        $scope.saveUser = function(){
            if($scope.selectedAuthority.name == 'admin'){
                $scope.user.authorities = $scope.authorities;
            } else {
                $scope.user.authorities = [];
                $scope.user.authorities.push($scope.selectedAuthority);
            }

            if($routeParams.id === 'new'){
                if($scope.user.password !== $scope.repeat_password){
                    $scope.open('error', 'Password don\'t match');
                } else {
                    UsersService.save($scope.user).$promise.then(
                        function onsuccess(response){
                            $scope.open("success", "User was created", "/admin/users");
                        },
                        function onerror(response){
                            $scope.open("error", response.data.message);
                        }
                    )
                }

            } else {
                UsersService.update({id: $routeParams.id}, $scope.user).$promise.then(
                    function onsuccess(response){
                        $scope.open("success", "User was updated", "/admin/users");
                    },
                    function onerror(response){
                        $scope.open("error", response.data.message);
                    }
                )

            }


        }
        $scope.cancel = function(){
            $location.path('/admin/users')
        }

        $scope.change = function(selectedAuthority){
            console.log(selectedAuthority);
        }
}]);