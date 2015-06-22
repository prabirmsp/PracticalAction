package com.nepotech.practicalanswers;

import org.json.JSONObject;

import java.util.ArrayList;

public class Community {
    private String id;
    private String dspace_id;
    private String parent_id;
    private String rgt;
    private String lft;
    private String level;
    private String title;
    private String description;
    private String alias;
    private String imageurl;
    protected ArrayList<Community> children;

    public Community () {
        this.children = new ArrayList<>();

    }

    public Community (String id, String dspace_id, String parent_id, String rgt, String lft,
                      String level, String title, String description, String alias, String imageurl) {
        this.id = id;
        this.dspace_id = dspace_id;
        this.parent_id = parent_id;
        this.rgt = rgt;
        this.lft = lft;
        this.level = level;
        this.title = title;
        this.description = description;
        this.alias = alias;
        this.imageurl = imageurl;
        this.children = new ArrayList<>();
    }

    public void addChild (Community child) {
        children.add(child);
    }

    public int getChildrenNumber () {
        return children.size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDspace_id() {
        return dspace_id;
    }

    public void setDspace_id(String dspace_id) {
        this.dspace_id = dspace_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getRgt() {
        return rgt;
    }

    public void setRgt(String rgt) {
        this.rgt = rgt;
    }

    public String getLft() {
        return lft;
    }

    public void setLft(String lft) {
        this.lft = lft;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


}
