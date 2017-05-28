package models;

import java.util.List;

public class CollectionDisplay {
    
	public String title;
	public String editor;
    public boolean completed;
    public List<BdDisplay> bdDisplay;
	public List<BdDisplay> getBdDisplay() {
		return bdDisplay;
	}
	public void setBdDisplay(List<BdDisplay> bdDisplay) {
		this.bdDisplay = bdDisplay;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
