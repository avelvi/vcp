appModal.controller('ModalController', ['$scope', '$location', '$uibModal', function ($scope, $location, $uibModal) {

    $scope.animationsEnabled = true;
    var modalInstance;

    $scope.open = function (type, message, path) {

        modalInstance = $uibModal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'static/appjs/public/modal/' + type + '-modal.html',
            controller: 'ModalInstanceController',
            backdrop  : 'static',
            keyboard  : false,
            scope: $scope,
            resolve: {
                modalMessage: function () {
                    return message;
                },
                path: function(){
                    return path;
                }
            }
        });

        modalInstance.result.then(function (path) {
            if(path){
                $location.path(path);
            }
        });

    };

}]);


appModal.controller('ModalInstanceController', ['$scope', '$uibModalInstance', 'modalMessage', 'path',
    function ($scope, $uibModalInstance, modalMessage, path) {

    $scope.modalMessage = modalMessage;
    $scope.close = function () {
        $uibModalInstance.close(path);
    };
}]);