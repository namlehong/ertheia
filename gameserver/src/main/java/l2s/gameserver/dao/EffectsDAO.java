package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.EffectsComparator;
import l2s.gameserver.utils.SqlBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 13:01/02.02.2011
 */
public class EffectsDAO
{
	private static final int SUMMON_SKILL_OFFSET = 100000;
	private static final Logger _log = LoggerFactory.getLogger(EffectsDAO.class);
	private static final EffectsDAO _instance = new EffectsDAO();

	EffectsDAO()
	{
		//
	}

	public static EffectsDAO getInstance()
	{
		return _instance;
	}

	public void restoreEffects(Playable playable)
	{
		int objectId, id;
		if(playable.isPlayer())
		{
			objectId = playable.getObjectId();
			id = ((Player) playable).getActiveClassId();
		}
		else if(playable.isServitor())
		{
			objectId = playable.getPlayer().getObjectId();
			id = ((Servitor) playable).getEffectIdentifier();
			if(playable.isSummon())
			{
				id += SUMMON_SKILL_OFFSET;
				id *= 10;
				id += playable.getPlayer().getSummons().length;
			}
		}
		else
			return;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration`,`is_self` FROM `character_effects_save` WHERE `object_id`=? AND `id`=? ORDER BY (`effect_cur_time` / `duration`) ASC, `skill_id` ASC");
			statement.setInt(1, objectId);
			statement.setInt(2, id);
			rset = statement.executeQuery();
			while(rset.next())
			{
				int skillId = rset.getInt("skill_id");
				int skillLvl = rset.getInt("skill_level");

				Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
				if(skill == null)
					continue;

				boolean isSelf = rset.getInt("is_self") > 0;

				EffectTemplate[] effectTemplates;
				if(isSelf)
				{
					if(!skill.hasSelfEffects())
						continue;

					effectTemplates = skill.getSelfEffectTemplates();
				}
				else
				{
					if(!skill.hasEffects())
						continue;

					effectTemplates = skill.getEffectTemplates();
				}

				int effectCount = rset.getInt("effect_count");
				long effectCurTime = rset.getLong("effect_cur_time");
				long duration = rset.getLong("duration");

				for(EffectTemplate et : effectTemplates)
				{
					if(et == null)
						continue;

					Env env = new Env(playable, playable, skill);
					Effect effect = et.getEffect(env);
					if(effect == null || !effect.isSaveable() || effect.getTemplate().isSingle())
						continue;

					effect.setCount(effectCount);
					effect.setPeriod(effectCount == 1 ? duration - effectCurTime : duration);

					playable.getEffectList().addEffect(effect);
				}
			}

			DbUtils.closeQuietly(statement, rset);

			statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id = ? AND id=?");
			statement.setInt(1, objectId);
			statement.setInt(2, id);
			statement.execute();
			DbUtils.close(statement);
		}
		catch(final Exception e)
		{
			_log.error("Could not restore active effects data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}
	}

	public void insert(Playable playable)
	{
		int objectId, id;
		if(playable.isPlayer())
		{
			objectId = playable.getObjectId();
			id = ((Player) playable).getActiveClassId();
		}
		else if(playable.isServitor())
		{
			objectId = playable.getPlayer().getObjectId();
			id = ((Servitor) playable).getEffectIdentifier();
			if(playable.isSummon())
			{
				id += SUMMON_SKILL_OFFSET;
				id *= 10;
				id += playable.getPlayer().getSummons().length;
			}
		}
		else
			return;

		Collection<Effect> effects = playable.getEffectList().getEffects();
		if(effects.isEmpty())
			return;

		Connection con = null;
		Statement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();

			SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_effects_save` (`object_id`,`skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration`,`id`,`is_self`) VALUES");

			StringBuilder sb;
			for(Effect effect : effects)
			{
				if(effect != null)
				{
					if(effect.isSaveable())
					{
						sb = new StringBuilder("(");
						sb.append(objectId).append(",");
						sb.append(effect.getSkill().getId()).append(",");
						sb.append(effect.getSkill().getLevel()).append(",");
						sb.append(effect.getCount()).append(",");
						sb.append(effect.getTime()).append(",");
						sb.append(effect.getPeriod()).append(",");
						sb.append(id).append(",");
						sb.append(effect.isSelf() ? 1 : 0).append(")");
						b.write(sb.toString());
					}
					while((effect = effect.getNext()) != null && effect.isSaveable())
					{
						sb = new StringBuilder("(");
						sb.append(objectId).append(",");
						sb.append(effect.getSkill().getId()).append(",");
						sb.append(effect.getSkill().getLevel()).append(",");
						sb.append(effect.getCount()).append(",");
						sb.append(effect.getTime()).append(",");
						sb.append(effect.getPeriod()).append(",");
						sb.append(id).append(",");
						sb.append(effect.isSelf() ? 1 : 0).append(")");
						b.write(sb.toString());
					}
				}
			}

			if(!b.isEmpty())
				statement.executeUpdate(b.close());
		}
		catch(final Exception e)
		{
			_log.error("Could not store active effects data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}
