package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_CLOSING_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_CLOSING_DATE_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_INSTITUTION_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PANEL_DEADLINE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PANEL_DEADLINE_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROGRAM_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROJECT_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.DEADLINE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.advert.ClosingDate;
import com.zuehlke.pgadmissions.domain.advert.PanelDeadline;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

public enum PrismScope {

	SYSTEM(null, System.class, null, 1, "SM", null, null, SYSTEM_SYSTEM, null), //
	INSTITUTION(ORGANIZATION, Institution.class, Lists.newLinkedList(Arrays.asList(SYSTEM)), 2, "IN", new ColumnDefinition().add("institution", "title")
	        .getAll(), null, SYSTEM_INSTITUTION, SYSTEM_INSTITUTION_PLURAL), //
	PROGRAM(OPPORTUNITY, Program.class, Lists.newLinkedList(Arrays.asList(INSTITUTION)), 3, "PM", new ColumnDefinition().add("institution", "title")
	        .add("program", "title").getAll(), null, SYSTEM_PROGRAM, SYSTEM_PROGRAM_PLURAL), //
	PROJECT(OPPORTUNITY, Project.class, Lists.newLinkedList(Arrays.asList(PROGRAM)), 4, "PT", new ColumnDefinition().add("program", "title")
	        .add("project", "title").getAll(), null, SYSTEM_PROJECT,
	        SYSTEM_PROJECT_PLURAL), //
	CLOSING_DATE(DEADLINE, ClosingDate.class, Lists.newLinkedList(Arrays.asList(PROGRAM, PROJECT)), 5, "CD", null, null, SYSTEM_CLOSING_DATE,
	        SYSTEM_CLOSING_DATE_PLURAL), //
	PANEL_DEADLINE(DEADLINE, PanelDeadline.class, Lists.newLinkedList(Arrays.asList(PROGRAM, PROJECT)), 6, "PD", null, null, SYSTEM_PANEL_DEADLINE,
	        SYSTEM_PANEL_DEADLINE_PLURAL), //
	APPLICATION(PrismScopeCategory.APPLICATION, Application.class, Lists.newLinkedList(Arrays.asList(PROGRAM, PROJECT, CLOSING_DATE, PANEL_DEADLINE)), 7, "AN",
	        new ColumnDefinition().add("program", "title").add("project", "title").getAll(), null, SYSTEM_APPLICATION, SYSTEM_APPLICATION_PLURAL);

	private static final HashMap<Class<? extends Resource>, PrismScope> byResourceClass = Maps.newHashMap();

	private static final LinkedHashMultimap<PrismScope, PrismScope> byParentScope = LinkedHashMultimap.create();

	static {
		for (PrismScope scope : values()) {
			byResourceClass.put(scope.getResourceClass(), scope);
			for (PrismScope parentScope : scope.getParentScopes()) {
				byParentScope.put(parentScope, scope);
			}
		}
	}

	private PrismScopeCategory prismScopeCategory;

	private Class<? extends Resource> resourceClass;

	private LinkedList<PrismScope> parentScopes;

	private Integer precedence;

	private String shortCode;

	private HashMultimap<String, String> consoleListCustomColumns;

	private HashMultimap<String, String> reportListCustomColumns;

	private PrismDisplayPropertyDefinition displayPropertyDefinition;

	private PrismDisplayPropertyDefinition displayPropertyDefinitionPlural;

	private PrismScope(PrismScopeCategory prismScopeCategory, Class<? extends Resource> resourceClass, LinkedList<PrismScope> parentScopes, Integer precedence,
	        String shortCode, HashMultimap<String, String> consoleListCustomColumns, HashMultimap<String, String> reportListCustomColumns,
	        PrismDisplayPropertyDefinition displayPropertyDefinition, PrismDisplayPropertyDefinition displayPropertyDefinitionPlural) {
		this.prismScopeCategory = prismScopeCategory;
		this.resourceClass = resourceClass;
		this.parentScopes = parentScopes;
		this.precedence = precedence;
		this.shortCode = shortCode;
		this.consoleListCustomColumns = consoleListCustomColumns;
		this.reportListCustomColumns = reportListCustomColumns;
		this.displayPropertyDefinition = displayPropertyDefinition;
		this.displayPropertyDefinitionPlural = displayPropertyDefinitionPlural;
	}

	public PrismScopeCategory getPrismScopeCategory() {
		return prismScopeCategory;
	}

	public Class<? extends Resource> getResourceClass() {
		return resourceClass;
	}

	public List<PrismScope> getParentScopes() {
		return parentScopes == null ? Lists.<PrismScope> newLinkedList() : parentScopes;
	}

	public Integer getPrecedence() {
		return precedence;
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

	public PrismDisplayPropertyDefinition getDisplayPropertyDefinition() {
		return displayPropertyDefinition;
	}

	public PrismDisplayPropertyDefinition getDisplayPropertyDefinitionPlural() {
		return displayPropertyDefinitionPlural;
	}

	public static PrismScope getByResourceClass(Class<? extends Resource> resourceClass) {
		return byResourceClass.get(resourceClass);
	}

	public Set<PrismScope> getChildScopes() {
		return byParentScope.get(this);
	}
	
	public String getLowerCaseName() {
		return resourceClass.getSimpleName().toLowerCase();
	}

	public static PrismScope getSystemScope() {
		return SYSTEM;
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
