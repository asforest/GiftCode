package cn.innc11.giftcode.dt;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.nukkit.utils.TextFormat;

import java.util.*;
import java.util.regex.Pattern;

public class Codes
{
    public UUID uuid;
    public String label = "";
    public UUID giftUuid;
    public boolean enable;
    public boolean isOneTime;
    public HashMap<String, Boolean> codes = new HashMap<>();
    public String publicGiftCode = "";
    public long timeOut;
    public int _codeLength;
    public int _codeCount;
    public long _timeout;
    public String _specifiedCode;

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
        if (isOneTime)
        {
            for (String key : codes.keySet())
            {
                if (!codes.get(key).booleanValue())
                    count++;
            }
        } else
        {
            count = codes.size();
        }
        return count;
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
        int count = -1;
        if (isOneTime)
            count = codes.size();
        return count;
    }

    public void printToConsole()
    {
        if (!isInitialized())
            return;

        if (isOneTime)
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
        } else
        {
            ArrayList<String> usedCodes = new ArrayList<>();
            for (String codeText : codes.keySet())
                usedCodes.add(codeText);
            GiftCodePlugin.ins.getLogger().info(label + ": " + publicGiftCode);
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

    public boolean isInitialized()
    {
        if (isOneTime)
            return !codes.isEmpty();
        return !publicGiftCode.isEmpty();
    }

    public boolean needRegenerate()
    {
        return getUsedCount() == 0 && getTotalCount() == 0;
    }

    public void regenerate()
    {
        codes.clear();
        publicGiftCode = "";
        LinkedList<String> list = new LinkedList<>();
        for (Codes codeSet : GiftCodePlugin.ins.codes.values())
        {
            for (String code : codeSet.codes.keySet())
                list.add(code);
        }
        timeOut = (_timeout != 0L) ? (System.currentTimeMillis() / 1000L / 60L + _timeout) : 0L;
        List<String> rt = getRandomGiftCode(list, isOneTime ? _codeCount : 1);
        if (isOneTime)
        {
            for (String code : rt)
                codes.put(code, true);
        } else {
            publicGiftCode = rt.get(0);
        }
    }

    public boolean isTimeout()
    {
        if (timeOut == 0L)
            return false;
        long cu = System.currentTimeMillis() / 1000L / 60L;
        return (cu > timeOut);
    }

    private List<String> getRandomGiftCode(List<String> list, int count)
    {
        if(_specifiedCode.isEmpty() || isOneTime)
        {
            LinkedList<String> llist = new LinkedList<>();
            for (int i = 0; i < count; )
            {
                while (true)
                {
                    String temp = _getRandomGiftCode(_codeLength);
                    boolean notExist = !list.contains(temp) && GiftCodePlugin.ins.getCodesWithGiftCode(temp)==null;

                    if (notExist)
                    {
                        list.add(temp);
                        llist.add(temp);
                        break;
                    }
                }
                i++;
            }

            return llist;
        }else {
            String b = _specifiedCode;

            while (b.contains("*"))
            {
                b = b.replaceFirst("\\*", _getRandomGiftCode(1));
            }

            return Arrays.asList(b);
        }

    }

    public boolean isModified()
    {
        boolean modified = false;
        if (!codes.isEmpty())
            modified = (isOneTime && Pattern.matches("^[a-zA-Z0-9]+$", (codes.keySet().toArray(new String[0]))[0]));
        return modified;
    }

    public Gift getGift()
    {
        if (giftUuid != null)
            return GiftCodePlugin.ins.getGiftWithUUID(giftUuid);
        return null;
    }

    public int DgetDays()
    {
        if (_timeout >= 1440L)
            return (int) (_timeout / 1440L);
        return 0;
    }

    public int DgetHours()
    {
        if (_timeout >= 60L)
            return (int) (_timeout % 1440L / 60L);
        return 0;
    }

    public int DgetMinutes()
    {
        if (_timeout >= 60L)
            return (int) (_timeout % 60L);
        return 0;
    }
}
