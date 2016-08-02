'use strict';

appUsers.controller('UserVideosController', ['$scope', '$location', '$controller', '$routeParams', 'ENTRIES_PER_PAGE', 'UsersService', 'VideoService',
    function($scope, $location, $controller, $routeParams,  ENTRIES_PER_PAGE, UsersService, VideoService){
        $scope.entriesPerPage = ENTRIES_PER_PAGE;
        var search = $location.search();
        var page = search.page||0;
        var size = search.size||5;
        $scope.size = size;
        UsersService.get({id: $routeParams.id}).$promise.then(
            function onsuccess(response){
                $scope.user = response;
                UsersService.getUserVideos({id: $routeParams.id, page: page, size: size}).$promise.then(
                    function onsuccess(response){
                        $scope.videos = response;
                    },
                    function onerror(response){
                        $scope.open("error", response.data.message);
                    }
                );
            },
            function onerror(response){
                $scope.open("error", response.data.message);
            }
        );

        $scope.onNumPerPageChange = function(size){
            this.goToPage(0, size);
        }

        $scope.goToPage = function(page, size){
            UsersService.getUserVideos({id: $routeParams.id, page: page, size: size}).$promise.then(
                function onsuccess(response){
                    $scope.videos = response;
                },
                function onerror(response){
                    $scope.open("error", response.data.message);
                }
            );
        }

        $scope.editVideo = function(id){
            $location.path('/admin/videos/' + id + '/edit');
        }

        $scope.deleteVideo = function(id){
            $controller('ModalController', {$scope: $scope})
            $controller('ConfirmController', {$scope: $scope})

            $scope.openConfirm('Are you sure you want to delete this entry', function(result) {
                if (result) {
                    VideoService.delete({id: id}).$promise.then(
                        function onsuccess() {
                            $scope.open("success", "Video was deleted");
                            UsersService.getUserVideos({id: $routeParams.id, page: page, size: size}).$promise.then(
                                function onsuccess(response) {
                                    $scope.videos = response;
                                },
                                function onerror(response) {
                                    $scope.open("error", response.data.message);
                                }
                            );
                        },
                        function onerror(response) {
                            $scope.open("error", response.data.message);
                            UsersService.getUserVideos({id: $routeParams.id}, {page: page, size: size}).$promise.then(
                                function onsuccess(response) {
                                    $scope.videos = response;
                                },
                                function onerror(response) {
                                    $scope.open("error", response.data.message);
                                }
                            );
                        }
                    )
                }
            })
        }
    }]);