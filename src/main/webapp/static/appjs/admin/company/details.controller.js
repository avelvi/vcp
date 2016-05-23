'use strict';

appCompany.controller('CompanyDetailsController', ['$scope', '$routeParams', '$location', '$controller', 'CompanyService',
    function($scope, $routeParams, $location,$controller, CompanyService){
    if($routeParams.id !== 'new'){
        $scope.company = CompanyService.get({id: $routeParams.id});
    }
    $scope.saveCompany = function(){
        $controller('ModalController', {$scope: $scope})
        if($routeParams.id === 'new'){
            CompanyService.save($scope.company).$promise.then(
                function onsuccess(){
                    $scope.open("success", "Company was created", "/admin/companies");
                },
                function onerror(response){
                    $scope.open("error", response.data.message);
                }
            )
        } else {
            CompanyService.update({id: $routeParams.id}, $scope.company).$promise.then(
                function onsuccess(){
                    $scope.open("success", "Company was updated", "/admin/companies");
                },
                function onerror(response){
                    $scope.open("error", response.data.message);
                }
            )
        }

    }
    $scope.cancel = function(){
        $location.path('/admin/companies')
    }
}]);