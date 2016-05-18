

appUpload.controller('UploadController', ['$scope', '$http', '$location', 'Upload', function ($scope, $http,    $location,  Upload) {
    $scope.loading = false;
    $scope.upload = function(){

        Upload.upload({
            url: '/users/upload',
            fields: {
                title: $scope.title,
                description: $scope.description
        },
            file: $scope.file
        }).progress(function (evt) {
            $scope.loading = true;
            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            $scope.percentValue = progressPercentage;
            console.log('progress: ' + progressPercentage + '/100');
        }).success(function (data, status, headers, config) {
            $scope.loading = false;
            console.log('file ' + config.file.name + 'uploaded. Response: ' + data);
        });

    }

    $scope.cancel = function(){
        $scope.title = null;
        $scope.description = null;
        $scope.file = null;
    }

}]);

