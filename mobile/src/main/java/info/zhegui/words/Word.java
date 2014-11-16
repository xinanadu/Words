package info.zhegui.words;

/**
 * Created by Administrator on 2014/11/15.
 */
public class Word {
    public int id;
    public String key;
    public String content;
    public boolean remember;
    public int times;
    public int lesson;
    public String type;

    public Word(int id, String key, String content, boolean remember, int times, int lesson, String type) {
        this.id = id;
        this.key = key;
        this.content = content;
        this.remember = remember;
        this.times = times;
        this.lesson = lesson;
        this.type = type;
    }
}
