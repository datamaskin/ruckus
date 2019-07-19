define([ 'jquery'], function () {
        ruckus.modules.navigation = function(){ /* constructor */ };

        ruckus.modules.navigation.toRoute = function(route) {
                window.location.href = '#' + route;
        };

        ruckus.modules.navigation.reload = function() {
                window.location.reload(true);
        };
});
