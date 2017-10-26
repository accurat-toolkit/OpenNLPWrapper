package com.shef.ac.uk.util;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;

import kea.main.KEAKeyphraseExtractor;
import kea.main.KEAModelBuilder;
import kea.stemmers.PorterStemmer;
import kea.stopwords.StopwordsEnglish;
import opennlp.maxent.io.PooledGISModelReader;
import opennlp.tools.lang.english.NameFinder;
import opennlp.tools.lang.english.SentenceDetector;
import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.sentdetect.SentenceDetectorME;


public class TextProcessingUtilEnglish {
	private static SentenceDetectorME SENTENCE_DETECTOR = null;

	private static NameFinder[] FINDERS = null;
	private static NameFinder[] FINDERS2 = null;
	private static NameFinder[] FINDERS3 = null;
	private static Tokenizer TOKENIZER = null;




    public String[] detectSentences(String aText) throws IOException {
		if (SENTENCE_DETECTOR == null) {
			SENTENCE_DETECTOR = new SentenceDetector(OpenNLPConstants.ENGLISH_SENT_SPLIT_MAX_ENT_MODEL);				
		}
		return SENTENCE_DETECTOR.sentDetect(aText);

    }
    public static void main(String[] args) throws Exception {
//    	TextProcessingUtilEnglish util = new TextProcessingUtilEnglish();
    	//util.trainMoelForTE();
//    	util.detectTE("");
    	String xml = "<root>I am <PERSON>Ahmet <ORGANIZATION>Aker</ORGANIZATION> 4</PERSON>.</root>";
		Element root = Util.getFileContentAsDomElementFromString(xml).getRootElement();
		StringBuffer buffer = new StringBuffer();
		List list = root.getContent();
		for (int i = 0; i < list.size(); i++) {
			Object obj = list.get(i);
			if (obj instanceof Text) {
				buffer.append(((Text)obj).getText().trim()).append(" ");
			} else if (obj instanceof Element) {
				List list2 = ((Element)obj).getContent();
				buffer.append("<ENAMEX TYPE=\"").append(((Element)obj).getName()).append("\">");
				for (int j = 0; j < list2.size(); j++) {
					Object obj2 = list2.get(j);
					if (obj2 instanceof Text) {
						buffer.append(((Text)obj2).getText().trim());
					} else if (obj instanceof Element) {
						buffer.append(((Element)obj2).getText().trim());
					}
					if (j < list2.size()-1) {
						buffer.append(" ");														
					}

				}
				buffer.append("</").append("ENAMEX").append(">");				
			}
		}
		System.out.println(buffer.toString());
	}
    
    public void trainMoelForTE() throws Exception {
    	KEAModelBuilder km = new KEAModelBuilder();
		
		// A. required arguments (no defaults):
		
		// 1. Name of the directory -- give the path to your directory with documents and keyphrases
		//    documents should be in txt format with an extention "txt"
		//    keyphrases with the same name as documents, but extension "key"
		//    one keyphrase per line!
		km.setDirName("testdocs/en/train");
		
		// 2. Name of the model -- give the path to where the model is to be stored and its name
		km.setModelName("testdocs/model/model.model");
		 
		// 3. Name of the vocabulary -- name of the file (without extension) that is stored in VOCABULARIES
		//    or "none" if no Vocabulary is used (free keyphrase extraction).
		km.setVocabulary("none");
		
		// 4. Format of the vocabulary in 3. Leave empty if vocabulary = "none", use "skos" or "txt" otherwise.
		km.setVocabularyFormat("");
		
//		 B. optional arguments if you want to change the defaults
		// 5. Encoding of the document
		km.setEncoding("UTF-8");
		
		// 6. Language of the document -- use "es" for Spanish, "fr" for French
		//    or other languages as specified in your "skos" vocabulary 
		km.setDocumentLanguage("en"); // es for Spanish, fr for French
		
		// 7. Stemmer -- adjust if you use a different language than English or if you want to alterate results
		// (We have obtained better results for Spanish and French with NoStemmer) 		
		km.setStemmer(new PorterStemmer()); 
		
		// 8. Stopwords -- adjust if you use a different language than English!
		km.setStopwords(new StopwordsEnglish());
		
		// 9. Maximum length of a keyphrase
		km.setMaxPhraseLength(5);
		
		// 10. Minimum length of a keyphrase
		km.setMinPhraseLength(1);
		
		// 11. Minumum occurrence of a phrase in the document -- use 2 for long documents!
		km.setMinNumOccur(2);
	
	//  Optional: turn off the keyphrase frequency feature	
		km.setUseKFrequency(false);
		km.buildModel(km.collectStems());
		km.saveModel();

    }
    
    public Vector<String> detectTE(String aText) throws Exception {
    	Util.doSaveUTF("workingFolderForTE/text.txt", aText);
    	KEAKeyphraseExtractor ke = new KEAKeyphraseExtractor();
		
		// A. required arguments (no defaults):
		
		// 1. Name of the directory -- give the path to your directory with documents
		//    documents should be in txt format with an extention "txt".
		//    Note: keyphrases with the same name as documents, but extension "key"
		//    one keyphrase per line!
		
		ke.setDirName("workingFolderForTE/text.txt");
		
		// 2. Name of the model -- give the path to the model 
		ke.setModelName("testdocs/model/model.model");
		 
		// 3. Name of the vocabulary -- name of the file (without extension) that is stored in VOCABULARIES
		//    or "none" if no Vocabulary is used (free keyphrase extraction).
		ke.setVocabulary("none");
		
		// 4. Format of the vocabulary in 3. Leave empty if vocabulary = "none", use "skos" or "txt" otherwise.
		ke.setVocabularyFormat("");
		
//		 B. optional arguments if you want to change the defaults
		// 5. Encoding of the document
		ke.setEncoding("UTF-8");
		
		// 6. Language of the document -- use "es" for Spanish, "fr" for French
		//    or other languages as specified in your "skos" vocabulary 
		ke.setDocumentLanguage("en"); // es for Spanish, fr for French
		
		// 7. Stemmer -- adjust if you use a different language than English or want to alterate results
		// (We have obtained better results for Spanish and French with NoStemmer)
		ke.setStemmer(new PorterStemmer());
		
		
		// 8. Stopwords
		ke.setStopwords(new StopwordsEnglish());
		
		// 9. Number of Keyphrases to extract
		ke.setNumPhrases(10);
		
		// 10. Set to true, if you want to compute global dictionaries from the test collection
		ke.setBuildGlobal(false);		
		
		ke.loadModel();
		ke.extractKeyphrases(ke.collectStems());
		Vector<String> tes = Util.getFileContentAsVector("workingFolderForTE/text.key");
		return tes;
	}

    public String tagWithNE(String aSentence) throws IOException {
    	if (FINDERS == null) {
            FINDERS = new NameFinder[OpenNLPConstants.NAME_TYPE_PATH.length];
            for (int i = 0; i < OpenNLPConstants.NAME_TYPE_PATH.length; i++) {
              String modelName = OpenNLPConstants.NAME_TYPE_PATH[i];
              FINDERS[i] = new NameFinder(new PooledGISModelReader(new File(modelName)).getModel());
            }    
    	}
    	
        String annotatedSentence = NameFinder.processText(FINDERS, OpenNLPConstants.NAME_TYPES, aSentence);
        if (annotatedSentence.contains("<LOCATION><ORGANIZATION>")) {
        	annotatedSentence = annotatedSentence.replaceAll("</LOCATION></ORGANIZATION>", "</ORGANIZATION></LOCATION>");
        }
        if (annotatedSentence.contains("<LOCATION><PERSON>")) {
        	annotatedSentence = annotatedSentence.replaceAll("</LOCATION></PERSON>", "</PERSON></LOCATION>");
        }
        if (annotatedSentence.contains("<ORGANIZATION><PERSON>")) {
        	annotatedSentence = annotatedSentence.replaceAll("</ORGANIZATION></PERSON>", "</PERSON></ORGANIZATION>");
        }
        String xml = "<root>" + annotatedSentence + "</root>";
        try {
        	StringBuffer buffer = new StringBuffer();
			Element root = Util.getFileContentAsDomElementFromString(xml).getRootElement();
			List list = root.getContent();
			for (int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				if (obj instanceof Text) {
					buffer.append(((Text)obj).getText().trim()).append(" ");
				} else if (obj instanceof Element) {
					List list2 = ((Element)obj).getContent();
					buffer.append("<ENAMEX TYPE=\"").append(((Element)obj).getName()).append("\">");
					for (int j = 0; j < list2.size(); j++) {
						Object obj2 = list2.get(j);
						if (obj2 instanceof Text) {
							buffer.append(((Text)obj2).getText().trim());
						} else if (obj instanceof Element) {
							buffer.append(((Element)obj2).getText().trim());
						}
						if (j < list2.size()-1) {
							buffer.append(" ");														
						}

					}
					buffer.append("</").append("ENAMEX").append(">");				
				}
			}
			annotatedSentence = buffer.toString();
        } catch (JDOMException anException) {
        	return aSentence;
		}
        return annotatedSentence;
    }
    
    public String tagWithNECopy(String aSentence) throws IOException {
    	if (FINDERS == null) {
            FINDERS = new NameFinder[OpenNLPConstants.NAME_TYPE_PATH.length];
            for (int i = 0; i < OpenNLPConstants.NAME_TYPE_PATH.length; i++) {
              String modelName = OpenNLPConstants.NAME_TYPE_PATH[i];
              FINDERS[i] = new NameFinder(new PooledGISModelReader(new File(modelName)).getModel());
            }    
    	}
        String annotatedSentence = NameFinder.processText(FINDERS, OpenNLPConstants.NAME_TYPES, aSentence);
        return annotatedSentence;
    }

    
    public Map<String, String> detectNamedEntities(String aSentence) throws IOException {
    	Map<String, String> map = new HashMap<String, String>();
    	
    	if (FINDERS == null) {
            FINDERS = new NameFinder[OpenNLPConstants.NAME_TYPE_PATH.length];
            for (int i = 0; i < OpenNLPConstants.NAME_TYPE_PATH.length; i++) {
              String modelName = OpenNLPConstants.NAME_TYPE_PATH[i];
              FINDERS[i] = new NameFinder(new PooledGISModelReader(new File(modelName)).getModel());
            }    
    	}
    	
        String annotatedSentence = NameFinder.processText(FINDERS, OpenNLPConstants.NAME_TYPES, aSentence);
        for (int i = 0; i < OpenNLPConstants.NAME_TYPES.length; i++) {
        	StringBuffer buffer = new StringBuffer("<");
        	String featureName = OpenNLPConstants.NAME_TYPES[i];
        	buffer.append(featureName).append(">");
        	String bufferStr = buffer.toString();
        	if (annotatedSentence.contains(bufferStr)) {
        		String toSaves[] = annotatedSentence.split(bufferStr);
        		for (int j = 1; j < toSaves.length; j++) {
            		String toSave = toSaves[j].replaceAll("</.*", "");
            		toSave = toSave.replaceAll("<.*>", "").replaceAll("\\n", "");
            		map.put(toSave, featureName);
				}
        	}
        }
        return map;
    }
    
    public Vector<String> getNgramNE(String aSentence, int aNumberOfTokens, boolean isInSmallLetter) throws IOException {
    	Vector<String> ngrams = new Vector<String>();
    	String []tokens = tokenize(aSentence);
    	outer:
    	for (int i = 0; i < tokens.length - (aNumberOfTokens - 1); i++) {
			StringBuffer ngram = new StringBuffer();
    		for (int j = 0, k=i; j < aNumberOfTokens; j++, k++) {
    			if (tokens[k].trim().toLowerCase().equals(tokens[k].trim())) {
    				continue outer;
    			}
				ngram.append(tokens[k].trim()).append(" ");
			}
    		
    		String ngramString = ngram.toString().trim();
    		if (isInSmallLetter) {
    			ngramString = ngramString.toLowerCase();    			
    		}
    		ngrams.add(ngramString);
		}
    	return ngrams;
    }

    public Vector<String> getNgramNE(Vector<String> aList, int aNumberOfTokens, boolean isInSmallLetter) throws IOException {
    	Vector<String> ngrams = new Vector<String>();
    	for (int i = 0; i < aList.size() - (aNumberOfTokens - 1); i++) {
			StringBuffer ngram = new StringBuffer();
    		for (int j = 0, k=i; j < aNumberOfTokens; j++, k++) {
    			if (!aList.get(k).trim().equals("")) {
    				ngram.append(aList.get(k).trim()).append(" ");    				
    			}
			}
    		
    		String ngramString = ngram.toString().trim();
    		if (ngramString.split("\\s").length < aNumberOfTokens || ngramString.equals("")) {
    			continue;
    		}
    		if (isInSmallLetter) {
    			ngramString = ngramString.toLowerCase();    			
    		}
    		ngrams.add(ngramString);
		}
    	return ngrams;
    }

    public String[] tokenize(String aSentence) throws IOException {
		if (TOKENIZER == null) {
			TOKENIZER = new Tokenizer(OpenNLPConstants.ENGLISH_TOKEN_MAX_ENT_MODEL);				
		}
		if (aSentence == null) {
			return new String[0];
		}
    	return TOKENIZER.tokenize(aSentence.trim());
    }
 
}
