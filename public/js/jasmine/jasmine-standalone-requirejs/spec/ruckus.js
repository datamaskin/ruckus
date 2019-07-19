define([
  	// all specs should 
  	// with jasmine setup and plugins
  	'spec/SpecHelper',

  	// spec dependencies
  	'/assets/js/htmlcontrols/base.js',
  	'/assets/js/htmlcontrols/textbox.js'
],
function (jasmine) {

	describe("Ruckus HTML Controls", function(){
		// TEXTBOX CONTROL
		describe("Textbox Control", function() {
			var ctrlTextbox;

			var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			ctrlTextbox = new ruckus.htmlcontrols.textbox({
				'container'             : $('#maintest'),
				'placeholder'           : 'Email Address',
				'width'                 : 300,
				'validationRegex'       : emailRegex
			});
			ctrlTextbox.setValue('mytestemail@test.com');

			describe("getValue and setValue", function(){
				it("should be able to retrieve a previously set value", function(){
					expect(ctrlTextbox.getValue()).toEqual('mytestemail@test.com');
				});

				it("should not equal a previously set email address", function(){
					ctrlTextbox.setValue('mytestemail@newtest.com');
					expect(ctrlTextbox.getValue()).toNotEqual('mytestemail@test.com');
				});
			});
		
			describe("isValid", function(){
				it("should register as a valid email address", function(){
					expect(ctrlTextbox.isValid()).toEqual(true);
				});

				it("should register as invalid for email addresses not matching the expected pattern", function(){
					ctrlTextbox.setValue('whatever');
					expect(ctrlTextbox.isValid()).toEqual(false);	
				});
			});
		});
	
	});
});

