'use strict';

appUsers.controller('UserDetailsController', ['$scope', '$routeParams', '$location', 'AuthorityService', 'UsersService', function($scope, $routeParams, $location, AuthorityService, UsersService){
    if($routeParams.id !== 'new'){
        $scope.user = UsersService.get({id: $routeParams.id});
    }

    AuthorityService.getAuthorities().then(function(data){
        $scope.authorities = data;
    });

    $scope.saveUser = function(){
        if($routeParams.id === 'new'){
            console.log($scope.user)
            //UsersService.save($scope.user)
        } else {
            UsersService.update({id: $routeParams.id}, $scope.user)
        }
        $location.path('/admin/users');

    }
    $scope.cancel = function(){
        $location.path('/admin/users')
    }
}]);