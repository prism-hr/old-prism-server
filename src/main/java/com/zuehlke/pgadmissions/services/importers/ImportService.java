package com.zuehlke.pgadmissions.services.importers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.xerces.impl.xpath.regex.REUtil;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.domain.ImportedObject;
import com.zuehlke.pgadmissions.referencedata.adapters.ImportData;

@Service
public class ImportService {

	private static class ImportedObjectComparator implements Comparator<ImportedObject> {
		@Override
		public int compare(ImportedObject o1, ImportedObject o2) {
			int compareResult = o1.getCode().compareTo(o2.getCode());
			if (compareResult == 0) {
				if (o1.getEnabled())
					return -1;
				else if (o2.getEnabled())
					return 1;
				else
					return 0;
			} else {
				return compareResult;
			}
		}

	}

	private static class CodeComparator implements Comparator<CodeObject> {

		@Override
		public int compare(CodeObject o1, CodeObject o2) {
			return o1.getCode().compareTo(o2.getCode());
		}

	}

	private final ImportedObjectComparator importedObjectComparator = new ImportedObjectComparator();
	private final CodeComparator codeComparator = new CodeComparator();

	// Requires RandomAccess lists to run fast
	@SuppressWarnings("unchecked")
	public <T extends ImportedObject, U extends ImportData> List<T> merge(List<T> currentData, List<U> importData) {
		List<T> changes = new ArrayList<T>();
		Collections.sort(currentData, importedObjectComparator);
		Collections.sort(importData, codeComparator);
		int i = 0, j = 0;
		T currentElement;
		U importElement;
		while (i < currentData.size() && j < importData.size()) {
			currentElement = currentData.get(i);
			importElement = importData.get(j);
			int comparisionResult = codeComparator.compare(currentElement, importElement);
			if (comparisionResult == 0) {
				if (!importElement.equalAttributes(currentElement)) {
					disableElement(currentElement, changes);
					ImportedObject domainObject = importElement.createDomainObject();
					changes.add((T) domainObject);
				} else if (!currentElement.getEnabled()) {
					currentElement.setEnabled(true);
					changes.add(currentElement);
				}
				do {
					i++;
				} while (i < currentData.size() && currentData.get(i).getCode().equals(currentElement.getCode()));
				j++;
			} else if (comparisionResult < 0) {
				disableElement(currentElement, changes);
				i++;
			} else if (comparisionResult > 0) {
				ImportedObject domainObject = importElement.createDomainObject();
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
			ImportedObject domainObject = importElement.createDomainObject();
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

}
