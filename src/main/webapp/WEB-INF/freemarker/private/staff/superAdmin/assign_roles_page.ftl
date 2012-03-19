<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>

		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<!-- Styles for Application List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/superadmin.css' />"/>
		<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
		<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/engine.js'/>"></script>
	    <script type='text/javascript' language="javascript" src="<@spring.url '/dwr/util.js'/>"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/applicationList/formActions.js'/>"></script>
	    
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
			
				<!-- Main content area. -->
				<article id="content" role="main">
					<div class="content-box">
				    	<div class="content-box-inner">
				        
				        <div class="row">
                            <span class="label"><strong>Select Programme</strong></span>
                                
                            <select class="actionType" name="programSelect" id="programSelect">
                                
                            <option value="">Select...</option>
                                <#list model.programs as program>
                                    <option value="${program.id}">${program.title}</option>               
                                </#list>
                            </select>
                    </div>
				          		
				          	<form class="roles">
				          
								<div class="row">
				            		<label>Email address</label>
				              		<input type="text" name="email" />
				            	</div>
				
					          	<div class="row">
					            	<label>Role(s) in application process</label>
					              	<select multiple size="4">
							        	<option>Administrator</option>
							            <option>Approver</option>
							            <option>Interviewer</option>
							            <option>Reviewer</option>
					              	</select>
					            </div>
				
				            	<button class="plus"><span></span> Add another</button>
				
				          	</form>
				          
				          	<hr>
				          
				          	<table class="data" border="0">
				            	<colgroup>
				              		<col style="width: 220px;" />
				              		<col style="width: auto;" />
				              		<col style="width: 200px;" />
				            	</colgroup>
				            	<thead>
				              		<tr>
						                <th scope="col">Email address</th>
						                <th scope="col">Name</th>
						                <th scope="col">Role(s)</th>
						            </tr>
				            	</thead>
				            
				            	<tbody>
				            	<#list model.usersInRoles as userInRole>
				              		<tr>
						                <td scope="col">${userInRole.email}</td>
						                <td scope="col">${userInRole.firstName} ${userInRole.lastName}</td>
						                <td scope="col">${userInRole.rolesList}</td>
				              		</tr>
				              	</#list>	
				            	</tbody>
				          	</table>
				
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
