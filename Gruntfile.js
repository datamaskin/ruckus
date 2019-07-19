module.exports = function (grunt) {
        grunt.initConfig({
                pkg: grunt.file.readJSON('package.json'),

                // ----------------------------------------
                // Code Validation
                // ----------------------------------------
		jshint: {
                        public_js: {
                                options: {
                                        '-W041':false, // !== (changing this will break stuff)
                                        '-W080':false, // var x = undefined (we can turn this on later but its a small detail)
                                        '-W008':false, // A leading decimal point can be confused with a dot: '{a}' (whatever)
                                        '-W083':false  // Don't make functions within a loop (requires individual thought if we think its an issue)
                                },
                                files: {
                                        src: [
                                                'Gruntfile.js',
                                                'public/js/base.js',
                                                'public/js/controller.js',
                                                'public/js/definition.js',
                                                'public/js/main.js',
//                                                'public/js/htmlcontrols/**/*.js', (not used, could be deleted eventually)
                                                'public/js/models/**/*.js',
                                                'public/js/modules/**/*.js',
                                                'public/js/pagecontrols/**/*.js',
                                                'public/js/subpagecontrols/**/*.js'
                                        ]
                                }
                        }
                },
                requirejs: {
                        compile: {
                                options: {
                                        baseUrl: "public/js",
                                        mainConfigFile: "public/app.build.js",
                                        name: "path/to/almond", // assumes a production build using almond
                                        out: "path/to/optimized.js"
                                   }
                        }
                },
                // ----------------------------------------
                // Watch Tasks
                // ----------------------------------------
                watch: {
                        files: [
                                'public/**/*.js',
                                'Gruntfile.js'
                        ],
                        tasks: [
                                'jshint:public_js'
                        ],
                        options: {
                                nospawn: true
                        }
                }
        });

        grunt.registerTask('watch', 'watch');

        grunt.loadNpmTasks('grunt-contrib-watch');
        grunt.loadNpmTasks('grunt-contrib-jshint');
        grunt.loadNpmTasks('grunt-htmlhint');
        grunt.loadNpmTasks('grunt-contrib-requirejs');
};
