
'use strict';

var services = angular.module('app-services', ['ngResource']);

services.factory("VideoService", function($http) {
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

services.service('Session', function () {
    this.create = function (data) {
        this.id = data.id;
        this.login = data.login;
        this.name = data.name;
        this.surname = data.surname;
        this.email = data.email;
        this.avatar = data.avatar;
        this.userRoles = [];
        angular.forEach(data.authorities, function (value, key) {
            this.push(value.name);
        }, this.userRoles);
    };
    this.invalidate = function () {
        this.id = null;
        this.login = null;
        this.name = null;
        this.surname = null;
        this.email = null;
        this.avatar = null;
        this.userRoles = null;
    };
    return this;
});

services.factory('AuthSharedService', ['$rootScope', '$http', '$resource', '$location', '$timeout', 'authService', 'Session',
    function ($rootScope, $http, $resource, $location, $timeout, authService, Session) {
    return {
        login: function (userName, password, rememberMe) {
            var config = {
                params: {
                    username: userName,
                    password: password,
                    rememberme: rememberMe
                },
                ignoreAuthModule: 'ignoreAuthModule'
            };
            $http.post('/login', '', config)
                .success(function (data, status, headers, config) {
                    authService.loginConfirmed(data);

                }).error(function (data, status, headers, config) {
                    $rootScope.authenticationError = true;
                    Session.invalidate();
                });
        },
        getAccount: function () {
            $rootScope.loadingAccount = true;
            $http.get('security/user')
                .then(function (response) {
                    authService.loginConfirmed(response.data);
                });
        },
        isAuthorized: function (authorizedRoles) {
            if (!angular.isArray(authorizedRoles)) {
                if (authorizedRoles == '*') {
                    return true;
                }
                authorizedRoles = [authorizedRoles];
            }
            var isAuthorized = false;
            angular.forEach(authorizedRoles, function (authorizedRole) {
                var authorized = (!!Session.login &&
                Session.userRoles.indexOf(authorizedRole) !== -1);
                if (authorized || authorizedRole == '*') {
                    isAuthorized = true;
                }
            });
            return isAuthorized;
        },
        logout: function () {
            $rootScope.authenticationError = false;
            $rootScope.authenticated = false;
            $rootScope.account = null;
            $http.get('logout');
            Session.invalidate();
            authService.loginCancelled();
        }
    };
}]);

services.factory('SignUpService', ['$rootScope', '$http', '$resource', '$location',
    function ($rootScope, $http, $resource, $location) {
        return {
            register: function (userName, email, password) {
                var userData = {
                    login: userName,
                    email: email,
                    password: password
                }
                $http.post('/register', userData)
                    .success(function () {
                        $location.path("/signin")
                    });
            }
        };
    }]);

app.factory('UsersService', ['$http', function($http){
    return {
        getAllUsers: function (){
            return $http.get('/admin/users').then(function(respponse){
                return respponse.data;
            })
        }
    }
}]);

app.factory('UserService', ['$http', function($http){
    return {
        getUser: function(id){
            return $http.get('/admin/user/' + id).then(function(response) {
                return response.data;
            })
        },
        updateUser: function(user){
            return $http.put('/admin/user/' + id, user).then(function(response) {
                return response.data;
            })
        }
    }
}]);

services.factory("VideosService", function($http) {
    return {
        getAllVideosByUserId: function(id){
            return $http.get('/admin/videos', {
                params: {
                    userId: id
                }
            }).then(function(respponse){
                return respponse.data;
            })
        },
        findOne: function(id){
            return $http.get('/video/' + id).then(function(respponse){
                return respponse.data;
            })
        },
        listAllBySearchQuery : function(query, page) {
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