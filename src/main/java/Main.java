import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Scanner;


public class Main {
    static final String logo = ("          _      _   _ _       _     _     ____  _             \n" +
            "__      _| | __ | \\ | (_) __ _| |__ | |_  / ___|(_) __ _ _ __  \n" +
            "\\ \\ /\\ / / |/ / |  \\| | |/ _` | '_ \\| __| \\___ \\| |/ _` | '_ \\ \n" +
            " \\ V  V /|   <  | |\\  | | (_| | | | | |_   ___) | | (_| | | | |\n" +
            "  \\_/\\_/ |_|\\_\\ |_| \\_|_|\\__, |_| |_|\\__| |____/|_|\\__, |_| |_|\n" +
            "                         |___/                     |___/       \n");

    static final String HELP = "[?] 使用id=学号 img=本地图片路径 或 id=学号 imgbase64=图片base64编码 直接进行签到";
    static final String help = logo+"\n\t[?] 这是一个智慧维科每晚拍照脚本\n"+HELP;

    /**
     * 传入学号与图片的base64编码,自动完成就寝打卡
     * username: 学号
     * img: 图片的base64编码
     */
    static String id;
    static String base64;
    static String img;
    public static void main(String[] args) {

        if(args.length < 1) {
            System.out.println(logo+"[!] 参数错误\n[?] 可使用help查看帮助");
            return;
        }
        for(String s : args){
            String[] s2;
            if(s.indexOf("=")!=-1){
                s2 = s.split("=");
                switch(s2[0]){
                    case "id":
                        id = s2[1];
                        break;
                    case "img":
                        img = s2[1];
                        break;
                    case "base64":
                        base64 = s2[1];
                        break;
                    default:
                        System.out.println("未知的参数:"+s2[1]);
                }
                continue;
            }
            switch (s){
                case "help":
                    System.out.println(help);
                    return;
                case "command":
                    command();
                    return;
                default:
                    System.out.println("未知的参数:"+s);
                    return;
            }
        }
        signstart();
        System.out.println("[*] 运行结束");
    }

    public static void command(){
        System.out.println("\n" +
                "   mmm   mmmm  m    m m    m   mm   mm   m mmmm  \n" +
                " m\"   \" m\"  \"m ##  ## ##  ##   ##   #\"m  # #   \"m\n" +
                " #      #    # # ## # # ## #  #  #  # #m # #    #\n" +
                " #      #    # # \"\" # # \"\" #  #mm#  #  # # #    #\n" +
                "  \"mmm\"  #mm#  #    # #    # #    # #   ## #mmm\" \n\n");
        Scanner input = new Scanner(System.in);
        System.out.print("[+] 请输入学号:");
        id= input.next();
        System.out.print("[?] 请选择:\n\t[1]使用本地图片\t[2]使用图片base64编码:");
        String choose = input.next();
        switch (choose){
            case "1":
                img = choose;
                break;
            case "2":
                base64 = choose;
                break;
            default:
                System.out.println("参数错误");
                return;
        }
        signstart();
    }

    private static void signstart() {
        if(id!=null){
            if(img!=null){
                File file = new File(img);
                if(file.exists()){
                    Encoder encoder = Base64.getEncoder();
                    // 将图片文件转化为二进制流
                    InputStream in = null;
                    byte[] data = null;
                    // 读取图片字节数组
                    try {
                        in = new FileInputStream(img);
                        data = new byte[in.available()];
                        in.read(data);
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 图片头
                    String imghead = "data:image/jpeg;base64,";
                    String imgbase64 = encoder.encodeToString(data);
                    base64 = imghead+imgbase64;
                    Student stu = new Student(id, base64);
                    String result = stu.StartSign();
                    System.out.println("\n[*] 本次令牌:"+stu.token+"\n\n[*] 图片保存位置:"+stu.urlPrefix+"/"+stu.filePath.replaceAll("%2F","/")+"\n\n[*] 学生信息:"+stu.StuInfo+"\n\n[*] 打卡信息:"+stu.Task+"\n\n[*] 执行结果:"+result+"\n");
                }
            }else if(base64!=null){
                new Student(id,base64).StartSign();
            }
        }else {
            System.out.println("[!] 参数不全");
        }
    }
}
