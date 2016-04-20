angular.module('app-controllers', ['ngRoute'])
    .config(function($routeProvider){
        $routeProvider.when('/videos', {
            templateUrl: 'static/html/videos.html',
            controller:'videoController'
        });
        $routeProvider.otherwise({redirectTo:'videos'});
    })
    .controller('videoController', ['$scope', 'videoService', function($scope, videoService){
        $scope.videosPage = videoService.listAll();
    }]);