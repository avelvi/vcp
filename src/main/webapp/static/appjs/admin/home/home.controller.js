'use strict';

appAdminHome.controller('AdminHomeController', ['$scope', 'AdminHomeService',
    function($scope, AdminHomeService){
        $scope.title = 'Portal Statistic';

        AdminHomeService.get().$promise.then(function(data){
            $scope.statistics = data;
        });
    }
]);