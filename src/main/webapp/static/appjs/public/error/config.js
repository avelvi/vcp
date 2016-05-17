'use strict';

var appError = angular.module('app.error', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when("/error/:code", {
                    templateUrl: "static/appjs/public/error/error.html",
                    controller: "ErrorController",
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }
    ]);