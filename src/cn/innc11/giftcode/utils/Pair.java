package cn.innc11.giftcode.utils;

public class Pair<K, V>
{
    public K key;

    public V value;

    public Pair(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    public K getKey()
    {
        return this.key;
    }

    public V getValue()
    {
        return this.value;
    }
}
