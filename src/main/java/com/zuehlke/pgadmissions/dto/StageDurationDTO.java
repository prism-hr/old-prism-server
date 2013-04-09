package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.StageDuration;

public class StageDurationDTO {
	
	private List<StageDuration> stagesDuration = new ArrayList<StageDuration>();

	public List<StageDuration> getStagesDuration() {
		return stagesDuration;
	}

	public void setStagesDuration(List<StageDuration> stagesDuration) {
		for (StageDuration stageDuration : stagesDuration) {
			if(stageDuration != null){
				this.stagesDuration.add(stageDuration);
			}
		}
	}

}
