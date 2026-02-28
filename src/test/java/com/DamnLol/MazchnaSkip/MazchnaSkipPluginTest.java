package com.DamnLol.MazchnaSkip;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MazchnaSkipPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MazchnaSkipPlugin.class);
		RuneLite.main(args);
	}
}