<#import "/spring.ftl" as spring />

<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>

<table class="data-table">

	<thead>
		<tr>
			<th>Email</th>
			<th>Primary</th>
			<th>Aware</th>
			<th></th>
		</tr>
	</thead>

	<tfoot>
	
		<tr>
			<td>
				<input type="email" placeholder="Email Address" name="email"/>
			</td>
			<td>
				<input type="radio" name="isPrimary" value=""/>
			</td>
			<td>
				<input type="checkbox" name="isAware" value=""/>
			</td>	
			<td>
				<button type="button blue" name="add">Add Another</button>
			</td>
		</tr>	
	</tfoot>
	
	<tbody>
	
	</tbody>
	
</table>


<script type="text/javascript">

	$(document).ready(function(){
	
		$('table.data-table button').click(function(){
		
			//clone the row that contains the button element
			var $copiedRow = $(this).parent().parent().clone();
		
			//Validate the Email field against null/empty values
			if($copiedRow.find('input[name=email]').val() != ""){
		
				//change the attributes and value of the button element
				$copiedRow.find('button')
					.attr({"name":"Delete","id":"delete-button"})
					.html("Delete");
				
				// Find each input element with type text, inside a given row,
				// replace the value of input element after it's position,
				// remove the input elemenet completely.
				$copiedRow.find('input[type=text]').each(function(){
				
					$(this).after($(this).val()).remove();
				
				});
			
				//Append the copeid row in the table body
				$('table.data-table tbody').append($copiedRow);
				
				//clear the fields
				$(this).parent().parent().find('input[type=text]')
						.val("")
						.focus();
				
				applyRowStyle();
				
				return false;
			}
		
		});
		
		$('table.data-table button[name=Delete]').live('click',function(){
	
			$(this).parent().parent().remove();
	
		});
		
		function applyRowStyle(){
		
			$('table.data-table tbody tr').each(function(i, item){
			
				if(i % 2 == 1){
					$(item).removeClass('even');
					$(item).addClass('odd');
				}else{
					$(item).removeClass('odd');
					$(item).addClass('even');
				}
			
			});
		
		};
		
	});

</script>