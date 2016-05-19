
'use strict';

var app = angular.module('app', ['ngResource', 'ngRoute', 'ngFileUpload', 'ui.router', 'ui.bootstrap', 'http-auth-interceptor',
    'app-controllers', 'app-services', 'app.constants', 'app.company',
    'app.category', 'app.auth', 'app.users', 'app.profile',
    'app.adminHome', 'app.adminVideo', 'app.video', 'app.navBar',
    'app.error', 'app.search', 'app.upload'
]);

angular.module('app.constants', [])
    .constant('USER_ROLES', {
        all: '*',
        admin: 'admin',
        user: 'user'
    })
    .constant('ENTRIES_PER_PAGE', {
        1: 1,
        5: 5,
        10: 10,
        20: 20
    });

app.config(['$routeProvider', '$httpProvider', 'USER_ROLES',
    function($routeProvider, $httpProvider, USER_ROLES){
        $routeProvider
            .when('/loading', {
            templateUrl: 'partials/public/loading.html',
            access: {
                loginRequired: false,
                authorizedRoles: [USER_ROLES.all]
            }
        }).otherwise({
            redirectTo: '/error/404',
            access: {
                loginRequired: false,
                authorizedRoles: [USER_ROLES.all]
            }
        });
    }

]);

app.run(function ($rootScope, $location, $http, AuthSharedService, Session, USER_ROLES, $q, $timeout) {

    $rootScope.$on('$routeChangeStart', function (event, next, previous) {

        $rootScope.history = [];

        $rootScope.authenticationError = false;
        $rootScope.registrationError = false;
        if(previous){
            if($rootScope.history.length > 1){
                $rootScope.history.pop()
            }
            $rootScope.history.push(previous.originalPath);
        }

        if(next.originalPath === "/signin" && $rootScope.authenticated) {
            event.preventDefault();
        } else if (next.access && next.access.loginRequired && !$rootScope.authenticated) {
            event.preventDefault();
            $rootScope.$broadcast("event:auth-loginRequired", {});
        } else if (next.access && !AuthSharedService.isAuthorized(next.access.authorizedRoles)) {
            event.preventDefault();
            $rootScope.$broadcast("event:auth-forbidden", {});
        }
    });

    // Call when the the client is confirmed
    $rootScope.$on('event:auth-loginConfirmed', function (event, data) {
        console.log('login confirmed start ' + data);
        $rootScope.loadingAccount = false;
        var nextLocation = ($rootScope.requestedUrl ? $rootScope.requestedUrl : "/videos");
        var delay = ($location.path() === "/loading" ? 1500 : 0);

        $timeout(function () {
            Session.create(data);
            $rootScope.account = Session;
            $rootScope.authenticated = true;
            $location.path(nextLocation).replace();
        }, delay);

    });

    // Call when the 401 response is returned by the server
    $rootScope.$on('event:auth-loginRequired', function (event, data) {
        if ($rootScope.loadingAccount && data.status !== 401) {
            $rootScope.requestedUrl = $location.path()
            $location.path('/loading');
        } else {
            Session.invalidate();
            $rootScope.authenticated = false;
            $rootScope.loadingAccount = false;
            $location.path('/signin');
        }
    });

    // Call when the 403 response is returned by the server
    $rootScope.$on('event:auth-forbidden', function (rejection) {
        $rootScope.$evalAsync(function () {
            $location.path('/error/403').replace();
        });
    });

    // Call when the user logs out
    $rootScope.$on('event:auth-loginCancelled', function () {
        $location.path('/videos').replace();
    });


    $rootScope.$on('event:app-error', function(rejection, responseBody){
        $rootScope.message = responseBody.data.message;
        $location.path('/error/' + responseBody.status).replace();
    })

    // Get already authenticated user account
    AuthSharedService.getAccount();
});


