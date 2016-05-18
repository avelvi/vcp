'use strict';

appUsers.factory('UsersService', ['$resource', function($resource){
    var data = $resource('/users/:id', {id: '@id'},
        {
            query: {
                method: 'GET',
                isArray: false
            },
            update: {
                method: 'PUT'
            },
            getUserVideos: {
                url: '/users/:id/videos',
                method: 'GET',
                isArray: false
            }

        });
    return data;

}]);

appUsers.factory('AuthorityService', ['$http', function($http){
    return {
        getAuthorities: function (){
            return $http.get('/admin/authorities').then(function(respponse){
                return respponse.data;
            })
        }
    }
}]);