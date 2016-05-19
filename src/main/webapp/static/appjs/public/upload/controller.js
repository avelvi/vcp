

appUpload.controller('UploadController', ['$scope', '$http', '$location', 'CategoryService', 'Upload',
    function ($scope, $http, $location, CategoryService, Upload) {
    $scope.loading = false;
    $scope.categories = CategoryService.query();
    $scope.upload = function(){

        Upload.upload({
            url: '/users/upload',
            fields: {
                title: $scope.title,
                description: $scope.description,
                categoryId: $scope.category.id,
                categoryName: $scope.category.name
        },
            file: $scope.file
        }).progress(function (evt) {
            $scope.loading = true;
            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            $scope.percentValue = progressPercentage;
        }).success(function (data, status, headers, config) {
            $scope.loading = false;
            $scope.cancel()
        });

    }

    $scope.cancel = function(){
        $scope.title = null;
        $scope.description = null;
        $scope.file = null;
        $scope.category = null
    }

}]);

