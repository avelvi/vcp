appModal.controller('ConfirmController', ['$scope', '$location', '$uibModal', function ($scope, $location, $uibModal) {

    $scope.animationsEnabled = true;
    var confirmInstance;

    $scope.openConfirm = function (message, callBack) {

        confirmInstance = $uibModal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'static/appjs/public/confirm/confirm-modal.html',
            controller: 'ConfirmInstanceController',
            backdrop  : 'static',
            keyboard  : false,
            scope: $scope,
            resolve: {
                confirmMessage: function () {
                    return message;
                }
            }
        });

        confirmInstance.result.then(
            function (result) {
                callBack(result);
            }
        );

    };

}]);


appModal.controller('ConfirmInstanceController', ['$scope', '$uibModalInstance', 'confirmMessage',
    function ($scope, $uibModalInstance, confirmMessage) {

    $scope.confirmMessage = confirmMessage;
    $scope.ok = function () {
        $uibModalInstance.close(true);
    };
    $scope.cancel = function () {
        $uibModalInstance.close(false);
    };
}]);