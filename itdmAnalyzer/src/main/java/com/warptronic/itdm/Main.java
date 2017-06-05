package com.warptronic.itdm;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import com.warptronic.itdm.analyzer.Case;
import com.warptronic.itdm.config.CredentialsException;
import com.warptronic.itdm.data.IssueStatus;
import com.warptronic.itdm.data.IssueType;
import com.warptronic.itdm.data.JiraIssue;

public class Main {

	public static void main(String[] args) throws CredentialsException {

//		Case hibernateCase = new Case("https://hibernate.atlassian.net/", "", "", "", "", "HHH");
		Case hibernateCase = new Case("https://jira.spring.io", "", "", "", "", "SPR");

		hibernateCase.gatherData();
		Map<JiraIssue, List<JiraIssue>> parentList = hibernateCase.findParentsForIssues();
		Map<IssueType, List<JiraIssue>> issueByMajorType = hibernateCase.getIssuesFilteredByMajorType();
		Map<IssueStatus, List<JiraIssue>> issueByMajorStatus = hibernateCase.getIssuesFilteredByMajorStatus();
		Map<IssueStatus, List<JiraIssue>> issueOpenVsReopen = hibernateCase.getIssuesOpenVsReopen();
		List<ZonedDateTime> startDateDispersion = hibernateCase.getStartDateDispersion();
		List<ZonedDateTime> endDateDispersion = hibernateCase.getEndDateDispersion();
		List<Long> durationDispersion = hibernateCase.getIssueDurationDispersion();
		
		double avgIssueOpenDays = hibernateCase.getAverageOpenTimeDays();
		Map<IssueType, Double> avgOpenDaysByIssueType = hibernateCase.getAverageOpenDaysByIssueType();
		
		
 		System.out.println("Done");
		
	}

}
