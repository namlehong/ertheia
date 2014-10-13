package l2s.gameserver.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by archer on 11:15 AM 10/13/14.
 */
public final class ParentClass
{
    private int id;
    private List<SkillLearn> skills;

    public ParentClass(int id, List<SkillLearn> skills)
    {
        this.id = id;
        this.skills = skills;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public List<SkillLearn> getSkills()
    {
        if(this.skills == null)
            skills = new ArrayList<SkillLearn>();
        return skills;
    }
}
