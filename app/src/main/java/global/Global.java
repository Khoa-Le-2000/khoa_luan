package global;

public class Global {
    private static int[] correctAnswer;

    public static void setCorrectAnswer(int[] correctAnswer) {
        Global.correctAnswer = correctAnswer.clone();
        for (int i = 0; i < Global.correctAnswer.length; i++) {
            Global.correctAnswer[i]++;
        }
    }

    public static boolean checkHaveCorrectAnswer() {
        return Global.correctAnswer != null;
    }

    public static int[] getCorrectAnswer() {
        return correctAnswer;
    }
}
