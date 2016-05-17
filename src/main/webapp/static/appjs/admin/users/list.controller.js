'use strict';

appUsers.controller('UsersController', ['$scope', '$location', 'ENTRIES_PER_PAGE', 'UsersService', function($scope, $location, ENTRIES_PER_PAGE, UsersService){
    $scope.entriesPerPage = ENTRIES_PER_PAGE;
    var search = $location.search();
    var page = search.page||0;
    var size = search.size||5;
    $scope.size = size;
    $scope.users = UsersService.query({page: page, size: size});

    $scope.onNumPerPageChange = function(size){
        this.goToPage(0, size);
    }

    $scope.goToPage = function(page, size){
        $scope.users = UsersService.query({page: page, size: size});
    }

    $scope.editUser = function(id){
        $location.path('/admin/users/' + id + '/edit');
    }

    $scope.deleteUser = function(id){
        UsersService.delete({id: id})
        $scope.users = UsersService.query({page: page, size: size});
    }

    $scope.createUser = function(){
        $location.path('/admin/users/new/edit')
    }
}]);