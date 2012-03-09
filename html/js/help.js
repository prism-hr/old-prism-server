// JavaScript Document

$(function() {
    var autoTerms = [
      "ActionScript",
      "AppleScript",
      "Asp",
      "BASIC",
      "C",
      "C++",
      "Clojure",
      "COBOL",
      "ColdFusion",
      "Erlang",
      "Fortran",
      "Groovy",
      "Haskell",
      "Java",
      "JavaScript",
      "Lisp",
      "Perl",
      "PHP",
      "Python",
      "Ruby",
      "Scala",
      "Scheme"
    ];
    $('#help-keyword input.search').autocomplete({
      source: autoTerms,
    });
    
    $('fieldset.slider').each(function()
    {
      var $this = $(this);
      
      // append a DIV to use for the slider.
      $this.append('<div class="slider-container"><div class="slider" /></div>');
      var $sliderparent = $this.children('div.slider-container');
      var $slider = $sliderparent.children('div.slider');
      
      // grab all the labels.
      var $labels = $this.find('label');
      var $inputs = $this.find('label input');
      var init_value = $inputs.filter(':checked').parent().index();
      $labels.hide();
      
      // create the slider.
      $slider.slider({
        min: 1,
        max: $labels.length,
        value: init_value,
        change: function(event, ui) {
          $inputs[ui.value-1].checked = true; // checks the respective item.
        }
      });
      
      // Create ticks.
      var pc = 100 / $labels.length;
      $labels.each(function(i, label) { $sliderparent.append('<span class="tick" style="left: '+(i * pc)+'%;">'+$(label).text()+'</span>'); });
    });
  });