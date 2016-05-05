
'use strict';

var controllers = angular.module('app-controllers', []);


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

controllers.controller('HomeController', ['$scope', '$location', 'VideoService', 'ControllersFactory', function($scope, $location, VideoService, ControllersFactory){
        ControllersFactory.createPaginationController($scope, {
            getData: function(){
                return VideoService.listAll()
            }
        })
}]);

controllers.controller('NavController', ['$scope', '$location', 'AuthSharedService', function($scope, $location, AuthSharedService){

    $scope.isShowNavBar = function(){
        var excludeUrls = ["/signin", "/signup", "/error"];
        var path = $location.path();
        for (var i = 0; i < excludeUrls.length; i++) {
            var excludeUrl = excludeUrls[i];
            if (path.indexOf(excludeUrl) === 0) {
                return false;
            }
        }
        return true;
    }

    $scope.isAuthorized = function(roles){
        return AuthSharedService.isAuthorized(roles)
    }

    $scope.$on('$locationChangeSuccess', function(event, newurl, oldurl) {
        var path = $location.path();
        $scope.templateUrl = path.indexOf('/admin') !== -1 ? 'partials/admin/admin_navbar.html' : 'partials/public/navbar.html' ;
    });

    $scope.isActive = function(viewLocation){
        return viewLocation === $location.path();
        //var path = $location.path();
        //return path.indexOf(viewLocation) > -1;
    }
}]);

controllers.controller('SearchController', function($scope, $location){
    $scope.query = '';
    $scope.find = function (){
        if($scope.query.trim() != '') {
            $location.path('/home/search').search({query: $scope.query});
            $scope.query = '';
        } else{
            $location.path('/home')
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
});

controllers.controller('LoginController', function ($rootScope, $scope, AuthSharedService) {
    $scope.rememberMe = true;
    $scope.doLogin = function () {
        $rootScope.authenticationError = false;
        AuthSharedService.login(
            $scope.username,
            $scope.password,
            $scope.rememberMe
        );
    }
});

controllers.controller('SignUpController', function ($rootScope, $scope, SignUpService) {
    //$scope.rememberMe = true;
    $scope.doRegistration = function () {
        //$rootScope.authenticationError = false;
        SignUpService.register(
            $scope.username,
            $scope.email,
            $scope.password
        );
    }
})

controllers.controller('TokensController', function ($scope, UsersService, TokensService, $q) {

    var browsers = ["Firefox", 'Chrome', 'Trident']

    $q.all([
        UsersService.getAll().$promise,
        TokensService.getAll().$promise
    ]).then(function (data) {
        var users = data[0];
        var tokens = data[1];

        tokens.forEach(function (token) {
            users.forEach(function (user) {
                if (token.userLogin === user.login) {
                    token.firstName = user.firstName;
                    token.familyName = user.familyName;
                    browsers.forEach(function (browser) {
                        if (token.userAgent.indexOf(browser) > -1) {
                            token.browser = browser;
                        }
                    });
                }
            });
        });

        $scope.tokens = tokens;
    });


})

controllers.controller('LogoutController', function (AuthSharedService) {
    AuthSharedService.logout();
})

controllers.controller('ErrorController', function ($scope, $routeParams) {
    $scope.code = $routeParams.code;

    switch ($scope.code) {
        case "403" :
            $scope.message = "Sorry, but you don't have permissions to access this page"
            break;
        case "404" :
            $scope.message = "Page not found."
            break;
        default:
            $scope.code = 500;
            $scope.message = "Server internal error."
    }

});

controllers.controller('UsersListController', ['$scope', '$location', 'UsersService', function ($scope, $location, UsersService){
    $scope.editUser = function(id){
        $location.path('/admin/user/' + id);
    }

    $scope.showVideos = function(id){
        $location.path('/admin/users/' + id + '/videos')
    }

    UsersService.getAllUsers().then(function(data){
         $scope.users = data;
    });

}]);

controllers.controller('UserDetailsController', ['$scope', '$routeParams', '$location', 'UserService', function ($scope, $routeParams, $location, UserService){

    $scope.updateUser = function(){
        UserService.updateUser($scope.user).then(function(data){
            return;
        });
        $location.path('/admin')
    }

    $scope.cancel = function(){
        $location.path('/admin')
    }
    UserService.getUser($routeParams.id).then(function(data){
        $scope.user = data;
    });
}]);

controllers.controller('VideosController', ['$scope', '$routeParams', '$location', 'VideosService', function($scope, $routeParams, $location, VideosService){

    $scope.viewName = 'list';
    $scope.className = 'col-md-3';
    VideosService.getAllVideosByUserId($routeParams.id).then(function(data){
        $scope.videos = data;
    })

    $scope.changeView = function(viewName){
        if(viewName === 'list'){
            $scope.className = 'col-md-12';
        } else {

            $scope.className = 'col-md-3';
        }
        $scope.viewName = viewName;

    }

    $scope.isListView = function(){
        return $scope.viewName === 'list';
    }

    $scope.editVideo = function(id){
        $location.path('/admin/editVideo/' + id);
    }

}]);

controllers.controller('VideoController', function($scope, $routeParams, VideoService){
    VideoService.findOne($routeParams.videoId).then(function(video){
        $scope.video = video;
    });
});

controllers.controller('VideoDetailsController', ['$scope', '$routeParams', '$location', 'VideoService', function($scope, $routeParams, $location, VideoService){
    $scope.updateVideo = function(){
        VideoService.updateVideo($scope.video).then(function(data){
            return;
        });
        $location.path('/admin/users/' + $scope.video.id + '/videos')
    }

    $scope.cancel = function(userId){
        $location.path('/admin/users/' + userId + '/videos')
    }

    VideoService.findOne($routeParams.id).then(function(video){
        $scope.video = video;
    });
}])

