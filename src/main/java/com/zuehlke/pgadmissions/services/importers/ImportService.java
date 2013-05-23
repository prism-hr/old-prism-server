package com.zuehlke.pgadmissions.services.importers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.domain.ImportedObject;
import com.zuehlke.pgadmissions.domain.ProgramInstanceInterface;
import com.zuehlke.pgadmissions.referencedata.adapters.ImportData;

/**
 * This is UCL data import service.
 * User for situations where we pull data from UCL system (PORTICO).
 * Currently this is just a set of dictionaries we share.
 *
 * todo: consider changing name of this class to UclImportService (to achieve consistency with similar UclExportService)
 * todo: consider moving to the parent package
 */
@Service
public class ImportService {

	private static class CodeComparator implements Comparator<CodeObject> {
		@Override
		public int compare(CodeObject o1, CodeObject o2) {
			int compareResult = o1.getStringCode().compareTo(o2.getStringCode());
			if (compareResult == 0) {
				return o1.getName().compareTo(o2.getName());
			} else {
				return compareResult;
			}
		}
	}
	
	private static class ProgramComparator implements Comparator<ProgramInstanceInterface> {
		@Override
		public int compare(ProgramInstanceInterface o1, ProgramInstanceInterface o2) {
			int compareResult = codeComparator.compare(o1, o2);
			if (compareResult == 0) {
				if(o1.getIdentifier() == null) {
					compareResult = -1;
				} else if(o2.getIdentifier() == null) {
					compareResult = 1;
				} else {
					compareResult = o1.getIdentifier().compareTo(o2.getIdentifier());
				}
			}
			if (compareResult == 0) {
				compareResult = o1.getStudyOptionCode().compareTo(o2.getStudyOptionCode());
			}
			if (compareResult == 0) {
				compareResult = o1.getStudyOption().compareTo(o2.getStudyOption());
			}
			if (compareResult == 0) {
				compareResult = o1.getAcademic_year().compareTo(o2.getAcademic_year());
			}
			if (compareResult == 0) {
				compareResult = o1.getApplicationStartDate().compareTo(o2.getApplicationStartDate());
			}
			if (compareResult == 0) {
				compareResult = o1.getApplicationDeadline().compareTo(o2.getApplicationDeadline());
			}
			return compareResult;
		}
	}

	public static final CodeComparator codeComparator = new CodeComparator();
	private static final ProgramComparator programComparator = new ProgramComparator();

	// Requires RandomAccess lists to run fast
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends ImportedObject, U extends ImportData> List<T> merge(List<T> currentData, List<U> importData) {
		List<T> changes = new ArrayList<T>();
		Comparator comparator = null;
		if(!currentData.isEmpty()) {
			comparator = getComparator(currentData.get(0));
			Collections.sort(currentData, comparator);
			Collections.sort(importData, comparator);
		}
		int i = 0, j = 0;
		T currentElement;
		U importElement;
		while (i < currentData.size() && j < importData.size()) {
			currentElement = currentData.get(i);
			importElement = importData.get(j);
			int comparisionResult = comparator.compare(currentElement, importElement);
			if (comparisionResult == 0) {
				if (!currentElement.getEnabled()) {
					currentElement.setEnabled(true);
					changes.add(currentElement);
				}
				i++;
				j++;
			} else if (comparisionResult < 0) {
				disableElement(currentElement, changes);
				i++;
			} else if (comparisionResult > 0) {
				ImportedObject domainObject = importElement.createDomainObject(currentData, changes);
				changes.add((T) domainObject);
				j++;
			}
		}
		while (i < currentData.size()) {
			currentElement = currentData.get(i);
			disableElement(currentElement, changes);
			i++;
		}
		while (j < importData.size()) {
			importElement = importData.get(j);
			ImportedObject domainObject = importElement.createDomainObject(currentData, changes);
			changes.add((T) domainObject);
			j++;
		}
		return changes;
	}

	private <T extends ImportedObject> void disableElement(T currentElement, List<T> changes) {
		if (currentElement.getEnabled()) {
			currentElement.setEnabled(false);
			changes.add(currentElement);
		}
	}
	
	private Comparator<? extends CodeObject> getComparator(CodeObject object) {
		if(object instanceof ProgramInstanceInterface) {
			return programComparator;
		} else {
			return codeComparator;
		}
	}
}
