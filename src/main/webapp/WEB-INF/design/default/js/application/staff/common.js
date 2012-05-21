function bindDatePickers(){
 // Date pickers.
  $('input.date').attr("readonly", "readonly");  
  $('input.date').datepicker({
    dateFormat:    'dd-M-yy',
    changeMonth:  true,
    changeYear:    true,
    yearRange:    '1900:c+20'
  });
}

