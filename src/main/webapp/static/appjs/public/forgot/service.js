'use strict';

appForgot.factory('ForgotService', ['$resource', function($resource){
    var data = $resource('/restore/:data', {data: '@data'},
        {
            validate: {
                method: 'GET',
                isArray: false,
                url: '/validate/code/:data'
            },
            changePassword: {
                method: 'POST',
                url: '/updatePassword'
            },
            sentRestoreEmail: {
                method: 'POST'
            }
        });
    return data;
}]);