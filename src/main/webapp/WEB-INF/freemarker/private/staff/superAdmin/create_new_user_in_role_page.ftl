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
		<!-- Main content area. -->
		<article id="content" role="main">
		
			<div class="content-box">
				<div class="content-box-inner">
				
					<form id ="createNewUser" name="createNewUser" action="/pgadmissions/manageUsers/createNewUser" method="POST">
					
						<h1>Manage Users</h1>
						<div class="section-info-bar">
							Manage programme roles. You can also manage superadministrators.
						</div>
		
						<section class="form-rows">
							<div>
							
								<div class="row-group">
								
									<div class="row programme">
										<span class="plain-label">Programme</span>
										<span class="hint" data-desc=""></span>
										<div class="field">
											<select name="selectedProgram" id="selectedProgramForNewUser">
												<option value="">Please select a program</option>
												<option value="-1">All programs</option>
												<#list programs as program>"
												<option value='${program.code}' 
												<#if selectedProgram?? && selectedProgram.id == program.id>selected="selected"</#if>
												>${program.title?html}</option>               
												</#list>
											</select>
		
											<@spring.bind "newUserDTO.selectedProgram" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>
										</div>
									</div>
									
								</div>
		
								<div class="row-group">
								
									<div class="row">
										<span class="plain-label">First Name<em>*</em></span>
										<span class="hint" data-desc=""></span>
										<div class="field">
											<input class="full" type="text"  value="${(newUserDTO.firstName?html)!}" name="firstName" id="firstName"/>			                                  
											<@spring.bind "newUserDTO.firstName" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>			                             
										</div>
									</div>
			
									<div class="row">
										<span class="plain-label">Last Name<em>*</em></span>
										<span class="hint" data-desc=""></span>
										<div class="field">
											<input class="full" type="text" value="${(newUserDTO.lastName?html)!}"  name="lastName" id="lastName"/>
											<@spring.bind "newUserDTO.lastName" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>
										</div>
									</div>
			
									<div class="row">
										<span class="plain-label">Email<em>*</em></span>
										<span class="hint" data-desc=""></span>
										<div class="field">
											<input class="full" type="text" value="${(newUserDTO.email?html)!}"  name="email" id="email"/>
											<@spring.bind "newUserDTO.email" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>
										</div>
									</div>
		
									<div class="row">
										<span class="plain-label">Roles</span>
										<span class="hint" data-desc=""></span>
										<div class="field">
											<select multiple size="6" id="roles" name="selectedAuthorities" class="max">
												<#list authorities as authority>
												<option value="${authority}">${authority}</option>
												</#list>
											</select>
											<@spring.bind "newUserDTO.selectedAuthorities" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>
										</div>
									</div>
			
								</div>
		
								<div class="buttons">
									<button type="submit" value="createuser" >Create user</button>
								</div>
							</div>
			
						</section>
				
					</form>
			
				</div><!-- .content-box-inner -->
			</div><!-- .content-box -->
		
		</article>
	
	</div><!-- #middle -->
	
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
