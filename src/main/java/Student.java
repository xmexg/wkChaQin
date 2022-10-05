import com.google.gson.Gson;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Student {
    //网址前缀
    public final String urlPrefix = "http://xgjktb.wfust.edu.cn";

    //主要的
    public static String userid; //学号
    public static String IMGBASE64; //图片的base64编码

    //辅助的
    public String token; //令牌,可在 post /app/auth/getAccessToken 获取
    public String filePath; //图片在服务器中的保存位置

    /* 不必要的,名称和服务器的保持一致,猜一猜就知道名称是什么了
     * post /app/student/getSignSummary 接口中获取
     */

    /**
     * 学生信息
     *         public String user_name; //姓名,准确的说应该是昵称,但学校要求昵称就是真实姓名
     *         public int shouldSignCount; //应该打卡的次数
     *         public int unSignCount; //未打卡的次数
     *         public int signCount; //已经打卡的次数
     *         public int njmc; //年级名称
     *         public int yxid; //院系id
     *         public int zyid; //专业id
     *         public int njid; //年级id
     *         public String user_tel; //电话
     *         public String zymc; //专业名称
     *         public int bjid; //班级id
     *         public String yxmc; //院系名称
     *         public String user_card; //身份证号
     *         public String user_sex; //性别,1为男,0为女
     *         public String bjmc; //班级名称
     *         public String student_no; //学号,和userid一样的
     *         public String user_id; //用户id,是一串序列号
     *         public String user_realname; //真实姓名
     */
    public HashMap<String, String> StuInfo = new HashMap<String, String>();

    /**
     * 查寝信息
     *         public String task_name; //任务名称
     *         public String end_date; //结束日期
     *         public String weeks; //周数
     *         public String begin_date; //开始日期
     *         public String task_id; //任务id
     *         public String opt_begin_time; //每天开始时间
     *         public String frequency;
     *         public String mode;
     *         public String update_time;
     *         public String update_user;
     *         public String director_flag;
     *         public String place_id;
     *         public String all_count;
     *         public String photo_flag;
     *         public String place_name;
     *         public String director_type;
     *         public String create_time;
     *         public String opt_end_time;
     *         public String user_id; //用户id,是一串序列号
     *         public String director_name;
     *         public String within_flag;
     *         public String create_user;
     *         public String task_type;
     *         public String category;
     *         public String director_id;
     */
    public HashMap<String, String> Task = new HashMap<String, String>();


    private HttpSend httpSend = new HttpSend();//发送http请求的类

    private String getStuInfo(String date) {//日期类似于2022-10
        //获取学生信息,在/app/student/getSignSummary接口中获取
        HashMap<String, String> head = new HashMap<>();
        head.put("Host", "xgjktb.wfust.edu.cn");
        head.put("Referer", "http://xgjktb.wfust.edu.cn/dist/student/2daoHis.html");
        String result = httpSend.sendPost(urlPrefix + "/app/student/getSignSummary", "access_token=" + token + "&month=" + date + "&taskType=1", head);
        return result;
    }

    private String getToken(){//只需要学号就行
        //获取令牌,在/app/auth/getAccessToken接口中获取
        HashMap<String, String> head = new HashMap<>();
        head.put("Host", "xgjktb.wfust.edu.cn");
        String result = httpSend.sendPost(urlPrefix + "/app/auth/getAccessToken", "username=" + userid+"&flag=0", head);
        if(result == null || result.equals("")) {
            System.out.println("获取令牌失败");
            return null;
        }
        return result.substring(result.indexOf("access_token")+15, result.length()-3);
    }

    private void updata_All(){
        //更新全部信息,(可选,但不必要)
    }

    private String getTasks(){
        //获取查寝任务
        HashMap<String, String> head = new HashMap<>();
        head.put("Host", "xgjktb.wfust.edu.cn");
        head.put("Accept", "application/json, text/javascript, */*; q=0.01");
        head.put("Accept-Encoding", "gzip, deflate");
        head.put("Origin", "http://xgjktb.wfust.edu.cn");
        head.put("Referer", "http://xgjktb.wfust.edu.cn/dist/student/2daoIndex.html");
        String result = httpSend.sendPost(urlPrefix + "/app/student/getTasks", "access_token=" + token + "&endFlag=0&page=1&taskType=1&rows=10", head);
        return result;
    }

    private String getTaskByTaskId(){
        //获取查寝任务详情
        HashMap<String, String> head = new HashMap<>();
        head.put("Host", "xgjktb.wfust.edu.cn");
        head.put("Accept", "application/json, text/javascript, */*; q=0.01");
        head.put("Accept-Encoding", "gzip, deflate");
        head.put("Origin", "http://xgjktb.wfust.edu.cn");
        head.put("Referer", "http://xgjktb.wfust.edu.cn/dist/student/2daoIndex.html");
        String result = httpSend.sendPost(urlPrefix + "/app/task/getTaskByTaskId", "access_token=" + token + "&taskId=" + Task.get("task_id"), head);
        return result;
    }

    private void updata_Task(String json){
        //更新查寝任务
        json = json.substring(json.lastIndexOf('{'), json.indexOf('}')+1);
        Task = new Gson().fromJson(json, Task.getClass());
    }

    private void updata_StuInfo(String json){
        //更新学生信息
        json = json.replace("\"user\":{","");
        json = json.substring(json.lastIndexOf('{'), json.indexOf('}')+1);
        StuInfo = new Gson().fromJson(json, StuInfo.getClass());

    }

    private String[] upImg(){
        //上传查寝图片,有2个值,第一个为状态,0为成功;第二个为成功后的文件保存地址
        HashMap<String, String> head = new HashMap<>();
        head.put("Host", "xgjktb.wfust.edu.cn");
        head.put("Accept", "application/json, text/javascript, */*; q=0.01");
        head.put("Origin", "http://xgjktb.wfust.edu.cn");
        head.put("Referer", "http://xgjktb.wfust.edu.cn/dist/student/2daoDetail.html");
        if(IMGBASE64 == null || IMGBASE64.length()==0){
            System.out.println("没有图片");
            return new String[]{"-1", "没有图片"};
        }else {
            String imgbase64 = IMGBASE64.replaceAll(":", "%3A").replaceAll("/", "%2F").replaceAll("\\+", "%2B").replaceAll(",", "%2C");
            String result = httpSend.sendPost(urlPrefix + "/generic/transPic2TempForBase64", "access_token=" + token + "&file=" + imgbase64, head);
            if(result == null || result.equals("")) {
                System.out.println("上传图片失败");
                return new String[]{"-1", "上传图片失败"};
            }
            String code = result.substring(result.indexOf("code") + 6, result.indexOf(','));
            String info = result.substring(result.indexOf("filePath") + 11, result.length() - 3);
            if(code.equals("0")) {
                this.filePath = info;
            }
            return new String[]{code, info};
        }
    }

    private String sign(){
        //开始查寝签到,filePath是指服务器中保存的图片位置
        HashMap<String, String> head = new HashMap<>();
        head.put("Host", "xgjktb.wfust.edu.cn");
        head.put("Referer", "http://xgjktb.wfust.edu.cn/dist/student/2daoDetail.html");
        head.put("Origin", "http://xgjktb.wfust.edu.cn");
        head.put("Accept", "application/json, text/javascript, */*; q=0.01");
        filePath = filePath.replace("/", "%2F");
        String result = httpSend.sendPost(urlPrefix + "/app/student/sign", "access_token=" + token + "&taskId=" + Task.get("task_id") + "&filePath=" + filePath, head);
        return result;
    }

    Student(String userid, String imgbase64) {//传入学号和图片的base64编码, 初始化学生数据
        this.userid = userid;
        this.IMGBASE64 = imgbase64;
        this.token = getToken();
        updata_Task(getTasks());
//        System.out.println(Task);
//        String result = getTaskByTaskId();//不必要
//        System.out.println(result);
        //日期
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
        String da = sf.format(date);
        updata_StuInfo(getStuInfo(da));
    }

    public String StartSign(){
        String[] info = upImg();
        String result;
        if(info[0].equals("0")){
            result = sign();
        }else{
            return info[1];
        }
        return result;
    }
}
