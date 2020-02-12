package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Codes;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.UUID;

public class CodesOverviewPanel extends FormWindowSimple implements FormResponse
{
	String codesUUID;

	public CodesOverviewPanel(UUID codesUuid)
	{
		super("", "");

		codesUUID = codesUuid.toString();

		Codes codes = GiftCodePlugin.ins.getCodesWithUUID(codesUuid);

		setTitle(codes.label+"使用情况概览");

		ArrayList<String> used = new ArrayList<String>();
		ArrayList<String> unused = new ArrayList<String>();

		for(String codeOrPlayer : codes.codes.keySet())
		{
			boolean isUnused = codes.codes.get(codeOrPlayer);

			if(codes.isOneTimeCodes())
			{
				(isUnused? unused:used).add(codeOrPlayer);
			}else{
				used.add(codeOrPlayer);
			}
		}

		StringBuffer content = new StringBuffer();
		if(codes.isOneTimeCodes())
		{
			content.append("&b&l没有使用过的礼包码: ("+unused.size()+")\n&a");
			for (String p : unused)
			{
				content.append(p);
				content.append("\n");
			}

			content.append("&b&l已经使用过的礼包码: ("+used.size()+")\n&8");
			for (String p : used)
			{
				content.append(p);
				content.append("\n");
			}
		}else{
			content.append("&b礼包码: &e"+codes.publicCode +"\n");
			content.append("&b&l已经使用过的玩家: ("+used.size()+")\n&8");

			for (String p : used)
			{
				content.append(p);
				content.append("\n");
			}
		}


		setContent(TextFormat.colorize(content.toString().trim()));

	}


	@Override
	public void onFormResponse(PlayerFormRespondedEvent e)
	{
		e.getPlayer().showFormWindow(new CodesPanel(UUID.fromString(codesUUID)));
	}

	@Override
	public void onFormClose(PlayerFormRespondedEvent e)
	{
		e.getPlayer().showFormWindow(new CodesPanel(UUID.fromString(codesUUID)));
	}
}
