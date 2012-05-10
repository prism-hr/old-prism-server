<#if usersInRoles?has_content>
	<#assign hasUsers = true>
<#else>
	<#assign hasUsers = false>
</#if> 

<!DOCTYPE HTML>

<#import "/spring.ftl" as spring />

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>
		
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
		  		    <#include "/private/common/parts/nav_with_user_info.ftl"/>
		    	<!-- Main content area. -->
		    	<article id="content" role="main">
		      
		      		<div class="content-box">
		      			<div class="content-box-inner">
		        
			          		<form id ="programmeForm" name="programmeForm" action="/pgadmissions/manageUsers/updateRoles" method="POST">
			          
			          			<h1>Add Existing programme users and assign roles</h1>
			            		<p>Please add users to the programme.<br>You can select one or more roles for the user.</p>
			            
			            		<br>
			            
			            		<div class="row programme">
				              		<label>Select programme</label>
				              		<select name="programId" id="programId">
											<option value="">Please select a program</option>
	                                		<#list programs as program>"
	                                    		<option value='${program.id?string("######")}' 
	                                    			<#if selectedProgram?? && selectedProgram.id == program.id >
													 selected = "selected"
													</#if>
												>${program.title}</option>               
	                                		</#list>
				              		</select>
				              		<#if result?? && result.getFieldError('selectedProgram')??>
                                            <p class="invalid"><@spring.message  result.getFieldError('selectedProgram').code /></p>
                                    </#if>
			            		</div>
								<!-- // EXISTING USERS -->
											
				            	<hr>
					          	<!-- Left side -->
					          	<div class="left-column">
					            
					              	<div class="row">
					                	<label>Please choose a user</label>
					                	<select id="userId" name="userId">
					                			<option value="">Please choose a user</option>
					                			<#list availableUsers as user>						                			
						                			<option value="${user.id?string("######")}"
						                			<#if selectedUser?? && selectedUser.id == user.id >
													 selected = "selected"
													</#if>
						                			>${user.firstName?html} ${user.lastName?html}</option>      
												</#list>
					                		</select>
					                	<#if result?? && result.getFieldError('selectedUser')??>
                                            <p class="invalid"><@spring.message  result.getFieldError('selectedUser').code /></p>
                                        </#if>
					              	</div>
					              	<div class="row">
					                	or <a href="/pgadmissions/manageUsers/createNewUser">add a new user</a>
					              	</div>
					            </div>
					
								<!-- Right side -->
								<div class="right-column">
					            
					            	<div class="row">
					                	<label>Role(s) in application process</label>
					                	<select multiple size="5" id="roles" name="newRoles" >
                        				<#list authorities as authority>
                      						<option value="${authority}" <#if selectedUser?? && selectedUser.isInRoleInProgram(authority, selectedProgram)>selected="selected" </#if>>${authority}</option>
                      					</#list>
                      					</select>
					              	</div>
					            
					              	<div class="buttons">
					              		<button type="submit" value="adduser" >Add / update user</button>
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
		            				<#list usersInRoles as userInRole>
			              				<tr>
			                				<td scope="col">${userInRole.email?html}</td>
						                	<td scope="col">${userInRole.firstName?html} ${userInRole.lastName?html}</td>
						                	<td scope="col">${userInRole.getAuthoritiesForProgramAsString(selectedProgram)}</td>
			                				<td scope="col"><a href="<@spring.url '/manageUsers/showPage?programId=${selectedProgram.id?string("#######")}&userId=${userInRole.id?string("#######")}'/>">Edit</a> / <a href="#" name="removeuser" id="remove_${userInRole.id?string("#######")}">Remove</a></td>
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
		<script type="text/javascript" src="<@spring.url '/design/default/js/admin/manageusers.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/help.js' />"></script>
		
		
	</body>
</html>
