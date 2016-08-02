'use strict';

appUsers.controller('UsersController', ['$scope', '$location', '$controller', 'ENTRIES_PER_PAGE', 'UsersService',
    function($scope, $location, $controller, ENTRIES_PER_PAGE, UsersService){
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
        $controller('ModalController', {$scope: $scope})
        $controller('ConfirmController', {$scope: $scope})

        $scope.openConfirm('Are you sure you want to delete this entry', function(result) {
            if (result) {
                UsersService.delete({id: id}).$promise.then(
                    function onsuccess() {
                        $scope.open("success", "User was deleted");
                    },
                    function onerror(response) {
                        $scope.open("error", response.data.message);

                    }
                ).finally(function(){
                        $scope.users = UsersService.query({page: page, size: size});
                    })
            }
        })
    }

    $scope.showVideos = function(userId){
        $location.path('/admin/users/' + userId + '/videos');
    }

    $scope.createUser = function(){
        $location.path('/admin/users/new/edit')
    }
}]);