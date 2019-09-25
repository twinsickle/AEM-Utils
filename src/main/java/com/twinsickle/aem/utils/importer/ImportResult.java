package com.twinsickle.aem.utils.importer;

public class ImportResult {
    private boolean success;
    private String id;

    public ImportResult(String id, boolean success){
        this.success = success;
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getId() {
        return id;
    }
}
