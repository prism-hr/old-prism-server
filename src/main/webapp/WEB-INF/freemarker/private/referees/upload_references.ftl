
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
					       <@header/>
          <!-- Main content area. -->
          <article id="content" role="main">
          
						<div class="content-box">
							<div class="content-box-inner">
                
                
								<!---------- Reference -------------->
								<section class="form-rows">
									<h2>Provide Reference</h2>
									<div>
										<form id="documentUploadForm" method="POST" action="<@spring.url '/referee/submitReference'/>">
										
										<input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
											<div class="section-info-bar">
												Provide an assessment of the applicant's suitability for postgraduate study and for their chosen study programme.
											</div>

											<div class="row-group">
											<div class="row"> 
												<span id="comment-lbl" class="plain-label">Comment<em>*</em></span>
												<span class="hint" data-desc=""></span>
												<div class="field">		            				
													<textarea name="comment" id="comment" class="max" rows="6" cols="80" >${(comment.comment?html)!}</textarea>
													<@spring.bind "comment.comment" /> 
													<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
												</div>
											</div>
											</div>
											
											<div class="row-group">
												<#include "/private/staff/admin/comment/documents_snippet.ftl"/>
											
												<h3>Applicant Suitability</h3>
											
												<div class="row">
													<span id="suitable-lbl" class="plain-label">Is the applicant suitable for postgraduate study at UCL?<em>*</em></span>
													<span class="hint" data-desc=""></span>
													<div class="field">
														<label><input type="radio" name="suitableForUCL" value="true" id="suitableRB_true"
														<#if comment.isSuitableForUCLSet() && comment.suitableForUCL> checked="checked"</#if>
														/> Yes</label> 
														<label><input type="radio" name="suitableForUCL" value="false" id="suitableRB_false"
														<#if comment.isSuitableForUCLSet() && !comment.suitableForUCL> checked="checked"</#if>
														/> No</label> 
														<@spring.bind "comment.suitableForUCL" /> 
															<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
														</div>
												</div>
											
											<div class="row">
												<span id="supervise-lbl" class="plain-label">Is the applicant suitable for their chosen postgraduate study programme?<em>*</em></span>
												<span class="hint" data-desc=""></span>
												<div class="field">
													<label><input type="radio" name="suitableForProgramme" value="true" id="willingRB_true"
													<#if comment.isSuitableForProgrammeSet() && comment.suitableForProgramme> checked="checked"</#if> 
													/> Yes</label> 
													<label><input type="radio" name="suitableForProgramme" value="false" id="willingRB_false"
													<#if comment.isSuitableForProgrammeSet() && !comment.suitableForProgramme> checked="checked"</#if>
													/> No</label> 
													<@spring.bind "comment.suitableForProgramme" /> 
													<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
												</div>
											</div>
	
										</div>
												
												<div class="buttons">
													<button type="reset" value="cancel">Cancel</button>
													<button class="blue" type="submit" id="referenceSaveButton" value="close">Submit</button>              
												</div>                      
											</div>
										</form>
                    <!---------- End Reference -------------->
                  
                    <#if !comment.id?? >
										<div class="row-group">
											<form id="declineForm" method="POST" action="<@spring.url '/referee/decline'/>">
												<input type="hidden" name="referee" value='<#if comment.referee?? && comment.referee.id??>${encrypter.encrypt(comment.referee.id)}</#if>'/>                    
												<p>If you are not able to act as a referee in this case, please let us know by clicking the "Decline" button below.</p>
												<div class="buttons">
													<button class="blue" type="button" id="declineReference" value="close">Decline</button>              
				                </div>
        				      </form>
										</div>
                    </#if>
                    
									</div>
								</section>
                    
							</div><!-- .content-box-inner -->
						</div><!-- .content-box -->
          
          </article>
        
        </div>
      
<#include "/private/common/global_footer.ftl"/>
    
    </div>
    
    <!-- Scripts -->
    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/help.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/application/ajaxfileupload.js'/>"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/admin/comment/upload.js'/>"></script>
  </body>
</html>
