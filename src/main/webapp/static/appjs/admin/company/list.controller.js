'use strict';

appCompany.controller('CompanyController', ['$scope', '$location', '$controller', 'ENTRIES_PER_PAGE', 'CompanyService',
    function($scope, $location, $controller, ENTRIES_PER_PAGE, CompanyService){
    $scope.entriesPerPage = ENTRIES_PER_PAGE;
    var search = $location.search();
    var page = search.page||0;
    var size = search.size||5;
    $scope.size = size;
    $scope.companies = CompanyService.query({page: page, size: size});

    $scope.onNumPerPageChange = function(size){
        this.goToPage(0, size);
    }

    $scope.goToPage = function(page, size){
        $scope.companies = CompanyService.query({page: page, size: size});
    }

    $scope.editCompany = function(id){
        $location.path('/admin/companies/' + id + '/edit');
    }

    $scope.deleteCompany = function(id){
        $controller('ModalController', {$scope: $scope})
        CompanyService.delete({id: id}).$promise.then(
            function onsuccess(){
                $scope.open("success", "Company was deleted");
                $scope.companies = CompanyService.query({page: page, size: size});
            },
            function onerror(response){
                $scope.open("error", response.data.message);
                $scope.companies = CompanyService.query({page: page, size: size});
            }
        )

    }

    $scope.createCompany = function(){
        $location.path('/admin/companies/new/edit')
    }
}]);