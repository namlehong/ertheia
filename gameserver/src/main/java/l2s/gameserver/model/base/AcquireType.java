package l2s.gameserver.model.base;

/**
 * Author: VISTALL
 * Date:  11:53/01.12.2010
 */
public enum AcquireType
{
	NORMAL,
	FISHING,
	CLAN,
	SUB_UNIT,
	TRANSFORMATION,
	CERTIFICATION,
	DUAL_CERTIFICATION,
	COLLECTION,
	TRANSFER_CARDINAL,
	TRANSFER_EVA_SAINTS,
	TRANSFER_SHILLIEN_SAINTS,
	FORGOTTEN_SCROLL,
	NOBLESSE,
	HERO,
	GM,
	CHAOS,
	DUAL_CHAOS,
	ABILITY,
	dummy18, dummy19, 
	dummy20, dummy21, dummy22, dummy23, dummy24, dummy25, dummy26, dummy27, dummy28, dummy29,
	dummy30, dummy31, dummy32, dummy33, dummy34, dummy35, dummy36, dummy37, dummy38, dummy39,
	dummy40, dummy41, dummy42, dummy43, dummy44, dummy45, dummy46, dummy47, dummy48, dummy49,
	dummy50, dummy51, dummy52, dummy53, dummy54, dummy55, dummy56, dummy57, dummy58, dummy59,
	dummy60, dummy61, dummy62, dummy63, dummy64, dummy65, dummy66, dummy67, dummy68, dummy69,
	dummy70, dummy71, dummy72, dummy73, dummy74, dummy75, dummy76, dummy77, dummy78, dummy79,
	dummy80, dummy81, dummy82, dummy83, dummy84, dummy85, dummy86, dummy87, dummy88, dummy89,
	dummy90, dummy91, dummy92, dummy93, dummy94, dummy95, dummy96, dummy97, dummy98, dummy99,
	dummy110, dummy111, dummy112, dummy113, dummy114, dummy115, dummy116, dummy117, dummy118, dummy119,
	dummy100, dummy101, dummy102, dummy103, dummy104, dummy105, dummy106, dummy107, dummy108, dummy109,
	dummy120, dummy121, dummy122, dummy123, dummy124, dummy125, dummy126, dummy127, dummy128, dummy129,
	dummy130, dummy131, dummy132, dummy133, dummy134, dummy135, dummy136, dummy137, dummy138, dummy139,
	ALCHEMY;

	public static final AcquireType[] VALUES = AcquireType.values();

	public static AcquireType transferType(int classId)
	{
		switch(classId)
		{
			case 97:
				return TRANSFER_CARDINAL;
			case 105:
				return TRANSFER_EVA_SAINTS;
			case 112:
				return TRANSFER_SHILLIEN_SAINTS;
		}

		return null;
	}

	public int transferClassId()
	{
		switch(this)
		{
			case TRANSFER_CARDINAL:
				return 97;
			case TRANSFER_EVA_SAINTS:
				return 105;
			case TRANSFER_SHILLIEN_SAINTS:
				return 112;
		default:
			break;
		}

		return 0;
	}
}
