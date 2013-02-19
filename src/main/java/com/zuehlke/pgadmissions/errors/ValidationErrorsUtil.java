package com.zuehlke.pgadmissions.errors;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public final class ValidationErrorsUtil {

    private ValidationErrorsUtil() {
    }

    public static String getErrorString(BindingResult result) {
        StringBuilder errorStringBuilder = new StringBuilder();
        for (FieldError error : result.getFieldErrors()) {
            errorStringBuilder.append(error.getField() + ":" + error.getCode() + ";");
        }

        return errorStringBuilder.toString();
    }

    public static FundingErrors convertFundingErrors(String errs) {
        if (StringUtils.isBlank(errs)) {
            return null;
        }

        FundingErrors fundingErrors = new FundingErrors();
        String[] errors = errs.split(";");
        for (String error : errors) {
            String[] errorFieldAndCode = error.split(":");
            if ("fundingType".equals(errorFieldAndCode[0])) {
                fundingErrors.setFundingType(errorFieldAndCode[1]);
            } else if ("fundingDescription".equals(errorFieldAndCode[0])) {
                fundingErrors.setFundingDescription(errorFieldAndCode[1]);
            } else if ("fundingValue".equals(errorFieldAndCode[0])) {
                fundingErrors.setFundingValue(errorFieldAndCode[1]);
            } else if ("fundingAwardDate".equals(errorFieldAndCode[0])) {
                fundingErrors.setFundingAwardDate(errorFieldAndCode[1]);
            } else if ("fundingFile".equals(errorFieldAndCode[0])) {
                fundingErrors.setFundingFile(errorFieldAndCode[1]);
            }
        }
        return fundingErrors;
    }
}
