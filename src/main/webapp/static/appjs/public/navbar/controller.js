'use strict';

appNavBar.controller('NavController', ['$scope', '$location', 'Session', 'AuthSharedService', function($scope, $location, Session, AuthSharedService){

    $scope.isShowNavBar = function(){
        var excludeUrls = ["/signin", "/signup", "/error", "/loading", "/registration", "/forgot-password", "/recovery", "/activate"];
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

    $scope.isAdminPart = function(){
        return (this.isAuthorized('admin') && $location.path().indexOf("/admin") > -1);
    }

    $scope.doLogout = function(){
        AuthSharedService.logout();
    }

    $scope.isActive = function(viewLocation){
        if(viewLocation === $location.path()){
            return true;
        } else {
            if(viewLocation === '/admin'){
                return false;
            } else {
                return viewLocation === ($location.path().substr(0, viewLocation.length));
            }

        }
    }

    $scope.viewProfile = function(){
        $location.path('/profile/' + Session.id)
    }

    $scope.uploadForm = function(){
        $location.path("/upload")
    }
}]);
