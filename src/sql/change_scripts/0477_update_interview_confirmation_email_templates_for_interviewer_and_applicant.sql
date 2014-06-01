UPDATE EMAIL_TEMPLATE SET content='<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r <html xmlns=\"http://www.w3.org/1999/xhtml\">\r 	<head>\r 		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r 		<title>Untitled Document</title>\r 	</head>\r 	<body>\r 		<table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r 			<tr>\r 		    	<td colspan=\"3\">\r 		    		<img src=\"${host}/pgadmissions/design/default/images/email/header.jpg\" width=\"600\" height=\"160\" alt=\"Prism: A Spectrum of Postgraduate Research Opportunities\" />\r 		    	</td>\r 		  	</tr>\r 		  	<tr>\r 		    	<td width=\"50\">\r 		    		<img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"10\" alt=\"\" />\r 		    	</td>\r 		    	<td width=\"500\">\r 		      		<h1 style=\"font-size: 12pt;\">\r 		      			<font face=\"Arial, Helvetica, sans-serif\" color=\"#0055A1\">Dear ${interviewer.user.firstName?html},</font>\r 		      		</h1>\r 			      	<p>\r 			      		<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">We can confirm the arrangements for your interview of ${applicant.firstName?html} ${applicant.lastName?html} in connection with Application ${application.applicationNumber} for UCL \r 							<#if application.researchHomePage??><a href=\"${application.researchHomePage}\">${application.program.title}</a><#else>${application.program.title}</#if>.\r 			      		</font>\r 			      	</p>\r 					<p>\r 			      		<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">The interview will take place at ${interviewer.interview.interviewTime} on ${interviewer.interview.interviewDueDate?string(\"dd MMM yyyy\")}.</font>\r 			      	</p>\r 			      	<p>\r 			      		<#if interviewer.interview.furtherInterviewerDetails??><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">${interviewer.interview.furtherInterviewerDetails?html}</font></#if>\r 			      	</p>\r 					<p>\r 				      	<#if interviewer.interview.locationURL?has_content>\r 					      	<!-- Button -->\r 							<a style=\"text-decoration:none;\" href=\"${interviewer.interview.locationURL}\" title=\"Get Directions\">\r 					            <img border=\"0\" style=\"border: none;\" width=\"133\" height=\"36\" alt=\"Get Directions\" src=\"${host}/pgadmissions/design/default/images/email/get_directions.png\" />\r 					         </a>\r 					    </#if>\r 					    <!-- Button -->\r 						<a style=\"text-decoration:none;\" \r 							  <#if !interviewer.user.enabled>\r 									href=\"${host}/pgadmissions/register?activationCode=${interviewer.user.activationCode}&directToUrl=${\"/application?view=view&applicationId=${application.applicationNumber}\"?url(\'ISO-8859-1\')}\"\r 							  <#else>\r 									href=\"${host}/pgadmissions/application?view=view&applicationId=${application.applicationNumber}&activationCode=${interviewer.user.activationCode}\"\r 							  </#if>								\r 								title=\"View Application\">\r 							<img border=\"0\" style=\"border: none;\" width=\"150\" height=\"36\" alt=\"View Application\" src=\"${host}/pgadmissions/design/default/images/email/view_app.png\" />\r 						</a>\r 					</p>\r 			      	<p>\r 						<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Please let us know by <a href=\"mailto:${adminsEmails}\">e-mail</a> if you are unable to attend.</font>\r 			      	</p>\r 			      	<p>\r 			      		<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Yours sincerely,<br />UCL Prism</font>\r 			      	</p>\r 		    	</td>\r 		    	<td width=\"50\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"10\" alt=\"\" />\r 				</td>\r 		  	</tr>\r 		  	<tr>\r 		    	<td colspan=\"3\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"30\" alt=\"\" />\r 				</td>\r 		  	</tr>\r 		  	<tr>\r 		    	<td colspan=\"3\" bgcolor=\"#6D6E71\" style=\"background-color: #6D6E71; line-height: 1; white-space: nowrap;\">\r 					<table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r 						<tr>\r 							<td colspan=\"5\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"600\" height=\"9\" alt=\"\" />\r 							</td>\r 						</tr>\r 						<tr>\r 							<td width=\"48\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"48\" height=\"45\" alt=\"\" />\r 							</td>\r 							<td width=\"288\">\r 								<font face=\"Arial, Helvetica, sans-serif\" size=\"1\" color=\"#BEBEC0\" style=\"font-size: 7pt;\">\r 									University College London, Gower Street, London, WC1E 6BT\r 									<br/>\r 									Tel: +44 (0) 20 7679 2000\r 									<br/>\r 									&copy; UCL 1999&ndash;2012\r 								</font>\r 							</td>\r 							<td width=\"50\">\r 								<img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"45\" alt=\"\" />\r 							</td>\r 							<td width=\"214\">\r 								<img src=\"${host}/pgadmissions/design/default/images/email/footer_logo.gif\" width=\"214\" height=\"45\" alt=\"\" />\r 							</td>\r 						</tr>\r 					</table>\r 				</td>\r 			</tr>\r             <tr>\r 				<td colspan=\"5\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"600\" height=\"9\" alt=\"\" /></td>\r             </tr>\r 		</table>\r 	</body>\r </html>' WHERE name='INTERVIEWER_NOTIFICATION'
;
UPDATE EMAIL_TEMPLATE SET content='<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r <html xmlns=\"http://www.w3.org/1999/xhtml\">\r 	\r 	<head>\r 		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r 		<title>Untitled Document</title>\r 	</head>\r \r 	<body>\r 		<table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r 			<tr>\r 		    	<td colspan=\"3\">\r 		    		<img src=\"${host}/pgadmissions/design/default/images/email/header.jpg\" width=\"600\" height=\"160\" alt=\"Prism: A Spectrum of Postgraduate Research Opportunities\" />\r 		    	</td>\r 		  	</tr>\r 		  	<tr>\r 		    	<td width=\"50\">\r 		    		<img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"10\" alt=\"\" />\r 		    	</td>\r 		    	<td width=\"500\">\r 		      		<h1 style=\"font-size: 12pt;\">\r 		      			<font face=\"Arial, Helvetica, sans-serif\" color=\"#0055A1\">Dear ${application.applicant.firstName},</font>\r 		      		</h1>\r 			      	<p>\r 				      	<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">\r 				      	<#if (previousStage.displayValue() = \"Interview\")>\r 				      		 We can confirm that your Application ${application.applicationNumber} for UCL <#if application.researchHomePage??><a href=\"${application.researchHomePage}\">${application.program.title}</a><#else>${application.program.title}</#if> has been advanced to a further stage of interview.\r 					    <#else>\r 				      		  We are pleased to confirm that your Application ${application.applicationNumber} for UCL <#if application.researchHomePage??><a href=\"${application.researchHomePage}\">${application.program.title}</a><#else>${application.program.title}</#if> has been advanced to interview.\r 					    </#if>\r 			      		</font>\r 			      	</p>\r 			      	<p>\r 			      		<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">The interview will take place at ${application.latestInterview.interviewTime} on ${application.latestInterview.interviewDueDate?string(\"dd MMM yyyy\")}.</font>\r 			      	</p>\r 			      	<p>\r 			      		<#if application.latestInterview.furtherDetails??><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">${application.latestInterview.furtherDetails?html}</font></#if>\r 			      	</p>\r 			      	\r 			      	<p>\r 			      	<#if application.latestInterview.locationURL?has_content>\r 				      	\r 				          <!-- Button -->\r 							<a style=\"text-decoration:none;\" href=\"${application.latestInterview.locationURL}\" title=\"Get Directions\">\r 				            	<img border=\"0\" style=\"border: none;\" width=\"133\" height=\"36\" alt=\"Get Directions\" src=\"${host}/pgadmissions/design/default/images/email/get_directions.png\" />\r 				          	</a>\r 				      	\r 				      </#if>\r \r 				          <!-- Button -->\r 							<a style=\"text-decoration:none;\" href=\"${host}/pgadmissions/application?view=view&applicationId=${application.applicationNumber}&activationCode=${application.applicant.activationCode}\" title=\"View/Update Application\">\r 				            	<img border=\"0\" style=\"border: none;\" width=\"193\" height=\"36\" alt=\"View/Update Application\" src=\"${host}/pgadmissions/design/default/images/email/view_update_app.png\" />\r 				          	</a>\r 				        </p>\r \r 				      \r 			      	<p>\r 			      		<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Please let us know by <a href=\"mailto:${adminsEmails}\">e-mail</a> if you are unable to attend.</font>\r 			      	</p>\r 			      	<p>\r 			      		<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Yours sincerely,<br />UCL Prism</font>\r 			      	</p>\r 		    	</td>\r 		    	<td width=\"50\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"10\" alt=\"\" /></td>\r 		  	</tr>\r 		  	<tr>\r 		    	<td colspan=\"3\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"30\" alt=\"\" /></td>\r 		  	</tr>\r 		  	<tr>\r 		    	<td colspan=\"3\" bgcolor=\"#6D6E71\" style=\"background-color: #6D6E71; line-height: 1; white-space: nowrap;\">\r             <table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r               <tr>\r                 <td colspan=\"5\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"600\" height=\"9\" alt=\"\" /></td>\r               </tr>\r               <tr>\r                 <td width=\"48\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"48\" height=\"45\" alt=\"\" /></td>\r                 <td width=\"288\">\r                   <font face=\"Arial, Helvetica, sans-serif\" size=\"1\" color=\"#BEBEC0\" style=\"font-size: 7pt;\">\r                     University College London, Gower Street, London, WC1E 6BT<br />\r                     Tel: +44 (0) 20 7679 2000<br />\r                     &copy; UCL 1999&ndash;2012\r                   </font>\r                 </td>\r                 <td width=\"50\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"45\" alt=\"\" /></td>\r                 <td width=\"214\"><img src=\"${host}/pgadmissions/design/default/images/email/footer_logo.gif\" width=\"214\" height=\"45\" alt=\"\" /></td>\r               </tr>\r               <tr>\r                 <td colspan=\"5\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"600\" height=\"9\" alt=\"\" /></td>\r               </tr>\r             </table>\r 		    	</td>\r 		  	</tr>\r 		</table>\r 	\r 	</body>\r </html>' WHERE name='MOVED_TO_INTERVIEW_NOTIFICATION'
;
