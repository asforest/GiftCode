package cn.innc11.giftcode.dt;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.nukkit.utils.TextFormat;

import java.util.*;

public class Codes
{
    public UUID uuid;
    public String label = "";
    public UUID giftUuid;
    public boolean enable = false;
    public long timeOut = 0L;

    public HashMap<String, Boolean> codes = new HashMap<>(); // onetime Codes Or Used Players(value:false)
    public String publicCode = "";

    // generate parameter
    public int _codeLength = 8;
    public int _codeCount = 0;
    public long _timeout = 0L;
    public String _specifiedCode = "";

    private static String _getRandomGiftCode(int length)
    {
        String str = GiftCodePlugin.ins.charPool;
        Random random = new Random();
        StringBuffer sb = new StringBuffer(length);

        for (int i = 0; i < length; i++)
            sb.append(str.charAt(random.nextInt(str.length())));

        return sb.toString();
    }

    public int getUsedCount()
    {
        int count = 0;
        if (isOneTimeCodes())
        {
            for (String key : codes.keySet())
            {
                if (!codes.get(key).booleanValue())
                    count++;
            }
        } else {
            count = codes.size();
        }
        return count;
    }

    public boolean isOneTimeCodes()
    {
        return publicCode.isEmpty();
    }

    public boolean isPublicCodes()
    {
        return !isOneTimeCodes();
    }

    public boolean isCompleteParameters()
    {
        return (_codeLength >= 4 && giftUuid != null);
    }

    public boolean isSetGift()
    {
        return (giftUuid != null);
    }

    public boolean isSetDeadline()
    {
        return timeOut!=0;
    }

    public int getTotalCount()
    {
        if (isOneTimeCodes())
            return codes.size();
        else
            return getUsedCount();
    }

    public void printToConsole()
    {
        if (!isGenerated())
            return;

        if (isOneTimeCodes())
        {
            ArrayList<String> usedCodes = new ArrayList<>();
            ArrayList<String> unusedCodes = new ArrayList<>();
            for (String codeText : codes.keySet())
            {
                boolean used = (codes.get(codeText)).booleanValue();
                (!used ? usedCodes : unusedCodes).add(codeText);
            }
            GiftCodePlugin.ins.getLogger().info("已经使用过的礼包码(" + label + "):");
            printListToConsole(usedCodes, "&8   ");
            GiftCodePlugin.ins.getLogger().info("-----------End------------");
            GiftCodePlugin.ins.getLogger().info("没有使用过的礼包码(" + label + "):");
            printListToConsole(unusedCodes, "&a   ");
            GiftCodePlugin.ins.getLogger().info("-----------End------------");
        } else {
            ArrayList<String> usedCodes = new ArrayList<>();
            for (String codeText : codes.keySet())
                usedCodes.add(codeText);
            GiftCodePlugin.ins.getLogger().info(label + ": " + publicCode);
            GiftCodePlugin.ins.getLogger().info("已经使用过的礼包码的玩家" + label + "):");
            printListToConsole(usedCodes, "&8   ");
            GiftCodePlugin.ins.getLogger().info("-----------End------------");
        }
    }

    private void printListToConsole(ArrayList<String> list, String prefix)
    {
        for (String codeText : list)
        {
            GiftCodePlugin.ins.getLogger().info(TextFormat.colorize(String.format("%s%s", prefix, codeText)));
        }
    }

    public boolean isGenerated()
    {
        if (isOneTimeCodes())
            return getTotalCount()>0;

        return !publicCode.isEmpty();
    }

    public void regenerate()
    {
        codes.clear();
        publicCode = _codeCount==0? "5g9rtgh84hfh4t4gdf84fg56db1v":"";
        timeOut = (_timeout != 0L) ? (System.currentTimeMillis() / 1000L / 60L + _timeout) : 0L;

        List<String> genCodes = genRandomGiftCodes( isOneTimeCodes()? _codeCount : 1);

        if (isOneTimeCodes())
        {
            for (String code : genCodes)
            {
                codes.put(code, true);
            }
        } else {
            publicCode = genCodes.get(0);
        }

        GiftCodePlugin.ins.saveGiftCodesConfig();
    }

    public boolean isTimeout()
    {
        if (timeOut == 0L)
            return false;
        long cu = System.currentTimeMillis() / 1000L / 60L;
        return (cu > timeOut);
    }

    private List<String> genRandomGiftCodes(int count)
    {
        GiftCodePlugin plugin = GiftCodePlugin.ins;

        if( _specifiedCode.isEmpty() || isOneTimeCodes() )
        {
            LinkedList<String> results = new LinkedList<>();

            for (int i = 0; i < count; i++)
            {
                String temp = null;

                do{
                    temp = _getRandomGiftCode(_codeLength);
                }while (plugin.getCodesWithGiftCode(temp)!=null);

                results.add(temp);
            }

            return results;
        }else {
            String b = _specifiedCode;

            while (b.contains("*"))
            {
                b = b.replaceFirst("\\*", _getRandomGiftCode(1));
            }

            return Arrays.asList(b);
        }

    }

    public Gift getGift()
    {
        if (giftUuid != null)
            return GiftCodePlugin.ins.getGiftWithUUID(giftUuid);
        return null;
    }

    public int getDeadlineDays()
    {
        if (_timeout >= 1440L)
            return (int) (_timeout / 1440L);
        return 0;
    }

    public int getDeadlineHours()
    {
        if (_timeout >= 60L)
            return (int) (_timeout % 1440L / 60L);
        return 0;
    }

    public int getDeadlineMinutes()
    {
        if (_timeout >= 60L)
            return (int) (_timeout % 60L);
        return 0;
    }
}
