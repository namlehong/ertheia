package l2s.gameserver.ai;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.MonsterInstance;

/**
 * @author Bonux
**/
public class SpecialTautiMonsters extends Fighter
{
	public SpecialTautiMonsters(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if(!canAttack(actor.getNpcId(), attacker))
			return;

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	public boolean canAttackCharacter(Creature target)
	{
		NpcInstance actor = getActor();

		return canAttack(actor.getNpcId(), target);
	}

	@Override
	public int getMaxAttackTimeout()
	{
		return 0;
	}
	
	private boolean canAttack(int selfId, Creature target)
	{
		if(selfId == 33680 || selfId == 33679) //peace
		{
			if(target.isPlayable())
				return false;
			else 
				return target.isMonster();
		}
		else //not peace
		{
			if(target.isMonster())
			{
				MonsterInstance monster = (MonsterInstance) target;
				if(monster.getNpcId() == 19262 || monster.getNpcId() == 19263 || monster.getNpcId() == 19264 || monster.getNpcId() == 19265 || monster.getNpcId() == 19266)
					return false;
				else
					return true;
			}
			else
				return !target.isNpc();
		}	
				
	}
}