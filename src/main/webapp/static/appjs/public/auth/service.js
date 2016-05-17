'use strict';

appAuth.service('Session', function () {
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


appAuth.factory('AuthSharedService', ['$rootScope', '$http', '$resource', '$location', '$timeout', 'authService', 'Session',
    function ($rootScope, $http, $resource, $location, $timeout, authService, Session) {
        return {
            register: function (name, surname, login, email, password) {
                var userData = {
                    name: name,
                    surname: surname,
                    login: login,
                    email: email,
                    password: password
                }
                $http.post('/register', userData);
            },
            login: function (login, password, rememberMe) {
                var config = {
                    params: {
                        login: login,
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
            logout: function () {
                $rootScope.authenticationError = false;
                $rootScope.authenticated = false;
                $rootScope.account = null;
                $http.get('/logout');
                Session.invalidate();
                authService.loginCancelled();
            },
            getAccount: function () {
                //$rootScope.loadingAccount = true;
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
            }

        };
    }]);