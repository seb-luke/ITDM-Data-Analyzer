package com.warptronic.itdm.analyzer;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import com.warptronic.itdm.config.CredentialsException;
import com.warptronic.itdm.config.ProgramOptions;
import com.warptronic.itdm.core.ItdmException;
import com.warptronic.itdm.data.IssueStatus;
import com.warptronic.itdm.data.IssueType;
import com.warptronic.itdm.data.JiraIssue;
import com.warptronic.itdm.jira.Request;
import com.warptronic.itdm.utils.JsonUtils;
import com.warptronic.itdm.utils.StringUtils;

public class Case {
	
	private final String url;

	private final String user;
	private static final String USER_PARAM = "-user";
	
	private final String pwd;
	private static final String PASWD_PARAM = "-pwd";
	
	private final String cookie;
	private static final String COOKIE_PARAM = "-cookie";
	
	private final String authtype;
	private static final String AUTHTYPE_PARAM = "-authtype";
	
	private final String projectname;
	private static final String PROJECTNAME_PARAM = "-projectname";
	
	private final ProgramOptions options;
	private Map<String, JiraIssue> issueMap;
	
	public Case(String url, String user, String pwd, String cookie, String authtype, String projectname) {
		
		System.out.println(String.format("\n\tNew Case: project='%s', url='%s'.", projectname, url));
		
		this.url = url;
		this.user = user;
		this.pwd = pwd;
		this.cookie = cookie;
		this.authtype = authtype;
		this.projectname = projectname;
		
		this.options = ProgramOptions.fromArgs(generateArgs());
	}
	
	private String[] generateArgs() {
		
		ArrayList<String>args = new ArrayList<>();
		
		args.add(url);
		
		if (!StringUtils.isNullOrEmpty(user)) {
			args.add(USER_PARAM);
			args.add(user);
		}
		
		if (!StringUtils.isNullOrEmpty(pwd)) {
			args.add(PASWD_PARAM);
			args.add(pwd);
		}
		
		if (!StringUtils.isNullOrEmpty(cookie)) {
			args.add(COOKIE_PARAM);
			args.add(cookie);
		}
		
		if (!StringUtils.isNullOrEmpty(authtype)) {
			args.add(AUTHTYPE_PARAM);
			args.add(authtype);
		}
		
		if (StringUtils.isNullOrEmpty(projectname)) {
			throw new CredentialsException("Project name cannot be null or empty");
		} else {
			args.add(PROJECTNAME_PARAM);
			args.add(projectname);
		}
		
		return args.toArray(new String[0]);
	}
	
	public int gatherData() {
		
		JsonObject jsonData = new Request(options).getFilteredJiraIssues(JsonUtils::jiraMinimalFilter);
		JsonArray jsonIssueArray = jsonData.getJsonArray("issues");
		
		issueMap = new HashMap<>(jsonData.getInt("total") + 1);
		for (JsonValue j : jsonIssueArray) {
			
			if (!ValueType.OBJECT.equals(j.getValueType())) {
				throw new ItdmException("expected JsonValue was Object, but was " + j.getValueType());
			} 
			
			JsonObject json = (JsonObject) j;
			JiraIssue issue = JiraIssue.fromJsonObject(json);
			issueMap.put(issue.getKey(), issue);
		}
		
		return issueMap.size();
	}
	
	public Map<String, JiraIssue> getIssueMap() {
		return this.issueMap;
	}
	
	/**
	 * Links issues by their parents
	 * @return a Map where the key is the parent issue and the value is a list of child issues
	 */
	public Map<JiraIssue, List<JiraIssue>> findParentsForIssues() {
		
		return issueMap.entrySet().stream().map(Map.Entry::getValue)
				.filter(JiraIssue::hasParent)
				.collect(Collectors.groupingBy(t -> issueMap.get(t.getParentKey()), Collectors.toList()));
	}
	
	/**
	 * Combines issues with the same major issue type (New Feature, Improvement, Patch and Story)
	 * @return a Map where the key is the major issue type and the value is a list of issues having that major type
	 */
	public Map<IssueType, List<JiraIssue>> getIssuesFilteredByMajorType() {
		
		return issueMap.entrySet().stream().map(Map.Entry::getValue)
				.collect(Collectors.groupingBy(JiraIssue::getMajorIssueType, Collectors.toList()));
	}
	
	/**
	 * Combines issues with the same major issue status (Open, In Progress and Closed)
	 * @return a Map where the key is the major issue status and the value is a list of issues having that major status
	 */
	public Map<IssueStatus, List<JiraIssue>> getIssuesFilteredByMajorStatus() {
		
		return issueMap.entrySet().stream().map(Map.Entry::getValue)
				.collect(Collectors.groupingBy(JiraIssue::getMajorStatus, Collectors.toList()));
	}
	
	public Map<IssueStatus, List<JiraIssue>> getIssuesOpenVsReopen() {
		
		return issueMap.values().stream()
				.filter(i -> IssueStatus.OPEN.equals(i.getStatus()) || IssueStatus.REOPENED.equals(i.getStatus()))
				.collect(Collectors.groupingBy(JiraIssue::getStatus, Collectors.toList()));
	}
	
	public List<ZonedDateTime> getStartDateDispersion() {
		return issueMap.values().stream()
				.map(JiraIssue::getStartDate)
				.sorted()
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public List<ZonedDateTime> getEndDateDispersion() {
		return issueMap.values().stream()
				.filter(JiraIssue::hasEnded)
				.map(JiraIssue::getEndDate)
				.sorted()
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public double getAverageOpenTimeDays() {
		return issueMap.values().stream()
				.filter(JiraIssue::hasEnded)
				.mapToDouble(i -> i.getStartDate().until(i.getEndDate(), ChronoUnit.DAYS))
				.average()
				.getAsDouble();
	}
	
	public Map<IssueType, Double> getAverageOpenDaysByIssueType() {
		return issueMap.values().stream()
				.filter(JiraIssue::hasEnded)
				.sorted((i, j) -> i.getDurationSeconds().compareTo(j.getDurationSeconds()))
				.collect(Collectors.groupingBy(JiraIssue::getMajorIssueType,
						Collectors.averagingDouble(i -> i.getStartDate().until(i.getEndDate(), ChronoUnit.DAYS))));
	}
	
	public List<Long> getIssueDurationDispersion() {
		return issueMap.values().stream()
				.filter(JiraIssue::hasEnded)
				.map(i -> i.getStartDate().until(i.getEndDate(), ChronoUnit.DAYS))
				.sorted()
				.collect(Collectors.toList());
	}
	
	
	
}





















