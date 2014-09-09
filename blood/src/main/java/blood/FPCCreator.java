package blood;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.tables.SkillTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blood.dao.FakeNameDAO;
import blood.dao.FakePlayerDAO;
import blood.data.holder.NamePatternHolder;

public class FPCCreator
{
	// TODO - move code here
	
	private static final Logger 		_log = LoggerFactory.getLogger(FPCCreator.class);
	
	public static void createNewChar()
	{
		String name = FakeNameDAO.getInstance().getName();
		
		ClassId newClass = NamePatternHolder.getStartClass(name);
		
		if(newClass == null)
		{
			_log.info("new char bad name:"+name);
			return;
		}
		
		try{
			_log.info("Create NewChar:"+name+" Class: " + newClass);
			createNewChar(newClass.getId(), name, "_fake_account");
		}catch(Exception e){
			_log.error("create char name:"+name, e);
		}
	}
    
	public static void createNewChar(int _classId, String _name, String _account)
	{
		Sex sex = NamePatternHolder.getSexByName(_name);
		
		if(_classId == 123)
			sex = Sex.MALE;
		
		if(_classId == 124)
			sex = Sex.FEMALE;
		
		int _sex = sex != null ? sex.ordinal() : Rnd.get(0,1);
		
		int _hairStyle = Rnd.get(0, _sex == 1 ? 6 : 4);
		int _hairColor = Rnd.get(0,2);
		int _face = Rnd.get(0,2);
		
		Player newChar = Player.create(_classId, _sex, _account, _name, _hairStyle, _hairColor, _face);
		
		if(newChar == null)
			return;
		
		FakeNameDAO.getInstance().useName(_name);
		
		FakePlayerDAO.addFPC(newChar.getObjectId());
		
		_log.info("Create NewChar:"+_name+" in Account: "+_account+" Class: " + _classId + " Sex: " + _sex);
		
		int _obj_id = newChar.getObjectId();
		
		initNewChar(newChar);
		
		new FPCInfo(_obj_id);
	}
	
	public static void initNewChar(Player newChar)
	{
//		PlayerTemplate template = newChar.getTemplate();

		newChar.getSubClassList().restore();

       	newChar.setLoc(BloodConfig.FPC_CREATE_LOC);

		newChar.setHeading(Rnd.get(0, 90000));
		
		for(SkillLearn skill : SkillAcquireHolder.getInstance().getAvailableSkills(newChar, AcquireType.NORMAL))
			newChar.addSkill(SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()), true);

		newChar.setCurrentHpMp(newChar.getMaxHp(), newChar.getMaxMp());
		newChar.setCurrentCp(0); // retail
		newChar.setOnlineStatus(false);

		newChar.store(false);
		newChar.getInventory().store();
		newChar.deleteMe();
		
	}
}
