var controllers = angular.module('app-controllers', ['ngRoute']);

controllers.config(['$routeProvider',
    function($routeProvider){
        $routeProvider.when('/videos', {
            templateUrl: 'static/html/videos.html',
            controller: 'VideosController'
        }).when('/video/:videoId', {
            templateUrl: 'static/html/video.html',
            controller: 'VideoController'
        }).when('/signin', {
            templateUrl: 'static/html/signin.html'
        }).when('/signup', {
            templateUrl: 'static/html/signup.html'
        }).when('/videos/search', {
            templateUrl: 'static/html/searchResults.html',
            controller:'SearchResultsController'
        });
        $routeProvider.otherwise({redirectTo:'videos'});
    }

]);

controllers.factory('ControllersFactory', function(){
    return ({
        createPaginationController : createPaginationController
    })
    function createPaginationController(scope, service){

        service.getData().then(function(data){
            scope.videos = data;
        })
        scope.loading = false;
        scope.loadMore = function(){
            scope.loading = true;
            var nextPage = scope.videos.number + 1;
            service.getData(nextPage).then(function (data) {
                data.content = scope.videos.content.concat(data.content);
                scope.videos = data;
                scope.loading = false;
            });
        }
    }

});

controllers.controller('VideosController', ['$scope', '$location', 'VideoService', 'ControllersFactory', function($scope, $location, VideoService, ControllersFactory){
        ControllersFactory.createPaginationController($scope, {
            getData: function(){
                return VideoService.listAll()
            }
        })
}]);

controllers.controller('VideoController', function($scope, $routeParams, VideoService){
        VideoService.findOne($routeParams.videoId).then(function(video){
            $scope.video = video;
        });
});

controllers.controller('NavController', function($scope, $location){

    $scope.isShowNavBar = function(){
        var excludeUrls = ["/signin", "/signup"];
        var path = $location.path();
        for (var i = 0; i < excludeUrls.length; i++) {
            var excludeUrl = excludeUrls[i];
            if (path.indexOf(excludeUrl) === 0) {
                return false;
            }
        }
        return true;
    }
});

controllers.controller('LoginController', function($scope, $location){

    $scope.doLogin =  function(){
        var email = $scope.email;
        var password = $scope.password;
        console.log(email);
        console.log(password);
        $location.path('/')
    }
});

controllers.controller('SearchController', function($scope, $location){
    $scope.query = '';
    $scope.find = function (){
        if($scope.query.trim() != '') {
            $location.path('/videos/search').search({query: $scope.query});
            $scope.query = '';
        } else{
            $location.path('/videos')
        }
    };
    $scope.onEnter = function(keyEvent) {
        if (keyEvent.which === 13) {
            $scope.find();
        }
    }
});

controllers.controller('SearchResultsController', function($scope, $location, VideoService, ControllersFactory){
    ControllersFactory.createPaginationController($scope, {
        getData : function(page) {
            return VideoService.listAllBySearchQuery($location.search().query, page);
        }
    });
})