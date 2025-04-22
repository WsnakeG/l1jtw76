package com.lineage.server.model;

import com.lineage.config.ConfigRate;

/**
 * 物品升級機率
 * 
 * @author daien
 */
public class L1ItemUpdata {

	/**
	 * 武器現有強化質大於等於9(非YiWei用)
	 * 
	 * @param enchant_level_tmp 以強化質
	 * @return 升級機率 1/100
	 */
	public static double enchant_wepon_up9x(double enchant_level_tmp) {
		if (enchant_level_tmp <= 0) {
			enchant_level_tmp = 1;
		}
		// (>> 1: 除) (<< 1: 乘)
		return (100 + (enchant_level_tmp * ConfigRate.ENCHANT_CHANCE_WEAPON))
				/ (enchant_level_tmp * (enchant_level_tmp - 7));
	}

	/**
	 * 武器現有強化質大於等於9
	 * 
	 * @param enchant_level_tmp 以強化質
	 * @return 升級機率 1/100
	 */
	public static double enchant_wepon_up9(double enchant_level_tmp) {
		if (enchant_level_tmp <= 0) {
			enchant_level_tmp = 1;
		}
		// (>> 1: 除) (<< 1: 乘)
		return (100 + (enchant_level_tmp * ConfigRate.ENCHANT_CHANCE_WEAPON)) / (enchant_level_tmp * 2);
	}

	/**
	 * 武器現有強化質小於9
	 * 
	 * @param enchant_level_tmp 以強化質
	 * @return 升級機率 1/100
	 */
	public static double enchant_wepon_dn9(double enchant_level_tmp) {
		if (enchant_level_tmp <= 0) {
			enchant_level_tmp = 1;
		}
		// (>> 1: 除) (<< 1: 乘)
		return (100 + (enchant_level_tmp * ConfigRate.ENCHANT_CHANCE_WEAPON)) / enchant_level_tmp;
	}

	/**
	 * 防具現有強化質大於等於9(非YiWei用)
	 * 
	 * @param enchant_level_tmp 以強化質
	 * @return 升級機率 1/100
	 */
	public static double enchant_armor_up9x(double enchant_level_tmp) {
		if (enchant_level_tmp <= 0) {
			enchant_level_tmp = 1;
		}
		// (>> 1: 除) (<< 1: 乘)
		return (100 + (enchant_level_tmp * ConfigRate.ENCHANT_CHANCE_ARMOR))
				/ (enchant_level_tmp * (enchant_level_tmp - 7));
	}

	/**
	 * 防具現有強化質大於等於9
	 * 
	 * @param enchant_level_tmp 以強化質
	 * @return 升級機率 1/100
	 */
	public static double enchant_armor_up9(double enchant_level_tmp) {
		if (enchant_level_tmp <= 0) {
			enchant_level_tmp = 1;
		}
		// (>> 1: 除) (<< 1: 乘)
		return (100 + (enchant_level_tmp * ConfigRate.ENCHANT_CHANCE_ARMOR)) / (enchant_level_tmp * 2);
	}

	/**
	 * 防具現有強化質小於9
	 * 
	 * @param enchant_level_tmp 以強化質
	 * @return 升級機率 1/100
	 */
	public static double enchant_armor_dn9(double enchant_level_tmp) {
		if (enchant_level_tmp <= 0) {
			enchant_level_tmp = 1;
		}
		// (>> 1: 除) (<< 1: 乘)
		return (100 + (enchant_level_tmp * ConfigRate.ENCHANT_CHANCE_ARMOR)) / enchant_level_tmp;
	}

	/**
	 * 物攻強化卷
	 * 
	 * @param enchant_level_tmp 已強化質
	 * @return 升級機率 1/100
	 */
	public static int enchant_Dmg(int enchant_level_tmp) {
		if (enchant_level_tmp <= 0) {
			enchant_level_tmp = 1;
		}
		// (>> 1: 除) (<< 1: 乘)
		return 35 / enchant_level_tmp;
	}

	/**
	 * 命中強化卷
	 * 
	 * @param enchant_level_tmp 已強化質
	 * @return 升級機率 1/100
	 */
	public static int enchant_Hit(int enchant_level_tmp) {
		if (enchant_level_tmp <= 0) {
			enchant_level_tmp = 1;
		}
		// (>> 1: 除) (<< 1: 乘)
		return 35 / enchant_level_tmp;
	}

	/**
	 * 魔攻強化卷
	 * 
	 * @param enchant_level_tmp 已強化質
	 * @return 升級機率 1/100
	 */
	public static int enchant_Sp(int enchant_level_tmp) {
		if (enchant_level_tmp <= 0) {
			enchant_level_tmp = 1;
		}
		// (>> 1: 除) (<< 1: 乘)
		return 35 / enchant_level_tmp;
	}

	/**
	 * 1級強化石
	 * 
	 * @return 提高質(1/1000)
	 */
	public static int x1() {
		return 1;
	}

	/**
	 * 2級強化石
	 * 
	 * @return 提高質(1/1000)
	 */
	public static int x2() {
		return 2;
	}

	/**
	 * 3級強化石
	 * 
	 * @return 提高質(1/1000)
	 */
	public static int x3() {
		return 4;
	}

	/**
	 * 4級強化石
	 * 
	 * @return 提高質(1/1000)
	 */
	public static int x4() {
		return 8;
	}

	/**
	 * 5級強化石
	 * 
	 * @return 提高質(1/1000)
	 */
	public static int x5() {
		return 16;
	}

	/**
	 * 第1階段 幸運符
	 * 
	 * @param r
	 * @return 原始機率 + 原始機率提高0.10
	 */
	public static double w1(final double r) {
		return (r * 0.10);
	}

	/**
	 * 第2階段 幸運符
	 * 
	 * @param r
	 * @return 原始機率 + 原始機率提高0.20
	 */
	public static double w2(final double r) {
		return (r * 0.20);
	}

	/**
	 * 第3階段 幸運符
	 * 
	 * @param r
	 * @return 原始機率 + 原始機率提高0.30
	 */
	public static double w3(final double r) {
		return (r * 0.30);
	}

	/**
	 * 能量石基礎機率
	 * 
	 * @param itemid
	 * @return 基礎機率(1/1000)
	 */
	public static int poewr(final int itemid) {
		int r = 0;
		switch (itemid) {
		case 46510:// 一級能量石 (力量)
		case 46528:// 一級能量石 (力量)(無法轉移)
		case 46513:// 一級能量石 (敏捷)
		case 46531:// 一級能量石 (敏捷)(無法轉移)
		case 46516:// 一級能量石 (智力)
		case 46534:// 一級能量石 (智力)(無法轉移)
			r = 40;
			break;

		case 46519:// 一級能量石 (精神)
		case 46537:// 一級能量石 (精神)(無法轉移)
		case 46522:// 一級能量石 (魅力)
		case 46540:// 一級能量石 (魅力)(無法轉移)
		case 46525:// 一級能量石 (體質)
		case 46543:// 一級能量石 (體質)(無法轉移)
			r = 50;
			break;

		case 46514:// 二級能量石 (敏捷)
		case 46532:// 二級能量石 (敏捷)(無法轉移)
		case 46511:// 二級能量石 (力量)
		case 46529:// 二級能量石 (力量)(無法轉移)
		case 46517:// 二級能量石 (智力)
		case 46535:// 二級能量石 (智力)(無法轉移)
			r = 20;
			break;

		case 46520:// 二級能量石 (精神)
		case 46538:// 二級能量石 (精神)(無法轉移)
		case 46523:// 二級能量石 (魅力)
		case 46541:// 二級能量石 (魅力)(無法轉移)
		case 46526:// 二級能量石 (體質)
		case 46544:// 二級能量石 (體質)(無法轉移)
			r = 25;
			break;

		case 46515:// 三級能量石 (敏捷)
		case 46533:// 三級能量石 (敏捷)(無法轉移)
		case 46512:// 三級能量石 (力量)
		case 46530:// 三級能量石 (力量)(無法轉移)
		case 46518:// 三級能量石 (智力)
		case 46536:// 三級能量石 (智力)(無法轉移)
			r = 5;
			break;

		case 46521:// 三級能量石 (精神)
		case 46539:// 三級能量石 (精神)(無法轉移)
		case 46524:// 三級能量石 (魅力)
		case 46542:// 三級能量石 (魅力)(無法轉移)
		case 46527:// 三級能量石 (體質)
		case 46545:// 三級能量石 (體質)(無法轉移)
			r = 7;
			break;

		case 46546:// 一級能量石 (體力)
		case 46549:// 一級能量石 (體力)(無法轉移)
		case 46552:// 一級能量石 (魔力)
		case 46555:// 一級能量石 (魔力)(無法轉移)
			r = 150;
			break;

		case 46547:// 二級能量石 (體力)
		case 46550:// 二級能量石 (體力)(無法轉移)
		case 46553:// 二級能量石 (魔力)
		case 46556:// 二級能量石 (魔力)(無法轉移)
			r = 75;
			break;

		case 46548:// 三級能量石 (體力)
		case 46551:// 三級能量石 (體力)(無法轉移)
		case 46554:// 三級能量石 (魔力)
		case 46557:// 三級能量石 (魔力)(無法轉移)
			r = 35;
			break;

		case 46558:// 一級能量石 (魔攻)
		case 46561:// 一級能量石 (魔攻)(無法轉移)
			r = 15;
			break;

		case 46559:// 一級能量石 (攻擊力)
		case 46562:// 一級能量石 (攻擊力)(無法轉移)
			r = 15;
			break;

		case 46560:// 一級能量石 (攻擊成功)
		case 46563:// 一級能量石 (攻擊成功)(無法轉移)
			r = 15;
			break;

		case 46564:// 一級能量石 (額外魔法防禦)
		case 46567:// 一級能量石 (額外魔法防禦)(無法轉移)
			r = 15;
			break;

		case 46565:// 一級能量石 (傷害吸收)
		case 46568:// 一級能量石 (傷害吸收)(無法轉移)
			r = 15;
			break;

		case 46566:// 二級能量石 (傷害吸收)
		case 46569:// 二級能量石 (傷害吸收)(無法轉移)
			r = 7;
			break;

		case 46570:// 一級能量石 (寒冰耐性)
		case 46573:// 一級能量石 (石化耐性)
		case 46576:// 一級能量石 (睡眠耐性)
		case 46579:// 一級能量石 (暗黑耐性)
		case 46582:// 一級能量石 (昏迷耐性)
		case 46585:// 一級能量石 (支撐耐性)
		case 46588:// 一級能量石 (寒冰耐性)(無法轉移)
		case 46591:// 一級能量石 (石化耐性)(無法轉移)
		case 46594:// 一級能量石 (睡眠耐性)(無法轉移)
		case 46597:// 一級能量石 (暗黑耐性)(無法轉移)
		case 46600:// 一級能量石 (昏迷耐性)(無法轉移)
		case 46603:// 一級能量石 (支撐耐性)(無法轉移)
			r = 60;
			break;

		case 46571:// 二級能量石 (寒冰耐性)
		case 46574:// 二級能量石 (石化耐性)
		case 46577:// 二級能量石 (睡眠耐性)
		case 46580:// 二級能量石 (暗黑耐性)
		case 46583:// 二級能量石 (昏迷耐性)
		case 46586:// 二級能量石 (支撐耐性)
		case 46589:// 二級能量石 (寒冰耐性)(無法轉移)
		case 46592:// 二級能量石 (石化耐性)(無法轉移)
		case 46595:// 二級能量石 (睡眠耐性)(無法轉移)
		case 46598:// 二級能量石 (暗黑耐性)(無法轉移)
		case 46601:// 二級能量石 (昏迷耐性)(無法轉移)
		case 46604:// 二級能量石 (支撐耐性)(無法轉移)
			r = 30;
			break;

		case 46572:// 三級能量石 (寒冰耐性)
		case 46575:// 三級能量石 (石化耐性)
		case 46578:// 三級能量石 (睡眠耐性)
		case 46581:// 三級能量石 (暗黑耐性)
		case 46584:// 三級能量石 (昏迷耐性)
		case 46587:// 三級能量石 (支撐耐性)
		case 46590:// 三級能量石 (寒冰耐性)(無法轉移)
		case 46593:// 三級能量石 (石化耐性)(無法轉移)
		case 46596:// 三級能量石 (睡眠耐性)(無法轉移)
		case 46599:// 三級能量石 (暗黑耐性)(無法轉移)
		case 46602:// 三級能量石 (昏迷耐性)(無法轉移)
		case 46605:// 三級能量石 (支撐耐性)(無法轉移)
			r = 15;
			break;

		case 46620:// 一級能量石 (石化)
		case 46623:// 一級能量石 (暈眩)
		case 46626:// 一級能量石 (弱能)
		case 46629:// 一級能量石 (震撼)
		case 46632:// 一級能量石 (落雷)
		case 46635:// 一級能量石 (狂擊)
			r = 60;
			break;

		case 46621:// 二級能量石 (石化)
		case 46624:// 二級能量石 (暈眩)
		case 46627:// 二級能量石 (弱能)
		case 46630:// 二級能量石 (震撼)
		case 46633:// 二級能量石 (落雷)
		case 46636:// 二級能量石 (狂擊)
			r = 30;
			break;

		case 46622:// 三級能量石 (石化)
		case 46625:// 三級能量石 (暈眩)
		case 46628:// 三級能量石 (弱能)
		case 46631:// 三級能量石 (震撼)
		case 46634:// 三級能量石 (落雷)
		case 46637:// 三級能量石 (狂擊)
			r = 15;
			break;
		}
		return r;
	}

	/**
	 * 機率提高修正
	 * 
	 * @param enchant_level_tmp
	 * @return
	 */
	public static double upRX(final int itemid) {
		double r = 0;
		switch (itemid) {
		case 46610:// 一級強化石
		case 46615:// 一級強化石(無法轉移)
			r = 0.1;
			break;

		case 46611:// 二級強化石
		case 46616:// 二級強化石(無法轉移)
			r = 0.2;
			break;

		case 46612:// 三級強化石
		case 46617:// 三級強化石(無法轉移)
			r = 0.3;
			break;

		case 46613:// 四級強化石
		case 46618:// 四級強化石(無法轉移)
			r = 0.4;
			break;

		case 46614:// 五級強化石
		case 46619:// 五級強化石(無法轉移)
			r = 0.5;
			break;
		}
		return r;
	}

	/**
	 * 機率提高修正
	 * 
	 * @param enchant_level_tmp
	 * @return
	 */
	public static int upR(final int enchant_level_tmp) {
		int r = 0;
		switch (enchant_level_tmp) {
		case 1:
			r += 70;
			break;
		case 2:
			r += 65;
			break;
		case 3:
			r += 60;
			break;
		case 4:
			r += 55;
			break;
		case 5:
			r += 50;
			break;
		case 6:
			r += 45;
			break;
		case 7:
			r += 40;
			break;
		case 8:
			r += 35;
			break;
		case 9:
			r += 30;
			break;
		case 10:
			r += 25;
			break;
		case 11:
			r += 20;
			break;
		case 12:
			r += 15;
			break;
		case 13:
			r += 10;
			break;
		case 14:
			r += 5;
			break;
		case 15:
			r += 0;
			break;
		}
		return r;
	}
}
