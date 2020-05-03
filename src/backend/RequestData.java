package backend;

public class RequestData {

	private String email;
	private String password;
	private String domain;
	private int user_id;
	private int granted_to;
	private String privilege;
	private String username;
	private int notify_id;

	// Konstruktor do requesta logowania
	public RequestData(String email, String password, String domain) {
		this.email = email;
		this.password = password;
		this.domain = domain;
	}
	public RequestData(String email)
	{
		this.email = email;
	}

	//Konstruktor do requesta tworzenia domeny
	public RequestData(int user_id, String domain)
	{
		this.user_id = user_id;
		this.domain = domain;
	}
	//Konstruktor do requesta tworzenia nowych uzytkownikow
	public RequestData(String email, String password, String domain, int user_id, String privilege, String username)
	{
		this.email = email;
		this.password = password;
		this.domain = domain;
		this.user_id = user_id;
		this.privilege = privilege;
		this.username = username;
	}
	// Konstruktor do requesta aktualizacji uprawnien
	public RequestData(int user_id, int granted_to, String domain, String privilege)
	{
		this.user_id = user_id;
		this.granted_to = granted_to;
		this.domain = domain;
		this.privilege = privilege;
	}
	// Konstruktor do requesta usuwania uprawnien
	public RequestData(int user_id, int granted_to, String domain)
	{
		this.user_id = user_id;
		this.granted_to = granted_to;
		this.domain = domain;
	}
	//Konstruktoe do requesta put notifications
	public RequestData(int user_id, int notify_id)
	{
		this.user_id = user_id;
		this.notify_id = notify_id;
	}


	public String getDomain() {
		return domain;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public int getUser_id(){ return user_id; }
}
