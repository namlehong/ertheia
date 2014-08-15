package npc.model;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Kolobrodik
 * @date 17:54/16.06.13
 */
public class OctonBombInstance extends MonsterInstance
{
    private static final int _luckyBoxId = 19285;

    public OctonBombInstance(int objectId, NpcTemplate template)
    {
        super(objectId, template);
    }

    public class BombScheduleTimerTask extends RunnableImpl
    {
        NpcInstance _npc = null;

        public BombScheduleTimerTask(NpcInstance npc)
        {
            _npc = npc;
        }

        @Override
        public void runImpl() throws Exception
        {
			if(_npc == null) //possible
				return;
			Player player = null;
			
			for(Player p : getReflection().getPlayers())
			{
				player = p;
				break;
			}
			
			if(player == null) //possible
				return;
				
			for (NpcInstance npc : getAroundNpc(150, 200))
			{
				if (npc.getNpcId() == _luckyBoxId)
				{
					npc.doDie(player); //for correct drop
				}
			}		
            // TODO: по всей видимости нужно заюзать какой-то скилл для анимации взрыва в клиенте
            _npc.deleteMe();
        }
    }

	@Override
	public void onReduceCurrentHp(final double damage, final Creature attacker, Skill skill, final boolean awake, final boolean standUp, boolean directHp)
	{
		if(Rnd.chance(5)) //test
		{
			broadcastPacket(new MagicSkillUse(this, this, 6390, 1, 2000, 0));
			ThreadPoolManager.getInstance().schedule(new BombScheduleTimerTask(this), 3000L);
		}	
	}	

    @Override
    protected void onSpawn()
    {
        super.onSpawn();
		setNpcState(1); //is ssparking if 1
        
    }
}
