
'use strict';

var services = angular.module('app-services', ['ngResource']);

services.factory('VideoService', ['$resource', function($resource){
    var data = $resource('/videos/:id', {id: '@id'},
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


