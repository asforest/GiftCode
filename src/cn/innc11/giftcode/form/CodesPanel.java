package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Codes;
import cn.innc11.giftcode.dt.Gift;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class CodesPanel extends FormWindowSimple implements FormResponse
{
    String codesUUID;

    public CodesPanel(UUID uuid)
    {
        super("xx礼包码面板", "");

        GiftCodePlugin plugin = GiftCodePlugin.ins;
        Codes codes = plugin.getCodesWithUUID(uuid);
        boolean isEditing = codes!=null;
        
        if (isEditing)
        {
            setTitle(codes.label);

            Gift gift = plugin.getGiftWithUUID(codes.giftUuid);

            String deadline = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(codes.timeOut * 60L * 1000L);

            StringBuffer content = new StringBuffer();
            content.append("对应的礼包: "+(codes.isSetGift()? gift.label :"还未指定")+"\n");
            content.append("过期时间:   "+(codes.isSetDeadline()? deadline:"不会过期")+"\n");

            if (!codes.isCompleteParameters())
            {
                content.append("- 需要设置参数\n");
            }

            if (!codes.enable)
            {
                content.append("- 礼包码没有启用\n");
            }

            if (codes.isGenerated())
            {
                if (codes.isOneTimeCodes())
                {
                    content.append(String.format("- 使用情况: %d/%d\n", codes.getUsedCount(), codes.getTotalCount()));
                } else {
                    content.append(String.format("- 使用情况: %d\n", codes.getUsedCount()));
                }

                if(!codes.isGenerated())
                {
                    content.append("- 礼包码需要重新生成\n");
                }
            } else {
                content.append(String.format("- 使用情况: 还未生成礼包码\n"));
            }

            setContent(content.toString().trim());
        } else {
            setTitle("创建新的礼包码");

            codes = new Codes();

            do {
                codes.uuid = UUID.randomUUID();
            } while(plugin.codes.containsKey(uuid));

            codes.label = codes.uuid.toString().substring(0, 6);

            plugin.addCodes(codes);
            plugin.saveGiftCodesConfig();
        }

        codesUUID = codes.uuid.toString();

        addButton(new ElementButton("设置标签\n" + codes.label));
        addButton(new ElementButton("设置参数并重新生成\n"));
        addButton(new ElementButton((codes.enable ? "禁" : "启") + "用这组礼包码\n当前: " + (codes.enable ? "启用" : "禁用")));
        addButton(new ElementButton("在后台打印礼包码和使用过的玩家"));
        addButton(new ElementButton("礼包码概览"));
        addButton(new ElementButton(codes.isGenerated() ? "重新生成礼包码" : "生成礼包码"));
        addButton(new ElementButton("删除这组礼包码"));

    }

    @Override
    public void onFormClose(PlayerFormRespondedEvent e)
    {
        e.getPlayer().showFormWindow(new CodesListPanel());
    }

    public void onFormResponse(PlayerFormRespondedEvent e)
    {
        Player player = e.getPlayer();
        GiftCodePlugin plugin = GiftCodePlugin.ins;

        if (!player.isOp())
            return;

        final Codes codes = plugin.getCodesWithUUID(codesUUID);
        switch (getResponse().getClickedButtonId())
        {
            case 0:
            {
                player.showFormWindow(new CodesLabelEditPanel(codes.uuid));
                break;
            }

            case 1:
            {
                player.showFormWindow(new CodesEditPanel(codes.uuid));
                break;
            }

            case 2:
            {
                codes.enable = !codes.enable;
                plugin.saveGiftCodesConfig();
                player.showFormWindow(new CodesPanel(codes.uuid));
                break;
            }

            case 3:
            {
                if (codes.isGenerated())
                {
                    plugin.sendTitleMessage(player, "已在后台打印", ()->player.showFormWindow(new CodesPanel(codes.uuid)));
                    codes.printToConsole();
                } else {
                    plugin.sendTitleMessage(player, "礼包码没有生成", ()->player.showFormWindow(new CodesPanel(codes.uuid)));
                }
                break;
            }

            case 4:
            {
                if (codes.isGenerated())
                {
                    plugin.sendTitleMessage(player, TextFormat.colorize("&L概览加载中"), ()->player.showFormWindow(new CodesOverviewPanel(codes.uuid)));
                } else {
                    plugin.sendTitleMessage(player, "礼包码没有生成", ()->player.showFormWindow(new CodesPanel(codes.uuid)));
                }
                break;
            }

            case 5:
            {
                if (codes.isGenerated())
                {
                    player.showFormWindow(new RegenerateCodeSetConfirmPanel(codes.uuid));
                    break;
                }

                if (codes._codeLength < 4)
                {
                    plugin.sendTitleMessage(player, "礼包码需要设置长度", () -> player.showFormWindow(new CodesPanel(codes.uuid)));
                    return;
                }

                if (codes.giftUuid == null)
                {
                    plugin.sendTitleMessage(player, "需要设置对应的礼包", () -> player.showFormWindow(new CodesPanel(codes.uuid)));
                    return;
                }

                codes.regenerate();
                plugin.saveGiftCodesConfig();
                plugin.sendTitleMessage(player, "礼包码已经生成"+ (codes.isOneTimeCodes() ? (" x &6&l" + codes._codeCount) : ""), () -> player.showFormWindow(new CodesPanel(codes.uuid)));
                break;
            }

            case 6:
            {
                player.showFormWindow(new RemoveCodeSetConfirmPanel(codes.uuid));
                break;
            }
        }

    }

}
