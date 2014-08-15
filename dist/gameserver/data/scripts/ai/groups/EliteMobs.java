package ai.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.scripts.Functions;
import l2s.commons.util.Rnd;
import org.apache.commons.lang3.ArrayUtils;

public class EliteMobs extends Fighter
{
	private static final int[] _mobs = { 23191, 23192, 23197, 23198 };
	private static final List<Skill> _buffs = new ArrayList<Skill>();
	private static final Map<Integer, Integer[]> _onAttackSay = new HashMap<Integer, Integer[]>();
	private static final Map<Integer, Integer[]> _onKillSay = new HashMap<Integer, Integer[]>();

	private static final NpcString[] SAY_TEXTATTACK23191 = new NpcString[] 
	{
			NpcString.WHO_DARES_TO_BOTHER_US,
			NpcString.HOW_FOOLISH_THE_PRICE_OF_ATTACKING_ME_IS_DEATH,
			NpcString.MY_SWORD_WILL_TAKE_YOUR_LIFE,
			NpcString.YAAAH,
			NpcString.PREPARE_I_SHALL_GRANT_YOU_DEATH,		
			NpcString.OH_SHILEN_GIVE_ME_STRENGTH			
	};	

	private static final NpcString[] SAY_TEXTDEATH23191 = new NpcString[] 
	{
			NpcString.I_WOULD_DEFEATED,
			NpcString.DONT_THINK_THIS_IS_THE_END,
			NpcString.NO_I_LOST_ALL_THE_GATHERED_POWER_OF_LIGHT_TO_THIS_THIS
	};	
	
	private static final NpcString[] SAY_TEXTATTACK23192 = new NpcString[] 
	{
			NpcString.ARE_YOU_THE_ONE_TO_SHATTER_THE_PEACE,
			NpcString.OUR_MISSION_IS_TO_RESURRECT_THE_GODDESS_DO_NOT_INTERFERE,
			NpcString.I_WILL_LET_YOU_SLEEP_IN_DARKNESS_FOREVER,
			NpcString.HYAAAAAAH,
			NpcString.FEEL_THE_TRUE_TERROR_OF_DARKNESS,
			NpcString.OH_CREATURES_OF_THE_GODDESS_LEND_ME_YOUR_STRENGTH			
	};		
	
	private static final NpcString[] SAY_TEXTDEATH23192 = new NpcString[] 
	{
			NpcString.NO_N_NO_NO,
			NpcString.I_WILL_ALWAYS_WATCH_YOU_FROM_THE_DARKNESS,
			NpcString.I_MUSTNT_LOSE_THE_STRENGTH
	};	

	private static final NpcString[] SAY_TEXTATTACK23197 = new NpcString[] 
	{
			NpcString.HEHEHE_IM_GLAD_YOU_CAME_I_WAS_BORED,
			NpcString.HEHEHE_IM_GLAD_YOU_CAME_I_WAS_HUNGRY,
			NpcString.HEHEHE_SHALL_WE_PLAY,
			NpcString.KYAAAH,
			NpcString.SMALL_FRY_I_WILL_SHOW_YOU_TRUE_MADNESS_HAHAHA,		
			NpcString.HEHEHE_PREPARE_MY_MADNESS_WILL_SWALLOW_YOU_UP
	};	
	
	private static final NpcString[] SAY_TEXTDEATH23197 = new NpcString[] 
	{
			NpcString.HUH_WHAT_HAPPENED_I_I_LOST,
			NpcString.HUHUHU_HUHUHU_HUHAHAHA,
			NpcString.ACK_NO_MY_BODY_ITS_DISAPPEARING
	};	
	
	private static final NpcString[] SAY_TEXTATTACK23198 = new NpcString[] 
	{
			NpcString.DIE,
			NpcString.DO_NOT_INTERFERE,
			NpcString.FOR_THE_GODDESS,
			NpcString.OOOOH,
			NpcString.YOU_WILL_DIE,
			NpcString.YOU_WILL_BE_DESTROYED			
	};	
	
	private static final NpcString[] SAY_TEXTDEATH23198 = new NpcString[] 
	{
			NpcString.IS_THIS_THE_END,
			NpcString.OH_GODDESS,
			NpcString.NO_I_DIDNT_STAY_SILENT_ALL_THIS_TIME_JUST_TO_DISAPPEAR_NOW_LIKE_THIS,
			NpcString.NO_I_DONT_WANT_TO_DIE
	};		
	public EliteMobs(NpcInstance actor)
	{
		super(actor);

		//_buffs.add(SkillTable.getInstance().getInfo(14975, 1));
		//_buffs.add(SkillTable.getInstance().getInfo(14976, 1));
		//_buffs.add(SkillTable.getInstance().getInfo(14977, 1));	
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if(Rnd.chance(7))
		{
			switch(getActor().getNpcId())
			{
				case 23191:
					Functions.npcSay(actor, SAY_TEXTATTACK23191[Rnd.get(0, SAY_TEXTATTACK23191.length - 1)]);
					break;
				case 23192:
					Functions.npcSay(actor, SAY_TEXTATTACK23192[Rnd.get(0, SAY_TEXTATTACK23192.length - 1)]);
					break;	
				case 23197:
					Functions.npcSay(actor, SAY_TEXTATTACK23197[Rnd.get(0, SAY_TEXTATTACK23197.length - 1)]);
					break;	
				case 23198:
					Functions.npcSay(actor, SAY_TEXTATTACK23198[Rnd.get(0, SAY_TEXTATTACK23198.length - 1)]);
					break;						
			}
		}
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		for (Skill skill : _buffs)
		{
			actor.broadcastPacket(new MagicSkillUse(killer, killer, skill.getId(), 1, 0, 0, false));
			skill.getEffects(killer, killer, false, false);
		}
			
		switch(getActor().getNpcId())
		{
			case 23191:
				Functions.npcSay(actor, SAY_TEXTDEATH23191[Rnd.get(0, SAY_TEXTDEATH23191.length - 1)]);
				break;
			case 23192:
				Functions.npcSay(actor, SAY_TEXTDEATH23192[Rnd.get(0, SAY_TEXTDEATH23192.length - 1)]);
				break;	
			case 23197:
				Functions.npcSay(actor, SAY_TEXTDEATH23197[Rnd.get(0, SAY_TEXTDEATH23197.length - 1)]);
				break;	
			case 23198:
				Functions.npcSay(actor, SAY_TEXTDEATH23198[Rnd.get(0, SAY_TEXTDEATH23198.length - 1)]);
				break;						
		}
		super.onEvtDead(killer);	
	}
}