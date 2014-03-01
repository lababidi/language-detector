package com.optimaize.langdetect;

import be.frma.langguess.LangProfileReader;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.util.LangProfile;
import com.google.common.collect.ImmutableList;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.OldLangProfileConverter;
import com.optimaize.langdetect.text.*;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;


/**
 * @author Fabian Kessler
 */
public class LanguageDetectorImplTest {

    @Test
    public void german() throws LangDetectException, IOException {
        LanguageDetector languageDetector = makeNewDetector();
        List<DetectedLanguage> result = languageDetector.getProbabilities("Dies ist eine deutsche Text");
        DetectedLanguage best = result.get(0);
        assertEquals(best.getLanguage(), "de");
        assertTrue(best.getProbability() >= 0.9999d);
    }

    @Test
    public void germanShort() throws LangDetectException, IOException {
        LanguageDetector languageDetector = makeNewDetector();
        List<DetectedLanguage> result = languageDetector.getProbabilities("deutsche Text");
        DetectedLanguage best = result.get(0);
        assertEquals(best.getLanguage(), "de");
        assertTrue(best.getProbability() >= 0.9999d);
    }

    @Test
    public void germanShortWithUrl() throws LangDetectException, IOException {
        TextObjectFactory textObjectFactory = new TextObjectFactoryBuilder()
                .withTextFilter(RemoveMinorityScriptsTextFilter.forThreshold(0.3))
                .withTextFilter(UrlTextFilter.getInstance())
                .build();
        TextObject inputText = textObjectFactory.create().append("deutsche Text").append(" ").append("http://www.github.com/");

        LanguageDetector languageDetector = makeNewDetector();
        List<DetectedLanguage> result = languageDetector.getProbabilities(inputText);
        DetectedLanguage best = result.get(0);
        assertEquals(best.getLanguage(), "de");
        assertTrue(best.getProbability() >= 0.9999d);
    }

//    @Test
//    public void random() throws LangDetectException, IOException {
//        LanguageDetector languageDetector = makeNewDetector();
//        List<DetectedLanguage> result = languageDetector.detect("Aasdf werfasdf adsfaweasdf adsf");
//        System.out.println(result);
////        DetectedLanguage best = result.get(0);
////        assertEquals(best.getLanguage(), "de");
////        assertTrue(best.getProbability() >= 0.9999d);
//    }

    private LanguageDetector makeNewDetector() throws IOException, LangDetectException {
        LanguageDetectorBuilder builder = new LanguageDetectorBuilder();
        builder.skipUnknownNgrams(false);
        builder.shortTextAlgorithm(50);

        LangProfileReader langProfileReader = new LangProfileReader();
        for (String language : ImmutableList.of("en", "fr", "nl", "de")) {
            LangProfile langProfile = langProfileReader.readProfile(LanguageDetectorImplTest.class.getResourceAsStream("/languages/" + language));
            LanguageProfile languageProfile = OldLangProfileConverter.convert(langProfile);
            builder.withProfile(languageProfile);
        }

        return builder.build();
    }

}