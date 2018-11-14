package com.ranorex.jenkinsranorexplugin.util;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

public class RanorexParameter extends BaseArgument {
    private static final ArrayList<String> WHITELIST_PARAM_FLAGS = new ArrayList<>(Arrays.asList(
            "pa", "param"
    ));
    private static final String SEPARATOR = ":";


    public RanorexParameter(String parameterString) {
        if (isValid(parameterString)) {
            try {
                String[] splitParam = trySplitArgument(parameterString);
                this.flag = splitParam[0];
                this.name = splitParam[1];
                this.value = splitParam[2];
            } catch (InvalidParameterException e) {
                throw e;
            }
        } else {
            throw new InvalidParameterException("'" + parameterString + "' is not a valid Parameter");
        }
    }

    protected static String[] trySplitArgument(String parameterString) {
        if (StringUtil.isNullOrSpace(parameterString)) {
            throw new InvalidParameterException("Cannot split empty string");
        }

        String splitParam[] = new String[3];
        try {
            splitParam[0] = tryExtractFlag(parameterString);
        } catch (InvalidParameterException e) {
            System.out.println("[INFO] [RanorexParameter]: Method tryExtractFlag() threw an InvalidParameterException \n\t" + e.getMessage() + "\n\tParameterflag 'pa' will be used as default");
            splitParam[0] = "pa";
        }
        if (containsValidNameValuePair(parameterString) && isValidFlag(splitParam[0])) {
            int equalsPosition = parameterString.indexOf("=");
            int separatorPosition = parameterString.indexOf(SEPARATOR);
            splitParam[1] = parameterString.substring(separatorPosition + 1, equalsPosition);
            splitParam[2] = parameterString.substring(equalsPosition + 1);
        } else {
            throw new InvalidParameterException("Parameter '" + parameterString + "' is not valid");
        }
        return splitParam;
    }

    public static boolean isValidFlag(String parameterFlag) {
        return WHITELIST_PARAM_FLAGS.contains(parameterFlag.trim());
    }

    public static boolean containsValidNameValuePair(String parameterString) {
        int equalPosition = parameterString.indexOf("=");
        return equalPosition > 0 && equalPosition < parameterString.length() - 1;
    }

    @SuppressWarnings ("CatchMayIgnoreException")
    public static boolean isValid(String parameterString) {
        String flag = "";
        try {
            flag = tryExtractFlag(parameterString);
        } catch (Exception e) {
        }
        boolean isValidNameValuePair = containsValidNameValuePair(parameterString);
        return isValidFlag(flag) && isValidNameValuePair
                || StringUtil.isNullOrSpace(flag) && isValidNameValuePair;
    }

    public static String tryExtractFlag(String parameterString) {
        int separatorPosition = parameterString.indexOf(SEPARATOR);
        if (separatorPosition > 0) {
            String flag = parameterString.substring(0, separatorPosition);
            flag = StringUtil.removeHeadingSlash(flag);
            return flag;
        } else {
            throw new InvalidParameterException("Parameter '" + parameterString + "' does not contain a separator!");
        }
    }
}
