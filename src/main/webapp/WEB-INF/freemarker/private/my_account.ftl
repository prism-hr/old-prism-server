<section id="my_account_section" >					          	
<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>

		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<!-- Styles for Application List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
				<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/my-account.js' />"></script>
	    
	    
	</head>
	
	<!--[if IE 9]>
	<body class="ie9">
	<![endif]-->
	<!--[if lt IE 9]>
	<body class="old-ie">
	<![endif]-->
	<!--[if (gte IE 9)|!(IE)]><!-->
	<body>
	<!--<![endif]-->
	
		<!-- Wrapper Starts -->
		<div id="wrapper">

			<#include "/private/common/global_header.ftl"/>
			
			 <!-- Middle Starts -->
			<div id="middle">
			
				<#include "/private/common/parts/nav_with_user_info.ftl"/>
				<@header activeTab="account"/>
					<!-- Main content area. -->
					<article id="content" role="main">		    
						
						<!-- content box -->				      
						<div class="content-box">
							<div class="content-box-inner">
							
								<section id="configuration" class="form-rows">
									<h2>Account Details</h2>
									<div>
										<form>
										
											<div class="section-info-bar">
												Edit your account details.
											</div>
										
											<div class="row-group">
												<div class="row"> 
													<span id="email-lbl" class="plain-label">First Name</span>
													<span class="hint" data-desc="<@spring.message 'myaccount.firstName'/>"></span>
													<div class="field">	
														<input class="full" type="text" id="firstName" value="${user.firstName}" />
													</div>
													<@spring.bind "updatedUser.firstName" />
													<#list spring.status.errorMessages as error>
														<div class="field">
															<span class="invalid">${error}</span>
														</div>
													</#list>
												</div>
												
												<div class="row"> 
													<span id="email-lbl" class="plain-label">Last Name</span>
													<span class="hint" data-desc="<@spring.message 'myaccount.lastName'/>"></span>
													<div class="field">	
														<input class="full" type="text" id="lastName" value="${user.lastName}" />
													</div>
													<@spring.bind "updatedUser.lastName" />
													<#list spring.status.errorMessages as error>
														<div class="field">
															<span class="invalid">${error}</span>
														</div>
													</#list>
												</div>
												
												<div class="row"> 
													<span id="email-lbl" class="plain-label">Email</span>
													<span class="hint" data-desc="<@spring.message 'myaccount.email'/>"></span>
													<div class="field">	
														<input class="full" type="text" id="email" value="${user.email}" />
													</div>
													<@spring.bind "updatedUser.email" />
													<#list spring.status.errorMessages as error>
														<div class="field">
															<span class="invalid">${error}</span>
														</div>
													</#list>
												</div>
											</div>

											<div class="row-group">
												<h3>Change Password</h3>
												<div class="row"> 
													<span class="plain-label">Current Password</span>
													<span class="hint" data-desc="<@spring.message 'myaccount.currentPw'/>"></span>
													<div class="field">	
														<input class="full" id="currentPassword" type="password" />
													</div>
													<@spring.bind "updatedUser.password" />
													<#list spring.status.errorMessages as error>
														<div class="field">
															<span class="invalid">${error}</span>
														</div>
													</#list>
												</div>
												
												<div class="row"> 
													<span class="plain-label">New Password</span>
													<span class="hint" data-desc="<@spring.message 'myaccount.newPw'/>"></span>
													<div class="field">	
														<input class="full" id="newPassword" type="password"  />
													</div>
													<@spring.bind "updatedUser.newPassword" />
													<#list spring.status.errorMessages as error>
														<div class="field">
															<span class="invalid">${error}</span>
														</div>
													</#list>
												</div>
												
												<div class="row"> 
													<span class="plain-label">Re-enter new Password</span>
													<span class="hint" data-desc="<@spring.message 'myaccount.confirmPw'/>"></span>
													<div class="field">	
														<input class="full" id="confirmNewPass" type="password" />
													</div>
													<@spring.bind "updatedUser.confirmPassword" />
													<#list spring.status.errorMessages as error>
														<div class="field">
															<span class="invalid">${error}</span>
														</div>
													</#list>
												</div>
												
												
											</div><!-- .row-group -->
										
											<div class="buttons">						        		
												<button type="button" id="cancelMyACnt" value="cancel">Clear</button>
												<button class="blue" id="saveChanges" type="button" value="Submit">Save Changes</button>						        
											</div>
											
										</form>
									</div>
								</section>

							</div><!-- .content-box-inner -->
						</div><!-- .content-box -->
							
					</article>
				
				</div>
				<!-- Middle Ends -->
			
			<#include "/private/common/global_footer.ftl"/>
			
		</div>
		<!-- Wrapper Ends -->
		   
	</body>
</html>
</section>