package neta.crawler.process.dto.enumelate;

public enum Category {
	FUNNY(1, "面白系"), PROJECT(2, "プロジェクト系");
	
	private final int val;
	private final String name;
	
	private Category(int val, String name){
		this.name = name;
		this.val = val;
	}
	
	public int getValue(){
		return val;
	}
	
	public String getName(){
		return name;
	}
	
	public static Category valueOf(int val){
		for (Category c : Category.values()){
			if (c.getValue() == val){
				return c;
			}
		}
		return null;
	}	
}
