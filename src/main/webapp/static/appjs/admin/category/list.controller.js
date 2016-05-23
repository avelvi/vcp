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
        CategoryService.delete({id: id}).$promise.then(
            function onsuccess(){
                $scope.open("success", "Category was deleted");
                $scope.categories = CategoryService.query({page: page, size: size});
            },
            function onerror(response){
                $scope.open("error", response.data.message);
                $scope.categories = CategoryService.query({page: page, size: size});
            }
        )
    }

    $scope.createCategory = function(){
        $location.path('/admin/categories/new/edit')
    }
}]);