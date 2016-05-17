'use strict';

appAdminVideo.controller('AdminVideoDetailsController', ['$scope', '$routeParams', '$location', 'VideoService', function($scope, $routeParams, $location, VideoService){

    $scope.video = VideoService.get({id: $routeParams.id});

    $scope.updateVideo = function(){
        VideoService.update({id: $routeParams.id}, $scope.video)
        $location.path('/admin/videos');

    }
    $scope.cancel = function(){
        $location.path('/admin/videos')
    }
}]);