'use strict';

appCategory.controller('CategoryDetailsController', ['$scope', '$routeParams', '$location', '$controller', 'CategoryService',
    function($scope, $routeParams, $location, $controller, CategoryService){
    if($routeParams.id !== 'new'){
        $scope.category = CategoryService.get({id: $routeParams.id});
    }
    $scope.saveCategory = function(){
        $controller('ModalController', {$scope: $scope})
        if($routeParams.id === 'new'){
            CategoryService.save($scope.category).$promise.then(
                function onsuccess(){
                    $scope.open("success", "Category was created", "/admin/categories");
                },
                function onerror(response){
                    $scope.open("error", response.data.message);
                }
            )
        } else {
            CategoryService.update({id: $routeParams.id}, $scope.category).$promise.then(
                function onsuccess(){
                    $scope.open("success", "Category was updated", "/admin/categories");
                },
                function onerror(response){
                    $scope.open("error", response.data.message);
                }
            )
        }

    }
    $scope.cancel = function(){
        $location.path('/admin/categories')
    }
}]);