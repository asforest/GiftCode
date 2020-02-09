package cn.innc11.giftcode.config;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Gift;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class GiftsConfig extends MyConfig
{
    public GiftsConfig()
    {
        super("gifts.yml");
    }

    protected void _save()
    {
        this.config.getRootSection().clear();
        for (Gift gift : GiftCodePlugin.ins.gifts.values())
        {
            String uuid = gift.uuid.toString();
            this.config.set(uuid + ".lable", gift.label);
            this.config.set(uuid + ".uuid", uuid);
            for (Item item : gift.items)
            {
                UUID ruuid = UUID.randomUUID();
                String prefix = uuid + ".items." + ruuid;
                int id = item.getId();
                int metadata = item.getDamage();
                int count = item.getCount();
                String name = item.getName();
                String[] lore = item.getLore();
                String customName = item.getCustomName();
                Enchantment[] enchantments = item.getEnchantments();
                String nbtTag = item.hasCompoundTag() ? Base64.getEncoder().encodeToString(item.getCompoundTag()) : null;
                this.config.set(prefix + ".itemId", Integer.valueOf(id));
                this.config.set(prefix + ".metadata", Integer.valueOf(metadata));
                this.config.set(prefix + ".count", Integer.valueOf(count));
                if (item.hasCustomName() && !customName.isEmpty())
                    this.config.set(prefix + ".customName", customName);
                if (lore != null && lore.length > 0)
                    this.config.set(prefix + ".lore", lore);
                if (nbtTag != null && !nbtTag.isEmpty())
                    this.config.set(prefix + ".nbtTag", nbtTag);
                this.config.set(prefix + ".count", Integer.valueOf(count));
                this.config.set(prefix + ".name", name);
                for (Enchantment enchant : enchantments)
                    this.config.set(prefix + ".enchantment.E" + enchant.getId(), Integer.valueOf(enchant.getLevel()));
            }
        }
        this.config.save();
    }

    public void reload()
    {
        this.config.reload();
        GiftCodePlugin.ins.gifts.clear();
        for (String key : this.config.getKeys(false))
        {
            Gift gift = new Gift();
            gift.label = this.config.getString(key + ".lable");
            gift.uuid = UUID.fromString(this.config.getString(key + ".uuid"));
            for (String itemsKey : this.config.getSection(key + ".items").getKeys(false))
            {
                String prefix = key + ".items." + itemsKey;
                String nbtTagText = this.config.getString(prefix + ".nbtTag");
                List<String> loreText = this.config.getStringList(prefix + ".lore");
                int id = this.config.getInt(prefix + ".itemId");
                int metadata = this.config.getInt(prefix + ".metadata");
                int count = this.config.getInt(prefix + ".count");
                String name = this.config.getString(prefix + ".name");
                byte[] nbtTag = (nbtTagText != null && !nbtTagText.isEmpty()) ? Base64.getDecoder().decode(nbtTagText) : null;
                String customName = this.config.getString(prefix + ".customName");
                String[] lore = (loreText != null && !loreText.isEmpty()) ? loreText.toArray(new String[0]) : null;
                Item item = Item.get(id, Integer.valueOf(metadata), count);
                if (customName != null && !customName.isEmpty())
                    item.setCustomName(customName);
                if (nbtTag != null && nbtTag.length > 0)
                    item.setCompoundTag(nbtTag);
                if (lore != null && lore.length > 0)
                    item.setLore(lore);
                for (String enchantKey : this.config.getSection(prefix + ".enchantment").getKeys(false))
                {
                    int enchantmentId = Integer.parseInt(enchantKey.substring(1));
                    int enchantmentLevel = this.config.getInt(prefix + ".enchantment." + enchantKey);
                    Enchantment enchantment = Enchantment.getEnchantment(enchantmentId);
                    enchantment.setLevel(enchantmentLevel);
                    item.addEnchantment(enchantment);
                }
                gift.items.add(item);
            }
            GiftCodePlugin.ins.gifts.put(gift.uuid, gift);
        }
    }
}
