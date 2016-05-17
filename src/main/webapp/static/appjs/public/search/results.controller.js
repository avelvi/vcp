'use strict';

appSearch.controller('SearchResultsController', ['$scope', '$routeParams', '$location', 'SearchService', function($scope, $routeParams, $location, SearchService){

    $scope.loading = true;
    var search = $location.search();
    var query = search.query;
    var page = search.page||0;
    $scope.videos = SearchService.findResults({query: query});
    $scope.loading = false;

    $scope.loadMore = function(page){
        $scope.loading = true;
        query = search.query;
        page = $scope.videos.number + 1;
        var newData = SearchService.findResults({query: query, page: page});
        $scope.videos.content = $scope.videos.content.concat(newData.content);
        $scope.loading = false;
    }

}]);