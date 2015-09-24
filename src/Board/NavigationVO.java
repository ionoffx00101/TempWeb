package Board;

public class NavigationVO {
	
	private int currPage;
	private int totalPage;
	private int[] links;
	private boolean leftMore;
	private boolean rightMore;

	public int getCurrPage() {
		return currPage;
	}
	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}
	public int[] getLinks() {
		return links;
	}
	public void setLinks(int[] links) {
		this.links = links;
	}
	public boolean isLeftMore() {
		return leftMore;
	}
	public void setLeftMore(boolean leftMore) {
		this.leftMore = leftMore;
	}
	public boolean isRightMore() {
		return rightMore;
	}
	public void setRightMore(boolean rightMore) {
		this.rightMore = rightMore;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
}
