	$(document).ready(function(){
	
		$('.data-table').hide();
		
		$('#addSupervisor').click(function(){
			
			var $copiedForm = $('#supervisor-form').clone();
			var $firstName =  $copiedForm.find('input[name=firstName]').val();
			var $surname =  $copiedForm.find('input[name=surname]').val();
			var $email = $copiedForm.find('input[name=email]').val();
			var $isAware = $copiedForm.find('input[name=isAware]').is(':checked');
			
			if($firstName != "" && $surname != "" && $email != ""){
				
				var $fullname = $firstName + " " + $surname;
				
				var $emptyRow = '<tr class="tableRow">' 
					+'<td id="fullname">'+$fullname+'</td>'
					+'<td id="email">'+$email+'</td>'
					+'<td><input type="radio" name="isPrimary"/></td>'
					+'<td id="aware">'+$isAware+'</td>'	
					+'<td>'+'<a class="deleteButton" name="deleteButton">delete</a>'
					+'&nbsp;&nbsp;'+'<a class="editButton" name="editButton">edit</a>'+'</td>'
					+'</tr>';
				
				//Append the copeid row in the table body
				$('.data-table').show();
				$('table.data-table tbody').append($emptyRow);
				
			}else{
				$('.data-table').hide();
				alert("Please enter valid input!");
			}
			
			//clear the fields
			$(this).parent().find('input[type=text], input[type=checkbox]').each(function(){
				$(this).val("");
				
				if($(this).is(':checked') == true){
					$(this).prop("checked", false);
				}
			});
			
			$copiedForm.find('input[name=firstName]').focus();
			
			return false;			
		});
			
		$('.deleteButton').live('click',function(){
			$(this).parent().parent().remove();
			//TODO: remove the table structure after deleting the last row
		});
		
		$('.editButton').live('click',function(){
			
			// selectors
			//$('#supervisor-form input[name=firstName]').val("000900");
			//$('#supervisor-form').find('input[name=firstName]').val("000900");
			
			var $copiedRow = $(this).parent().parent();
			
			var $firstName =  $copiedRow.find('td#fullname').text().split(' ')[0];
			var $surname =  $copiedRow.find('td#fullname').text().split(' ')[1];
			var $email = $copiedRow.find('td#email').text();

			$('#supervisor-form').find('input[name=firstName]').val($firstName);
			$('#supervisor-form').find('input[name=surname]').val($surname);
			$('#supervisor-form').find('input[name=email]').val($email);
			if($copiedRow.find('td#aware').text() == "true"){
				$('#supervisor-form').find('input[name=isAware]').attr('checked',true);	
			}else{
				$('#supervisor-form').find('input[name=isAware]').attr('checked', false);
			}
		});
		
	});

	