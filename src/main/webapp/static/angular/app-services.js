angular.module('app-services', ['ngResource'])
    .service("videoService", ['$resource', function($resource) {
        return {
            listAll : function (){
                // https://docs.angularjs.org/api/ngResource/service/$resource
                // http://www.sitepoint.com/creating-crud-app-minutes-angulars-resource/
                return $resource('/videos').get();
            }
        }
    }]);