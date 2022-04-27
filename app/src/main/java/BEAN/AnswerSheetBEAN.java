package BEAN;

public class AnswerSheetBEAN {
    long answerSheetId;
    String name;
    QuestionAnswerBEAN[] listAnswer;

    public AnswerSheetBEAN(long answerSheetId, String name, QuestionAnswerBEAN[] listAnswer) {
        this.answerSheetId = answerSheetId;
        this.name = name;
        this.listAnswer = listAnswer;
    }

    public AnswerSheetBEAN(long answerSheetId, String name) {
        this.answerSheetId = answerSheetId;
        this.name = name;
    }

    public AnswerSheetBEAN(String name, QuestionAnswerBEAN[] listAnswer) {
        this.name = name;
        this.listAnswer = listAnswer;
    }

    public AnswerSheetBEAN() {
    }

    public long getAnswerSheetId() {
        return answerSheetId;
    }

    public void setAnswerSheetId(long answerSheetId) {
        this.answerSheetId = answerSheetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QuestionAnswerBEAN[] getListAnswer() {
        return listAnswer;
    }

    public void setListAnswer(QuestionAnswerBEAN[] listAnswer) {
        this.listAnswer = listAnswer;
    }

    public int[] getListAnswerInt() {
        int[] res = new int[listAnswer.length];
        for (QuestionAnswerBEAN questionAnswerBEAN :
                listAnswer) {
            res[questionAnswerBEAN.question - 1] = questionAnswerBEAN.answer;
        }
        return res;
    }
}
