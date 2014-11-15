package info.zhegui.words;

/**
 * Created by Administrator on 2014/11/15.
 */
public class Word {
    public int id;
    public String key;
    public boolean remember;
    public int times;
    public int lesson;
    public String type;

    public Word(int id, String key, boolean remember, int times, int lesson, String type) {
        this.id = id;
        this.key = key;
        this.remember = remember;
        this.times = times;
        this.lesson = lesson;
        this.type = type;
    }
}
