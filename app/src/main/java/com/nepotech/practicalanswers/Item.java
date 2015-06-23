package com.nepotech.practicalanswers;

/**
 * Created by prabir on 6/23/15.
 */
public class Item {
    private String id;
    private String dspaceId;
    private String collectionId;
    private String title;
    private String creator;
    private String publisher;
    private String description;
    private String language;
    private String dateIssued;
    private String type;
    private String bitstreamId;
    private String documentThumbHref;
    private String documentHref;
    private String documentSize;

    public Item (String id, String dspaceId, String collectionId, String title, String creator,
                 String publisher, String description, String language, String dateIssued,
                 String type, String bitstreamId, String documentThumbHref, String documentHref,
                 String documentSize) {
        this.id = dspaceId;
        this.dspaceId = dspaceId;
        this.collectionId = collectionId;
        this.title = title;
        this.creator = creator;
        this.publisher = publisher;
        this.description = description;
        this.language = language;
        this.dateIssued = dateIssued;
        this.type = type;
        this.bitstreamId = bitstreamId;
        this.documentThumbHref = documentThumbHref;
        this.documentHref = documentHref;
        this.documentSize = documentSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDspaceId() {
        return dspaceId;
    }

    public void setDspaceId(String dspaceId) {
        this.dspaceId = dspaceId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBitstreamId() {
        return bitstreamId;
    }

    public void setBitstreamId(String bitstreamId) {
        this.bitstreamId = bitstreamId;
    }

    public String getDocumentThumbHref() {
        return documentThumbHref;
    }

    public void setDocumentThumbHref(String documentThumbHref) {
        this.documentThumbHref = documentThumbHref;
    }

    public String getDocumentHref() {
        return documentHref;
    }

    public void setDocumentHref(String documentHref) {
        this.documentHref = documentHref;
    }

    public String getDocumentSize() {
        return documentSize;
    }

    public void setDocumentSize(String documentSize) {
        this.documentSize = documentSize;
    }
}
