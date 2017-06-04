package com.warptronic.itdm.data;

import com.warptronic.itdm.core.ItdmException;
import com.warptronic.itdm.utils.StringUtils;

public enum IssueType {
	
	BACKPORT("Backport"),
	BUG("Bug"),
	DEPRECATION("Deprecation"),
	EPIC("Epic"),
	IMPROVEMENT("Improvement"),
	NEW_FEATURE("New Feature"),
	PATCH("Patch"),
	PRUNING("Pruning"),
	REFACTORING("Refactoring"),
	REMOVE_FEATURE("Remove Feature"),
	STORY("Story"),
	SUBTASK("Sub-task"),
	TEHNICAL_TASK("Technical task"),
	TASK("Task");
	
	private String name;
	
	private IssueType(String name) {
		this.name = name;
	}
	
	public static IssueType fromName(String name) {
		
		if (StringUtils.isNullOrEmpty(name)) {
			throw new ItdmException("Provided issue name was empty or null");
		}
		
		for (IssueType issue : IssueType.values()) {
			if (issue.name.equalsIgnoreCase(name)) {
				return issue;
			}
		}
		
//		throw new ItdmException(String.format("Name '%s' is not recognized as a known issue type.", name));
		System.out.println(String.format("Name '%s' is not recognized as a known issue type.", name));
		return null;
	}

}
