'use strict';

appCompany.factory('CompanyService', ['$resource', function($resource){
    var data = $resource('/companies/:id', {id: '@id'},
        {
            query: {
                method: 'GET',
                isArray: false
            },
            update: {
                method: 'PUT'
            }
        });
    return data;
}]);