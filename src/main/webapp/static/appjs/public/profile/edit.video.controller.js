'use strict';

appProfile.controller('ProfileEditVideoController', ['$scope', '$routeParams', '$location', '$controller', 'UsersService', 'VideoService',
    function($scope, $routeParams, $location, $controller, UsersService, VideoService){

        $controller('ModalController', {$scope: $scope})
        VideoService.get({id: $routeParams.videoId}).$promise.then(
            function onsuccess(response){
                $scope.video = response;
            }
        );

        $scope.update =  function(){
            VideoService.update({id: $routeParams.videoId}, $scope.video).$promise.then(
                function onsuccess(){
                    $scope.open("success", "Video was updated", '/profile/' + $routeParams.id);
                },
                function onerror(response){
                    $scope.open("error", response.data.message);
                }
            );
        }

        $scope.cancel = function(){
            $location.path('/profile/' + $routeParams.id);
        }

}]);