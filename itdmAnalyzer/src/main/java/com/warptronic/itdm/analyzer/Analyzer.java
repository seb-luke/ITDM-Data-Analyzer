package com.warptronic.itdm.analyzer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.warptronic.itdm.core.ItdmException;
import com.warptronic.itdm.data.IssueStatus;
import com.warptronic.itdm.data.IssueType;
import com.warptronic.itdm.data.JiraIssue;

public class Analyzer {
	
	private static final int DISPERION_RESOLUTION = 50;
	
	private final Map<String, JiraIssue> issueMap;
	
	private final Map<JiraIssue, List<JiraIssue>> parentAndChildren;
	private final Map<IssueType, List<JiraIssue>> issueByMajorType;
	private final Map<IssueStatus, List<JiraIssue>> issueByMajorStatus;
	private final Map<IssueStatus, List<JiraIssue>> issueOpenVsReopen;
	
	private final List<ZonedDateTime> startDateDispersionOrdered;
	private final List<ZonedDateTime> endDateDispersionOrdered;
	private final List<Long> durationDispersionOrdered;
	
	private final double avgIssueOpenDays;
	private final Map<IssueType, Double> avgOpenDaysByIssueType;
	
	private final AnalysisWriter writer;
	
	public Analyzer(Case analyzedCase, String fileName) {
		
		analyzedCase.gatherData();
		this.issueMap = analyzedCase.getIssueMap();
		
		this.parentAndChildren = analyzedCase.findParentsForIssues();
		this.issueByMajorType = analyzedCase.getIssuesFilteredByMajorType();
		this.issueByMajorStatus = analyzedCase.getIssuesFilteredByMajorStatus();
		this.issueOpenVsReopen = analyzedCase.getIssuesOpenVsReopen();
		this.startDateDispersionOrdered = analyzedCase.getStartDateDispersion();
		this.endDateDispersionOrdered = analyzedCase.getEndDateDispersion();
		this.durationDispersionOrdered = analyzedCase.getIssueDurationDispersion();
		
		this.avgIssueOpenDays = analyzedCase.getAverageOpenTimeDays();
		this.avgOpenDaysByIssueType = analyzedCase.getAverageOpenDaysByIssueType();
		
		this.writer = new AnalysisWriter(fileName);
	}
	
	public void analyzeAndWrite() {
		
		writer.writeFormatted("Total issues: %d", issueMap.size());
		writer.newLine();
		
		compareMajorIssueTypes();
		compareTasksWithParents();
		avgChildrenPerParent();
		compareTasksByMajorStatus();
		taskOpeningDispersion();
		taskClosingDispersion();
		taskDurationDispersion();
		inProgressPerType();
		statusPerIssueType();
		avgDuration();
		avgDurationPerIssueType();
		openedVsReopened();
		
		try {
			writer.flushToFile();
		} catch (IOException e) {
			throw new ItdmException("Problem when writing to file", e);
		}
	}
	
	private void compareMajorIssueTypes() {
		
		writer.writeHeader("New Features vs. Improvements vs. Patches");
		
		String format = "%s\t%d";
		writer.writeln("IssueType\tCount");
		issueByMajorType.entrySet().forEach(map -> writer.writeFormatted(format, map.getKey(), map.getValue().size()));
		
		writer.newLine();
	}
	
	private void compareTasksWithParents() {
		
		writer.writeHeader("Issues with parents vs Issues without parents");
		
		Map<String, JiraIssue> allTasksHavingParents = parentAndChildren.values().stream()
				.flatMap(List::stream).collect(Collectors.toMap(JiraIssue::getKey, Function.identity()));
		
		String format = "%s\t%s";
		writer.writeln("TaskType\tCount");
		writer.writeFormatted(format, "HavingParents", parentAndChildren.values().stream().mapToInt(List::size).sum());
		writer.writeFormatted(format, "notHavingParents", issueMap.keySet().stream().filter(i -> !allTasksHavingParents.containsKey(i)).count());
		
		writer.newLine();
	}
	
	private void avgChildrenPerParent() {
		
		writer.writeHeader("Average number of children per parent");
		
		writer.writeFormatted("%s\t%.3f", "AvgChildrenPerParent", parentAndChildren.values().stream().mapToDouble(List::size).average().getAsDouble());
		writer.newLine();
	}
	
	private void compareTasksByMajorStatus() {
		
		writer.writeHeader("Open vs. In Progress vs. Closed");
		
		String format = "%s\t%d";
		writer.writeln("IssueStatus\tCount");
		issueByMajorStatus.entrySet().forEach(map -> writer.writeFormatted(format, map.getKey(), map.getValue().size()));
		writer.newLine();
	}
	
	private void taskOpeningDispersion() {
		
		writer.writeHeader("Dispersion of Issue Opening");
		
		String format = "%s\t%d";
		writer.writeln("UntilDate\tCount");
		
		ZonedDateTime first = startDateDispersionOrdered.get(0);
		ZonedDateTime last = startDateDispersionOrdered.get(startDateDispersionOrdered.size() - 1);
		
		long segmentSize = ChronoUnit.SECONDS.between(first, last) / DISPERION_RESOLUTION;
		ZonedDateTime current = first;
		
		List<ZonedDateTime> segments = new ArrayList<>();
		while (current.isBefore(last)) {
			segments.add(current);
			current = current.plusSeconds(segmentSize);
		}
		
		if (segments.get(segments.size() - 1).isBefore(last)) {
			segments.add(last);
		}
		
		segments.forEach(s -> writer.writeFormatted(format, s, startDateDispersionOrdered.stream()
				.filter(d -> {
					int curIndex = segments.indexOf(s);
					if (curIndex > 0) {
						return d.isBefore(s) && d.isAfter(segments.get(curIndex - 1));
					}
					return d.isBefore(s);
				}).count() ));
		writer.newLine();
	}
	
	private void taskClosingDispersion() {
		
		writer.writeHeader("Dispersion of Issue Closing");
		
		String format = "%s\t%d";
		writer.writeln("UntilDate\tCount");
		
		ZonedDateTime first = endDateDispersionOrdered.get(0);
		ZonedDateTime last = endDateDispersionOrdered.get(endDateDispersionOrdered.size() - 1);
		
		long segmentSize = ChronoUnit.SECONDS.between(first, last) / DISPERION_RESOLUTION;
		ZonedDateTime current = first;
		
		List<ZonedDateTime> segments = new ArrayList<>();
		while (current.isBefore(last)) {
			segments.add(current);
			current = current.plusSeconds(segmentSize);
		}
		
		if (segments.get(segments.size() - 1).isBefore(last)) {
			segments.add(last);
		}
		
		segments.forEach(s -> writer.writeFormatted(format, s, endDateDispersionOrdered.stream()
				.filter(d -> {
					int curIndex = segments.indexOf(s);
					if (curIndex > 0) {
						return d.isBefore(s) && d.isAfter(segments.get(curIndex - 1));
					}
					return d.isBefore(s);
				}).count()));
		writer.newLine();
	}
	
	private void taskDurationDispersion() {
		
		writer.writeHeader("Dispersion of Issue Duration");
		
		String format = "%s\t%d";
		writer.writeln("UntilDurationDays\tCount");
		
		long first = durationDispersionOrdered.get(0);
		long last = durationDispersionOrdered.get(durationDispersionOrdered.size() - 1);
		
		Long segmentSize = (last - first) / DISPERION_RESOLUTION;

		List<Long> segments = new ArrayList<>();
		for (long current = first + 1; current <= last; current += segmentSize) {
			segments.add(current);
		}
		
		if (segments.get(segments.size() - 1) < last) {
			segments.add(last);
		}
		
		segments.forEach(s -> writer.writeFormatted(format, s, durationDispersionOrdered.stream()
				.filter(d -> {
					int curIndex = segments.indexOf(s);
					if (curIndex > 0) {
						return d <= s && d > segments.get(curIndex - 1);
					}
					return d <= s;
				}).count()));
		writer.newLine();
	}
	
	private void inProgressPerType() {
		
		writer.writeHeader("Issues in progress for each major Issue type");
		
		String format = "%s\t%d";
		writer.writeln("TaskType\tInProgressCount");
		
		Map<IssueType, List<JiraIssue>> inProgressPerType = issueByMajorStatus.get(IssueStatus.IN_PROGRESS).stream()
				.collect(Collectors.groupingBy(JiraIssue::getMajorIssueType, Collectors.toList()));
		inProgressPerType.entrySet().forEach(map -> writer.writeFormatted(format, map.getKey(), map.getValue().size()));
		writer.newLine();
	}
	
	private void statusPerIssueType() {
		
		writer.writeHeader("Closed vs. In Progress vs. Open for each Issue Type");
		
		String format = "%s\t%d\t%d\t%d";
		writer.writeln("IssueType\tCountOpen\tCountInProgress\tCountClosed");
		issueByMajorType.entrySet().forEach(map -> writer.writeFormatted(
				format, 
				map.getKey(),
				map.getValue().stream().filter(issue -> IssueStatus.OPEN.equals(issue.getMajorStatus())).count(),
				map.getValue().stream().filter(issue -> IssueStatus.IN_PROGRESS.equals(issue.getMajorStatus())).count(),
				map.getValue().stream().filter(issue -> IssueStatus.CLOSED.equals(issue.getMajorStatus())).count()
				));
		writer.newLine();
	}
	
	private void avgDuration() {
		
		writer.writeHeader("Average duration of resolved issues in number of days");
		writer.writeFormatted("AverageDurationDays\t%.3f", avgIssueOpenDays);
		writer.newLine();
	}
	
	private void avgDurationPerIssueType() {
		
		writer.writeHeader("Average duration per Issue Type for resolved issues (days)");
		writer.writeln("IssueType\tAvgDurationDays");
		avgOpenDaysByIssueType.entrySet().forEach(map -> writer.writeFormatted("%s\t%.3f", map.getKey(), map.getValue()));
		writer.newLine();
	}
	
	private void openedVsReopened() {
		
		writer.writeHeader("Open vs Reopened");
		writer.writeln("Status\tCount");
		issueOpenVsReopen.entrySet().forEach(map -> writer.writeFormatted("%s\t%d", map.getKey(), map.getValue().size()));
		writer.newLine();
	}
	

}


































