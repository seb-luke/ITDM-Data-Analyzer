package com.warptronic.itdm;

import java.util.List;
import java.util.Map;

import com.warptronic.itdm.analyzer.Case;
import com.warptronic.itdm.config.CredentialsException;
import com.warptronic.itdm.data.IssueType;
import com.warptronic.itdm.data.JiraIssue;

public class Main {

	public static void main(String[] args) throws CredentialsException {

		Case hibernateCase = new Case("https://hibernate.atlassian.net/", "", "", "", "", "HHH");

		hibernateCase.gatherData();
		Map<JiraIssue, List<JiraIssue>> parentList = hibernateCase.findParentsForIssues();
		Map<IssueType, List<JiraIssue>> issueByMajorType = hibernateCase.getIssuesFilteredByMajorType();
		
		
		
		
		
		System.out.println("Done");
		
	}

}
