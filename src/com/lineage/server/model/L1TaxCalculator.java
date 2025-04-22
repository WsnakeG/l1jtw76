package com.lineage.server.model;

public class L1TaxCalculator {

	/**
	 * 戦争税は15%固定
	 */
	private static final int WAR_TAX_RATES = 15;

	/**
	 * 国税は10%固定（地域税に対する割合）
	 */
	private static final int NATIONAL_TAX_RATES = 10;

	/**
	 * ディアド税は10%固定（戦争税に対する割合）
	 */
	private static final int DIAD_TAX_RATES = 10;

	private final int _taxRatesCastle;
	private final int _taxRatesTown;
	private final int _taxRatesWar = WAR_TAX_RATES;

	/**
	 * @param merchantNpcId 計算対象商店のNPCID
	 */
	public L1TaxCalculator(final int merchantNpcId) {
		_taxRatesCastle = L1CastleLocation.getCastleTaxRateByNpcId(merchantNpcId);
		_taxRatesTown = L1TownLocation.getTownTaxRateByNpcid(merchantNpcId);
	}

	public int calcTotalTaxPrice(final int price) {
		final int taxCastle = price * _taxRatesCastle;
		final int taxTown = price * _taxRatesTown;
		final int taxWar = price * WAR_TAX_RATES;
		return (taxCastle + taxTown + taxWar) / 100;
	}

	// XXX 個別に計算する為、丸め誤差が出る。
	public int calcCastleTaxPrice(final int price) {
		return ((price * _taxRatesCastle) / 100) - calcNationalTaxPrice(price);
	}

	public int calcNationalTaxPrice(final int price) {
		return (price * _taxRatesCastle) / 100 / (100 / NATIONAL_TAX_RATES);
	}

	public int calcTownTaxPrice(final int price) {
		return (price * _taxRatesTown) / 100;
	}

	public int calcWarTaxPrice(final int price) {
		return (price * _taxRatesWar) / 100;
	}

	public int calcDiadTaxPrice(final int price) {
		return (price * _taxRatesWar) / 100 / (100 / DIAD_TAX_RATES);
	}

	/**
	 * 課税後の価格を求める。
	 * 
	 * @param price 課税前の価格
	 * @return 課税後の価格
	 */
	public int layTax(final int price) {
		return price + calcTotalTaxPrice(price);
	}
}
