package com.nepotech.practicalanswers.community;

import org.json.JSONException;
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

    public Community() {

    }

    public Community(String id, String dspace_id, String parent_id, String rgt, String lft,
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
    }

    public Community(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        id = jsonObject.getString("id");
        dspace_id = jsonObject.getString("dspace_id");
        parent_id = jsonObject.getString("parent_id");
        rgt = jsonObject.getString("rgt");
        lft = jsonObject.getString("lft");
        level = jsonObject.getString("level");
        title = jsonObject.getString("title");
        description = jsonObject.getString("description");
        alias = jsonObject.getString("alias");
        imageurl = jsonObject.getString("imageurl");

    }

    public String toJSON () throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("dspace_id", dspace_id);
        jsonObject.put("parent_id", parent_id);
        jsonObject.put("rgt", rgt);
        jsonObject.put("lft", lft);
        jsonObject.put("level", level);
        jsonObject.put("title", title);
        jsonObject.put("description", description);
        jsonObject.put("alias", alias);
        jsonObject.put("imageurl", imageurl);
        return jsonObject.toString();
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
