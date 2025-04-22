package nick.forMYSQL;



public class N1AutoMaticConfig
{
  private int _id;
  private String _data;
  private String _message;

  public N1AutoMaticConfig(int id, String message)
  {
	// 20240422
    _id = id;
    _message = message;
  }
  
  public N1AutoMaticConfig(String data, String message)
  {
	// 20240524
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
  

//    public static String ShowMessage(final int id) {
//        final L1SystemMessage System_Message = SystemMessageTable.getInstance().getTemplate(id);
//        if (System_Message == null) {
//            return "";
//        }
//        final String Message = System_Message.getMessage();
//        return Message;
//    }
    
}