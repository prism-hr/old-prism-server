<!-- Personal details -->
<section class="folding purple">
	<h2>
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Personal Details
	</h2>
	
	<div>
		<table class="existing">
			<colgroup>
				<col width="30" />
			    <col width="170" />
			    <col width="200" />
			    <col width="*" />
			    <col width="30" />
			    <col width="30" />
			</colgroup>
			<thead>
				<tr>
			    	<th colspan="2">First name</th>
			        <th>Surname</th>
			        <th>Email</th>
			        <th colspan="2">&nbsp;</th>
			    </tr>
			</thead>
			<tbody>
				<tr>
			    	<td><a class="row-arrow" href="#">-</a></td>
			        <td>Bob</td>
			        <td>Smith</td>
			        <td>bob.smith@test.com</td>
			        <td>edit</td>
			        <td>close?</td>
			    </tr>
			</tbody>
		</table>
		<form method="post" action="woooop">

              	<div>
                	<div class="row">
                  	<label class="label">First Name</label>
                    <span class="hint"></span>
                    <div class="field">
                    	<input class="full" type="text" value="Bob" name="firstName" id="firstName"/>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Last Name</label>
                    <span class="hint"></span>
                    <div class="field">
                    	<input class="full" type="text" value="Smith" name="lastName" id="lastName"/>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Gender</label>
                    <div class="field">
                      <label><input type="radio" name="gender" /> Male</label>
                      <label><input type="radio" name="gender" /> Female</label>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Date of Birth</label>
                    <span class="hint"></span>
                    <input class="half" type="date" value="" />
                  </div>
                </div>

              	<div class="buttons">
                  <a class="button blue" href="#">Close</a>
                  <button class="blue" type="submit">Save</button>
                </div>

		</form>
		
	</div>
</section>
