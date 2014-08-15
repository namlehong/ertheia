package l2s.gameserver.model.base;

import l2s.gameserver.Config;

public class Experience
{
	public final static long LEVEL[] = { -1L, // level 0 (unreachable)
			/* Lvl:1 */0L,
			/* Lvl:2 */68L,
			/* Lvl:3 */363L,
			/* Lvl:4 */1168L,
			/* Lvl:5 */2884L,
			/* Lvl:6 */6038L,
			/* Lvl:7 */11287L,
			/* Lvl:8 */19423L,
			/* Lvl:9 */31378L,
			/* Lvl:10 */48229L,
			/* Lvl:11 */71202L,
			/* Lvl:12 */101677L,
			/* Lvl:13 */141193L,
			/* Lvl:14 */191454L,
			/* Lvl:15 */254330L,
			/* Lvl:16 */331867L,
			/* Lvl:17 */426288L,
			/* Lvl:18 */540000L,
			/* Lvl:19 */675596L,
			/* Lvl:20 */835862L,
			/* Lvl:21 */920357L,
			/* Lvl:22 */1015431L,
			/* Lvl:23 */1123336L,
			/* Lvl:24 */1246808L,
			/* Lvl:25 */1389235L,
			/* Lvl:26 */1554904L,
			/* Lvl:27 */1749413L,
			/* Lvl:28 */1980499L,
			/* Lvl:29 */2260321L,
			/* Lvl:30 */2634751L,
			/* Lvl:31 */2844287L,
			/* Lvl:32 */3093068L,
			/* Lvl:33 */3389496L,
			/* Lvl:34 */3744042L,
			/* Lvl:35 */4169902L,
			/* Lvl:36 */4683988L,
			/* Lvl:37 */5308556L,
			/* Lvl:38 */6074376L,
			/* Lvl:39 */7029248L,
			/* Lvl:40 */8342182L,
			/* Lvl:41 */8718976L,
			/* Lvl:42 */12842357L,
			/* Lvl:43 */14751932L,
			/* Lvl:44 */17009030L,
			/* Lvl:45 */19686117L,
			/* Lvl:46 */22875008L,
			/* Lvl:47 */26695470L,
			/* Lvl:48 */31312332L,
			/* Lvl:49 */36982854L,
			/* Lvl:50 */44659561L,
			/* Lvl:51 */48128727L,
			/* Lvl:52 */52277875L,
			/* Lvl:53 */57248635L,
			/* Lvl:54 */63216221L,
			/* Lvl:55 */70399827L,
			/* Lvl:56 */79078300L,
			/* Lvl:57 */89616178L,
			/* Lvl:58 */102514871L,
			/* Lvl:59 */118552044L,
			/* Lvl:60 */140517709L,
			/* Lvl:61 */153064754L,
			/* Lvl:62 */168231664L,
			/* Lvl:63 */186587702L,
			/* Lvl:64 */208840245L,
			/* Lvl:65 */235877658L,
			/* Lvl:66 */268833561L,
			/* Lvl:67 */309192920L,
			/* Lvl:68 */358998712L,
			/* Lvl:69 */421408669L,
			/* Lvl:70 */493177635L,
			/* Lvl:71 */555112374L,
			/* Lvl:72 */630494192L,
			/* Lvl:73 */722326994L,
			/* Lvl:74 */834354722L,
			/* Lvl:75 */971291524L,
			/* Lvl:76 */1139165674L,
			/* Lvl:77 */1345884863L,
			/* Lvl:78 */1602331019L,
			/* Lvl:79 */1902355477L,
			/* Lvl:80 */2288742870L,
			/* Lvl:81 */2703488268L,
			/* Lvl:82 */3174205601L,
			/* Lvl:83 */3708727539L,
			/* Lvl:84 */4316300702L,
			/* Lvl:85 */5008025097L,
			/* Lvl:86 */10985069426L,
			/* Lvl:87 */19192594397L,
			/* Lvl:88 */33533938399L,
			/* Lvl:89 */44373087147L,
			/* Lvl:90 */63751938490L,
			/* Lvl:91 */88688523458L,
			/* Lvl:92 */120224273113L,
			/* Lvl:93 */157133602347L,
			/* Lvl:94 */208513860393L,
			/* Lvl:95 */266769078393L,
			/* Lvl:96 */377839508352L,
			/* Lvl:97 */592791113370L,
			/* Lvl:98 */1016243369039L,
			/* Lvl:99 */1956916677389L,
			/* Lvl:100 */6178380725000L };

	/**
	 * Return PenaltyModifier (can use in all cases)
	 *
	 * @param count	- how many times <percents> will be substructed
	 * @param percents - percents to substruct
	 *
	 * @author Styx
	 */

	/*
	 *  This is for fine view only ;)
	 *
	 *	public final static double penaltyModifier(int count, int percents)
	 *	{
	 *		int allPercents = 100;
	 *		int allSubstructedPercents = count * percents;
	 *		int penaltyInPercents = allPercents - allSubstructedPercents;
	 *		double penalty = penaltyInPercents / 100.0;
	 *		return penalty;
	 *	}
	 */
	public static double penaltyModifier(long count, double percents)
	{
		return Math.max(1. - count * percents / 100, 0);
	}

	/**
	 * Максимальный достижимый уровень
	 */
	public static int getMaxLevel()
	{
		return Config.ALT_MAX_LEVEL;
	}

	/**
	 * Максимальный уровень для саба
	 */
	public static int getMaxSubLevel()
	{
		return Config.ALT_MAX_SUB_LEVEL;
	}

	public static int getLevel(long thisExp)
	{
		int level = 0;
		for(int i = 0; i < LEVEL.length; i++)
		{
			long exp = LEVEL[i];
			if(thisExp >= exp)
				level = i;
		}
		return level;
	}

	public static long getExpForLevel(int lvl)
	{
		if(lvl >= Experience.LEVEL.length)
			return 0;
		return Experience.LEVEL[lvl];
	}

	public static double getExpPercent(int level, long exp)
	{
		return (exp - getExpForLevel(level)) / ((getExpForLevel(level + 1) - getExpForLevel(level)) / 100.0D) * 0.01D;
	}
}