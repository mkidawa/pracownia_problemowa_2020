package com.pracownia.vanet.util.csv;

import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.pracownia.vanet.exception.FileOperationException;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;

public class FileWriterCsv {

    /*------------------------ FIELDS REGION ------------------------*/
    public static final String CSV = ".csv";

    /*------------------------ METHODS REGION ------------------------*/
    public void writeCsvFile(String filename, CsvRecord csvRecord)
            throws FileOperationException {
        try (FileWriter fileWriter = new FileWriter(generateFilename(filename, CSV));
             ICSVWriter csvWriter = new CSVWriterBuilder(fileWriter)
                     .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                     .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                     .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                     .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                     .build()) {
            csvWriter.writeNext(csvRecord.getWholeHeader());
            csvWriter.writeNext(csvRecord.toStringArray());
        } catch (IOException e) {
            throw new FileOperationException(e);
        }
    }

    private String generateFilename(String name, String fileExtension) {
        return new StringBuilder()
                .append(name)
                .append("_")
                .append(LocalTime.now().getHour())
                .append(LocalTime.now().getMinute())
                .append(LocalTime.now().getSecond())
                .append(fileExtension)
                .toString();
    }
}
    