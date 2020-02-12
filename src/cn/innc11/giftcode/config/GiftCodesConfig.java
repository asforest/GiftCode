package cn.innc11.giftcode.config;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Codes;

import java.util.UUID;

public class GiftCodesConfig extends MyConfig
{
    public GiftCodesConfig()
    {
        super("gift-codes.yml");
    }

    public void reload()
    {
        config.reload();
        GiftCodePlugin.ins.codes.clear();
        for (String key : config.getKeys(false))
        {
            Codes codeSet = new Codes();
            codeSet.uuid = UUID.fromString(config.getString(key + ".uuid"));
            codeSet.label = config.getString(key + ".label", config.getString(key + ".lable", ""));
            codeSet.giftUuid = !config.getString(key + ".giftUuid").isEmpty() ? UUID.fromString(config.getString(key + ".giftUuid")) : null;
            codeSet.enable = config.getBoolean(key + ".enable");
            if(config.exists(key + ".giftCode_whileNotOneTime"))
            {
                codeSet.publicCode = config.getString(key + ".giftCode_whileNotOneTime");
            }else {
                codeSet.publicCode = config.getString(key + ".publicGiftCode");
            }
            codeSet.timeOut = config.getInt(key + ".timeOut");
            codeSet._codeLength = config.getInt(key + "._codeLength");
            codeSet._codeCount = config.getInt(key + "._codeCount");
            codeSet._timeout = config.getInt(key + "._timeout");
            codeSet._specifiedCode = config.getString(key + "._specifiedCode", "");
            for (String codeKey : config.getSection(key + ".codes").getKeys(false))
            {
                String k = codeKey;
                boolean v = config.getBoolean(key + ".codes." + codeKey);
                codeSet.codes.put(k, Boolean.valueOf(v));
            }
            GiftCodePlugin.ins.codes.put(codeSet.uuid, codeSet);
        }
    }

    protected void _save()
    {
        config.getRootSection().clear();
        for (Codes codeSet : GiftCodePlugin.ins.codes.values())
        {
            String uuid = codeSet.uuid.toString();
            config.set(uuid + ".uuid", uuid);
            config.set(uuid + ".label", codeSet.label);
            config.set(uuid + ".giftUuid", (codeSet.giftUuid == null) ? "" : codeSet.giftUuid.toString());
            config.set(uuid + ".enable", Boolean.valueOf(codeSet.enable));
            config.set(uuid + ".timeOut", Long.valueOf(codeSet.timeOut));
            config.set(uuid + ".publicGiftCode", codeSet.publicCode);
            codeSet.codes.forEach((k, v)->config.set(uuid + ".codes." + k, v));
            config.set(uuid + "._codeLength", Integer.valueOf(codeSet._codeLength));
            config.set(uuid + "._codeCount", Integer.valueOf(codeSet._codeCount));
            config.set(uuid + "._timeout", Long.valueOf(codeSet._timeout));
            config.set(uuid + "._specifiedCode", codeSet._specifiedCode);
        }
        config.save();
    }
}
