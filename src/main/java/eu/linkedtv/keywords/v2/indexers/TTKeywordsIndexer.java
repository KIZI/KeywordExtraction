package eu.linkedtv.keywords.v2.indexers;

import eu.linkedtv.keywords.v1.dao.DaoException;
import eu.linkedtv.keywords.v1.indexers.IndexingException;
import eu.linkedtv.keywords.v2.dao.KeywordsDao;
import eu.linkedtv.keywords.v2.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v2.languages.SupportedLanguages;
import eu.linkedtv.keywords.v2.models.TextFile;
import eu.linkedtv.keywords.v2.models.User;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.creole.tokeniser.DefaultTokeniser;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TTKeywordsIndexer
        extends KeywordsIndexer<TextFile> {

    private static final String PIPELINE_CLASS = "gate.creole.SerialAnalyserController";
    private static final String TOKENIZER_CLASS = "gate.creole.tokeniser.DefaultTokeniser";
    private static final String GENERIC_TAGGER_CLASS = "gate.taggerframework.GenericTagger";
    private static final String JAPE_TRANSDUCER_CLASS = "gate.creole.Transducer";

    private static final String KEYWORD_ANNOTATION_NAME = "keyword";

    private static final Logger logger = LoggerFactory.getLogger(TTKeywordsIndexer.class);

    protected final Corpus corpus;
    protected final SerialAnalyserController pipeline;

    @Autowired
    protected KeywordsOccurrencesDao keywordsOccurrencesDao;

    @Autowired
    protected KeywordsDao keywordsDao;

    public TTKeywordsIndexer(String anniePluginPath, String taggerFrameworkPluginPath, String corpusName,
            String treeTaggerPath, String japeGrammarResource) throws IndexingException {
        try {
            Gate.init();
        } catch (GateException e) {
            throw new IndexingException("Problem initializing Gate Framework", e);
        }
        try {
            Gate.getCreoleRegister().registerDirectories(
                    new File(anniePluginPath).toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IndexingException("Wrong path to ANNIE plugin " + anniePluginPath, e);
        } catch (GateException e) {
            throw new IndexingException("Problem loading ANNIE plugin from path " + anniePluginPath, e);
        }
        try {
            Gate.getCreoleRegister().registerDirectories(
                    new File(taggerFrameworkPluginPath).toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IndexingException("Wrong path to Tagger Framework plugin " + taggerFrameworkPluginPath, e);
        } catch (GateException e) {
            throw new IndexingException("Problem loading Tagger Framework plugin from path " + taggerFrameworkPluginPath, e);
        }
        try {
            corpus = Factory.newCorpus(corpusName);
        } catch (ResourceInstantiationException e) {
            throw new IndexingException("Problems initializing corpus - corpus name: " + corpusName, e);
        }

        pipeline = initPipeline(treeTaggerPath, japeGrammarResource);
    }

    private SerialAnalyserController initPipeline(String treeTaggerPath, String japeGrammarResource) throws IndexingException {
        SerialAnalyserController pipeline;
        try {
            pipeline = (SerialAnalyserController) Factory.createResource(PIPELINE_CLASS);
        } catch (ResourceInstantiationException e) {
            throw new IndexingException("Problem initializing pipeline (class: " + PIPELINE_CLASS + ")", e);
        }
        DefaultTokeniser tokeniser;
        try {
            tokeniser = (DefaultTokeniser) Factory.createResource(TOKENIZER_CLASS);
        } catch (ResourceInstantiationException e) {
            throw new IndexingException("Problem initializing tokenizer (class: " + TOKENIZER_CLASS + ")", e);
        }
        pipeline.add(tokeniser);

        try {
            pipeline.add(initTagger(treeTaggerPath));
        } catch (ResourceInstantiationException e) {
            throw new IndexingException("Problem instatntiating tagger on path " + treeTaggerPath, e);
        }
        try {
            pipeline.add(initJapeGrammar(japeGrammarResource));
        } catch (ResourceInstantiationException e) {
            throw new IndexingException("Problem instatntiating Jape transducer with grammar " + japeGrammarResource, e);
        }

        return pipeline;
    }

    private ProcessingResource initTagger(String treeTaggerPath) throws ResourceInstantiationException {
        FeatureMap taggerFeatureMap = Factory.newFeatureMap();
        taggerFeatureMap.put("debug", "false");
        taggerFeatureMap.put("encoding", "UTF-8");
        taggerFeatureMap.put("failOnUnmappableCharacter", "true");
        taggerFeatureMap.put("featureMapping", "lemma=3;category=2;string=1");
        taggerFeatureMap.put("inputAnnotationType", "Token");
        taggerFeatureMap.put("inputTemplate", "${string}");
        taggerFeatureMap.put("outputAnnotationType", "Token");
        taggerFeatureMap.put("regex", "(.+)\t(.+)\t(.+)");
        taggerFeatureMap.put("taggerBinary", treeTaggerPath);

        return (ProcessingResource) Factory.createResource(GENERIC_TAGGER_CLASS, taggerFeatureMap);
    }

    private ProcessingResource initJapeGrammar(String japeGrammarResource) throws ResourceInstantiationException {
        FeatureMap transducerFeatureMap = Factory.newFeatureMap();
        transducerFeatureMap.put("grammarURL", getClass().getResource(japeGrammarResource));
        transducerFeatureMap.put("encoding", "UTF-8");

        return (ProcessingResource) Factory.createResource(JAPE_TRANSDUCER_CLASS, transducerFeatureMap);
    }

    @Override
    public void indexTextFile(TextFile textFile, String text, User user, SupportedLanguages language) throws IndexingException {
        //List<String> keywords = Arrays.asList(text.replaceAll("\\p{Punct}", "").split(" "));

        Map<String, Integer> wordOccurrences = new HashMap<String, Integer>();
        double keywordsCount = 0;
        for (String keyword : executePipeline(textFile, text)) {
            keyword = keyword.trim();
            if ((keyword.length() > 0) && (!isStopWord(keyword))) {
                Integer wordCount = wordOccurrences.get(keyword);
                if (wordCount == null) {
                    wordOccurrences.put(keyword, 1);
                } else {
                    wordOccurrences.put(keyword, wordCount + 1);
                }
                keywordsCount++;
            }
        }

        getKeywordsOccurrencesDao().deleteOccurrencesForFile(textFile);
        Map<String, Long> keywords = getKeywordsDao().getKeywords(new HashSet<String>(wordOccurrences.keySet()), user, language);
        for (Entry<String, Integer> entry : wordOccurrences.entrySet()) {
            try {
                getKeywordsOccurrencesDao().setOccurrenceCount(keywords.get(entry.getKey()), textFile, entry.getValue(), entry.getValue() / keywordsCount);
                getKeywordsDao().flush();
            } catch (DaoException e) {
                throw new IndexingException("Unable to index keyword " + entry.getKey(), e);
            }
        }
        getKeywordsOccurrencesDao().updateOccurrencesForFile(textFile.getFileId());

//        getKeywordsDao().updateIdf(user.getUserId(), language);
//        getKeywordsOccurrencesDao().updateTf();
        logger.info("File " + textFile.getName() + " indexed");
    }

    @Override
    public int updateIdf(User user, SupportedLanguages language) {
        return getKeywordsDao().updateIdf(user.getUserId(), language);
    }

    private List<String> executePipeline(TextFile textFile, String text) throws IndexingException {
        corpus.clear();
        try {
            corpus.add(Factory.newDocument(text));
        } catch (ResourceInstantiationException e) {
            throw new IndexingException("Problem adding file " + textFile.getName() + " to the corpus.", e);
        }

        pipeline.setCorpus(corpus);
        try {
            pipeline.execute();
        } catch (ExecutionException e) {
            throw new IndexingException("Problem executing Gate pipeline", e);
        }

        List<String> keywords = extractKeywords(corpus);
        pipeline.cleanup();
        return keywords;
    }

    private List<String> extractKeywords(Corpus corpus) {
        Iterator<Document> documentIterator = corpus.iterator();
        List<String> keywords = new LinkedList<String>();

        while (documentIterator.hasNext()) {
            Document currDoc = documentIterator.next();
            AnnotationSet annotations = currDoc.getAnnotations().get(KEYWORD_ANNOTATION_NAME);
            for (Annotation annotation : annotations) {
                try {
                    String keyword = currDoc.getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
                    keywords.add(keyword);
                } catch (InvalidOffsetException e) {
                    logger.error("Invalid offset when extracting keyword for annotation " + annotation, e);
                }
            }
        }

        return keywords;
    }

    protected KeywordsDao getKeywordsDao() {
        return keywordsDao;
    }

    protected KeywordsOccurrencesDao getKeywordsOccurrencesDao() {
        return keywordsOccurrencesDao;
    }

}
