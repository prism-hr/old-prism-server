package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import au.com.bytecode.opencsv.CSVReader;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Charsets;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.indices.ImportedSubjectAreaIndex;
import com.zuehlke.pgadmissions.utils.PrismQueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class HesaDataImporter {

    private static final Logger log = LoggerFactory.getLogger(HesaDataImporter.class);

    private static final String[] columns = new String[]{"imported_institution_id", "imported_subject_area_id",
            "tariff_bands_1_79", "tariff_bands_80_119", "tariff_bands_120_179", "tariff_bands_180_239",
            "tariff_bands_240_299", "tariff_bands_300_359", "tariff_bands_360_419", "tariff_bands_420_479",
            "tariff_bands_480_539", "tariff_bands_540_over", "tariff_bands_unknown", "tariff_bands_not_applicable",
            "honours_first_class", "honours_upper_second_class", "honours_lower_second_class", "honours_third_class",
            "honours_unclassified", "honours_classification_not_applicable", "honours_not_applicable",
            "study_full_time", "study_part_time", "course_count", "fpe"};

    @Inject
    private DocumentService documentService;

    @Inject
    private EntityDAO entityDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedSubjectAreaIndex importedSubjectAreaIndex;

    @Transactional
    public void importHesaData() {
        StringBuilder insertValues = new StringBuilder();
        String columnsString = Stream.of(columns).collect(Collectors.joining(", "));

        S3Object hesaDataObject = documentService.getAmazonObject("prism-import-data", "hesa_raw.csv");
        try (CSVReader hesaDataReader = new CSVReader(new InputStreamReader(hesaDataObject.getObjectContent(), Charsets.UTF_8), ';')) {
            hesaDataReader.readNext();
            hesaDataReader.readNext();
            String[] line = hesaDataReader.readNext();
            String currentInstitutionId = null;
            while (line != null) {
                if (!line[0].isEmpty()) {
                    Integer hesaId = Integer.parseInt(line[0].split(" ", 2)[0]);
                    ImportedInstitution importedInstitution = entityService.getByProperty(ImportedInstitution.class, "hesaId", hesaId);
                    currentInstitutionId = importedInstitution.getId().toString();
                }
                line[0] = currentInstitutionId;
                String jacsCode = line[1].substring(1, 5);
                if (!jacsCode.equals("Y000")) {
                    line[1] = importedSubjectAreaIndex.getByJacsCode(jacsCode).getId().toString();
                    insertValues.append(Stream.of(line).collect(Collectors.joining(", ", "(", "),\n")));
                }
                line = hesaDataReader.readNext();
            }
            insertValues.delete(insertValues.length() - 2, insertValues.length());

            entityDAO.executeBulkInsertUpdate("imported_institution_subject_area", columnsString, insertValues.toString(),
                    PrismQueryUtils.generateOnDuplicateUpdateClause(columns));
        } catch (IOException | AmazonClientException e) {
            log.error("Could not import HESA data", e);
        }
    }

}
