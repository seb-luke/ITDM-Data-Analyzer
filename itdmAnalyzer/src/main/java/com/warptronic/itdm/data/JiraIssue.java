package com.warptronic.itdm.data;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import javax.json.JsonObject;

import com.warptronic.itdm.utils.StringUtils;

public class JiraIssue {

	private String key;
	private String parentKey;
	private IssueType issueType;
	private String status;
	private ZonedDateTime startDate;
	private ZonedDateTime endDate;
	
	private JiraIssue() {}
	
	public static JiraIssue fromJsonObject(JsonObject jsonIssue) {
		
		JiraIssue issue = new JiraIssue();
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
		        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
		        .appendPattern("x")
		        .toFormatter();
		
		issue.setKey(jsonIssue.getString("key"));
		issue.setParentKey(jsonIssue.getString("parent-key"));
		issue.setIssueType(IssueType.fromName(jsonIssue.getString("issuetype")));
		issue.setStatus(jsonIssue.getString("status"));
		issue.setStartDate(ZonedDateTime.parse(jsonIssue.getString("start-date"), formatter));
		
		String endDate = jsonIssue.getString("end-date");
		if (StringUtils.isNullOrEmpty(endDate)) {
			issue.setEndDate(null);
		} else {
			issue.setEndDate(ZonedDateTime.parse(endDate, formatter));
		}
		
		return issue;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getParentKey() {
		return parentKey;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	public IssueType getIssueType() {
		return issueType;
	}

	public void setIssueType(IssueType issueType) {
		this.issueType = issueType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public ZonedDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(ZonedDateTime endDate) {
		this.endDate = endDate;
	}
	
	public boolean hasParent() {
		return !StringUtils.isNullOrEmpty(this.parentKey);
	}
	
	@Override
	public String toString() {
		return this.key;
	}

	
}
