'use strict';

appProfile.controller('ProfileController', ['$scope', '$routeParams', '$location', 'UsersService', 'VideoService', function($scope, $routeParams, $location, UsersService, VideoService){

    $scope.user = UsersService.get({id: $routeParams.id});

    $scope.videos = UsersService.getUserVideos({id: $routeParams.id})

    $scope.deleteVideo = function(id){
        VideoService.delete({id: id}).$promise.then(function(){
            $scope.videos = UsersService.getUserVideos({id: $routeParams.id})
        })
    }

    $scope.editVideo = function(id){
        $location.path('/profile/' + $routeParams.id + '/video/' + id)
    }

}]);