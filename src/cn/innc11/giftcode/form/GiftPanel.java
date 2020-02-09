package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Codes;
import cn.innc11.giftcode.dt.Gift;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;

import java.util.List;
import java.util.UUID;

public class GiftPanel extends FormWindowSimple implements FormResponse
{
	String giftUuid;

	public GiftPanel(UUID uuid)
	{
		super("xx礼包编辑面板", "");

		GiftCodePlugin plugin = GiftCodePlugin.ins;
		Gift gift = null;

		if (uuid == null || plugin.getGiftWithUUID(uuid)==null)
		{
			setTitle("创建新的礼包");
			gift = new Gift();

			do{
				gift.uuid = UUID.randomUUID();
			}while (plugin.getGiftWithUUID(gift.uuid)!=null);

			gift.label = gift.uuid.toString().substring(0, 6);
			plugin.addGift(gift);

			giftUuid = gift.uuid.toString();

		}else{
			gift = plugin.getGiftWithUUID(uuid);
			setTitle(gift.label);

			String content = "";
			for (Item item : gift.items)
			{
				content += String.format("%s x %d\n", item.getName(),item.count);
			}
			setContent(content.trim());

			giftUuid = gift.uuid.toString();
		}

        addButton(new ElementButton("设置礼包标签"));
        addButton(new ElementButton("设置礼包为快捷栏物品"));
        addButton(new ElementButton("设置礼包为背包物品(不包括装备栏)"));
        addButton(new ElementButton("给自己一份这个礼包"));
        addButton(new ElementButton("删除这个礼包"));
	}

	@Override
	public void onFormClose(PlayerFormRespondedEvent e)
	{
		e.getPlayer().showFormWindow(new GiftListPanel());
	}

	public void onFormResponse(PlayerFormRespondedEvent e)
	{
		Player player = e.getPlayer();

		if (!player.isOp())
			return;

		int i;
		List<Codes> depends;
		String msg;
		Gift gift = GiftCodePlugin.ins.getGiftWithUUID(giftUuid);
		int clickedButtonIndex = getResponse().getClickedButtonId();
		PlayerInventory playerInv = player.getInventory();

		switch (clickedButtonIndex)
		{
			case 0:
			{
				player.showFormWindow(new GiftLableEditPanel(gift.uuid));
				break;
			}

			case 1:
			{
				gift.items.clear();
				for (i = 0; i < playerInv.getHotbarSize(); i++)
				{
					Item item = playerInv.getItem(i);
					if (item.getId() != 0)
						gift.items.add(item);
				}
				GiftCodePlugin.ins.saveGiftsConfig();
				player.showFormWindow(new GiftPanel(gift.uuid));
				break;
			}

			case 2:
			{
				gift.items.clear();
				for (i = 0; i < playerInv.getSize(); i++)
				{
					Item item = playerInv.getItem(i);
					if (item.getId() != 0)
						gift.items.add(item);
				}
				GiftCodePlugin.ins.saveGiftsConfig();
				player.showFormWindow(new GiftPanel(gift.uuid));
				break;
			}

			case 3:
			{
				if (!gift.give(player))
					player.sendMessage("背包剩余空间需要大于"+ gift.items.size() + "个格子");
				break;
			}

			case 4:
			{
				depends = gift.getCodeSet();

				if (depends.isEmpty())
				{
					player.showFormWindow(new RemoveGiftConfirmPanel(gift.uuid));
					break;
				}

				msg = gift.label+"有被依赖关系,包括这些礼包码: ";
				for (Codes codeSet : depends)
				{
					msg += codeSet.label + ",";
				}

				msg += " 需要先删除这些礼包码然后才能再删除这个礼包";
				player.sendMessage(msg);
				break;
			}
		}


	}
}
