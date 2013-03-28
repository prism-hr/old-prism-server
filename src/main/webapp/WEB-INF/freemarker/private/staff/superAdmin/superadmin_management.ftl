<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >

<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/superadmin.css' />"/>
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>

<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>

<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
</head>

<body>
<div id="wrapper">

	<#include "/private/common/global_header.ftl"/>
	
	<!-- Middle. -->
	<div id="middle">
		<#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
		<@header activeTab="users"/>
		<!-- Main content area. -->
		<article id="content" role="main">
		
			<div class="content-box">
				<div class="content-box-inner">

					<form id="editRoles" name="editRoles" action="/pgadmissions/manageUsers/superadmins" method="POST">
		
						<section id="superadmins" class="form-rows">
							<h2>Manage Superadministrators</h2>

							<div>
							
								<div id="existingUsers">
									<table class="data" border="0">
										<colgroup>
											<col style="width: 20px;" />
											<col style="width: 472px;" />
										</colgroup>
										<thead>
											<tr>
												<th scope="col">&nbsp;</th>
												<th scope="col">Name</th>
												<th scope="col">&nbsp;</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td colspan="4" class="scrollparent">
													<div class="scroll">
														<table>
															<colgroup>
																<col style="width: 20px;" />
																<col style="width: 672px;" />
															</colgroup>
															<tbody>
																<#list superadmins as superadmin>
																<tr>
																	<td><span class="arrow">&nbsp;</span></td>
																	<td scope="col">${(superadmin.firstName?html)!} ${(superadmin.lastName?html)!} (${(superadmin.email?html)!})</td>
																</tr>
																</#list>
															</tbody>
														</table>
													</td>
												</tr>
										</tbody>
									</table>
								</div><!-- #existingUsers -->

								<div class="alert alert-info">
          							<i class="icon-info-sign"></i> 
									Manage superadministrators. You can also <a class="proceed-link" href="<@spring.url '/manageUsers/edit'/>">manage programme roles.</a>
								</div>

								<div class="row-group">
								
									<div class="row">
										<label class="plain-label" for="firstName">First Name<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'manageUsers.firstName'/>"></span>
										<div class="field">
											<input class="full" type="text"  value="${(userDTO.firstName?html)!}" name="firstName" id="firstName" <#if !userDTO.newUser>disabled="disabled"</#if>/>			                                  
											<@spring.bind "userDTO.firstName" /> 
											<#list spring.status.errorMessages as error>
											<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
											</#list>			                             
										</div>
									</div>
			
									<div class="row">
										<label class="plain-label" for="lastName">Last Name<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'manageUsers.lastName'/>"></span>
										<div class="field">
											<input class="full" type="text" value="${(userDTO.lastName?html)!}" name="lastName" id="lastName"  <#if !userDTO.newUser>disabled="disabled"</#if>/>
											<@spring.bind "userDTO.lastName" /> 
											<#list spring.status.errorMessages as error>
											<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
											</#list>
										</div>
									</div>
			
									<div class="row">
										<label class="plain-label" for="email">Email<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'manageUsers.email'/>"></span>
										<div class="field">
											<input class="full" type="email" value="${(userDTO.email?html)!}" name="email" id="email" <#if !userDTO.newUser>disabled="disabled"</#if>/>
											<@spring.bind "userDTO.email" /> 
											<#list spring.status.errorMessages as error>
											<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
											</#list>
										</div>
									</div>
		
									<div class="row">
										<div class="field">
											<button class="btn btn-primary" type="submit"><#if userDTO.newUser>Add<#else>Update</#if></button>
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
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>

</body>
</html>
