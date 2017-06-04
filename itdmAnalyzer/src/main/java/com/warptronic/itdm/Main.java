package com.warptronic.itdm;

import com.warptronic.itdm.analyzer.Case;
import com.warptronic.itdm.config.CredentialsException;

public class Main {

	public static void main(String[] args) throws CredentialsException {

		Case hibernateCase = new Case("https://hibernate.atlassian.net/", "", "", "", "", "HHH");

		hibernateCase.gatherData();
		hibernateCase.findParentsForIssues();
		
		
	}

}
