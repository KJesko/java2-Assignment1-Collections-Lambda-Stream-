import java.io.IOException;

public class testLoadingCsv {
    public static void main(String[] args) throws IOException {
        OnlineCoursesAnalyzer onlineCoursesAnalyzer = new OnlineCoursesAnalyzer("local.csv");

        for (DataEntity dataEntity : onlineCoursesAnalyzer.dataList){
            System.out.println(dataEntity.strList.get(8));
        }
    }
}
