'use strict';

appVideo.controller('VideoDetailsController', ['$scope', '$routeParams', '$location', 'VideoService', function($scope, $routeParams, $location, VideoService){

    $scope.video = VideoService.get({id: $routeParams.id});

}]);