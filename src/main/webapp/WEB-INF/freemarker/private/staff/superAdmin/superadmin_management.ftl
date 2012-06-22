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
		<@header activeTab="users"/>
		<!-- Main content area. -->
		<article id="content" role="main">
		
			<div class="content-box">
				<div class="content-box-inner">
					<div id="existingUsers">
						<table class="data" border="0">
							<thead class="fixedHeader">
								<tr>
									<th scope="col">&nbsp;</th>
									<th scope="col">Name</th>
									<th scope="col">Role(s)</th>
									<th scope="col">&nbsp;</th>
								</tr>
							</thead>
							<tbody class="scrollContent">
								<#list superadmins as superadmin>
								<tr>
									<td><span class="arrow">&nbsp;</span></td>
									<td scope="col">${(superadmin.firstName?html)!} ${(superadmin.lastName?html)!} (${(superadmin.email?html)!} )</td>
									<td scope="col">
<#--
										<a class="button-edit" data-desc="Edit" href="<@spring.url '/manageUsers/edit?programCode=${selectedProgram.code}&user=${encrypter.encrypt(userInRole.id)}'/>">Edit</a>
										<a class="button-delete" data-desc="Remove" href="#" name="removeuser" id="remove_${encrypter.encrypt(userInRole.id)}">Remove</a>
-->
									</td>
								</tr>
								</#list>
							</tbody>
						</table>
					</div>

					<form id="editRoles" name="editRoles" action="/pgadmissions/manageUsers/superadmins" method="POST">
		
						<section class="form-rows">
							<h2>Manage Users</h2>
							<div>
							
								<div class="section-info-bar">
									Manage superadministrators. You can also <a href="<@spring.url '/manageUsers/edit'/>">manage programme roles.</a>
								</div>

								<div class="row-group">
								
									<div class="row">
										<span class="plain-label">First Name<em>*</em></span>
										<span class="hint" data-desc=""></span>
										<div class="field">
											<input class="full" type="text"  value="${(userDTO.firstName?html)!}" name="firstName" id="firstName" <#if !userDTO.newUser>readonly="readonly"</#if>/>			                                  
											<@spring.bind "userDTO.firstName" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>			                             
										</div>
									</div>
			
									<div class="row">
										<span class="plain-label">Last Name<em>*</em></span>
										<span class="hint" data-desc=""></span>
										<div class="field">
											<input class="full" type="text" value="${(userDTO.lastName?html)!}"  name="lastName" id="lastName"  <#if !userDTO.newUser>readonly="readonly"</#if>/>
											<@spring.bind "userDTO.lastName" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>
										</div>
									</div>
			
									<div class="row">
										<span class="plain-label">Email<em>*</em></span>
										<span class="hint" data-desc=""></span>
										<div class="field">
											<input class="full" type="text" value="${(userDTO.email?html)!}"  name="email" id="email" <#if !userDTO.newUser>readonly="readonly"</#if>/>
											<@spring.bind "userDTO.email" /> 
											<#list spring.status.errorMessages as error>
											<span class="invalid">${error}</span>
											</#list>
										</div>
									</div>
		
									<div class="buttons">
										<button type="submit"><#if userDTO.newUser>Add<#else>Edit</#if></button>
									</div>
			
								</div>
								
								<div class="buttons">
									<button type="reset" id="clear">Clear</button>
									<button type="submit" >Submit</button>
								</div>
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
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/help.js' />"></script>

</body>
</html>
