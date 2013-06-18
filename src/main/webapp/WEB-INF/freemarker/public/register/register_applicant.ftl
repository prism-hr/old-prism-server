<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Styles for Login List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
		
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/register.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/jquery-ui-1.8.23.custom.css' />"/>
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		
		<!-- Styles for login List Page -->
		
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
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

		<div id="wrapper">
		
			<#include "/public/common/global_header.ftl"/>
			
		  	<!-- Middle. -->
		  	<div id="middle">
		  
		    <!-- Prospectus container -->
      	<form id="applyForm" action="/pgadmissions/apply/new" method="POST">
      		<input type="hidden" id="program" name="program" value=""/>
      	</form>
        <div id="pContainer">
            <div id="pholder">
                <header>
                  <h1>Research Study Opportunities</h1></header>
                <section id="plist">
                	<ul></ul>
                </section>
                <footer class="clearfix">
                	<div class="left"><a href="www.engineering.ucl.ac.uk" target="_blank"><img src="<@spring.url '/design/default/images/ucl-engineering.jpg'/>" alt="" /></a></div>
                	<div class="right"><a href="http://prism.ucl.ac.uk" target="_blank"><img src="<@spring.url '/design/default/images/prism_small.jpg'/>" alt="" /></a></div>
                </footer>
            </div>
        </div>
        <!-- ENd Prospectus container -->
		
		    <!-- Login form. -->
		    <section id="registration-box">
		    
		    	<form method="post" action= "/pgadmissions/register/submit" class="form-horizontal">
		      		<fieldset>
		                <legend >Register Today</legend>

		            <input id="activationCode" type="hidden" name="activationCode" value="${pendingUser.activationCode!}"/>
		            <div class="control-group">
		                <label class="control-label" for="firstName">First Name <em>*</em></label>
		                <span class="hint" data-desc="Please enter your first name."></span>
		                <div class="controls">
			            <input id="firstName" type="text" name="firstName" value='${(pendingUser.firstName?html)!""}' <#if RequestParameters.activationCode?has_content>readonly="readonly"</#if> />
			            <@spring.bind "pendingUser.firstName" /> 
	              		<#list spring.status.errorMessages as error>	
	              		 	<div class="alert alert-error">	                                		
	                    	<i class="icon-warning-sign"></i> ${error}
	                    	</div>                 		
	                    </#list>
						</div>
                	</div>

		            <div class="control-group">
		                <label class="control-label" for="lastName">Last Name <em>*</em></label>
		                <span class="hint" data-desc="Please enter your last name."></span>
		                <div class="controls">
			            <input id="lastName" type="text" name="lastName" value='${(pendingUser.lastName?html)!""}' <#if RequestParameters.activationCode?has_content>readonly="readonly"</#if> />
	                    <@spring.bind "pendingUser.lastName" /> 
	                    <#list spring.status.errorMessages as error>		                                		
	                        <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>                     		
	                    </#list>
		           		</div>
                	</div>
					
					<div class="control-group">
		                <label class="control-label" for="email">Email <em>*</em></label>
		                <span class="hint" data-desc="Please enter your email address."></span>
		                <div class="controls">
			            <#if RequestParameters.activationCode?has_content>
			            <div id="email_tooltip_input" name="email_tooltip_input">
			            </#if>
			            <input id="email" type="email" placeholder="Email Address" name="email" value='${(pendingUser.email?html)!""}' <#if RequestParameters.activationCode?has_content>readonly="readonly"</#if> />
			            <#if RequestParameters.activationCode?has_content>
	                    </div>
	                    </#if>
			            <@spring.bind "pendingUser.email" /> 
	                    <#list spring.status.errorMessages as error>		                                		
	                        <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>                   		
	                    </#list>
		            	</div>
                	</div>
					
					<div class="control-group">
		                <label class="control-label" for="password">Password <em>*</em></label>
		                <span class="hint" data-desc="<@spring.message 'myaccount.newPw'/>"></span>
		                <div class="controls">
			            <input id="password" type="password" name="password" placeholder="Password"/>
	                    <@spring.bind "pendingUser.password" /> 
			        	<#list spring.status.errorMessages as error>		                                		
	                        <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>                     		
	                    </#list>
	                	</div>
	                </div>
	                <div class="control-group">
	                	<label class="control-label" for="password">Confirm <em>*</em></label>
	                	<span class="hint" data-desc="<@spring.message 'myaccount.confirmPw'/>"></span>
			            <div class="controls">
			            <input id="confirmPassword" type="password" name="confirmPassword" placeholder="Confirm password"/>
	                    <@spring.bind "pendingUser.confirmPassword" /> 
			            <#list spring.status.errorMessages as error>		                                		
	                    	<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>                      		
	                    </#list>
	                  	</div>
                	</div>  
		            <div class="control-group">
		                <div class="controls">
		                 <button type="submit" class="btn btn-primary">Register</button>
		                 </div>
		            </div>
		        	<div class="control-group">
               			<div class="controls">  
               				<#if Session.applyRequest?has_content>
                        		<a href="/pgadmissions/login">Already Registered?</a>
		                    </#if>
               			 </div>
		            </div>	
                    </fieldset>
		      	</form>
		    
		    </section>
			<ul id="conections">
                <li><a href="https://www.youtube.com/user/UCLPRISM/" target="_blank"><img src="<@spring.url '/design/default/images/youtube.png'/>" alt="" /><span>Online demonstrations</span></a></li>
                <li><a href="http://uclprism.freshdesk.com/support/home" target="_blank"><img src="<@spring.url '/design/default/images/freshdesk.png'/>" alt="" /><span>Help for UCL PRiSM users</span></a></li>
            </ul>
		  </div>
		  
		<#include "/public/common/global_footer.ftl"/>
		
		</div>
		
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery-ui-1.8.23.custom.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/register_applicant.js'/>"></script>
        <script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
        <script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/adverts.js' />"></script>
	</body>
</html>
