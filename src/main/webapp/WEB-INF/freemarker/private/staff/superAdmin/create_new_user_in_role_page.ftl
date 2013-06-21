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
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >

<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/superadmin.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
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
					
					<div id="configManageUsersBox" class="tabbox">
						<input type="hidden" id="currentUserEmail" value="${user.email}">
				        <ul class="tabs">
				            <#if user.isInRole('SUPERADMINISTRATOR') || user.isInRole('ADMINISTRATOR')>
				            	<li><a href="#manageUsers">Manage Users</a></li>
				            </#if>
				            <#if user.isInRole('SUPERADMINISTRATOR')>
				            	<li><a href="#manageSuperadmins">Manage Super Administrators</a></li>
				            </#if>
				            <#if user.isInRole('SUPERADMINISTRATOR') || user.isInRole('ADMITTER')>
				            	<li><a href="#manageRegistryContacts">Manage Registry Contacts</a></li>
				            </#if>
				        </ul>
				        
				        <#if user.isInRole('SUPERADMINISTRATOR') || user.isInRole('ADMINISTRATOR')>
					        <div id="manageUsers"  class="tab-page">
						
								<!-- Remove form. -->
								<form id="removeForm" action="/pgadmissions/manageUsers/remove" method="POST">
									<input type="hidden" id="deleteFromUser" name="user" value=""/>						
									<input type="hidden" id="deleteFromProgram" name="selectedProgram" value=""/>
								</form>
									<section class="form-rows">
										<h2 class="no-arrow">Manage Users</h2>
										<div>
										<form id="editRoles" name="editRoles" action="/pgadmissions/manageUsers/edit/saveUser" method="POST"  autocomplete="off">
											<!-- Table of users. -->
											<div class="tableContainer existingUsers"></div>
				
											<div class="row-group">
											
												<div class="row">
													<label for="programs" class="plain-label">Programme<em>*</em></label>
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
														 <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
														</#list>
													</div>
												</div>
												
											</div>
						
											<div class="row-group" id="editUser">
											
												<h3><#if userDTO.newUser>Add New User<#else>Edit User Roles</#if></h3>
												
												<div class="row">
													<label for="firstName" class="plain-label<#if !userDTO.newUser> grey-label</#if>">First Name<em>*</em></label>
													<span class="hint<#if !userDTO.newUser> grey</#if>" data-desc="<@spring.message 'manageUsers.firstName'/>"></span>
													<div class="field">
														<input class="max" type="text" value="${(userDTO.firstName?html)!}" autocomplete="off" name="firstName" id="firstName" <#if !userDTO.newUser>disabled="disabled"</#if>/>			    
														<#if !userDTO.newUser><input type="hidden" value="${(userDTO.firstName?html)!}" name="firstName" /></#if>                              
														<@spring.bind "userDTO.firstName" /> 
														<#list spring.status.errorMessages as error>
															<div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
														</#list>			                             
													</div>
												</div>
						
												<div class="row">
													<label for="lastName" class="plain-label<#if !userDTO.newUser> grey-label</#if>">Last Name<em>*</em></label>
													<span class="hint<#if !userDTO.newUser> grey</#if>" data-desc="<@spring.message 'manageUsers.lastName'/>"></span>
													<div class="field">
														<input class="max" type="text" value="${(userDTO.lastName?html)!}" autocomplete="off" name="lastName" id="lastName"  <#if !userDTO.newUser>disabled="disabled"</#if>/>
														<#if !userDTO.newUser><input type="hidden" value="${(userDTO.lastName?html)!}" name="lastName" /></#if>    
														<@spring.bind "userDTO.lastName" /> 
														<#list spring.status.errorMessages as error>
															<div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
														</#list>
													</div>
												</div>
							
												<div class="row">
													<label for="email" class="plain-label<#if !userDTO.newUser> grey-label</#if>">Email<em>*</em></label>
													<span class="hint<#if !userDTO.newUser> grey</#if>" data-desc="<@spring.message 'manageUsers.email'/>"></span>
													<div class="field">
														<input class="max" type="email" value="${(userDTO.email?html)!}" autocomplete="off" name="email" id="email" <#if !userDTO.newUser>disabled="disabled"</#if>/>
														<#if !userDTO.newUser><input type="hidden" value="${(userDTO.email?html)!}" name="email" /></#if>    
														<@spring.bind "userDTO.email" /> 
														<#list spring.status.errorMessages as error>
															<div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
														</#list>
													</div>
												</div>
					
												<div class="row">
													<label for="roles" class="plain-label">Roles<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'manageUsers.roles'/>"></span>
													<div class="field">
														<select multiple size="5" id="roles" name="selectedAuthorities" class="max">
															<#list authorities as authority>
																<option value="${authority}" <#if userDTO.isInAuthority(authority)>selected="selected"</#if>>${authority?capitalize}</option>
															</#list>
														</select>
														<@spring.bind "userDTO.selectedAuthorities" /> 
														<#list spring.status.errorMessages as error>
															<div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
														</#list>
													</div>
												</div>
																						
												<div class="row">
													<div class="field">
														<button class="btn btn-primary" type="submit"><#if userDTO.newUser>Add<#else>Update</#if></button>
													</div>
												</div>
												
											</div><!-- .row-group -->
												</form>
										</div>
									</section>
									
								
							<!-- close manage users tab page -->
							</div>
						</#if>
						
						<#if user.isInRole('SUPERADMINISTRATOR')>
							<div id="manageSuperadmins" class="tab-page">
									<section id="superadmins" class="form-rows">
										<h2>Manage Super Administrators</h2>
										<div>
										<form id="editSuperadmins" name="editSuperadmins" action="/pgadmissions/manageUsers/edit/saveSuperadmin" method="POST"  autocomplete="off">
											<div class="tableContainer table table-condensed existingUsers">
												<table class="data" border="0">
													<colgroup>
														<col style="width: 30px;" />
														<col/>
													</colgroup>
													<tbody>
														<tr>
															<td colspan="4" class="scrollparent">
																<div class="scroll">
																	<table class="table-hover table-hover table-striped">
																		<colgroup>
																			<col style="width:30px;" />
																			<col  />
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
                                                                    </div>
																</td>
															</tr>
													</tbody>
												</table>
											</div><!-- #existingUsers -->
	
											<div class="row-group">
											
												<div class="row">
													<label class="plain-label" for="firstName">First Name<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'manageUsers.firstName'/>"></span>
													<div class="field">
														<input class="full" type="text"  value="${(adminDTO.firstName?html)!}" name="firstName" id="firstName" <#if !adminDTO.newUser>disabled="disabled"</#if>/>			                                  
														<@spring.bind "adminDTO.firstName" /> 
														<#list spring.status.errorMessages as error>
														<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
														</#list>			                             
													</div>
												</div>
						
												<div class="row">
													<label class="plain-label" for="lastName">Last Name<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'manageUsers.lastName'/>"></span>
													<div class="field">
														<input class="full" type="text" value="${(adminDTO.lastName?html)!}" name="lastName" id="lastName"  <#if !adminDTO.newUser>disabled="disabled"</#if>/>
														<@spring.bind "adminDTO.lastName" /> 
														<#list spring.status.errorMessages as error>
														<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
														</#list>
													</div>
												</div>
						
												<div class="row">
													<label class="plain-label" for="email">Email<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'manageUsers.email'/>"></span>
													<div class="field">
														<input class="full" type="email" value="${(adminDTO.email?html)!}" name="email" id="email" <#if !adminDTO.newUser>disabled="disabled"</#if>/>
														<@spring.bind "adminDTO.email" /> 
														<#list spring.status.errorMessages as error>
														<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
														</#list>
													</div>
												</div>
					
												<div class="row">
													<div class="field">
														<button class="btn btn-primary" type="submit"><#if adminDTO.newUser>Add<#else>Update</#if></button>
													</div>
												</div>
						
											</div><!-- .row-group -->
											</form>  
										</div>
									</section>
													
							<!-- close manage superadmins tab page -->
							</div>
						</#if>
						
						<#if user.isInRole('ADMITTER') || user.isInRole('ADMINISTRATOR')>
							<div id="manageRegistryContacts" class="tab-page">
									<section class="form-rows">
											<h2>Manage Registry Contacts</h2>
										<div>
											<form id="addRemoveRegistryUsers"  autocomplete="off">
											<div class="tableContainer table table-condensed ">
	                                        <div class="tableContainer table table-condensed existingUsers">
													<table class="data" border="0">
														<tbody>
															<tr>
																<td colspan="4" class="scrollparent">
																	<div class="scroll">
																		<table id="registryUsers" class="table-hover table-hover table-striped">
	                                                                        <colgroup>
	                                                                            <col style="width: 30px;" />
	                                                                            <col />
	                                                                            <col style="width: 30px;" />
	                                                                        </colgroup>
	                                                                        <tbody>
	                                                                            <#list allRegistryUsers! as regUser>
	                                                                            <tr>
	                                                                               <td>
	                                                                                   <span class="arrow"></span>
	                                                                               </td>
	                                                                                <td>
	                                                                                    ${regUser.firstname?html} ${regUser.lastname?html} (${regUser.email?html})
	                                                                                </td>
	                                                                                <td>
	                                                                                    <button class="button-delete" type="button" data-desc="Remove">Remove</button>
	                                                                                    <input type="hidden" name="firstname" value="${regUser.firstname!}" />
	                                                                                    <input type="hidden" name="lastname" value="${regUser.lastname!}" />
	                                                                                    <input type="hidden" name="email" value="${regUser.email!}" />
	                                                                                    <input type="hidden" name="id" value="<#if regUser.id??>${encrypter.encrypt(regUser.id)}</#if>" />
	                                                                                </td>
	                                                                            </tr>
	                                                                            </#list>
	                                                                        </tbody>
	                                                                    </table>
	                                                                    </div>
																	</td>
																</tr>
														</tbody>
													</table>
											</div>
										
											<div class="row-group">
												<!-- Entry form. -->
												<div class="row">
													<label for="reg-firstname" class="plain-label">First Name<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'configuration.firstName'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="reg-firstname" autocomplete="off" name="regUserFirstname" />
													</div>
												</div><!-- .row -->
												
												<div class="row">
													<label for="reg-lastname" class="plain-label">Last Name<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'configuration.lastName'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="reg-lastname" autocomplete="off" name="regUserLastname" />
													</div>
												</div><!-- .row -->
												
												<div class="row">
													<label for="reg-email" class="plain-label">Email Address<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'configuration.email'/>"></span>
													<div class="field">	
														<input type="email" class="full" id="reg-email" autocomplete="off" name="regUserEmail" />
													</div>
												</div><!-- .row -->
								
												<div class="row">
													<div class="field">	
														<button class="btn btn-primary" type="button" id="registryUserAdd">Add</button>
													</div>
												</div><!-- .row -->
											</div>
											</form>
										</div>
										<div id = "regContactData" style="display:none;"></div>
									</section>			
								
							<!-- close manage registry contacts tab page -->
							</div>
						</#if>
						
					<!-- close tab box -->
					</div>
			
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
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js'/>"></script>

</body>
</html>
