package com.ra4king.gameservers.jwords.server.words;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import com.ra4king.gameservers.jwords.server.JWordsServer;

public class Words {
	private ArrayList<String> words;
	
	public Words() throws Exception {
		words = new ArrayList<String>();
		
		Scanner scanner = new Scanner(new File(JWordsServer.codeBase + "dict.txt"));
		while(scanner.hasNextLine())
			words.add(scanner.nextLine().trim());
		scanner.close();
	}
	
	public boolean verify(String word) {
		for(String s : words)
			if(s.equals(word))
				return true;
		
		return false;
	}
}