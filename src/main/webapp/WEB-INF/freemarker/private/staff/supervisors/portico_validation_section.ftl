<input type="hidden" id="applicationId" value="${applicationForm.applicationNumber}"/>

<section id="qualificationsSection" class="folding form-rows orange"><#include "/private/staff/supervisors/components/qualification_portico_validation.ftl"/></section>

<section id="referencesSection" class="folding form-rows navy"><#include "/private/staff/supervisors/components/reference_portico_validation.ftl"/></section>

<hr />

<div class="buttons">
    <button class="btn btn-primary" type="button" id="applyQualificationsAndReferences">Submit</button>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/supervisor/portico_validation.js'/>"></script>