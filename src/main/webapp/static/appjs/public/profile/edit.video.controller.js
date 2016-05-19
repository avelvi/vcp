'use strict';

appProfile.controller('ProfileEditVideoController', ['$scope', '$routeParams', '$location', 'UsersService', 'VideoService', function($scope, $routeParams, $location, UsersService, VideoService){

    $scope.video = VideoService.get({id: $routeParams.videoId})

    $scope.update =  function(){
        VideoService.update({id: $routeParams.videoId}, $scope.video);
        $location.path('/profile/' + $routeParams.id);
    }

}]);