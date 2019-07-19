require([
  	'domReady',
	'jasmine',
	'jasmine-html',

  	// add new specs here
  	'spec/ruckus'
],
function(domReady, jasmine) {
  var jasmineEnv = jasmine.getEnv();
  jasmineEnv.updateInterval = 1000;

  var htmlReporter = new jasmine.HtmlReporter();
  jasmineEnv.addReporter(htmlReporter);
  jasmineEnv.specFilter = function(spec) {
    return htmlReporter.specFilter(spec);
  };

  domReady(function () {
    jasmineEnv.execute();
  });

});
