'use strict';

appSearch.controller('SearchController', ['$scope', '$location', function($scope, $location){

    $scope.query = '';
    $scope.find = function (){
        if($scope.query.trim() != '') {
            $location.path('/search').search({query: $scope.query});
            $scope.query = '';
        } else{
            $location.path('/videos')
        }
    };
    $scope.onEnter = function(keyEvent) {
        if (keyEvent.which === 13) {
            $scope.find();
        }
    }

}]);