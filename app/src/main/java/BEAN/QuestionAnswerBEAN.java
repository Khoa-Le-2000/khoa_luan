package BEAN;

public class QuestionAnswerBEAN {
    int question;
    int answer;

    public QuestionAnswerBEAN(int question, int answer) {
        this.question = question;
        this.answer = answer;
    }

    public QuestionAnswerBEAN() {
    }

    public int getQuestion() {
        return question;
    }

    public int getAnswer() {
        return answer;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
