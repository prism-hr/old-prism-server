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
<meta http-equiv="X-UA-Compatible" content="IE=7,chrome=1" />

<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
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
		<@header activeTab="users"/>
		<!-- Main content area. -->
		<article id="content" role="main">
		
			<div class="content-box">
				<div class="content-box-inner">
					
					<!-- Remove form. -->
					<form id="removeForm" action="/pgadmissions/manageUsers/remove" method="POST">
						<input type="hidden" id="deleteFromUser" name="user" value=""/>						
						<input type="hidden" id="deleteFromProgram" name="selectedProgram" value=""/>
					</form>
					
					<form id="editRoles" name="editRoles" action="/pgadmissions/manageUsers/edit" method="POST">
					
						<section class="form-rows">
							<h2 class="no-arrow">Manage Users</h2>
	
							<div>
								<!-- Table of users. -->
								<div id="existingUsers" class="tableContainer"></div>
								
								<div class="section-info-bar">
									Manage programme roles.<#if user.isInRole('SUPERADMINISTRATOR')> You can also <a class="proceed-link" href="<@spring.url '/manageUsers/superadmins'/>">manage superadministrators.</a></#if>
								</div>
	
								<div class="row-group">
								
									<div class="row programme">
										<span class="plain-label">Programme<em>*</em></span>
										<span class="hint" data-desc="<@spring.message 'manageUsers.programme'/>"></span>
										<div class="field">
											<select class="max" name="selectedProgram" id="programs">
												<option value="">Please select a program</option>
												<#list programs as program>"
												<option value='${program.code}' 
												<#if userDTO.selectedProgram?? && userDTO.selectedProgram.id == program.id >selected="selected"</#if>
												>${program.title?html}</option>               
												</#list>
											</select>
		
											<@spring.bind "userDTO.selectedProgram" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>
										</div>
									</div>
									
								</div>
			
								<div class="row-group">
								
									<h3><#if userDTO.newUser>Add New User<#else>Edit User Roles</#if></h3>
									
									<div class="row">
										<span class="plain-label<#if !userDTO.newUser> grey-label</#if>">First Name<em>*</em></span>
										<span class="hint" data-desc="<@spring.message 'manageUsers.firstName'/>"></span>
										<div class="field">
											<input class="max" type="text" value="${(userDTO.firstName?html)!}" name="firstName" id="firstName" <#if !userDTO.newUser>disabled="disabled"</#if>/>			    
											<#if !userDTO.newUser><input type="hidden" value="${(userDTO.firstName?html)!}" name="firstName" /></#if>                              
											<@spring.bind "userDTO.firstName" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>			                             
										</div>
									</div>
			
									<div class="row">
										<span class="plain-label<#if !userDTO.newUser> grey-label</#if>">Last Name<em>*</em></span>
										<span class="hint" data-desc="<@spring.message 'manageUsers.lastName'/>"></span>
										<div class="field">
											<input class="max" type="text" value="${(userDTO.lastName?html)!}"  name="lastName" id="lastName"  <#if !userDTO.newUser>disabled="disabled"</#if>/>
											<#if !userDTO.newUser><input type="hidden" value="${(userDTO.lastName?html)!}" name="lastName" /></#if>    
											<@spring.bind "userDTO.lastName" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>
										</div>
									</div>
				
									<div class="row">
										<span class="plain-label<#if !userDTO.newUser> grey-label</#if>">Email<em>*</em></span>
										<span class="hint" data-desc="<@spring.message 'manageUsers.email'/>"></span>
										<div class="field">
											<input class="max" type="text" value="${(userDTO.email?html)!}"  name="email" id="email" <#if !userDTO.newUser>disabled="disabled"</#if>/>
											<#if !userDTO.newUser><input type="hidden" value="${(userDTO.email?html)!}" name="email" /></#if>    
											<@spring.bind "userDTO.email" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>
										</div>
									</div>
		
									<div class="row">
										<span class="plain-label">Roles<em>*</em></span>
										<span class="hint" data-desc="<@spring.message 'manageUsers.roles'/>"></span>
										<div class="field">
											<select multiple size="5" id="roles" name="selectedAuthorities" class="max">
												<#list authorities as authority>
												<option value="${authority}" <#if userDTO.isInAuthority(authority)>selected="selected"</#if>>${authority?capitalize}</option>
												</#list>
											</select>
											<@spring.bind "userDTO.selectedAuthorities" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>
										</div>
									</div>
																			
									<div class="row">
										<div class="field">
											<button class="blue" type="submit"><#if userDTO.newUser>Add<#else>Update</#if></button>
										</div>
									</div>
									
								</div><!-- .row-group -->
									
							</div>
						</section>
						
					</form>
			
				</div><!-- .content-box-inner -->
			</div><!-- .content-box -->
		
		</article>
	
	</div><!-- #middle -->
	
<#include "/private/common/global_footer.ftl"/>

</div>

<!-- Scripts -->
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/roles.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>

</body>
</html>
