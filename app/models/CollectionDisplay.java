package models;

import java.util.List;

public class CollectionDisplay {
    
	public String id;
	public String title;
	public String editor;
    public boolean completed;
    public boolean followOnWebstore;
    public List<BdDisplay> bdDisplay;
	public List<BdDisplay> getBdDisplay() {
		return bdDisplay;
	}
	public void setBdDisplay(List<BdDisplay> bdDisplay) {
		this.bdDisplay = bdDisplay;
	}
	public boolean isFollowOnWebstore() {
		return followOnWebstore;
	}
	public void setFollowOnWebstore(boolean followOnWebstore) {
		this.followOnWebstore = followOnWebstore;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public boolean getExistingNewBD() {
		boolean result =this.getBdDisplay().stream()
				  .filter(e->e.getIsbn().equals("N/A"))
				  .map(e->true)
				  .findFirst()
				  .orElse(false);
		return result;
		
	}
	
}
