<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>UCL Postgraduate Admissions</title>
    </head>
    <body>
	   <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	   <#if feedId??><input type="hidden" id="feedId" value="${feedId}"/></#if>
	   <#if user??><input type="hidden" id="user" value="${user}"/></#if>
	   <#if upi??><input type="hidden" id="upi" value="${upi}"/></#if>
	   <#include "/private/prospectus/adverts.ftl"/>
    </body>
</html>
