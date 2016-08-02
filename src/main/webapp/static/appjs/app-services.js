
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
            },
            getLatest: {
                method: 'GET',
                isArray: false,
                url: '/videos/latest'
            },
            getPopular: {
                method: 'GET',
                isArray: false,
                url: '/videos/popular'
            }
        });
    return data;
}]);

services.factory('VideoStatisticService', ['$resource', function($resource){
    var data = $resource('/statistic/:id', {id: '@id'},
        {
            query: {
                method: 'GET',
                isArray: false
            },
            updateVideoStatistic: {
                method: 'PUT'
            }
        });
    return data;
}]);


