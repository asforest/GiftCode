package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Codes;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;

public class CodesListPanel extends FormWindowSimple implements FormResponse
{
	static final int buttons = 1;
	String codesIndex = "";

	public CodesListPanel()
	{
		super(String.format("礼包码管理面板 (%d)", GiftCodePlugin.ins.codes.size()), "");

		addButton(new ElementButton("创建一组新的礼包码"));

		GiftCodePlugin plugin = GiftCodePlugin.ins;

		for(Codes codes : plugin.codes.values())
		{
			String string1 = codes.label;
			String string2 = codes.isCompleteParameters() ? "("+ codes.getGift().label +")" : "";
			String string3 = String.format("%d%s", codes.getUsedCount(), codes.isOneTimeCodes() ? ("/" + codes.getTotalCount()) : "");

			String buttonText = String.format("%s %s\n%s", string1, string2, string3);

			if(!codes.isGenerated() || !codes.enable)
			{
				buttonText = TextFormat.colorize("&l"+buttonText);
			}

			addButton(new ElementButton(buttonText));

			codesIndex += String.format("%s;", codes.uuid);
		};

	}

	@Override
	public void onFormClose(PlayerFormRespondedEvent e)
	{
		e.getPlayer().showFormWindow(new MainPanel());
	}

	public void onFormResponse(PlayerFormRespondedEvent e)
	{
		Player player = e.getPlayer();

		if (!player.isOp())
			return;

		int clickedButtonIndex = getResponse().getClickedButtonId();

		if (clickedButtonIndex == 0)
		{
			player.showFormWindow(new CodesPanel(null));
		}  else {
			ArrayList<String> list = new ArrayList<>();

			for (String cu : codesIndex.split(";"))
			{
				list.add(cu);
			}

			Codes codes = GiftCodePlugin.ins.getCodesWithUUID(list.get(clickedButtonIndex - buttons));
			player.showFormWindow(new CodesPanel(codes.uuid));
		}
	}
}
