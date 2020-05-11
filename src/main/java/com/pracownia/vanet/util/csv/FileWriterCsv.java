package com.pracownia.vanet.util.csv;

import com.opencsv.CSVWriter;
import com.pracownia.vanet.exception.FileOperationException;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;

public class FileWriterCsv {

    /*------------------------ FIELDS REGION ------------------------*/
    public static final String CSV = ".csv";

    /*------------------------ METHODS REGION ------------------------*/
    public void writeCsvFile(String filename) throws FileOperationException {
//        try (FileWriter fileWriter = new FileWriter(generateFilename(filename, CSV));
//             CSVWriter csvWriter=new) {
//
//        } catch (IOException e) {
//            throw new FileOperationException(e);
//        }
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
    