'use strict';

appAdminVideo.controller('AdminVideoDetailsController', ['$scope', '$routeParams', '$location', 'VideoService', 'CategoryService',
    function($scope, $routeParams, $location, VideoService, CategoryService){

    $scope.video = VideoService.get({id: $routeParams.id});

    CategoryService.query({page: 0, size: Number.MAX_VALUE}).$promise.then(function(data){
        $scope.categories = data.content;
    });

    $scope.updateVideo = function(){
        VideoService.update({id: $routeParams.id}, $scope.video)
        $location.path('/admin/videos');

    }
    $scope.cancel = function(){
        $location.path('/admin/videos')
    }
}]);