package com.nepotech.practicalanswers;

public class RowItem {
    
	private String imageId;
    private String title;
    private String desc;
    private String alias;
     
    public RowItem(String imageId, String title, String desc, String alias) {
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.alias = alias;
    }
    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }   
    /**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}
	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
}