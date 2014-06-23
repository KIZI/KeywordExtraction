package eu.linkedtv.keywords.v2.languages;

import java.util.HashMap;
import java.util.Map;

public enum SupportedLanguages {
    ENGLISH(1), GERMAN(2), DUTCH(3);
    
    private static final Map<String, SupportedLanguages> LANGUAGE_MAP = new HashMap<String, SupportedLanguages>();
    
    private final int languageId;
    
    static {
        LANGUAGE_MAP.put("german", GERMAN);
        LANGUAGE_MAP.put("dutch", DUTCH);
        LANGUAGE_MAP.put("english", ENGLISH);
    }
    
    private SupportedLanguages(int languageId) {
            this.languageId = languageId;
    }

    public int getLanguageId() {
        return languageId;
    }
    
   public static SupportedLanguages languageFactory(String languageName) {
       return LANGUAGE_MAP.get(languageName);
   }
}
