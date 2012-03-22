<#if model.usersInRoles?has_content>
	<#assign hasUsers = true>
<#else>
	<#assign hasUsers = false>
</#if> 

<!DOCTYPE HTML>

<#import "/spring.ftl" as spring />

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/superadmin.css' />"/>
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	</head>
	
	<body>
	
		<div id="wrapper">
		
			<#include "/private/common/global_header.ftl"/>
		  
		  	<!-- Middle. -->
		  	<div id="middle">
		  		<a href="/pgadmissions/"> back to applications list </a>
		    	<!-- Main content area. -->
		    	<article id="content" role="main">
		      
		      		<div class="content-box">
		      			<div class="content-box-inner">
		        
			          		<form name="programmeForm" action="/pgadmissions/assignUser/submit" method="POST">
			          
			          			<h1>Add Existing programme users and assign roles</h1>
			            		<p>Please add users to the programme, using their email addresses.<br>You can select one or more roles for the user.</p>
			            
			            		<br>
			            
			            		<div class="row programme">
				              		<label>Select programme</label>
				              		<select name="programSelect" id="programSelect" onChange="top.location.href=this.form.programSelect.options[this.form.programSelect.selectedIndex].value;return false;">
											<option value="">Please select a program</option>
	                                		<#list model.programs as program>
	                                    		<option value="/pgadmissions/manageUsers/showPage?programId=${program.id}" 
	                                    			<#if model.selectedProgram?? && model.selectedProgram.id == program.id >
													 selected = "selected"
													</#if>>${program.title}</option>               
	                                		</#list>
				              		</select>
			            		</div>
			            		<#if model.selectedProgram??>
			            		${model.selectedProgram.id}
			            		<input type="hidden" id="programId" value="${model.selectedProgram.id}"/>
								</#if>
								<!-- // EXISTING USERS -->
											
				            	<hr>
					          	<!-- Left side -->
					          	<div class="left-column">
					            
					              	<div class="row">
					                	<label>Please choose a user</label>
					                	<select id="userId">
					                			<option value="">Please choose a user</option>
					                			<#list model.usersInRoles as userInRole>
						                			<option value="${userInRole.id}">${userInRole.firstName} ${userInRole.lastName}</option>      
												</#list>
					                		</select>
					              	</div>
					              
					              	<div class="row">
					                	or <a href="#">add a new user</a>
					              	</div>
					              
					            </div>
					
								<!-- Right side -->
								<div class="right-column">
					            
					            	<div class="row">
					                	<label>Role(s) in application process</label>
					                	<select multiple size="4" id="roles" name="roles" >
                        				<#list model.roles as role>
                      						<option value="${role}">${role.displayValue}</option>
                      					</#list>
                      					</select>
					              	</div>
					            
					              	<div class="buttons">
					              		<input type="submit" value="Submit"/>
					              		<button class="blue" type="submit" value="adduser" >Add user</button>
					            	</div>
					              
								</div>
								
							</form>
		          
		          			<hr>
		          			
		          			<table class="data" border="0">
		            			<colgroup>
		              				<col style="width: 220px;" />
		              				<col style="width: auto;" />
		              				<col style="width: 200px;" />
		              				<col style="width: 100px;" />
		            			</colgroup>
		            			<thead>
		              				<tr>
		                				<th scope="col">Email address</th>
		                				<th scope="col">Name</th>
		                				<th scope="col">Role(s)</th>
		                				<th scope="col">Action</th>
		              				</tr>
		            			</thead>
		            			<tbody>
		            				<#list model.usersInRoles as userInRole>
			              				<tr>
			                				 <td scope="col">${userInRole.email}</td>
						                	<td scope="col">${userInRole.firstName} ${userInRole.lastName}</td>
						                	<td scope="col">${userInRole.rolesList}</td>
			                				<td scope="col"><a href="#">Edit</a> / <a href="#">Remove</a></td>
			              				</tr>
									</#list>			              			
		            			</tbody>
		          			</table>
		
		        		</div><!-- .content-box-inner -->
		      		</div><!-- .content-box -->
		      
		    	</article>
		    
		  	</div>
		  
		  	<!-- Footer. -->
		  	<div id="footer">
		    	<ul>
		      		<li><a href="#">Privacy</a></li>
		      		<li><a href="#">Terms &amp; conditions</a></li>
		      		<li><a href="#">Contact us</a></li>
		      		<li><a href="#">Glossary</a></li>
		    	</ul>
		  	</div>
		
		</div>
		
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/help.js' />"></script>
		
	</body>
</html>
