'use strict';

appCategory.factory('CategoryService', ['$resource', function($resource){
    var data = $resource('/categories/:id', {id: '@id'},
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