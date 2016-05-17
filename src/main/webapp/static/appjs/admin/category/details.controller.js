'use strict';

appCategory.controller('CategoryDetailsController', ['$scope', '$routeParams', '$location', 'CategoryService', function($scope, $routeParams, $location, CategoryService){
    if($routeParams.id !== 'new'){
        $scope.category = CategoryService.get({id: $routeParams.id});
    }
    $scope.saveCategory = function(){
        if($routeParams.id === 'new'){
            CategoryService.save($scope.category)
        } else {
            CategoryService.update({id: $routeParams.id}, $scope.category)
        }
        $location.path('/admin/categories');

    }
    $scope.cancel = function(){
        $location.path('/admin/categories')
    }
}]);