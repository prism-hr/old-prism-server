<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/style.css' />"/>
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/actions.css' />"/>
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	</head>

	<body>
		
		<div id="wrapper">
		
		  <!-- Header. -->
		  <div id="header">
		    <p>Postgraduate Research Admissions Tool</p>
		  </div>
		  
		  <!-- Middle. -->
		  <div id="middle">
		  
		    <header>
		
		      <!-- App logo and tagline -->
		      <div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
		
		      <div class="tagline">Your Gateway to Research Opportunities</div>
		
		      <!-- Main tabbed menu -->
		      <nav>
		        <ul>
		          <li><a href="#">My account</a></li>    
		          <li class="current"><a href="#">My applications</a></li>    
		          <li><a href="#">Messages</a></li>    
		          <li><a href="#">Help</a></li>    
		        </ul>
		        
		        <div class="user">
		          Jonathan Smith
		          <a class="button user-logout" href="<@spring.url '/j_spring_security_logout'/>">Logout</a>
		        </div>
		      </nav>
		      
		    </header>
		    
		    
		    <!-- Main content area. -->
		    <article id="content" role="main">
		          
		      <!-- content box -->
		      <div class="content-box">
		        <div class="content-box-inner">
		          
							<div id="programme-details">
		          
		          	<div class="row">
		            	<label>Programme Name</label>
		              <input disabled size="109" value="${model.application.project.program.code!} - ${model.application.project.program.title!}" />
		            </div>
		            
		          	<div class="row half">
		            	<label>Application Number</label>
		              <input disabled size="20" value="${model.application.id!}" />
		            </div>
		          </div>
		
				<form id="actions" action="<@spring.url '/reviewer/reviewerSuccess'/>" method = "POST">
		                    
		          	<div class="row">
		            	<label>Assigned Reviewers</label>
		              Jane Highsmith, Frank Johnson
		            </div>
		          
		          	<div class="row">
		            	<label>Assign Reviewer</label>
		            	<select name="reviewers" multiple="multiple">
				        
					        <#list model.reviewers as reviewer>
					            <option value="${reviewer.id}">${reviewer.firstName} ${reviewer.lastName}</option>               
					        </#list>
		            	
			             <select>
		            </div>
		            
		            <br />
		
		          	<div class="row">
		            	<label><input type="radio" /> Approve</label>
		            	<label><input type="radio" /> Reject</label>
		            </div>
		            
		            <br />
		
		            <div class="buttons">
		              <button type="submit">Assign</button>
		            </div>
		          
		         </form><!-- #actions -->
		          
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
		<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="js/libraries.js"></script>
		<script type="text/javascript" src="js/script.js"></script>
	</body>
</html>
