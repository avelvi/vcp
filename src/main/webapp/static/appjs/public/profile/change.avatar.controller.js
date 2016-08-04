'use strict';

appProfile.controller('AvatarController', ['$rootScope', '$scope', '$location', '$routeParams', '$controller', 'Upload',
    function ($rootScope, $scope, $location, $routeParams, $controller, Upload) {
        $scope.avatarSrc = $rootScope.account.avatar;

        $scope.upload = function(){
            $controller('ModalController', {$scope: $scope})
            Upload.upload({
                url: '/users/' + $routeParams.id + '/changeAvatar',
                file: $scope.file
            }).success(function (data, status, headers, config) {
                $scope.open("success", "Your avatar was changed", "/profile/" + $routeParams.id);
                $rootScope.account.avatar = data;
            }).error(function(response){
                $scope.open("error", response.data.message);
                $scope.file = null;
            });

        }

        $scope.cancel = function(){
            $location.path("/profile/" + $routeParams.id);
        }

    }]);

