package com.warptronic.itdm.data;

import com.warptronic.itdm.core.ItdmException;
import com.warptronic.itdm.utils.StringUtils;

public enum IssueStatus {
	AWAITING_RESPONSE("Awaiting Response"),
	AWAITING_TEST_CASE("Awaiting Test Case"),
	ASSIGNED("Assigned"),
	BUG_FOUND("Bug Found"),
	CLOSED("Closed"),
	DONE("Done"),
	IN_PROGRESS("In Progress"),
	IN_TESTING("In Testing"),
	INVESTIGATING("Investigating"),
	IMPLEMENTED("Implemented"),
	NEW("New"),
	NEEDS_VERIFICATION("Needs Verification"),
	OPEN("Open"),
	REOPENED("Reopened"),
	RESOLVED("Resolved"),
	TO_BE_VERIFIED("To Be Verified"),
	TO_BE_DEPLOYED("To Be Deployed"),
	TO_BE_TESTED("To Be Tested"),
	TODO("To Do"),
	WAITING_FOR_FEEDBACK("Waiting for Feedback"),
	VERIFIED("Verified"),
	
	OTHER("Other");
	
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
		
		System.out.println(String.format("Name '%s' is not recognized as a known issue status.", name));
		return OTHER;
	}
	
	public IssueStatus getMajorStatus() {
		
		switch (this) {
		case OPEN:
		case REOPENED:
		case NEW:
		case ASSIGNED:
		case TODO:
		case BUG_FOUND:
		case NEEDS_VERIFICATION:
		case VERIFIED:
			return IssueStatus.OPEN;

		case AWAITING_RESPONSE:
		case AWAITING_TEST_CASE:
		case IN_PROGRESS:
		case INVESTIGATING:
		case WAITING_FOR_FEEDBACK:
		case TO_BE_VERIFIED:
		case TO_BE_DEPLOYED:
		case IMPLEMENTED:
		case TO_BE_TESTED:
		case IN_TESTING:
		case OTHER: //usually cases not listed in the enum are new names for "In Progress"
			return IssueStatus.IN_PROGRESS;

		case CLOSED:
		case RESOLVED:
		case DONE:
			return IssueStatus.CLOSED;

		default:
			throw new ItdmException("Program should be able to get here, current status is " + this.toString());
		}
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
