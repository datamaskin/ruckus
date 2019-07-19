if (ruckus == null) {
        var ruckus = {
                'models': {},
                'views': {
                        'repositories':{},
                        'models': {}
                },
                'htmlcontrols': {},
                'modules': {
                        'counters': {}
                },
                'pagecontrols': {},
                'subpagecontrols': {},
                'definition': undefined,
                'pubsub': {},
                'ws': undefined
        };
}

requirejs.config({
//	noGlobal: true, // added for moment.js ... but doesn't seem to do anything
        paths: {
                // Third Party components
                'jquery': '/assets/js/libraries/jquery.min',
                'underscore-min': '/assets/js/libraries/underscore-min',
                'backbone': '/assets/js/libraries/backbone-min',
                'postal': '/assets/js/libraries/postal',
                'moment': '/assets/js/libraries/moment.min',
                'moment-timezone': '/assets/js/libraries/moment-timezone.min',
                'moment-timezone-data': '/assets/js/libraries/moment-timezone-data',
                'dhtmlxcommon': '/assets/js/libraries/dhtmlxcommon', //		'dhtmlxslider' : '/assets/js/libraries/dhtmlxslider.js',
                'dust': '/assets/js/libraries/dust-core.min',
                'jqslider': '/assets/js/libraries/jquery-ui/jquery-ui-1.10.4.custom.min', // jquery slider
                'jqtouchpunch': '/assets/js/libraries/jquery-ui/jquery.ui.touch-punch.min', // jquery slider hack for mobile drag - http://touchpunch.furf.com/
                'atmosphere': '/assets/js/libraries/jquery.atmosphere',
                'easypiechart': '/assets/js/libraries/easypiechart/jquery.easypiechart',
                'nicescroll': '/assets/js/libraries/nicescroll/jquery.nicescroll.min',
		        'simpleweather' : '/assets/js/libraries/simpleweather/jquery.simpleWeather',

                // Proprietary components
                'rg_global_base': '/assets/js/base',
                'rg_page_base': '/assets/js/pagecontrols/base',
                'rg_subpage_base': '/assets/js/subpagecontrols/base',
                'rg_model_base': '/assets/js/models/basews',
                'rg_viewrepo_base': '/assets/js/viewrepositories/base',
                'rg_league_def': '/assets/js/definition',
                'rg_pubsub': '/assets/js/definition.pubsub'
        },
        shim: {
                'jquery': {
                        exports: '$'
                },
                'underscore-min': {
                        deps: [ 'jquery' ],
                        exports: '_'
                },
                'backbone': {
                        deps: [ 'underscore-min' ],
                        exports: 'Backbone'
                },
                'postal': {
                        deps: [ 'underscore-min' ],
                        exports: 'postal'
                },
                'dhtmlxcommon': {
                        exports: 'dhtmlx'
                },
                'atmosphere': {
                        deps: [ 'jquery' ],
                        exports: 'atmosphere'
                },
                'nicescroll': {
                        deps: [ 'jquery' ],
                        exports: 'nicescroll'
                },
		        'simpleweather': {
                        deps: [ 'jquery' ],
                        exports: 'simpleweather'
                },


                // Custom components
                'rg_global_base': {
                        deps: [ 'jquery' ],
                        exports: 'rg_global_base'
                },
                'rg_page_base': {
                        deps: [ 'rg_global_base' ],
                        exports: 'rg_page_base'
                },
                'rg_subpage_base': {
                        deps: [ 'rg_global_base' ],
                        exports: 'rg_subpage_base'
                },
                'rg_viewrepo_base': {
                        deps: [ 'rg_global_base' ],
                        exports: 'rg_viewrepo_base'
                }
        }
});

require([
        "assets/js/libraries/jquery.min.js",
        "assets/js/controller.js"
], function () {
        new ruckus.controller({
                'divHeader': $('#header'),
                'divMain': $('#main'),
                'divFooter': $('#footer')
        });
});
