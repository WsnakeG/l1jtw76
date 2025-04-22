package nick.forMYSQL;

public class ControlTeleport
{
  private int _id;
  private String _data;
  private String _message;

  public ControlTeleport(int id, String message)
  {
    _id = id;
    _message = message;
  }
  
  public ControlTeleport(String data, String message)
  {
    _data = data;
    _message = message;
  }

  /**
   * 傳回ID
   * @return
   */
  public int getId() {
    return _id;
  }
  
  /**
	 * 傳回 data String
	 * 
	 * @return
	 */
	public String getData() {
		return _data;
	}

  /**
   * 傳回設定文字
   * @return
   */
  public String getMessage() {
    return _message;
  }
}