package blood.base;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import blood.BloodConfig;
import blood.FPCInfo;
import blood.ai.FPCDefaultAI;
import blood.ai.IdleFPC;
import blood.ai.MarketFPC;
import blood.ai.impl.ArcherPC;
import blood.ai.impl.EnchanterPC;
import blood.ai.impl.FPCAeore;
import blood.ai.impl.FPCFeoh;
import blood.ai.impl.FPCIss;
import blood.ai.impl.FPCOthell;
import blood.ai.impl.FPCSigel;
import blood.ai.impl.FPCTyrr;
import blood.ai.impl.FPCWynn;
import blood.ai.impl.FPCYul;
import blood.ai.impl.FighterPC;
import blood.ai.impl.HealerPC;
import blood.ai.impl.MysticPC;
import blood.ai.impl.RougePC;
import blood.ai.impl.SummonerPC;
import blood.ai.impl.TankerPC;
import blood.ai.impl.WarriorPC;
import blood.ai.impl.WizardPC;

public enum FPCRole {
	IDLE("idle", BloodConfig.FPC_IDLE),
	NEXUS_EVENT("nexus", BloodConfig.FPC_NEXUS),
	MARKET("market", BloodConfig.FPC_MARKET);
	
	private String _name;
	private FPCBase _players = new FPCBase();
	private int _quota = 0;
	private int _quota_base = 0;
	private int _quota_adjust_rate	= 15; //percent
	
	private FPCRole(String name, int quota)
	{
		_name = name;
		setQuota(quota, true);
	}
	
	public void quotaPadding()
	{
		int	_adjust_ccu	= _quota_base*_quota_adjust_rate/100;
		
		if(_adjust_ccu > 0)
			_adjust_ccu		=  Rnd.get(_adjust_ccu*2)-_adjust_ccu;
		
		_quota			= _quota_base + _adjust_ccu;
		
		if(_quota < 0) _quota = 0;
	}
	
	public void setQuota(int quota)
	{
		setQuota(quota, false);
	}
	
	public void setQuota(int quota, boolean is_init)
	{
		_quota_base = quota;
		quotaPadding();
		
		if(!is_init)
			FPCSpawnStatus.setPopulation(getExpectCCU()*FPCSpawnStatus.getOnlineRatio());
	}
	
	public int getQuota()
	{
		return _quota;
	}
	
	public int getSize()
	{
		return _players.getPs().size();
	}
	
	public int getPadding()
	{
		return _quota - getSize();
	}
	
	public FPCInfo getRandom()
	{
		return _players.getRandom();
	}
	
	public FPCBase getAll()
	{
		return _players;
	}
	
	public FPCInfo getPlayer(int obj_id)
	{
		return _players.getPlayer(obj_id);
	}
	
	public FPCInfo getPlayer(Player player)
	{
		return _players.getPlayer(player);
	}
	
	public void add(FPCInfo player)
	{
		_players.addInfo(player);
	}
	
	public void remove(FPCInfo player)
	{
		_players.deleteInfo(player);
	}
	
	public static int getExpectCCU()
	{
		int tmp = 0;
		for(FPCRole role: FPCRole.values())
		{
			tmp += role.getQuota();
		}
		return tmp;
	}
	
	public static int getCCU()
	{
		return IDLE.getSize() + NEXUS_EVENT.getSize() + MARKET.getSize();
	}
	
	public static int getTotalPadding()
	{
		return getExpectCCU() - getCCU();
	}
	
	public FPCDefaultAI getAI(Player player)
	{
		if(player == null)
			return null;
		
		switch(this)
		{
			case IDLE:
				return new IdleFPC(player);
			
			case MARKET:
				return new MarketFPC(player);
				
			case NEXUS_EVENT:
				return getAggresiveAI(player);
				//return new EventFPC(player);
			
			default:
				return new IdleFPC(player);
		}
	}
	
	private FPCDefaultAI getAggresiveAI(Player player)
	{
		FPCDefaultAI ai = null;
		ClassId playerClassId = player.getClassId();
		
		if(playerClassId.getType2() == null){
			switch (playerClassId.getType()) {
			case MYSTIC:
				ai = new MysticPC(player);
				break;

			default:
				ai = new FighterPC(player);
				break;
			}
		}else{
			switch (playerClassId.getType2()) {
			case KNIGHT:
				ai = playerClassId.isOfLevel(ClassLevel.AWAKED) ? new FPCSigel(player) : new TankerPC(player);
				break;
				
			case WARRIOR:
				ai = playerClassId.isOfLevel(ClassLevel.AWAKED) ? new FPCTyrr(player) : new WarriorPC(player);
				break;
				
			case ROGUE:
				ai = playerClassId.isOfLevel(ClassLevel.AWAKED) ? new FPCOthell(player) : new RougePC(player);
				break;
				
			case ARCHER:
				ai = playerClassId.isOfLevel(ClassLevel.AWAKED) ? new FPCYul(player) : new ArcherPC(player);
				break;
				
			case WIZARD:
				ai = playerClassId.isOfLevel(ClassLevel.AWAKED) ? new FPCFeoh(player) : new WizardPC(player);
				break;
				
			case SUMMONER:
				ai = playerClassId.isOfLevel(ClassLevel.AWAKED) ? new FPCWynn(player) : new SummonerPC(player);
				break;
				
			case ENCHANTER:
				ai = playerClassId.isOfLevel(ClassLevel.AWAKED) ? new FPCIss(player) : new EnchanterPC(player);
				break;
				
			case HEALER:
				ai = playerClassId.isOfLevel(ClassLevel.AWAKED) ? new FPCAeore(player) : new HealerPC(player);
				break;
			}
		}
		
		return ai;
	}
	
	public int getQuotaBase()
	{
		return _quota_base;
	}

	public void setQuotaBase(int _quota_base)
	{
		this._quota_base = _quota_base;
	}

	public int getQuotaAdjustRate()
	{
		return _quota_adjust_rate;
	}

	public void setQuotaAdjustRate(int _quota_adjust_rate)
	{
		this._quota_adjust_rate = _quota_adjust_rate;
	}

	public String getName()
	{
		return _name;
	}

	
}