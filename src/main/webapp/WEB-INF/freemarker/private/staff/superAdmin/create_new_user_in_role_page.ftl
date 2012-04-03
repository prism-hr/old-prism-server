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
		  		    <#include "/private/common/parts/nav_with_user_info.ftl"/>
		    	<!-- Main content area. -->
		    	<article id="content" role="main">
		      
		      		<div class="content-box">
		      			<div class="content-box-inner">
		        
			          		<form id ="createNewUser" name="createNewUser" action="/pgadmissions/manageUsers/createNewUser" method="POST">
			          
			          			<h1>Add New programme users and assign roles</h1>
			            		<p>Please create a new user to administer the programme.</p>
			            
			            		<br>
			            
			            		<div class="row programme">
				              		<label>Select programme</label>
				              		<select name="selectedProgramForNewUser" id="selectedProgramForNewUser">
											<option value="">Please select a program</option>
	                                		<#list programs as program>"
	                                    		<option value='${program.id?string("######")}' 
	                                    			<#if selectedProgram?? && selectedProgram.id == program.id >
													 selected = "selected"
													</#if>
												>${program.title}</option>               
	                                		</#list>
				              		</select>
			            		</div>
					          	<div class="left-column">
					            
					            <div class="row">
                                    <label class="label">First Name<em>*</em></label>
                                            <div class="field">
                                                <input class="full" type="text" <#if newUserFirstName??> value="${newUserFirstName}"</#if>  name="newUserFirstName" id="newUserFirstName"/>
                               <#if result?? && result.getFieldError('newUserFirstName')??>
                                   <p class="invalid"><@spring.message  result.getFieldError('newUserFirstName').code /></p>
                               </#if>      
                               </div>
                               </div>

                                <div class="row">
                                    <label class="label">Last Name<em>*</em></label>
                                    <div class="field">
                                        <input class="full" type="text" <#if newUserLastName??> value="${newUserLastName}"</#if>  name="newUserLastName" id="newUserLastName"/>
                                         <#if result?? && result.getFieldError('newUserLastName')??>
                                            <p class="invalid"><@spring.message  result.getFieldError('newUserLastName').code /></p>
                                        </#if> 
                                    </div>
                                </div>
                                
                                 <div class="row">
                                    <label class="label">Email<em>*</em></label>
                                            <div class="field">
                                                <input class="full" type="text" <#if newUserEmail??> value="${newUserEmail}"</#if>  name="newUserEmail" id="newUserEmail"/>
                                                <#if result?? && result.getFieldError('newUserEmail')??>
                                                    <p class="invalid"><@spring.message  result.getFieldError('newUserEmail').code /></p>
                                                </#if> 
                                            </div>
                                 </div>
                                
					            </div>
					
								<!-- Right side -->
								<div class="right-column">
					            
					            	<div class="row">
					                	<label>Role(s) in application process</label>
					                	<select multiple size="4" id="roles" name="newRoles" >
                        				<#list authorities as authority>
                      						<option value="${authority}">${authority}</option>
                      					</#list>
                      					</select>
					              	</div>
					            
					              	<div class="buttons">
					              		<button type="submit" value="createuser" >Create user</button>
					            	</div>
					              
								</div>
								
							</form>
		          
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
