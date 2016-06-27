app.directive('access', [ 'AuthSharedService', function (AuthSharedService) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var roles = attrs.access.split(',');
            if (roles.length > 0) {
                if (AuthSharedService.isAuthorized(roles)) {
                    element.removeClass('hide');
                } else {
                    element.addClass('hide');
                }
            }
        }
    };
}]);

app.directive('video', ['VideoStatisticService', function(VideoStatisticService){
    return {
        restrict: 'A',
        link: function(scope, element, attrs){
            var videos = [];
            element[0].addEventListener('play', function(){
                var v = this;
                if(!videos[v.id]){
                    videos[v.id] = []
                    videos[v.id]['video'] = v;
                    videos[v.id]['viewed'] = false;
                }

            });
            element[0].addEventListener('timeupdate', function(){
                var v = this;
                if(!videos[v.id.viewed]){
                    var total = 0;
                    for(var i = 0; i < v.played.length; i++){
                        total += (v.played.end(i) - v.played.start(i));
                    }
                    var totalPercentage = ((total / v.duration) * 100).toFixed(2);
                    if(totalPercentage > 30){
                        videos[v.id.viewed] = true;
                        VideoStatisticService.updateVideoStatistic({id: v.id})
                    }
                }

            });
        }
    };
}]);
