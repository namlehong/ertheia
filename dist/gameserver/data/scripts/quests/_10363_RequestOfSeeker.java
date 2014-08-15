package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk dev.fairytale-world.ru
public class _10363_RequestOfSeeker extends Quest implements ScriptFile
{
	private static final int nagel = 33450;
	private static final int celin = 33451;
	private static final int soul1 = 19157;
	private static final int soul2 = 19158;
	private static final int corps1 = 32961;
	private static final int corps2 = 32962;
	private static final int corps3 = 32963;
	private static final int corps4 = 32964;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10363_RequestOfSeeker()
	{
		super(false);
		addStartNpc(nagel);
		addTalkId(celin);
		addTalkId(nagel);

		addLevelCheck(12, 20);
		addQuestCompletedCheck(_10362_CertificationOfSeeker.class);
		addRaceCheck(true, true, true, true, true, true, false);

	}

	@Override
	public void onSocialActionUse(QuestState st, int actionId)
	{
		Player player = st.getPlayer();
		GameObject npc1 = player.getTarget();
		if(player.getTarget() == null || !player.getTarget().isNpc() || ((NpcInstance) npc1).isDead())
			//заглушка на пустой таргет/в таргетe не НПС/и на зажатие клавишы действия,  иначе ошибка в логе и дюп со спавном мобов
			return;
		else
		{
			int target = ((NpcInstance) npc1).getNpcId();
			double dist = player.getDistance(npc1);
			int cond = st.getCond();
			if(actionId == SocialActionPacket.SORROW)
			{
				if(dist < 70 && (target == corps1 || target == corps2 || target == corps3 || target == corps4))
				{
					if(cond == 1)
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.YOUVE_SHOWN_YOUR_CONDOLENCES_TO_ONE_CORPSE, 4500, ScreenMessageAlign.TOP_CENTER));
						st.setCond(2);
						st.playSound(SOUND_MIDDLE);
						((NpcInstance) npc1).doDie(player);
					}
					else if(cond == 2)
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.YOUVE_SHOWN_YOUR_CONDOLENCES_TO_A_SECOND_CORPSE, 4500, ScreenMessageAlign.TOP_CENTER));
						st.setCond(3);
						st.playSound(SOUND_MIDDLE);
						((NpcInstance) npc1).doDie(player);
					}
					else if(cond == 3)
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.YOUVE_SHOWN_YOUR_CONDOLENCES_TO_A_THIRD_CORPSE, 4500, ScreenMessageAlign.TOP_CENTER));
						st.setCond(4);
						st.playSound(SOUND_MIDDLE);
						((NpcInstance) npc1).doDie(player);
					}
					else if(cond == 4)
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.YOUVE_SHOWN_YOUR_CONDOLENCES_TO_A_FOURTH_CORPSE, 4500, ScreenMessageAlign.TOP_CENTER));
						st.setCond(5);
						st.playSound(SOUND_MIDDLE);
						((NpcInstance) npc1).doDie(player);
					}
					else if(cond == 5)
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.YOUVE_SHOWN_YOUR_CONDOLENCES_TO_A_FIFTH_CORPSE, 4500, ScreenMessageAlign.TOP_CENTER));
						st.setCond(6);
						st.playSound(SOUND_MIDDLE);
						((NpcInstance) npc1).doDie(player);
						//npc1.deleteMe();
					}
					else if(cond == 6)
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.GRUDGE_OF_YE_SAGIRA_VICTIMS_HAVE_BEEN_RELIEVED_WITH_YOUR_TEARS, 4500, ScreenMessageAlign.TOP_CENTER));
						npc1.deleteMe();
					}
				}
				else if(dist >= 70 && (target == corps1 || target == corps2 || target == corps3 || target == corps4))
					player.sendPacket(new ExShowScreenMessage(NpcString.YOU_ARE_TOO_FAR_FROM_THE_CORPSE_TO_SHOW_YOUR_CONDOLENCES, 4500, ScreenMessageAlign.TOP_CENTER));
			}
			else if(actionId == SocialActionPacket.LAUGH || actionId == SocialActionPacket.DANCE)
			{
				if(dist < 70 && (target == corps1 || target == corps2 || target == corps3 || target == corps4))
				{
					if(cond == 1 || cond == 2 || cond == 3 || cond == 4 || cond == 5 || cond == 6)
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.DONT_TOY_WITH_THE_DEAD, 4500, ScreenMessageAlign.TOP_CENTER));
						NpcInstance asa = NpcUtils.spawnSingle(soul1, new Location(player.getX() - Rnd.get(100), player.getY() - Rnd.get(100), player.getZ(), 0));
						asa.getAggroList().addDamageHate(st.getPlayer(), 0, 10000);
						asa.setAggressionTarget(player);
						NpcInstance ass = NpcUtils.spawnSingle(soul2, new Location(player.getX() - Rnd.get(100), player.getY() - Rnd.get(100), player.getZ(), 0));
						ass.getAggroList().addDamageHate(st.getPlayer(), 0, 10000);
						ass.setAggressionTarget(player);
						((NpcInstance) npc1).doDie(player);
					}
				}
				else if(dist >= 70 && (target == corps1 || target == corps2 || target == corps3 || target == corps4))
					player.sendPacket(new ExShowScreenMessage(NpcString.YOU_ARE_TOO_FAR_FROM_THE_CORPSE_TO_DO_THAT, 4500, ScreenMessageAlign.TOP_CENTER));
			}
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			htmltext = "0-3.htm";
		}

		if(event.equalsIgnoreCase("qet_rev"))
		{
			htmltext = "1-3.htm";
			st.getPlayer().addExpAndSp(70200, 8100);
			st.giveItems(57, 48000);
			st.giveItems(1060, 100);
			st.giveItems(43, 1);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);

		}
		return htmltext;
	}

	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		if(npcId == nagel)
		{
			if(st.isCompleted())
				htmltext = "0-c.htm";
			else if(cond == 0 && checkStartCondition(st.getPlayer()))
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "0-4.htm";
			else if(cond == 6)
			{
				htmltext = "0-5.htm";
				st.setCond(7);
			}
			else if(cond == 7)
				htmltext = "0-6.htm";
			else
				return htmltext;
		}
		if(npcId == celin)
		{
			if(st.isCompleted())
				htmltext = "1-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 7)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}
}