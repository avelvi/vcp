'use strict';

appSearch.controller('SearchResultsController', ['$scope', '$routeParams', '$location', '$controller', 'SearchService',
    function($scope, $routeParams, $location, $controller, SearchService){
        $controller('ModalController', {$scope: $scope})
        $scope.loading = true;
        var search = $location.search();
        var query = search.query;
        SearchService.findResults({query: query}).$promise.then(
            function onsuccess(response){
                $scope.videos = response;
                $scope.loading = false;
            },
            function onerror(response){
                $scope.loading = false;
                $scope.open("error", response.data.message);
            }
        );

        $scope.loadMore = function(){
            $scope.loading = true;
            query = search.query;
            var page = $scope.videos.number + 1;

            SearchService.findResults({query: query, page: page}).$promise.then(
                function onsuccess(response){
                    response.content = $scope.videos.content.concat(response.content);
                    $scope.videos = response;
                    $scope.loading = false;

                },
                function onerror(response){
                    $scope.loading = false;
                    $scope.open("error", response.data.message);
                }
            );
        }

}]);