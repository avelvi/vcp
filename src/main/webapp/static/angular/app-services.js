var services = angular.module('app-services', ['ngResource']);

services.service("VideoService", function($http) {
    return {
        listAll: function (){
            return $http.get('/videos').then(function(respponse){
                return respponse.data;
            })
        },
        findOne: function(id){
            return $http.get('/video/' + id).then(function(respponse){
                return respponse.data;
            })
        },
        listAllBySearchQuery : function(query, page) {
            var params = {
                query : query != undefined ? query : '',
                page : page != undefined ? page : 0
            };
            return $http.get('/videos/search', {
                params : {
                    query : query != undefined ? query : '',
                    page : page != undefined ? page : 0
                }
            }).then(function(response) {
                return response.data;
            });
        }
    }
});