'use strict';

var appSearch = angular.module('app.search', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/search', {
                    templateUrl: 'static/appjs/public/search/results.html',
                    controller:'SearchResultsController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }
    ]);