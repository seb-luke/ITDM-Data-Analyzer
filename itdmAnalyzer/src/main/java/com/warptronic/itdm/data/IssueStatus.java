package com.warptronic.itdm.data;

import com.warptronic.itdm.core.ItdmException;
import com.warptronic.itdm.utils.StringUtils;

public enum IssueStatus {
	AWAITING_RESPONSE("Awaiting Response"),
	AWAITING_TEST_CASE("Awaiting Test Case"),
	CLOSED("Closed"),
	IN_PROGRESS("In Progress"),
	INVESTIGATING("Investigating"),
	OPEN("Open"),
	REOPENED("Reopened"),
	RESOLVED("Resolved"),
	WAITING_FOR_FEEDBACK("Waiting for Feedback");
	
	private String name;
	
	private IssueStatus(String name) {
		this.name = name;
	}
	
	public static IssueStatus fromName(String name) {
		
		if (StringUtils.isNullOrEmpty(name)) {
			throw new ItdmException("Provided issue name was empty or null");
		}
		
		for (IssueStatus issue : IssueStatus.values()) {
			if (issue.name.equalsIgnoreCase(name)) {
				return issue;
			}
		}
		
//		throw new ItdmException(String.format("Name '%s' is not recognized as a known issue type.", name));
		System.out.println(String.format("Name '%s' is not recognized as a known issue status.", name));
		return null;
	}
	
	public IssueStatus getMajorStatus() {
		
		switch (this) {
		case OPEN:
		case REOPENED:
			return IssueStatus.OPEN;

		case AWAITING_RESPONSE:
		case AWAITING_TEST_CASE:
		case IN_PROGRESS:
		case INVESTIGATING:
		case WAITING_FOR_FEEDBACK:
			return IssueStatus.IN_PROGRESS;

		case CLOSED:
		case RESOLVED:
			return IssueStatus.CLOSED;

		default:
			throw new ItdmException("Program should be able to get here");
		}
	}
}
