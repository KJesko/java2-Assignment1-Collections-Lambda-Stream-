import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        OnlineCoursesAnalyzer onlineCoursesAnalyzer = new OnlineCoursesAnalyzer("local.csv");

        //basic info
        System.out.println(Arrays.toString(onlineCoursesAnalyzer.colName));
        System.out.println(onlineCoursesAnalyzer.colName.length);
        System.out.println(onlineCoursesAnalyzer.dataList.size());
//        System.out.println(onlineCoursesAnalyzer.dataList.get(25).strList.get(17));

        System.out.println("--------test1 pass--------");
        System.out.println(onlineCoursesAnalyzer.getPtcpCountByInst());
        System.out.println("--------test2 pass--------");
        System.out.println(onlineCoursesAnalyzer.getPtcpCountByInstAndSubject());
        System.out.println("--------test3 may pass--------");
        System.out.println(onlineCoursesAnalyzer.getCourseListOfInstructor());
        System.out.println("--------test4.1 pass--------");
        System.out.println(onlineCoursesAnalyzer.getCourses(20,"hours"));
        System.out.println("--------test4.2 pass--------");
        System.out.println(onlineCoursesAnalyzer.getCourses(5,"participants"));
        System.out.println("--------test6 going--------");

        System.out.println(onlineCoursesAnalyzer.recommendCourses(18,1,1));
    }
}