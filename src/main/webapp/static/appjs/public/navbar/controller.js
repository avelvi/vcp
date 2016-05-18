'use strict';

appNavBar.controller('NavController', ['$scope', '$location', 'Session', 'AuthSharedService', function($scope, $location, Session, AuthSharedService){

    $scope.isShowNavBar = function(){
        var excludeUrls = ["/signin", "/signup", "/error", "/loading", "/registration"];
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
        return viewLocation === $location.path();
    }

    $scope.viewProfile = function(){
        $location.path('/profile/' + Session.id)
    }

    $scope.uploadForm = function(){
        $location.path("/upload")
    }
}]);
