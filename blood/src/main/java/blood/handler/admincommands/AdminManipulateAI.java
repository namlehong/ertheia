package blood.handler.admincommands;

import l2s.gameserver.ai.PlayerAI;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.tables.SkillTable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blood.FPCInfo;
import blood.base.FPCPveStyle;
import blood.base.FPCRole;

public class AdminManipulateAI implements IAdminCommandHandler
{
	
	@SuppressWarnings("unused")
	private static final Logger _log = LoggerFactory.getLogger(AdminManipulateAI.class);
	
	private static enum Commands
	{
		admin_tryai_party, 
		admin_tryai,
		admin_stopai,
		admin_dump_skills
		}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;
		
		FPCInfo newInfo;

		switch(command)
		{
			case admin_tryai:
				newInfo = new FPCInfo(activeChar);
				newInfo.setPVEStyle(FPCPveStyle.SOLO);
				newInfo.setAI(FPCRole.NEXUS_EVENT.getAI(activeChar));
				newInfo.getAI().toggleDebug();
	//			newInfo.setParty();
			break;
			
			case admin_tryai_party:
				newInfo = new FPCInfo(activeChar);
				newInfo.setPVEStyle(FPCPveStyle.PARTY);
				newInfo.setAI(FPCRole.NEXUS_EVENT.getAI(activeChar));
				newInfo.getAI().toggleDebug();
				newInfo.setParty();
				break;
				
			case admin_stopai:
				activeChar.setAI(new PlayerAI(activeChar));
				break;
				
			case admin_dump_skills:
				
				ClassId activeClass = activeChar.getClassId();
				String className = getClassName(activeClass);
				
				if(activeChar.getClassLevel() > 0)
				{
					ClassId parentClass = activeClass.getParent(activeChar.getSex().ordinal());
					String parentName = getClassName(parentClass);
					System.out.println("\tpublic class "+className+" extends "+parentName+" {");
				}
				else
					System.out.println("\tpublic class "+className+" {");
				
				System.out.println("\t\tpublic static final int");
				System.out.println("\t\t//======= Start Skill list of "+className+" ID:"+activeClass.ordinal()+"=======");
				for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(activeChar, AcquireType.NORMAL))
				{
					if(sl.getMinLevel() < 20 && activeChar.getClassLevel() > 0)
						continue;
					
					if(sl.getMinLevel() < 40 && activeChar.getClassLevel() > 1)
						continue;
					
					if(sl.getMinLevel() < 76 && activeChar.getClassLevel() > 2)
						continue;
					
					Skill skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
					if(skill == null)
						continue;
					
					if(skill.isPassive())
						continue;
					
					
					
					String niceName = skill.getName().toUpperCase().replace(" ", "_").replace("'", "").replace(":", "");
					double tab_repeat = Math.floor((40 - niceName.length())/4.);
					String tabs = StringUtils.repeat("\t", (int) tab_repeat);
					System.out.println("\t\tSKILL_"+niceName+tabs+"= "+skill.getId()+", // Lv."+skill.getLevel());
					
				}
				System.out.println("\t\t//======= End Skill list of "+className+" ID:"+activeClass.ordinal()+"=======");
				System.out.println("\t\tSKILL_DUMMY = 1;");
				System.out.println("\t};");
				System.out.println("");
			
		}
		return true;
	}
	
	public String getClassName(ClassId classId)
	{
		return WordUtils.capitalize(classId.toString().toLowerCase().replace("_", " ")).replace(" ", "");
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

}
