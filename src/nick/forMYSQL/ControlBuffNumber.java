package nick.forMYSQL;

public class ControlBuffNumber {

	// 20240414
	// 伺服器使用編號
	// 20240520 update
	// 20240521 update
	// 20240522 update
	// 20240523 update
	// 20240608 update
	// 20240701 update
	
	// final String[] aaa = N1ProfessionConfigNumber._32.split(",");
	// Integer.valueOf(aaa[0])
	// .hasSkillEffect(
	// String 轉 boolean 方式
	// final String[] NewCharLocTrueXYM = N1SeverConfigNumber.NewCharLoc.split(",");
	// boolean NewCharLoc = Boolean.parseBoolean(NewCharLocTrueXYM[0]);
	//	final String[] makeItemCreateChance = N1SeverConfigNumber.makeItemCreateChance.split(",");
	//	Integer.valueOf(makeItemCreateChance[0])
	
	public static String addBuffForSelf = String.valueOf(ControlBuffTable.get().getTemplate2("addBuffForSelf").getMessage());
	public static String SelfConsumeItems = String.valueOf(ControlBuffTable.get().getTemplate2("SelfConsumeItems").getMessage());
	public static String addBuffForParty = String.valueOf(ControlBuffTable.get().getTemplate2("addBuffForParty").getMessage());
	public static String PartyConsumeItems = String.valueOf(ControlBuffTable.get().getTemplate2("PartyConsumeItems").getMessage());
	public static String addBuffForClan = String.valueOf(ControlBuffTable.get().getTemplate2("addBuffForClan").getMessage());
	public static String ClanConsumeItems = String.valueOf(ControlBuffTable.get().getTemplate2("ClanConsumeItems").getMessage());
	public static String addBuffForAll = String.valueOf(ControlBuffTable.get().getTemplate2("addBuffForAll").getMessage());
	public static String AllConsumeItems = String.valueOf(ControlBuffTable.get().getTemplate2("AllConsumeItems").getMessage());
}
