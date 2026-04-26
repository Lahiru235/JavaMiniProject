package dao;

// -------------------------------------------------------
// CaCalculator – Handles CA-specific calculations and rules
// -------------------------------------------------------
public final class CaCalculator {

    public static final double QUIZ_MAX = 5.0;
    public static final double MID_MAX = 30.0;
    public static final double CA_MAX = 40.0;
    public static final double CA_PASS_MARK = 16.0; // 40% of CA (40)

    private CaCalculator() {
        // Utility class
    }

    // Compute CA (best 2 quizzes out of 3 + mid exam)
    public static double calculateCaMarks(double quiz1, double quiz2, double quiz3, double midMarks) {
        validateCaComponents(quiz1, quiz2, quiz3, midMarks);

        double lowestQuiz = Math.min(quiz1, Math.min(quiz2, quiz3));
        double bestTwoQuizTotal = quiz1 + quiz2 + quiz3 - lowestQuiz;
        return bestTwoQuizTotal + midMarks;
    }

    public static boolean isCaEligible(double caMarks) {
        return caMarks >= CA_PASS_MARK;
    }

    public static void validateCaComponents(double quiz1, double quiz2, double quiz3, double midMarks) {
        validateRange("Quiz 1", quiz1, 0, QUIZ_MAX);
        validateRange("Quiz 2", quiz2, 0, QUIZ_MAX);
        validateRange("Quiz 3", quiz3, 0, QUIZ_MAX);
        validateRange("Mid exam", midMarks, 0, MID_MAX);
    }

    private static void validateRange(String label, double value, double min, double max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(label + " must be between " + min + " and " + max + ".");
        }
    }
}

