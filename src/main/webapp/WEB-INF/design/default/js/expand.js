$(document).ready(function() {

    var $expander = $(".textContainer");
    var expandheight = 54;

    $.each($expander, function() {
        if ($(this).height() > expandheight) {
            var maxHeight = $(this).height();

            $(this).append('<a class="expander expand">Show more</a>').toggle(function() {
                $(this).animate({
                    height : maxHeight
                }, 200);
                $(this).find('.expander').removeClass('expand').addClass('collapsed').text('Show less').animate (
                    {
                        
                        top: maxHeight + 39
                    }, 200);
            }, function() {
                $(this).animate({
                    height : expandheight
                }, 200);
                $(this).find('.expander').removeClass('collapsed').addClass('expand').text('Show more').animate (
                    {
                        top: 90
                    }, 200);
            });

            $(this).addClass('expandable').height(expandheight);
        }
    });

});