package com.daisybell.myapp.theory;

public class UploadPDF {

    public String name;
    public String url;
    public String namePDFStorage;

    public UploadPDF() {
    }

    public UploadPDF(String name, String url, String namePDFStorage) {
        this.name = name;
        this.url = url;
        this.namePDFStorage = namePDFStorage;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getNamePDFStorage() {
        return namePDFStorage;
    }
}
