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
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application_list.css' />"/>
		<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>	    
	    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/applicationList/formActions.js'/>"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
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
				
				    <!-- Main content area. -->
				    <article id="content" role="main">
				      
				      <!-- content box -->
				      <input type="hidden" id="appList" name="appList" />
				      <div class="content-box">
				        <div class="content-box-inner">
							
									<p style="color:red;">${(message?html)!}</p>
									<div id="search-box"> 
										<label for="searchTerm">Search</label>
										<input type="text" id="searchTerm" name="searchTerm" />
										<button type="button">search</button>
										
										<label for="searchCategory">Filter by Actions</label>
										<select name="searchCategory" id="searchCategory">
											<option value="">Column...</option>
											<#list searchCategories as category>
											<option value="${category}">${category.displayValue()}</option>               
											</#list>
										</select>	
									</div>
							
									<section id="applicationListSection"></section>
                      	    
				          <p class="right">
				            <#if (user.isInRole('SUPERADMINISTRATOR') || user.isInRole('ADMINISTRATOR'))>
<!--
                                <a id="manageUsersButton" class="button">Manage Users</a>
-->
                            </#if>
				            <#if (user.isInRole('SUPERADMINISTRATOR'))>
<!--
                                <a id="configuration" class="button">Configuration</a>
-->
                            </#if>
                            <#if (applications?size > 0)>
				          		<a class="button" name="downloadAll" id="downloadAll">Download</a>
				          	</#if>
				          	<#include "/private/common/feedback.ftl"/>
				          </p>
                    	  
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
