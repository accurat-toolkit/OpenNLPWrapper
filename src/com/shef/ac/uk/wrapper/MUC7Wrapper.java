package com.shef.ac.uk.wrapper;

import java.io.IOException;
import java.util.Vector;

import com.shef.ac.uk.util.TextProcessingUtilEnglish;
import com.shef.ac.uk.util.Util;


public class MUC7Wrapper {
	private TextProcessingUtilEnglish util = new TextProcessingUtilEnglish();
	
	public String wrapOpenNLPToDUC7(String anEnglishText) throws IOException {
    	String englishSentences[] = util.detectSentences(anEnglishText);
    	StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < englishSentences.length; i++) {
			String englishSentence = englishSentences[i];			
			String neSentence = util.tagWithNE(englishSentence);
			
//			if (neSentence.contains("<LOCATION>")) {
//				neSentence = neSentence.replaceAll("<LOCATION>", "<ENAMEX TYPE=\"LOCATION\">");
//				neSentence = neSentence.replaceAll("</LOCATION>", "</ENAMEX>");
//			}
//			if (neSentence.contains("<PERSON>")) {
//				neSentence = neSentence.replaceAll("<PERSON>", "<ENAMEX TYPE=\"PERSON\">");
//				neSentence = neSentence.replaceAll("</PERSON>", "</ENAMEX>");
//			}
//			if (neSentence.contains("<ORGANIZATION>")) {
//				neSentence = neSentence.replaceAll("<ORGANIZATION>", "<ENAMEX TYPE=\"ORGANIZATION\">");
//				neSentence = neSentence.replaceAll("</ORGANIZATION>", "</ENAMEX>");
//			}
			buffer.append(neSentence).append("\n");
			
		}
		return buffer.toString();

	}
	


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		MUC7Wrapper wrapper = new MUC7Wrapper();
		if (args.length < 1) {
			System.out.println("Please provide as input the file from which to read the file information");
			return;
		}
        String fileList = args[0].trim();
        Vector<String> lines = Util.getFileContentAsVector(fileList);
        
        for (int i = 0; i < lines.size(); i++) {
        	String values[] = lines.get(i).split("\t");
        	if (values.length == 2) {
                String englishContent = Util.getFileContentAsBuffer(values[0]).toString();
                String taggedEnglishContent = wrapper.wrapOpenNLPToDUC7(englishContent);
                System.out.println("Saving file "+values[1]);
                Util.doSaveUTF(values[1],taggedEnglishContent);        		
        	} else {
        		System.out.println("Error in the input file: Please make sure that the file contains path to the file to parse \t path to the file where there rsults shoule be saved");
        		break;
        	}
		}

	}

}
