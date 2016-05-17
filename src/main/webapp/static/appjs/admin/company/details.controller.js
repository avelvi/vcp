'use strict';

appCompany.controller('CompanyDetailsController', ['$scope', '$routeParams', '$location', 'CompanyService', function($scope, $routeParams, $location, CompanyService){
    if($routeParams.id !== 'new'){
        $scope.company = CompanyService.get({id: $routeParams.id});
    }
    $scope.saveCompany = function(){
        if($routeParams.id === 'new'){
            CompanyService.save($scope.company)
        } else {
            CompanyService.update({id: $routeParams.id}, $scope.company)
        }
        $location.path('/admin/companies');

    }
    $scope.cancel = function(){
        $location.path('/admin/companies')
    }
}]);