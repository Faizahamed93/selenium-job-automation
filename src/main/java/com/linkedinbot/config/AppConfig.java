
package com.linkedinbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class AppConfig {

    @Value("${personal.first_name}")   public String firstName;
    @Value("${personal.last_name}")    public String lastName;
    @Value("${personal.phone_number}") public String phoneNumber;
    @Value("${secrets.username}")      public String username;
    @Value("${secrets.password}")      public String password;
    @Value("${secrets.use_ai}")        public boolean useAI;
    @Value("${search.terms}")          public String searchTermsRaw;
    @Value("${search.location}")       public String searchLocation;
    @Value("${search.switch_number}")  public int switchNumber;
    @Value("${search.easy_apply_only}") public boolean easyApplyOnly;
    @Value("${search.bad_words:}")     public String badWordsRaw;
    @Value("${search.current_experience}") public int currentExperience;
    @Value("${questions.years_of_experience}") public String yearsOfExperience;
    @Value("${questions.desired_salary}")      public int desiredSalary;
    @Value("${questions.pause_before_submit}") public boolean pauseBeforeSubmit;
    @Value("${settings.file_name}")    public String fileName;
    @Value("${settings.click_gap}")    public int clickGap;
    @Value("${settings.run_in_background}") public boolean runInBackground;
    @Value("${settings.safe_mode}")    public boolean safeMode;

    public List<String> getSearchTerms() { return parseList(searchTermsRaw); }
    public List<String> getBadWords()    { return parseList(badWordsRaw); }

    private List<String> parseList(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.stream(raw.split(","))
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .toList();
    }
}