/*
 * Name: Adit Jain
 * EID: aj27923
 */

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Your solution goes in this class.
 * 
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    private int getPreferenceIndex(ArrayList<Integer> preferenceList, int entity) {
        int index = -1;
        for (int i = 0; i < preferenceList.size(); i++) {
            if (preferenceList.get(i) == entity) {
                index = i;
                break;
            }
        }
        return index;
    }


    /**
     * Compute the preference lists for each internship, given weights and student metrics.
     * Return a ArrayList<ArrayList<Integer>> prefs, where prefs.get(i) is the ordered list of preferred students for
     * internship i, with length studentCount.
     */
    public static ArrayList<ArrayList<Integer>> computeInternshipPreferences(int internshipCount, int studentCount,
                                                                      ArrayList<ArrayList<Integer>>internship_weights,
                                                                      ArrayList<Double> student_GPA,
                                                                      ArrayList<Integer> student_months,
                                                                      ArrayList<Integer> student_projects){
        // First compute student scores
        ArrayList<ArrayList<Integer>> internshipPref = new ArrayList<>(internshipCount);
        //initialize rows
        for(int i = 0; i < internshipCount; i++) {
            internshipPref.add(new ArrayList<Integer>());
        }

        double[][] studentScores = new double[internshipCount][studentCount];


        for(int i = 0; i < internshipCount; i++) {
            for(int k = 0; k < studentCount; k++) {
                studentScores[i][k] = computeInternshipStudentScore(student_GPA.get(k), student_months.get(k), student_projects.get(k), internship_weights.get(i).get(0), internship_weights.get(i).get(1), internship_weights.get(i).get(2));
            }
        }

        /* DEBUGGING
        for(double i[] : studentScores)
            for(double j : i)
                System.out.println(j);
        */

        // construct internship preference list by adding the student scores for every internship in order of preference
        for(int i = 0; i < internshipCount; i++) {
            for(int j = 0; j < studentScores[i].length; j++) {
                double maxScore = studentScores[i][0];
                int studentWithMax = 0;
                for(int k = 0; k < studentScores[i].length; k++) {      // find max
                    if(studentScores[i][k] > maxScore) {
                        maxScore = studentScores[i][k];
                        studentWithMax = k;
                    }
                }
                internshipPref.get(i).add(studentWithMax);
                studentScores[i][studentWithMax] = -1;   // won't check this student again when looking for max

            }
        }
        // DEBUGGING
        //System.out.println(internshipPref);

        return internshipPref;
    }


    /**
     * Computes a student's score given the specific internship's weights and of course the student's data.
     */
    private static Double computeInternshipStudentScore(double studentGPA, int studentExp, int studentProjects, int
                                                        weightGPA, int weightExp, int weightProjects){
        return studentGPA*weightGPA + studentExp*weightExp + studentProjects*weightProjects;
    }


    /**
     * Determines whether a candidate Matching represents a solution to the Stable Marriage problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    public boolean isStableMatching(Matching marriage) {
        /* TODO implement this function */

        int n = marriage.getStudentCount();
        int m = marriage.getInternshipCount();
        ArrayList<ArrayList<Integer>> studentPrefs = marriage.getStudentPreference();
        ArrayList<ArrayList<Integer>> internPrefs = marriage.getInternshipPreference();
        ArrayList<Integer> matching = marriage.getStudentMatching();

        ArrayList<ArrayList<Integer>> internshipSlots = new ArrayList<ArrayList<Integer>>();
        //First initializing rows
        for(int i = 0; i < m; i++) {
            internshipSlots.add(new ArrayList<Integer>());
        }

        //Populating internshipSlots with all students that got matched to each internship
        for(int i = 0; i < matching.size(); i++) {
            if(matching.get(i) != -1) {
                internshipSlots.get(matching.get(i)).add(i);    // i are students, matching.get(i) is the internship
            }
        }

        ArrayList<Integer> numSlots = marriage.getInternshipSlots();
        for(int i = 0; i < internshipSlots.size(); i++) {
            if(internshipSlots.get(i).size() < numSlots.get(i))
                return false;
        }

        ArrayList<Integer> worstStudent = new ArrayList<Integer>();
        for(int i = 0; i < internshipSlots.size(); i++) {    // upper bound is number of internships
            int min = internshipSlots.get(i).get(0);        // setting first student in internship to min
            for (int k = 0; k < internshipSlots.get(i).size(); k++) {
                if (internPrefs.get(i).indexOf(internshipSlots.get(i).get(k)) > min)
                    min = internshipSlots.get(i).get(k);
            }
            worstStudent.add(min);      // each index is a internship, the value at each index is the worst student ID (not index)
        }

        for(int i = 0; i < m; i++) {    // go through every internship
            /*
            int studentMatched = matching.indexOf(i);   // which student got matched to the internship under consideration
            int indexOfMatchedOnInternshipPref = internPrefs.get(i).indexOf(studentMatched);    // index of matched student on internship's pref list
            int indexOfMatchedOnStudentPref = studentPrefs.get(studentMatched).indexOf(i);  // index of internship on matched student's pref list
            */
            int lastStudentIndex = internPrefs.get(i).indexOf(worstStudent.get(i));  // index of worst student matched with internship i in i's preference list

            for(int k = 0; k < lastStudentIndex; k++) {
                int studentK = internPrefs.get(i).get(k);
                if(matching.get(studentK) == -1)
                    return false;

                if(matching.get(studentK) != i) {  // only evaluating students that haven't been matched to this internship i
                    for(int x = 0; x < internshipSlots.get(i).size(); x++) {
                        int studentKInternship = matching.get(studentK);                    // which internship does student k have

                        int studentKInternshipIndex = studentPrefs.get(studentK).indexOf(studentKInternship);   // what index is student k's internship on his pref list
                        int indexOfInternshipIFromStudentK = studentPrefs.get(studentK).indexOf(i);    // what index is internship i on student k's preference list
                        // k : what index is student K on internship i's list
                        int studentCurrentlyMatched = internshipSlots.get(i).get(x);    // student that is matched to internship i; this will change on every iteration of the loop
                        int indexOfStudentCurrMatched = internPrefs.get(i).indexOf(studentCurrentlyMatched);  // what index is student x on internship i's pref list

                        if(k < indexOfStudentCurrMatched) {    // does internship i prefer k to x
                            if(indexOfInternshipIFromStudentK < studentKInternshipIndex)    // does k prefer i to i'
                                return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * Determines a solution to the Stable Marriage problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_studentoptimal(Matching marriage) {
        /* TODO implement this function */

        // Creating a queue of students who are free, initially filled with all students
        Queue<Integer> freeStudents = new LinkedList<>();
        for(int i = 0; i < marriage.getStudentCount(); i++) {
            freeStudents.add(i);
        }
        int[] nextProposalToInternship = new int[marriage.getStudentCount()];   // array holding the index of each student's next proposal to be made, initially filled with 0s (everyone must propose to their first choice)
        int[] matchings = new int[marriage.getStudentCount()];

        // make internshipSlots array(list), every internship's live list of all currently engaged students
        ArrayList<ArrayList<Integer>> internshipSlots = new ArrayList<ArrayList<Integer>>();    // keeping track of who all is currently engaged with any internship (will later check InternshipSlot.get(i).size() < marriage.getInternshipSlots(i)
        //First initializing rows
        for(int i = 0; i < marriage.getInternshipCount(); i++) {
            internshipSlots.add(new ArrayList<Integer>());
        }

        // keep track of lowest preferred student who is currently matched with an internship
        //int[] lowestStudent = new int[marriage.getInternshipCount()];   // each internship has a lowest preferred current matching (student IDs stored)

        while(freeStudents.size() != 0) {
            int freeS = freeStudents.remove();  // choose a student that is free and hasn't proposed to every internship yet
            int internshipToProposeTo = 0;
            if(nextProposalToInternship[freeS] != -1)
                internshipToProposeTo = marriage.getStudentPreference().get(freeS).get(nextProposalToInternship[freeS]);    // actual internship ID
            else
                internshipToProposeTo = -1;

            if(internshipToProposeTo == -1) {   // if next internship freeS needs to propose to is -1, this implies freeS has exhausted his list of proposals
                matchings[freeS] = -1;
            }
            else if(marriage.getStudentPreference().get(freeS).indexOf(internshipToProposeTo) ==  marriage.getStudentPreference().get(freeS).size() - 1)    // if internshipToProposeTo is at the last index of studentPref list
                nextProposalToInternship[freeS] = -1;   // if this student gets rejected here, they don't have anyone else to match with
            else {
                nextProposalToInternship[freeS]++;  // increment next proposal freeS will make, in case he gets dropped later; we are incrementing the index to indicate the next internship to propose to on freeS' preference list
            }


            int worstStudent = 0;
            double worstScore = 0;

            // CALCULATING WORST STUDENT FOR INTERNSHIP TO PROPOSE TO
            if (internshipToProposeTo != -1 && internshipSlots.get(internshipToProposeTo).size() > 0) {    // if there are people currently matched to this internship
                if(marriage.getInternshipSlots().get(internshipToProposeTo) > 0) {     // the slots available is greater than zero
                    worstStudent = internshipSlots.get(internshipToProposeTo).get(0);
                    worstScore = computeInternshipStudentScore(marriage.getStudentGPA().get(worstStudent), marriage.getStudentMonths().get(worstStudent), marriage.getStudentProjects().get(worstStudent), marriage.getInternshipWeights().get(internshipToProposeTo).get(0), marriage.getInternshipWeights().get(internshipToProposeTo).get(1), marriage.getInternshipWeights().get(internshipToProposeTo).get(2));

                    for (int i = 0; i < internshipSlots.get(internshipToProposeTo).size(); i++) {
                        int studentUnderConsideration = internshipSlots.get(internshipToProposeTo).get(i);
                        double studentScore = computeInternshipStudentScore(marriage.getStudentGPA().get(studentUnderConsideration), marriage.getStudentMonths().get(studentUnderConsideration), marriage.getStudentProjects().get(studentUnderConsideration), marriage.getInternshipWeights().get(internshipToProposeTo).get(0), marriage.getInternshipWeights().get(internshipToProposeTo).get(1), marriage.getInternshipWeights().get(internshipToProposeTo).get(2));
                        if (studentScore < worstScore) {
                            worstScore = studentScore;
                            worstStudent = studentUnderConsideration;
                        }
                    }
                }
            }
            else {      // no one currently matched to the internship
                if(internshipToProposeTo != -1 && marriage.getInternshipSlots().get(internshipToProposeTo) > 0) {     // the slots available is greater than zero
                    worstStudent = freeS;
                    worstScore = computeInternshipStudentScore(marriage.getStudentGPA().get(worstStudent), marriage.getStudentMonths().get(worstStudent), marriage.getStudentProjects().get(worstStudent), marriage.getInternshipWeights().get(internshipToProposeTo).get(0), marriage.getInternshipWeights().get(internshipToProposeTo).get(1), marriage.getInternshipWeights().get(internshipToProposeTo).get(2));
                }
            }
            // use worstStudent instead of lowestStudent[internshipToProposeTo]


            // Write out logic to actually propose
            // if internshipToProposeTo has open slots, engage freeS and internship
            // else
            //   if internshipToProposeTo prefers freeS over their lowest preferred current engagement, engage freeS and drop lowest preferred current engagement, adding lowest onto the queue since he is now free
            //   else (freeS is not preferred) add freeS back onto the queue

            if(matchings[freeS] != -1) {

                double scoreOfFreeS = computeInternshipStudentScore(marriage.getStudentGPA().get(freeS), marriage.getStudentMonths().get(freeS), marriage.getStudentProjects().get(freeS), marriage.getInternshipWeights().get(internshipToProposeTo).get(0), marriage.getInternshipWeights().get(internshipToProposeTo).get(1), marriage.getInternshipWeights().get(internshipToProposeTo).get(2));
                if (internshipSlots.get(internshipToProposeTo).size() < marriage.getInternshipSlots().get(internshipToProposeTo)) {   // if internshipToProposeTo has open slots

                    matchings[freeS] = internshipToProposeTo;   // set internship matched to freeS' matching
                    internshipSlots.get(internshipToProposeTo).add(freeS);  // add freeS on internshipSlots of currently engaged students

                } else {  // internshipToProposeTo has NO OPEN SLOTS

                    if (scoreOfFreeS > worstScore) {      // check to see if internshipToProposeTo prefers freeS over their lowest preferred current engagement. Must update lowestStudent and drop previous engagements accordingly!!
                        internshipSlots.get(internshipToProposeTo).add(freeS);      // add freeS
                        internshipSlots.get(internshipToProposeTo).remove(internshipSlots.get(internshipToProposeTo).indexOf(worstStudent));    // remove lowest preferred
                        freeStudents.add(worstStudent);
                        matchings[freeS] = internshipToProposeTo;
                    }
                    else {      // freeS not preferred, add freeS back onto the queue
                        freeStudents.add(freeS);
                    }
                }
            }
        }

        ArrayList<Integer> finalMatchingList = new ArrayList<Integer>();
        for(int i : matchings)
            finalMatchingList.add(i);

        return new Matching(marriage, finalMatchingList);
    }


    private ArrayList<Matching> getAllStableMarriages(Matching marriage) {
        ArrayList<Matching> marriages = new ArrayList<>();
        int n = marriage.getStudentCount();
        int slots = marriage.totalInternshipSlots();

        Permutation p = new Permutation(n, slots);
        Matching matching;
        while ((matching = p.getNextMatching(marriage)) != null) {
            if (isStableMatching(matching)) {
                marriages.add(matching);
            }
        }

        return marriages;
    }


    @Override
    public Matching stableMarriageBruteForce_studentoptimal(Matching marriage) {
        ArrayList<Matching> allStableMarriages = getAllStableMarriages(marriage);
        Matching studentOptimal = null;
        int n = marriage.getStudentCount();
        int m = marriage.getInternshipCount();
        System.out.println("student" + n + "internship" + m);
        ArrayList<ArrayList<Integer>> student_preference = marriage.getStudentPreference();

        //Construct an inverse list for constant access time
        ArrayList<ArrayList<Integer>> inverse_student_preference = new ArrayList<ArrayList<Integer>>(0) ;
        for (Integer i=0; i<n; i++) {
            ArrayList<Integer> inverse_preference_list = new ArrayList<Integer>(m) ;
            for (Integer j=0; j<m; j++)
                inverse_preference_list.add(-1) ;
            ArrayList<Integer> preference_list = student_preference.get(i) ;

            for (int j=0; j<m; j++) {
                inverse_preference_list.set(preference_list.get(j), j) ;
            }
            inverse_student_preference.add(inverse_preference_list) ;
        }

        // bestStudentMatching stores the rank of the best Internship each student matched to
        // over all stable matchings
        int[] bestStudentMatching = new int[marriage.getStudentCount()];
        Arrays.fill(bestStudentMatching, -1);
        for (Matching mar : allStableMarriages) {
            ArrayList<Integer> student_matching = mar.getStudentMatching();
            for (int i = 0; i < student_matching.size(); i++) {
                if (student_matching.get(i) != -1 && (bestStudentMatching[i] == -1 ||
                        inverse_student_preference.get(i).get(student_matching.get(i)) < bestStudentMatching[i])) {
                    bestStudentMatching[i] = inverse_student_preference.get(i).get(student_matching.get(i));
                    studentOptimal = mar;
                }
            }
        }

        return studentOptimal;
    }
}
