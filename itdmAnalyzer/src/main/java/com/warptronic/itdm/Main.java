package com.warptronic.itdm;

import com.warptronic.itdm.analyzer.Analyzer;
import com.warptronic.itdm.analyzer.Case;
import com.warptronic.itdm.config.CredentialsException;

public class Main {

	public static void main(String[] args) throws CredentialsException {
		
		Case hibernateCase = new Case("https://hibernate.atlassian.net/", "", "", "", "", "HHH");
		new Analyzer(hibernateCase, "Hibernate_HHH.txt").analyzeAndWrite();
		
		Case springCase = new Case("https://jira.spring.io", "", "", "", "", "SPR");
		new Analyzer(springCase, "Spring_SPR.txt").analyzeAndWrite();
		
		
 		System.out.println("\n\n\t\tDone");
	}

}
