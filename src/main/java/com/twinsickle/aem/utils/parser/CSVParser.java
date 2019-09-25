package com.twinsickle.aem.utils.parser;

import com.twinsickle.aem.utils.importer.ImportResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface CSVParser {

    ImportResult processLine(CSVRecord record);

    default boolean reportFailures(ImportResult item){
        return !item.isSuccess();
    }

    default String getId(ImportResult item) {
        return item.getId();
    }

    default List<String> parse(BufferedReader reader) throws IOException {
        return parse(reader, 1);
    }

    default List<String> parse(BufferedReader reader, long skip) throws IOException {
        return StreamSupport.stream(CSVFormat.RFC4180.parse(reader).spliterator(), false)
                .skip(skip)
                .map(this::processLine)
                .filter(this::reportFailures)
                .map(this::getId)
                .collect(Collectors.toList());
    }
}
