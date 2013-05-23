<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
${host}<@spring.url '/apply/new'/>?program=${programmeCode}
