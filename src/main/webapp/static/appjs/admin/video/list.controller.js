'use strict';

appAdminVideo.controller('AdminVideoController', ['$scope', '$location', '$controller','ENTRIES_PER_PAGE', 'VideoService',
    function($scope, $location, $controller, ENTRIES_PER_PAGE, VideoService){
    $scope.entriesPerPage = ENTRIES_PER_PAGE;
    var search = $location.search();
    var page = search.page||0;
    var size = search.size||5;
    $scope.size = size;
    $scope.videos = VideoService.query({page: page, size: size});

    $scope.onNumPerPageChange = function(size){
        this.goToPage(0, size);
    }

    $scope.goToPage = function(page, size){
        $scope.videos = VideoService.query({page: page, size: size});
    }

    $scope.editVideo = function(id){
        $location.path('/admin/videos/' + id + '/edit');
    }

    $scope.deleteVideo = function(id){
        $controller('ModalController', {$scope: $scope})
        VideoService.delete({id: id}).$promise.then(
            function onsuccess(){
                $scope.open("success", "Video was deleted");
                $scope.videos = VideoService.query({page: page, size: size});
            },
            function onerror(response){
                $scope.open("error", response.data.message);
                $scope.videos = VideoService.query({page: page, size: size});
            }
        )
    }
}]);