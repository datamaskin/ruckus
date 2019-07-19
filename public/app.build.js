({
        paths: {
                // Third Party components
                'jquery': 'libraries/jquery.min',
                'underscore-min': 'libraries/underscore-min',
                'backbone': 'libraries/backbone-min',
                'postal': 'libraries/postal',
                'moment': 'libraries/moment.min',
                'moment-timezone': 'libraries/moment-timezone.min',
                'moment-timezone-data': 'libraries/moment-timezone-data',
                'dhtmlxcommon': 'libraries/dhtmlxcommon', //		'dhtmlxslider' : 'libraries/dhtmlxslider.js',
                'dust': 'libraries/dust-core.min',
                'jqslider': 'libraries/jquery-ui/jquery-ui-1.10.4.custom.min', // jquery slider
                'jqtouchpunch': 'libraries/jquery-ui/jquery.ui.touch-punch.min', // jquery slider hack for mobile drag - http://touchpunch.furf.com/
                'atmosphere': 'libraries/jquery.atmosphere',
                'easypiechart': 'libraries/easypiechart/jquery.easypiechart',
                'nicescroll': 'libraries/nicescroll/jquery.nicescroll.min',
		'simpleweather' : 'libraries/simpleweather/jquery.simpleWeather',

                // Proprietary components
                'rg_global_base': 'base',
                'rg_page_base': 'pagecontrols/base',
                'rg_subpage_base': 'subpagecontrols/base',
                'rg_model_base': 'models/basews',
                'rg_viewrepo_base': 'viewrepositories/base',
		'rg_league_def': 'definition',
                'rg_pubsub': 'definition.pubsub'
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
                'rg_model_base': {
                        deps: [ 'rg_global_base' ],
                        exports: 'rg_model_base'
                },
                'rg_viewrepo_base': {
                        deps: [ 'rg_global_base' ],
                        exports: 'rg_viewrepo_base'
                }
        },
    appDir: "./",
    baseUrl: "js",
    dir: "./build",
    modules: [
        {
            name: "main"
        }
    ]
})
