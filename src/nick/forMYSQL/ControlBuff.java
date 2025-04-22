package nick.forMYSQL;

public class ControlBuff
{
  private int _id;
  private String _data;
  private String _message;

  public ControlBuff(int id, String message)
  {
    _id = id;
    _message = message;
  }
  
  public ControlBuff(String data, String message)
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