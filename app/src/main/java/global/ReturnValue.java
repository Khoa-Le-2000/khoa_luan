package global;

public class ReturnValue {
    public final boolean success;
    public final int mark;
    public final float fps;

    public ReturnValue(boolean success, int mark, float fps) {
        this.success = success;
        this.mark = mark;
        this.fps = fps;
    }
}
