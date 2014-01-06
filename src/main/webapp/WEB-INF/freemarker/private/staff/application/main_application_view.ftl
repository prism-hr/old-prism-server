<!-- Programme -->
<section id="programmeDetailsSection" class="form-rows violet">
<#include "/private/staff/application/components/programme_details.ftl"/>
</section>

<!-- Personal Details -->
<section id="personalDetailsSection" class="form-rows purple">
<#include "/private/staff/application/components/personal_details.ftl"/>
</section>

<!-- Address -->
<section class="form-rows red">
<#include "/private/staff/application/components/address_details.ftl"/>
</section>

<!-- Qualifications -->
<section class="form-rows orange">
<#include "/private/staff/application/components/qualification_details.ftl"/>
</section>

<!-- Employment positions -->
<section class="form-rows yellow">
<#include "/private/staff/application/components/employment_position_details.ftl"/>
</section>

<!-- Funding -->
<section class="form-rows green">
<#include "/private/staff/application/components/funding_details.ftl"/>
</section>

<!-- References -->
<section class="form-rows navy">
<#include "/private/staff/application/components/references_details.ftl"/>
</section>

<!-- Documents -->
<section class="form-rows blue">
<#include "/private/staff/application/components/documents.ftl"/>
</section>

<!-- Additional information -->
<#if user == applicationForm.applicant>
  <section id="additionalInformationSection" class="form-rows lightblue">
  <#include "/private/staff/application/components/additional_information.ftl"/>
  </section>
</#if>