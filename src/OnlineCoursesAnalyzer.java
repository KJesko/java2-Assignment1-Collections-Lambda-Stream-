import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DataEntity {
    String institution; // 0.online course holders
    String CourseNumber; // 1.the unique id of each course
    Calendar launchDate;  // 2.the launch date of each course
    String courseTitle; // 3.the title of each course
    String instructors; // 4.the instructors of each course
    String courseSubject; //5.the subject of each course
    int Year; //6.the last time of each course
    int honorCodeCertificates;//7.with (1), without (0).
    int participants; //8.(Course Content Accessed)the number of participants who have accessed the course
    int audited; //9.(> 50% Course Content Accessed) the number of participants who have audited more than 50% of the course
    int certified; //10.Total number of votes
    double auditedPercent;//11.the percent of the audited
    double certifiedPercent;//12.the percent of the certified

    double percentCertifiedAudited; //13.the percent of the certified with accessing the course more than 50%
    double playedVideoPercent; //14.the percent of playing video
    double postedInForumPercent; //15.the percent of posting in forum
    double gradeHigherThanZeroPercent; //16.the percent of grade higher than zero
    double totalCourseHours; //17.total course hours(per 1000)
    double MedianHoursForCertification; //18.median hours for certification
    double medianAge; //19.median age of the participants
    double MalePercent; //20.the percent of the male
    double FemalePercent; //21.the percent of the female
    double BachelorDegreeOrHigherPercent; //22.the percent of bachelor's degree of higher

    List<String> strList = new ArrayList<>();

    public DataEntity(String[] strArr) {
        for (String s : strArr) {
            s = s.replace("\"", "");
            this.strList.add(s);
        }
    }

    public Calendar getLaunchDate() {
        String[] strCalendar = strList.get(2).split("/");
        Calendar launchDate = Calendar.getInstance();
        try {
            launchDate.set(Integer.parseInt(strCalendar[2]), Integer.parseInt(strCalendar[0]) - 1, Integer.parseInt(strCalendar[1]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Calendar.getInstance();
        }

        return launchDate;
    }

    public String getCourseNumber() {
        return strList.get(1);
    }


    public String getInstitution() {
        return strList.get(0);
    }

    public String getInstAndSubject() {
        return strList.get(0) + "-" + strList.get(5);
    }

    public String getCourseTitle() {
        return strList.get(3);
    }

    public String[] getInstructors() {
        return strList.get(4).split(",");
    }

    public double getTotalCourseHours() {
        double totalCourseHours;
        try {
            totalCourseHours = Double.parseDouble(strList.get(17));
        } catch (NumberFormatException e) {
            return 0;
        }
        return totalCourseHours;
    }

    public double getAuditedPercent() {
        double auditedPercent;
        try {
            auditedPercent = Double.parseDouble(strList.get(11));
        } catch (NumberFormatException e) {
            return 0;
        }
        return auditedPercent;
    }

    public int getParticipants() {
        int participants;
        try {
            participants = Integer.parseInt(strList.get(8));
        } catch (NumberFormatException e) {
            return 0;
        }
        return participants;
    }

    public double getMedianAge() {
        double medianAge;
        try {
            medianAge = Double.parseDouble(strList.get(19));
        } catch (NumberFormatException e) {
            return 0;
        }
        return medianAge;
    }

    public double getMalePercent() {
        double malePercent;
        try {
            malePercent = Double.parseDouble(strList.get(20));
        } catch (NumberFormatException e) {
            return 0;
        }
        return malePercent;
    }

    public double getBachelorDegreeOrHigherPercent() {
        double bachelorDegreeOrHigherPercent;
        try {
            bachelorDegreeOrHigherPercent = Double.parseDouble(strList.get(22));
        } catch (NumberFormatException e) {
            return 0;
        }
        return bachelorDegreeOrHigherPercent;
    }


}

class CourseHelper {
    static Map<String, CourseHelper> helperMap = new HashMap<>();
    private Calendar launchDate = Calendar.getInstance();
    private String courseTitle;
    private int participantsTotal;
    private double malePercentAverage;
    private double ageAverage;
    private double degreePercentAverage;


    public CourseHelper() {
        this.launchDate.set(1, Calendar.FEBRUARY, 1);
        this.courseTitle = "";
        this.participantsTotal = 0;
        this.malePercentAverage = 0;
        this.ageAverage = 0;
        this.degreePercentAverage = 0;
    }

    public Calendar getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Calendar launchDate) {
        this.launchDate = launchDate;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getParticipantsTotal() {
        return participantsTotal;
    }

    public void setParticipantsTotal(int participantsTotal) {
        this.participantsTotal = participantsTotal;
    }

    public double getMalePercentAverage() {
        return malePercentAverage;
    }

    public void setMalePercentAverage(double malePercentAverage) {
        this.malePercentAverage = malePercentAverage;
    }

    public double getAgeAverage() {
        return ageAverage;
    }

    public void setAgeAverage(double ageAverage) {
        this.ageAverage = ageAverage;
    }

    public double getDegreePercentAverage() {
        return degreePercentAverage;
    }

    public void setDegreePercentAverage(double degreePercentAverage) {
        this.degreePercentAverage = degreePercentAverage;
    }

    public double calculateSimilarityValue(int age, int gender, int isBachelorOrHigher) {
        return Math.pow((age - this.ageAverage), 2)
                + Math.pow((gender * 100 - this.malePercentAverage), 2)
                + Math.pow((isBachelorOrHigher * 100 - this.degreePercentAverage), 2);
    }
}

public class OnlineCoursesAnalyzer {
    List<DataEntity> dataList = new ArrayList<>();
    String[] colName;

    public OnlineCoursesAnalyzer(String datasetPath) throws IOException {
        File csv = new File(datasetPath);
        String[] lineStringArray;
        List<String> lineStringList;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), StandardCharsets.UTF_8))) {
            String lineString = br.readLine();
            colName = lineString.split(","); //first row -- column name
            while ((lineString = br.readLine()) != null) {
                lineStringArray = lineString.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                DataEntity dataEntity = new DataEntity(lineStringArray);
                dataList.add(dataEntity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return This method returns a <institution, count> map, where the key is the institution while the
     * value is the total number of participants who have accessed the courses of the institution.
     * The map should be sorted by the alphabetical order of the institution.
     */
    public Map<String, Integer> getPtcpCountByInst() {


        Map<String, Integer> map = dataList.stream().collect(Collectors.groupingBy(DataEntity::getInstitution,
                Collectors.summingInt(DataEntity::getParticipants)));
        return OnlineCoursesAnalyzer.sortMapByKey(map);

    }

    /**
     * @return This method returns a <institution-course Subject, count> map, where the key is the string
     * concatenating the Institution and the course Subject (without quotation marks) using '-' while the value is
     * the total number of participants in a course Subject of an institution.
     * <p></p>
     * The map should be sorted by descending order of count (i.e., from most to least participants). If two
     * participants have the same count, then they should be sorted by the alphabetical order of the
     * institution-course Subject.
     */
    public Map<String, Integer> getPtcpCountByInstAndSubject() {

        Map<String, Integer> map = dataList.stream().collect(Collectors.groupingBy(DataEntity::getInstAndSubject,
                Collectors.summingInt(DataEntity::getParticipants)));

        return OnlineCoursesAnalyzer.sortMapByValue(map);
    }


    /**
     * An instructor may be responsible for multiple courses, including independently responsible courses and codeveloped courses.
     * <p>
     * This method returns a <Instructor, [[course1, course2,...],[coursek,coursek+1,...]]>
     * map, where the key is the name of the instructor (without quotation marks) while the value is a list
     * containing 2-course lists, where List 0 is the instructor's independently responsible courses, if s/he has no
     * independently responsible courses, this list also needs to be created, but with no elements. List 1 is the
     * instructor's co-developed courses, if there are no co-developed courses, do the same as List 0.
     * <p>
     * Note that the course title (without quotation marks) should be sorted by alphabetical order in the list, and the case of
     * identical names should be treated as the same person.
     */

    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        List<String[]> instructorsList = dataList.stream().map(DataEntity::getInstructors).toList();
        List<String> courseTitle = dataList.stream().map(DataEntity::getCourseTitle).toList();

        Map<String, List<List<String>>> map = new HashMap<>();
        for (int i = 0; i < instructorsList.size(); i++) {
            int flag = instructorsList.get(i).length;
            for (String instructor : instructorsList.get(i)) {

                List<List<String>> courseList;
                if (map.containsKey(instructor)) {
                    courseList = map.get(instructor);
                } else {
                    courseList = new ArrayList<>();
                    courseList.add(new ArrayList<>());
                    courseList.add(new ArrayList<>());
                }

                if (flag == 1) {
                    if (!courseList.get(0).contains(courseTitle.get(i)))
                        courseList.get(0).add(courseTitle.get(i));
                } else if (flag > 1) {
                    if (!courseList.get(1).contains(courseTitle.get(i)))
                        courseList.get(1).add(courseTitle.get(i));
                }

                map.put(instructor, courseList);

            }
            for (String instructor : map.keySet()) {
                List<List<String>> courseList = map.get(instructor);
                courseList.add(0, courseList.get(0).stream().distinct().sorted().collect(Collectors.toList()));
                courseList.remove(1);
                courseList.add(1, courseList.get(1).stream().distinct().sorted().collect(Collectors.toList()));
                courseList.remove(2);
            }
        }
        return map;
    }

    /**
     * This method returns the top K courses (parameter topK) by the given criterion (parameter by).
     *
     * @param topK top K courses
     * @param by   Specifically,
     *             by="hours": the results should be courses sorted by descending order of Total Course Hours
     *             (Thousands) (from the longest course to the shortest course).
     *             by="participants": the results should be courses sorted by descending order of the number of
     *             the Participants (Course Content Accessed) (from the most to the least).
     * @return Note that the results should be a list of Course titles. If two courses have the same total Course hours or
     * participants, then they should be sorted by alphabetical order of their titles. The same course title can only
     * occur once in the list.
     */
    public List<String> getCourses(int topK, String by) {
        List<String> list = new ArrayList<>();
        Map<String, Double> tempMap = new HashMap<>();
        if (by.equals("hours")) {
            dataList.forEach((DataEntity dataEntity)->{
                double hours = 0;
                String title = dataEntity.getCourseTitle();
                if (tempMap.containsKey(title)){
                    hours = tempMap.get(title);
                }
                hours = Math.max(dataEntity.getTotalCourseHours(), hours);
                tempMap.put(title,hours);
            });
            //            System.out.println("-------------------");
            //            System.out.println(map);
            //            System.out.println("-------------------");
        } else if (by.equals("participants")) {
            dataList.forEach((DataEntity dataEntity)->{
                double participants = 0;
                String title = dataEntity.getCourseTitle();
                if (tempMap.containsKey(title)){
                    participants = tempMap.get(title);
                }
                participants = Math.max(dataEntity.getParticipants(), participants);
                tempMap.put(title,participants);
            });
//            System.out.println(map.values().stream().limit(topK).collect(Collectors.toList()));
        }

        Map<String, Double> map = tempMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal, LinkedHashMap::new));
        list = map.keySet().stream().limit(topK).collect(Collectors.toList());
        System.out.println(map.values().stream().limit(topK).collect(Collectors.toList()));//

        return list;

    }

    /**
     * This method searches courses based on three criteria:
     * <p></p>
     *
     * @param courseSubject    Fuzzy matching is supported and case-insensitive. If the input courseSubject is
     *                         "science", all courses whose course subject includes "science" or "Science" or whatever (case-insensitive) meet the criteria.
     * @param percentAudited   the percent of the audited should >= percentAudited
     * @param totalCourseHours the Total Course Hours (Thousands) should <= totalCourseHours
     * @return Note that the results should be a list of course titles that meet the given criteria, and sorted by alphabetical
     * order of the titles. The same course title can only occur once in the list.
     */

    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        Predicate<DataEntity> p = (DataEntity dataEntity) -> dataEntity.strList.get(5).toLowerCase().contains(courseSubject.toLowerCase());
        p = p.and((DataEntity dataEntity) -> dataEntity.getAuditedPercent() >= percentAudited);
        p = p.and((DataEntity dataEntity) -> dataEntity.getTotalCourseHours() <= totalCourseHours);
        return dataList.stream().filter(p).map(DataEntity::getCourseTitle).sorted().toList().stream().distinct().collect(Collectors.toList());
    }

    /**
     * This method recommends 10 courses based on the following input parameter:
     * First, calculate the average Median Age, average % Male, and average % Bachelor's Degree or
     * Higher for each course. Note that Course Number is the unique id of each course;
     * <p>
     * Note that the results should be a list of course titles. A Course Number may correspond to different
     * course titles, please return the course title with the latest Launch Date and the same course
     * title can only occur once in the list. The courses should be sorted by their similarity values. If two courses
     * have the same similarity values, then they should be sorted by alphabetical order of their titles.
     * <p></p>
     *
     * @param age                age of the user
     * @param gender             0-female, 1-male
     * @param isBachelorOrHigher 0-Not get bachelor degree, 1- Bachelor degree or higher
     * @return return the top 10 courses with the smallest similarity value.
     */
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        List<CourseHelper> courseInfos = dataList.stream().map(OnlineCoursesAnalyzer::generateHelper).toList();
        Map<String,Double> titleAndSimilarityValues = CourseHelper.helperMap.values().stream()
                .collect(Collectors.toMap(CourseHelper::getCourseTitle,c -> c.calculateSimilarityValue(age, gender, isBachelorOrHigher),(o1,o2)->o1<o2?o1:o2));

        titleAndSimilarityValues = titleAndSimilarityValues.entrySet().stream()
                .sorted(Map.Entry.<String,Double>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey())).limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal, LinkedHashMap::new));

        System.out.println(titleAndSimilarityValues);

        return courseInfos.stream().sorted((o1, o2) -> {
            double diff = o1.calculateSimilarityValue(age, gender, isBachelorOrHigher)
                    - o2.calculateSimilarityValue(age, gender, isBachelorOrHigher);
            if (diff > 0) return 1;
            else if (diff < 0) return -1;
            else return o1.getCourseTitle().compareTo(o2.getCourseTitle());
        }).map(CourseHelper::getCourseTitle).distinct().limit(10).collect(Collectors.toList());

    }


    public static Map<String, Integer> sortMapByKey(Map<String, Integer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal, LinkedHashMap::new));

    }

    public static Map<String, Integer> sortMapByValue(Map<String, Integer> map) {//descending order
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()).thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal, LinkedHashMap::new));

    }

    public static CourseHelper generateHelper(DataEntity dataEntity) {
        String courseId = dataEntity.getCourseNumber();
        CourseHelper courseInfo;
        if (CourseHelper.helperMap.containsKey(courseId)) {
            courseInfo = CourseHelper.helperMap.get(courseId);
        } else courseInfo = new CourseHelper();

        int newParticipantsTotal = courseInfo.getParticipantsTotal() + dataEntity.getParticipants();

        double averAge = courseInfo.getAgeAverage() * ((double) courseInfo.getParticipantsTotal() / newParticipantsTotal)
                + dataEntity.getMedianAge() * ((double) dataEntity.getParticipants() / newParticipantsTotal);
        double averMalePercent = courseInfo.getMalePercentAverage() * ((double) courseInfo.getParticipantsTotal() / newParticipantsTotal)
                + dataEntity.getMalePercent() * ((double) dataEntity.getParticipants() / newParticipantsTotal);
        double averDegreePercent = courseInfo.getDegreePercentAverage() * ((double) courseInfo.getParticipantsTotal() / newParticipantsTotal)
                + dataEntity.getBachelorDegreeOrHigherPercent() * ((double) dataEntity.getParticipants() / newParticipantsTotal);


        courseInfo.setParticipantsTotal(newParticipantsTotal);
        courseInfo.setAgeAverage(averAge);
        courseInfo.setMalePercentAverage(averMalePercent);
        courseInfo.setDegreePercentAverage(averDegreePercent);
        if (courseInfo.getLaunchDate().before(dataEntity.getLaunchDate())) {
            courseInfo.setCourseTitle(dataEntity.getCourseTitle());
            courseInfo.setLaunchDate(dataEntity.getLaunchDate());
        }

        CourseHelper.helperMap.put(courseId, courseInfo);
        return courseInfo;
    }


}
