'use strict';

appCategory.controller('CategoryController', ['$scope', '$location', '$controller', 'ENTRIES_PER_PAGE', 'CategoryService',
    function($scope, $location, $controller, ENTRIES_PER_PAGE, CategoryService){
    $scope.entriesPerPage = ENTRIES_PER_PAGE;
    var search = $location.search();
    var page = search.page||0;
    var size = search.size||5;
    $scope.size = size;
    $scope.categories = CategoryService.query({page: page, size: size});

    $scope.onNumPerPageChange = function(size){
        this.goToPage(0, size);
    }

    $scope.goToPage = function(page, size){
        $scope.categories = CategoryService.query({page: page, size: size});
    }

    $scope.editCategory = function(id){
        $location.path('/admin/categories/' + id + '/edit');
    }

    $scope.deleteCategory = function(id){
        $controller('ModalController', {$scope: $scope})
        $controller('ConfirmController', {$scope: $scope})

        $scope.openConfirm('Are you sure you want to delete this entry', function(result){
            if(result){
                CategoryService.delete({id: id}).$promise.then(
                    function onsuccess(){
                        $scope.open("success", "Category was deleted");

                    },
                    function onerror(response){
                        $scope.open("error", response.data.message);
                    }
                ).finally(function(){
                        $scope.categories = CategoryService.query({page: page, size: size});
                    })
            }
        });
    }

    $scope.createCategory = function(){
        $location.path('/admin/categories/new/edit')
    }
}]);