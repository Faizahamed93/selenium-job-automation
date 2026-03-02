package com.linkedinbot.validation;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigValidator {

    private static Properties props;

    public static void validateAll() throws Exception {
        props = loadProperties();
        validatePersonals();
        validateSecrets();
        validateSearch();
        validateQuestions();
        validateSettings();
        System.out.println("✓ All config values are valid!");
    }

    private static void validatePersonals() {
        checkString("personal.first_name", 1);
        checkString("personal.last_name", 1);
        checkString("personal.phone_number", 10);
        checkString("personal.ethnicity", 0,
            "Decline","Hispanic/Latino","Asian",
            "Black or African American","White","Other",
            "American Indian or Alaska Native",
            "Native Hawaiian or Other Pacific Islander");
        checkString("personal.gender", 0, "Male","Female","Other","Decline","");
        checkString("personal.disability_status", 0, "Yes","No","Decline");
        checkString("personal.veteran_status", 0, "Yes","No","Decline");
    }

    private static void validateSecrets() {
        checkString("secrets.username", 5);
        checkString("secrets.password", 5);
        checkBoolean("secrets.use_ai");
        checkString("secrets.ai_provider", 1, "openai","deepseek","gemini");
        checkBoolean("secrets.stream_output");
    }

    private static void validateSearch() {
        checkNonEmpty("search.terms");
        checkInt("search.switch_number", 1);
        checkBoolean("search.easy_apply_only");
        checkBoolean("search.under_10_applicants");
        checkBoolean("search.security_clearance");
        checkInt("search.current_experience", -1);
        checkBoolean("search.pause_after_filters");
    }

    private static void validateQuestions() {
        checkString("questions.require_visa", 0, "Yes","No");
        checkInt("questions.desired_salary", 0);
        checkInt("questions.notice_period", 0);
        checkBoolean("questions.pause_before_submit");
        checkBoolean("questions.pause_at_failed_question");
        checkBoolean("questions.overwrite_previous_answers");
    }

    private static void validateSettings() {
        checkString("settings.file_name", 1);
        checkString("settings.failed_file_name", 1);
        checkInt("settings.click_gap", 0);
        checkBoolean("settings.run_in_background");
        checkBoolean("settings.safe_mode");
    }

    private static void checkString(String key, int minLen, String... options) {
        String val = props.getProperty(key);
        if (val == null)
            throw new IllegalArgumentException("Missing: " + key);
        val = val.trim();
        if (val.length() < minLen)
            throw new IllegalArgumentException(
                key + " must be at least " + minLen + " chars. Got: " + val);
        if (options.length > 0 && !Arrays.asList(options).contains(val))
            throw new IllegalArgumentException(
                key + " must be one of " + Arrays.toString(options)
                + ". Got: " + val);
    }

    private static void checkNonEmpty(String key) {
        String val = props.getProperty(key);
        if (val == null || val.isBlank())
            throw new IllegalArgumentException(key + " must not be empty");
    }

    private static void checkBoolean(String key) {
        String val = props.getProperty(key);
        if (val == null || (!val.equalsIgnoreCase("true")
                         && !val.equalsIgnoreCase("false")))
            throw new IllegalArgumentException(
                key + " must be true or false. Got: " + val);
    }

    private static void checkInt(String key, int min) {
        String val = props.getProperty(key);
        if (val == null) throw new IllegalArgumentException("Missing: " + key);
        try {
            int i = Integer.parseInt(val.trim());
            if (i < min) throw new IllegalArgumentException(
                key + " must be >= " + min + ". Got: " + i);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                key + " must be an integer. Got: " + val);
        }
    }

    private static Properties loadProperties() throws Exception {
        Properties p = new Properties();
        var s = ConfigValidator.class.getClassLoader()
                    .getResourceAsStream("application.properties");
        if (s == null) throw new RuntimeException(
            "application.properties not found!");
        p.load(s);
        return p;
    }
}