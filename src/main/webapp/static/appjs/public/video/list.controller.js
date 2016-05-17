'use strict';

appVideo.controller('VideoController', ['$scope', '$location', 'VideoService', function($scope, $location, VideoService){
    $scope.videos = VideoService.query();

}]);