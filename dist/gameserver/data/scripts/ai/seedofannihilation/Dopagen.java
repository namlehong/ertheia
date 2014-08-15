package ai.seedofannihilation;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.instances.NpcInstance;

// Если его ударить включается таймер 20 минут, если не убить его за 20 минут, он исчезает и появляеться на месте респавна через 5 минут с полным ХП. (за 2 мин до исчезновения появляеться надпись, что РБ готовится к исчезновению)
// Появляются 2 колонны, если подвести к одной, то первая снижает ему Физ.Защ., вторая Маг.Защ.
// Ставит в паралич всех самонов, которые тронут его skill 6651.
public class Dopagen extends Fighter
{

	public Dopagen(NpcInstance actor)
	{
		super(actor);
	}
}