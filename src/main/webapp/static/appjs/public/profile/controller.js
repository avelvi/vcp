'use strict';

appProfile.controller('ProfileController', ['$scope', '$routeParams', '$location', 'UsersService', 'VideoService', function($scope, $routeParams, $location, UsersService, VideoService){

    $scope.user = UsersService.get({id: $routeParams.id});

    $scope.videos = UsersService.getUserVideos({id: $routeParams.id})

}]);