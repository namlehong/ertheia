package l2s.gameserver.data.htm;

import java.io.File;
import java.io.IOException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import l2s.commons.map.hash.TIntStringHashMap;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.Language;
import l2s.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Кэширование html диалогов.
 * 
 * @author G1ta0
 * @reworked VISTALL
 * В кеше список вот так
 * admin/admhelp.htm
 * admin/admin.htm
 * admin/admserver.htm
 * admin/banmenu.htm
 * admin/charmanage.htm
 */
public class HtmCache
{
	public static final int DISABLED = 0; // все диалоги кешируются при загрузке сервера
	public static final int LAZY = 1; // диалоги кешируются по мере обращения
	public static final int ENABLED = 2; // кеширование отключено (только для тестирования)

	private static final Logger _log = LoggerFactory.getLogger(HtmCache.class);

	private final static HtmCache _instance = new HtmCache();

	public static HtmCache getInstance()
	{
		return _instance;
	}

	private Cache[] _cache = new Cache[Language.VALUES.length];

	private HtmCache()
	{
		for(int i = 0; i < _cache.length; i++)
			_cache[i] = CacheManager.getInstance().getCache(getClass().getName() + "." + Language.VALUES[i].name());
	}

	public void reload()
	{
		clear();

		switch(Config.HTM_CACHE_MODE)
		{
			case ENABLED:
				for(Language lang : Language.VALUES)
				{
					File root = new File(Config.DATAPACK_ROOT, "data/html-" + lang.getShortName());
					if(!root.exists())
					{
						_log.info("HtmCache: Not find html dir for lang: " + lang);
						continue;
					}
					load(lang, root, root.getAbsolutePath() + "/");
				}
				for(int i = 0; i < _cache.length; i++)
				{
					Cache c = _cache[i];
					_log.info(String.format("HtmCache: parsing %d documents; lang: %s.", c.getSize(), Language.VALUES[i]));
				}
				break;
			case LAZY:
				_log.info("HtmCache: lazy cache mode.");
				break;
			case DISABLED:
				_log.info("HtmCache: disabled.");
				break;
		}
	}

	private void load(Language lang, File f, final String rootPath)
	{
		if(!f.exists())
		{
			_log.info("HtmCache: dir not exists: " + f);
			return;
		}
		File[] files = f.listFiles();

		//FIXME [VISTALL] может лучше использовать Apache FileUtils?
		for(File file : files)
		{
			if(file.isDirectory())
				load(lang, file, rootPath);
			else
			{
				if(file.getName().endsWith(".htm"))
					try
					{
						putContent(lang, file, rootPath);
					}
					catch(IOException e)
					{
						_log.info("HtmCache: file error" + e, e);
					}
			}
		}
	}

	public void putContent(Language lang, File f, final String rootPath) throws IOException
	{
		String content = FileUtils.readFileToString(f, "UTF-8");

		String path = f.getAbsolutePath().substring(rootPath.length()).replace("\\", "/");

		_cache[lang.ordinal()].put(new Element(path.toLowerCase(), HtmlUtils.bbParse(content)));
	}

	public String getNotNull(String fileName, Player player)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		String cache = getCache(fileName, lang);

		if(StringUtils.isEmpty(cache))
			cache = "Dialog not found: " + fileName + "; Lang: " + lang;

		return cache;
	}

	public String getNullable(String fileName, Player player)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		String cache = getCache(fileName, lang);

		if(StringUtils.isEmpty(cache))
			return null;

		return cache;
	}

	public TIntStringHashMap getTemplates(String fileName, Player player)
	{
		return Util.parseTemplates(getNotNull(fileName, player));
	}

	private String getCache(String file, Language lang)
	{
		if(file == null)
			return null;

		final String fileLower = file.toLowerCase();
		String cache = get(lang, fileLower);

		if(cache == null)
		{
			switch(Config.HTM_CACHE_MODE)
			{
				case ENABLED:
					for(Language otherLang: Language.VALUES)
					{
						if(otherLang.equals(lang))
							continue;
						
						if(cache != null)
							continue;
						
						cache = get(otherLang, fileLower);
					}
					break;
				case LAZY:
					cache = loadLazy(lang, file);
					for(Language otherLang: Language.VALUES)
					{
						if(otherLang.equals(lang))
							continue;
						
						if(cache != null)
							continue;
						
						cache = loadLazy(otherLang, file);
					}
					break;
				case DISABLED:
					cache = loadDisabled(lang, file);
					for(Language otherLang: Language.VALUES)
					{
						if(otherLang.equals(lang))
							continue;
						
						if(cache != null)
							continue;
						
						cache = loadDisabled(otherLang, file);
					}
					break;
			}
		}

		return cache;
	}

	private String loadDisabled(Language lang, String file)
	{
		String cache = null;
		File f = new File(Config.DATAPACK_ROOT, "data/html-" + lang.getShortName() + "/" + file);
		if(f.exists())
			try
			{
				cache = FileUtils.readFileToString(f, "UTF-8");
				cache = HtmlUtils.bbParse(cache);
			}
			catch(IOException e)
			{
				_log.info("HtmCache: File error: " + file + " lang: " + lang);
			}
		return cache;
	}

	private String loadLazy(Language lang, String file)
	{
		String cache = null;
		File f = new File(Config.DATAPACK_ROOT, "data/html-" + lang.getShortName() + "/" + file);
		if(f.exists())
			try
			{
				cache = FileUtils.readFileToString(f, "UTF-8");
				cache = HtmlUtils.bbParse(cache);

				_cache[lang.ordinal()].put(new Element(file, cache));
			}
			catch(IOException e)
			{
				_log.info("HtmCache: File error: " + file + " lang: " + lang);
			}
		return cache;
	}

	private String get(Language lang, String f)
	{
		Element element = _cache[lang.ordinal()].get(f);
		
		for(Language otherLang: Language.VALUES)
		{
			if(otherLang.equals(lang))
				continue;
			
			if(element != null)
				continue;
			
			element = _cache[otherLang.ordinal()].get(f);
		}

		return element == null ? null : (String) element.getObjectValue();
	}

	public void clear()
	{
		for(int i = 0; i < _cache.length; i++)
		{
			_cache[i].removeAll();
		}
	}
}
