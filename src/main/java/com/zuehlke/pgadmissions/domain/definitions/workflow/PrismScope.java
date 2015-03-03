package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.APPLICATION_CATEGORY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY_CATEGORY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION_CATEGORY;

import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

public enum PrismScope {

	SYSTEM(null, System.class, "SM", null, null), //
	INSTITUTION(ORGANIZATION_CATEGORY, Institution.class, "IN", new ColumnDefinition().add("institution", "title").getAll(), null), //
	PROGRAM(OPPORTUNITY_CATEGORY, Program.class, "PM", new ColumnDefinition().add("institution", "title").add("program", "title").getAll(), null), //
	PROJECT(OPPORTUNITY_CATEGORY, Project.class, "PT", new ColumnDefinition().add("program", "title").add("project", "title").getAll(), null), //
//	CLOSING_DATE(DEADLINE_CATEGORY, ClosingDate.class, "CD", null, null), //
//	PANEL_DEADLINE(DEADLINE_CATEGORY, PanelDeadline.class, "PD", null, null), //
	APPLICATION(APPLICATION_CATEGORY, Application.class, "AN", new ColumnDefinition().add("program", "title").add("project", "title").getAll(), null);

	private static final Map<Class<? extends Resource>, PrismScope> byResourceClass = Maps.newHashMap();

	static {
		for (PrismScope scope : values()) {
			byResourceClass.put(scope.getResourceClass(), scope);
		}
	}

	private PrismScopeCategory prismScopeCategory;

	private Class<? extends Resource> resourceClass;

	private String shortCode;

	private HashMultimap<String, String> consoleListCustomColumns;

	private HashMultimap<String, String> reportListCustomColumns;

	private PrismScope(PrismScopeCategory prismScopeCategory, Class<? extends Resource> resourceClass, String shortCode,
	        HashMultimap<String, String> consoleListCustomColumns, HashMultimap<String, String> reportListCustomColumns) {
		this.prismScopeCategory = prismScopeCategory;
		this.resourceClass = resourceClass;
		this.shortCode = shortCode;
		this.consoleListCustomColumns = consoleListCustomColumns;
		this.reportListCustomColumns = reportListCustomColumns;
	}

	public PrismScopeCategory getPrismScopeCategory() {
		return prismScopeCategory;
	}

	public Class<? extends Resource> getResourceClass() {
		return resourceClass;
	}

	public String getShortCode() {
		return shortCode;
	}

	public final HashMultimap<String, String> getConsoleListCustomColumns() {
		return consoleListCustomColumns;
	}

	public final HashMultimap<String, String> getReportListCustomColumns() {
		return reportListCustomColumns;
	}

	public static PrismScope getByResourceClass(Class<? extends Resource> resourceClass) {
		return byResourceClass.get(resourceClass);
	}

	public String getLowerCamelName() {
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
	}

	public String getUpperCamelName() {
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
	}

	public PrismDisplayPropertyDefinition getDisplayPropertyDefinition() {
		return PrismDisplayPropertyDefinition.valueOf("SYSTEM_" + name());
	}

	public PrismDisplayPropertyDefinition getDisplayPropertyDefinitionPlural() {
		return PrismDisplayPropertyDefinition.valueOf("SYSTEM_" + name() + "_PLURAL");
	}

	private static class ColumnDefinition {

		private final HashMultimap<String, String> definitions = HashMultimap.create();

		public ColumnDefinition add(String table, String column) {
			definitions.put(table, column);
			return this;
		}

		public HashMultimap<String, String> getAll() {
			return definitions;
		}

	}

}
