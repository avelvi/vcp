'use strict';

appVideo.controller('VideoController', ['$scope', '$location', '$controller', 'VideoService',
    function($scope, $location, $controller, VideoService){
        $controller('ModalController', {$scope: $scope})
        $scope.loading = true;
        VideoService.query().$promise.then(
            function onsuccess(response){
                $scope.videos = response;
                $scope.loading = false;
            }
        );

        $scope.loadMore = function(){
            $scope.loading = true;
            var page = $scope.videos.number + 1;
            VideoService.query({page: page}).$promise.then(
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