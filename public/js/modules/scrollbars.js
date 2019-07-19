define([ 'jquery', 'nicescroll' ], function () {
        ruckus.modules.scrollbars = {  };

        ruckus.modules.scrollbars.resize = function(){
			$(".niceScroll").getNiceScroll().resize();
        };

        ruckus.modules.scrollbars.hide = function(){
            $(".niceScroll").getNiceScroll().hide();
        };
		
		ruckus.modules.scrollbars.remove = function(){
			$('.nicescroll-rails').remove();
        };
		
});