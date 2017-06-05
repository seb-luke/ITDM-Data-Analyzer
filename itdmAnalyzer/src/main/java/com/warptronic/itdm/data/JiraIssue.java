package com.warptronic.itdm.data;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;

import javax.json.JsonObject;

import com.warptronic.itdm.core.ItdmException;
import com.warptronic.itdm.utils.StringUtils;

public class JiraIssue {

	private String key;
	private String parentKey;
	private IssueType issueType;
	private IssueStatus status;
	private ZonedDateTime startDate;
	private ZonedDateTime endDate;
	
	private JiraIssue() {}
	
	public static JiraIssue fromJsonObject(JsonObject jsonIssue) {
		
		JiraIssue issue = new JiraIssue();
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
		        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
		        .appendPattern("X")
		        .toFormatter();
		
		issue.setKey(jsonIssue.getString("key"));
		issue.setParentKey(jsonIssue.getString("parent-key"));
		issue.setIssueType(IssueType.fromName(jsonIssue.getString("issuetype")));
		issue.setStatus(IssueStatus.fromName(jsonIssue.getString("status")));
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
	
	/**
	 * @return either New Feature, Improvement, Patch or Story
	 */
	public IssueType getMajorIssueType() {
		return this.issueType.getMajorIssueType();
	}

	public void setIssueType(IssueType issueType) {
		this.issueType = issueType;
	}

	public IssueStatus getStatus() {
		return status;
	}
	
	/**
	 * @return either Open, In Progress or Closed
	 */
	public IssueStatus getMajorStatus() {
		return this.status.getMajorStatus();
	}

	public void setStatus(IssueStatus status) {
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
	
	public boolean hasEnded() {
		return null != this.endDate;
	}
	
	public Long getDurationSeconds() {
		
		if(this.endDate == null) {
			throw new ItdmException("Cannot compute duration if end date is null");
		}
		
		return startDate.until(endDate, ChronoUnit.SECONDS);
	}
	
	@Override
	public String toString() {
		return this.key;
	}

	
}
